package net.pawel.domain

import org.joda.time.DateTime

case class UpdateState(session: Session, updated: Boolean)

case class Session(date: DateTime, sets: Sets, exercise: Lift, id: Option[String] = None) {
  def updateSet(set: Set) =
    sets
      .updateSet(set)
      .map(updatedSet => UpdateState(copy(sets = updatedSet), true))
      .getOrElse(UpdateState(this, false))
}

case class Sets(warmup: List[Set], workingSets: List[Set]) {
  def updateSet(set: Set) = {
    val copied = copy(
      warmup = warmup.map(replaceSet(set)),
      workingSets = workingSets.map(replaceSet(set))
    )
    if (copied == this) None else Some(copied)
  }

  def replaceSet(replaceWith: Set)(set: Set) = if (set.id == replaceWith.id) replaceWith else set
}

case class Set(id: String, weight: BigDecimal, reps: Int) {
  def toLabel = s"$lastLabel $reps"

  def lastLabel = s"$weight kg x "
}