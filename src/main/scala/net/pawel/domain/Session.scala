package net.pawel.domain

import org.joda.time.{DateTime, LocalDate}

case class Session(date: DateTime, sets: Sets, exercise: Lift) {
  def updateSet(set: Set) = copy(sets = sets.updateSet(set))
}

case class Sets(warmup: List[Set], workingSets: List[Set]) {
  def updateSet(set: Set) = copy(
    warmup = warmup.map(replaceSet(set)),
    workingSets = workingSets.map(replaceSet(set))
  )

  def replaceSet(replaceWith: Set)(set: Set) = if (set.id == replaceWith.id) replaceWith else set
}

case class Set(id: String, weight: BigDecimal, reps: Int) {
  def toLabel = s"$lastLabel $reps"

  def lastLabel = s"$weight kg x "
}