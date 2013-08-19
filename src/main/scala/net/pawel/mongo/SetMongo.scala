package net.pawel.mongo

import net.liftweb.mongodb.record.{BsonMetaRecord, BsonRecord}
import net.liftweb.record.field.{IntField, DecimalField, StringField}
import java.math.MathContext
import net.pawel.domain.Set

class SetMongo extends BsonRecord[SetMongo] {
  def meta = SetMongo

  object id extends StringField(this, 20)
  object weight extends DecimalField(this, MathContext.UNLIMITED, 2)
  object reps extends IntField(this)

  def toSet = Set(id.get, weight.get, reps.get)
}

object SetMongo extends SetMongo with BsonMetaRecord[SetMongo] {
  def fromSet(set: Set) = createRecord
    .id(set.id)
    .weight(set.weight)
    .reps(set.reps)
}