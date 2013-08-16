package net.pawel.domain

import org.joda.time.LocalDate

case class State(squat: LiftState,
                 deadLift: LiftState,
                 press: LiftState,
                 bench: LiftState,
                 currentLiftIndex: Int = 0) {
  val currentLift = Stream.continually(Stream(Squat, Press, Deadlift, Squat, Bench)).flatten.drop(currentLiftIndex)

  def nextSession = {
    currentLift.head match {
      case Squat => {
        val (session, state) = squat.nextSession
        (session, copy(currentLiftIndex = currentLiftIndex + 1, squat = state))
      }
      case Deadlift => {
        val (session, state) = deadLift.nextSession
        (session, copy(currentLiftIndex = currentLiftIndex + 1, deadLift = state))
      }
      case Press => {
        val (session, state) = press.nextSession
        (session, copy(currentLiftIndex = currentLiftIndex + 1, press = state))
      }
      case Bench => {
        val (session, state) = bench.nextSession
        (session, copy(currentLiftIndex = currentLiftIndex + 1, bench = state))
      }
    }

  }
}

case class LiftState(oneRepMax: BigDecimal, increment: BigDecimal, session: SessionType = Five, exercise: Lift) {
  val sets = session.sets(oneRepMax)

  def nextSession = (Session(new LocalDate(), sets, exercise), nextState)

  def nextState = copy(
    oneRepMax = if (session == Deload) oneRepMax + increment else oneRepMax,
    session = session.next
  )
}





