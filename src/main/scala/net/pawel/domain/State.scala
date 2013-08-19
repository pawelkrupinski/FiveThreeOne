package net.pawel.domain

import org.joda.time.{DateTime, LocalDate}

case class State(squat: LiftState,
                 deadLift: LiftState,
                 press: LiftState,
                 bench: LiftState,
                 currentLiftIndex: Int = 0) {

  val consecutiveLifts = List(Squat, Press, Deadlift, Squat, Bench)
  val numberOfLifts = consecutiveLifts.size
  val currentLift = consecutiveLifts.drop(currentLiftIndex).head
  val nextLiftIndex = (currentLiftIndex + 1) % numberOfLifts
  val liftStates = List(squat, deadLift, bench, press)

  def updateSessionType(lift: Lift, sessionType: SessionType) = lift match {
    case Squat => copy(squat = squat.copy(session = sessionType))
    case Deadlift => copy(deadLift = deadLift.copy(session = sessionType))
    case Press => copy(press = press.copy(session = sessionType))
    case Bench => copy(bench = bench.copy(session = sessionType))
  }

  def updateOneRepMax(lift: Lift, oneRepMax: BigDecimal) = lift match {
    case Squat => copy(squat = squat.copy(oneRepMax = oneRepMax))
    case Deadlift => copy(deadLift = deadLift.copy(oneRepMax = oneRepMax))
    case Press => copy(press = press.copy(oneRepMax = oneRepMax))
    case Bench => copy(bench = bench.copy(oneRepMax = oneRepMax))
  }

  def nextSession = {
    currentLift match {
      case Squat => {
        val (session, state) = squat.nextSession
        (session, copy(currentLiftIndex = nextLiftIndex, squat = state))
      }
      case Deadlift => {
        val (session, state) = deadLift.nextSession
        (session, copy(currentLiftIndex = nextLiftIndex, deadLift = state))
      }
      case Press => {
        val (session, state) = press.nextSession
        (session, copy(currentLiftIndex = nextLiftIndex, press = state))
      }
      case Bench => {
        val (session, state) = bench.nextSession
        (session, copy(currentLiftIndex = nextLiftIndex, bench = state))
      }
    }
  }

  def updateCurrentLift(currentLift: Int): State = copy(currentLiftIndex = currentLift)
}

case class LiftState(oneRepMax: BigDecimal,
                     increment: BigDecimal,
                     session: SessionType = Five,
                     exercise: Lift,
                     uuidGenerator: () => String) {
  val sets = session.sets(oneRepMax, uuidGenerator)

  def nextSession = (Session(new DateTime(), sets, exercise), nextState)

  def nextState = copy(
    oneRepMax = if (session == Deload) oneRepMax + increment else oneRepMax,
    session = session.next
  )
}





