package net.pawel.domain

sealed trait Lift {
  def name: String
}

case object Squat extends Lift {
  def name: String = "Squat"
}

case object Press extends Lift {
  def name: String = "Press"
}

case object Deadlift extends Lift {
  def name: String = "Deadlift"
}

case object Bench extends Lift {
  def name: String = "Bench"
}
