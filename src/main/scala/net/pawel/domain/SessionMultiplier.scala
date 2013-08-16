package net.pawel.domain

import scala.math.BigDecimal.RoundingMode

case class SetMultiplier(multiplier: BigDecimal) {
  def weight(oneRepMax: BigDecimal) = roundToClosest(multiplier * oneRepMax, BigDecimal("2.5"))

  def roundToClosest(number: BigDecimal, closest: BigDecimal) = {
    (number / closest).setScale(0, RoundingMode.UP) * closest
  }

  def toSet(oneRepMax: BigDecimal, reps: Int) = Set(weight(oneRepMax), reps)
}

object SetMultiplier {
  def apply(multiplier: String): SetMultiplier = SetMultiplier(BigDecimal(multiplier))
}

case class MultiplierAndReps(setMultiplier: SetMultiplier, reps: Int) {
  def toSet(oneRepMax: BigDecimal) = setMultiplier.toSet(oneRepMax, reps)
}