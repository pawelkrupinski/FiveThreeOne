package net.pawel.comet

import net.pawel.domain._
import net.pawel.domain.Set
import net.pawel.domain.State
import net.pawel.domain.Session
import net.liftweb.http.ListenerManager
import net.liftweb.actor.LiftActor
import java.util.UUID
import net.pawel.mongo.{StateRepository, SessionRepository}

object StateActor extends ListenerManager with LiftActor {
  val uuidGenerator = () => UUID.randomUUID().toString

  val sessionRepository = new SessionRepository
  val stateRepository = new StateRepository

  lazy val defaultState = State(
    squat = LiftState(BigDecimal("150"), BigDecimal("2.5"), Five, Squat, uuidGenerator),
    deadLift = LiftState(BigDecimal("160"), BigDecimal("5"), Five, Deadlift, uuidGenerator),
    press = LiftState(BigDecimal("65"), BigDecimal("2.5"), Five, Press, uuidGenerator),
    bench = LiftState(BigDecimal("105"), BigDecimal("2.5"), Five, Bench, uuidGenerator),
    currentLiftIndex = 0
  )

  var state = stateRepository.state(uuidGenerator).getOrElse(defaultState)

  var sessions = sessionRepository.sessions().sortBy(-_.date.getMillis)

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

    val inserted = sessionRepository.insert(session)
    sessions = session.copy(id = Some(inserted.id.toString)) :: sessions.filterNot(_ == session)
    updateListeners()
  }

  def updateSet(set: Set) {
    val updatedSessions = sessions.map(_.updateSet(set))
    updateSessions(updatedSessions.map(_.session))
    sessionRepository.update(updatedSessions.find(_.updated).map(_.session).get)

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
    stateRepository.update(state)
  }
}

case class NewState(state: State, sessions: List[Session])

case object NewSession
case class UpdateSet(set: Set)
case class DeleteSession(session: Session)
case class UpdateNextSessionType(lift: Lift, sessionType: SessionType)
case class UpdateOneRepMax(lift: Lift, oneRepMax: BigDecimal)
case class UpdateCurrentLift(liftIndex: Int)


