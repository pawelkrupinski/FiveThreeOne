package net.pawel.domain

sealed trait Lift {
  def name: String
}

object Lift {
  val values = List(Squat, Deadlift, Press, Bench)

  def forName(name: String) = values.find(_.name == name).get
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
