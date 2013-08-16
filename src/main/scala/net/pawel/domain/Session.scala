package net.pawel.domain

import org.joda.time.LocalDate

case class Session(date: LocalDate, sets: Sets, exercise: Lift)

case class Sets(warmup: List[Set], workingSets: List[Set])

case class Set(weight: BigDecimal, reps: Int) {
  def toLabel = s"$lastLabel $reps"

  def lastLabel = s"$weight kg x "
}