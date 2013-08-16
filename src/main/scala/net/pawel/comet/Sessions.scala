package net.pawel.comet

import net.liftweb.http.CometActor
import net.liftweb.http.SHtml._
import scala.xml.Text
import net.liftweb.http.js.JsCmds
import net.pawel.domain._
import net.liftweb.common.Full

class Sessions extends CometActor {
  var state = State(
    squat = LiftState(BigDecimal("150"), BigDecimal("2.5"), Five, Squat),
    deadLift = LiftState(BigDecimal("160"), BigDecimal("5"), Five, Deadlift),
    press = LiftState(BigDecimal("65"), BigDecimal("2.5"), Five, Press),
    bench = LiftState(BigDecimal("105"), BigDecimal("2.5"), Five, Bench),
    currentLiftIndex = 0
  )

  var sessions: List[Session] = List()

  val (session, nextState) = state.nextSession
  state = nextState
  sessions = session :: sessions

  def render = ".session" #> sessions.map(session => {
    ".date *" #> session.date.toString("dd/MM/yyyy") &
      ".exercise *" #> session.exercise.name &
      ".warmup *" #> warmup(session) &
      ".workSets *" #> workingSets(session)
  }) &
  ".next" #> ajaxButton("Next workout", () => {addWorkout(); JsCmds.Noop })


  def warmup(session: Session) =
    <span>
      {session.sets.warmup.flatMap(set => List(Text(set.toLabel), <br/>))}
    </span>

  def workingSets(session: Session) = {
    <span>
      {
      val workingSets = session.sets.workingSets
      val last = workingSets.last
      val withoutLast = workingSets.take(2)
      withoutLast.flatMap(set => List(Text(set.toLabel), <br/>)) ::: lastWorkingSet(last)}
    </span>
  }


  def lastWorkingSet(last: Set) = {
    List(Text(last.lastLabel),
      ajaxSelect((1 to 15).toList.map(_.toString).map(e => (e, e)), Full(last.reps.toString), value => {
        replaceReps(value.toInt)
        JsCmds.Noop
      }))
  }

  def addWorkout() {
    val (session, nextState) = state.nextSession
    state = nextState
    sessions = session :: sessions
    reRender()
  }

  def replaceReps(reps: Int) {

  }
}