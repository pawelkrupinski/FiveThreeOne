package net.pawel.domain

trait SessionType {
  val next: SessionType
  val warmupMultipliers: List[SetMultiplier]
  val workingSetMultipliers: List[MultiplierAndReps]

  def sets(oneRepMax: BigDecimal) = Sets(
    warmupMultipliers.map(_.toSet(oneRepMax, 5)),
    workingSetMultipliers.map(_.toSet(oneRepMax)))
}

case object Five extends SessionType {
  val next = Three
  val warmupMultipliers = List(
    SetMultiplier("0.35"),
    SetMultiplier("0.5")
  )
  val workingSetMultipliers = List(
    MultiplierAndReps(SetMultiplier("0.65"), 5),
    MultiplierAndReps(SetMultiplier("0.75"), 5),
    MultiplierAndReps(SetMultiplier("0.85"), 5)
  )
}

case object Three extends SessionType {
  val next: SessionType = One
  val warmupMultipliers: List[SetMultiplier] = List(
    SetMultiplier("0.4"),
    SetMultiplier("0.55")
  )
  val workingSetMultipliers: List[MultiplierAndReps] = List(
    MultiplierAndReps(SetMultiplier("0.7"), 3),
    MultiplierAndReps(SetMultiplier("0.8"), 3),
    MultiplierAndReps(SetMultiplier("0.9"), 3)
  )
}

case object One extends SessionType {
  val next: SessionType = Deload
  val warmupMultipliers: List[SetMultiplier] = List(
    SetMultiplier("0.3"),
    SetMultiplier("0.45"),
    SetMultiplier("0.6")
  )
  val workingSetMultipliers: List[MultiplierAndReps] = List(
    MultiplierAndReps(SetMultiplier("0.75"), 5),
    MultiplierAndReps(SetMultiplier("0.85"), 3),
    MultiplierAndReps(SetMultiplier("0.95"), 1)
  )
}

case object Deload extends SessionType {
  val next: SessionType = Five
  val warmupMultipliers: List[SetMultiplier] = Nil
  val workingSetMultipliers: List[MultiplierAndReps] = List(
    MultiplierAndReps(SetMultiplier("0.4"), 5),
    MultiplierAndReps(SetMultiplier("0.5"), 5),
    MultiplierAndReps(SetMultiplier("0.6"), 5)
  )
}
