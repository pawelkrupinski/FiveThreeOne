package net.pawel.domain

import scala.math.BigDecimal.RoundingMode
import java.util.UUID

case class SetMultiplier(multiplier: BigDecimal) {
  def weight(oneRepMax: BigDecimal) = roundToClosest(multiplier * oneRepMax, BigDecimal("2.5"))

  def roundToClosest(number: BigDecimal, closest: BigDecimal) = {
    (number / closest).setScale(0, RoundingMode.UP) * closest
  }

  def toSet(oneRepMax: BigDecimal, reps: Int, uuidGenerator: () => String) = Set(
    uuidGenerator(),
    roundToClosest(oneRepMax * multiplier, BigDecimal("2.5")),
    reps)
}

object SetMultiplier {
  def apply(multiplier: String): SetMultiplier = SetMultiplier(BigDecimal(multiplier))
}

case class MultiplierAndReps(setMultiplier: SetMultiplier, reps: Int) {
  def toSet(oneRepMax: BigDecimal, uuidGenerator: () => String) = setMultiplier.toSet(oneRepMax, reps, uuidGenerator)
}