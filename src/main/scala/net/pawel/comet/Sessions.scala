package net.pawel.comet

import net.liftweb.http.{CometListener, ListenerManager, CometActor}
import net.liftweb.http.SHtml._
import scala.xml.Text
import net.liftweb.http.js.JsCmds
import net.pawel.domain._
import net.liftweb.common.{SimpleActor, Full}
import java.util.UUID
import net.liftweb.actor.LiftActor
import net.pawel.mongo.SessionRepository

object StateActor extends ListenerManager with LiftActor {
  val uuidGenerator = () => UUID.randomUUID().toString

  val sessionRepository = new SessionRepository

  var state = State(
    squat = LiftState(BigDecimal("150"), BigDecimal("2.5"), Five, Squat, uuidGenerator),
    deadLift = LiftState(BigDecimal("160"), BigDecimal("5"), Five, Deadlift, uuidGenerator),
    press = LiftState(BigDecimal("65"), BigDecimal("2.5"), Five, Press, uuidGenerator),
    bench = LiftState(BigDecimal("105"), BigDecimal("2.5"), Five, Bench, uuidGenerator),
    currentLiftIndex = 0
  )

  var sessions: List[Session] = sessionRepository.sessions().sortBy(-_.date.getMillis)

  protected def createUpdate = NewState(state, sessions)

  override protected def lowPriority = {
    case NewSession => addWorkout()
    case DeleteSession(session) => delete(session)
    case UpdateNextSessionType(lift, sessionType) => updateState(state.updateSessionType(lift, sessionType))
    case UpdateOneRepMax(lift, oneRepMax) => updateState(state.updateOneRepMax(lift, oneRepMax))
    case UpdateCurrentLift(liftIndex) => updateState(state.updateCurrentLift(liftIndex))
    case UpdateSet(set) => updateSet(set)
  }

  def addWorkout() {
    val (session, nextState) = state.nextSession
    state = nextState
    sessions = session :: sessions
    updateListeners()
    sessionRepository.insert(session)
  }

  def updateSet(set: Set) {
    updateSessions(sessions.map(_.updateSet(set)))
  }

  def delete(session: Session) = {
    updateSessions(sessions.filterNot(_ == session))
    sessionRepository.delete(session)
  }

  def updateSessions(sessions: List[Session]) {
    this.sessions = sessions
    updateListeners()
  }

  def updateState(state: State) {
    this.state = state
    updateListeners()
  }
}

case class NewState(state: State, sessions: List[Session])

case object NewSession
case class UpdateSet(set: Set)
case class DeleteSession(session: Session)
case class UpdateNextSessionType(lift: Lift, sessionType: SessionType)
case class UpdateOneRepMax(lift: Lift, oneRepMax: BigDecimal)
case class UpdateCurrentLift(liftIndex: Int)

class Sessions extends CometActor with CometListener {
  var state: Option[State] = None

  var sessions: List[Session] = Nil

  protected def registerWith = StateActor

  override def lowPriority = {
    case NewState(newState, sessions) => {
      state = Some(newState)
      this.sessions = sessions
      reRender()
    }
  }

  def render = ".session" #> sessions.map(session => {
    ".date *" #> session.date.toString("dd/MM/yyyy") &
    ".exercise *" #> session.exercise.name &
    ".warmup *" #> warmup(session) &
    ".workSets *" #> workingSets(session) &
    ".delete *" #> ajaxButton("Delete", () => {
      StateActor ! DeleteSession(session); JsCmds.Noop
    }, ("class" -> "btn btn-large btn-primary"))
  }) & ".liftState" #> state.toList.flatMap(_.liftStates.map(liftState => {
    ".liftName *" #> liftState.exercise.name &
    ".nextSession *" #> nextSessionSelect(liftState) &
    ".oneRepMax *" #> oneRepMaxSelect(liftState)
  })) &
    ".nextWorkout" #> ajaxButton("Next workout", () => {
      StateActor ! NewSession; JsCmds.Noop
    }, ("class" -> "btn btn-large btn-primary")) &
    ".nextSession *" #> nextLiftSelect

  def nextLiftSelect = state match {
    case Some(state) => ajaxSelect(
      state.consecutiveLifts.map(_.name).zipWithIndex.map({case (name, index) => (index.toString, name)}),
      Full(state.currentLiftIndex.toString),
      index => {StateActor ! UpdateCurrentLift(index.toInt); JsCmds.Noop },
      ("class" -> "input-small")
    )
    case None => Text("")
  }


  def nextSessionSelect(liftState: LiftState) = ajaxSelect(
    SessionType.values.map(_.toString).map(value => (value, value)),
    Full(liftState.session.toString),
    sessionTypeName => {
      StateActor ! UpdateNextSessionType(liftState.exercise, SessionType.fromName(sessionTypeName))
      JsCmds.Noop
    },
    ("class" -> "input-small")
  )

  def oneRepMaxSelect(liftState: LiftState) = ajaxSelect(
    weights(liftState.exercise).map(_.setScale(1)).map(_.toString).map(value => (value, s"$value kg")),
    Full(liftState.oneRepMax.setScale(1).toString),
    newOneRepMax => {
      StateActor ! UpdateOneRepMax(liftState.exercise, BigDecimal(newOneRepMax))
      JsCmds.Noop
    },
    ("class" -> "input-small")
  )

  def weights(increment: BigDecimal, until: BigDecimal): Stream[BigDecimal] =
    Stream.from(1, 1).map(_ * increment).takeWhile(_ <= until)

  def weights(lift: Lift): Stream[BigDecimal] = lift match {
    case Deadlift => weights(BigDecimal("5"), BigDecimal("600"))
    case _ => weights(BigDecimal("2.5"), BigDecimal("500"))
  }

  def warmup(session: Session) =
    <span>
      {session.sets.warmup.flatMap(set => List(Text(set.toLabel), <br/>))}
    </span>

  def workingSets(session: Session) = {
    <span>
    {
      val workingSets = session.sets.workingSets
      val last = workingSets.last
      val withoutLast = workingSets.take(workingSets.size - 1)
      withoutLast.flatMap(set => List(Text(set.toLabel), <br/>)) ::: lastWorkingSet(last)
    }
    </span>
  }

  def lastWorkingSet(last: Set) = {
    List(Text(last.lastLabel),
      ajaxSelect((1 to 15).toList.map(_.toString).map(e => (e, e)), Full(last.reps.toString), value => {
        StateActor ! UpdateSet(last.copy(reps = value.toInt))
        JsCmds.Noop
      }, ("style" -> "width: 40px")))
  }
}