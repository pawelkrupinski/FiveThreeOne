package net.pawel.mongo

import net.liftweb.mongodb.record._
import net.liftweb.record.field._
import java.math.MathContext
import net.pawel.domain.{Lift, SessionType, LiftState}

class LiftStateMongo extends BsonRecord[LiftStateMongo] {
  def toLiftState(uuidGenerator: () => String): LiftState = LiftState(
    oneRepMax.get,
    increment.get,
    SessionType.fromName(sessionType.get),
    Lift.forName(exercise.get),
    uuidGenerator
  )

  def meta = LiftStateMongo

  object oneRepMax extends DecimalField(this, MathContext.UNLIMITED, 2)
  object increment extends DecimalField(this, MathContext.UNLIMITED, 2)
  object sessionType extends StringField(this, 10)
  object exercise extends StringField(this, 10)
}

object LiftStateMongo extends LiftStateMongo with BsonMetaRecord[LiftStateMongo] {
  def fromLiftState(state: LiftState): LiftStateMongo =
    createRecord
      .oneRepMax(state.oneRepMax)
      .increment(state.increment)
      .sessionType(state.session.toString)
      .exercise(state.exercise.name)
}
