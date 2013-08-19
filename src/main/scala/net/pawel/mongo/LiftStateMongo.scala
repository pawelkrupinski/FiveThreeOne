package net.pawel.mongo

import com.foursquare.rogue.LiftRogue._
import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._
import net.liftweb.record.field._
import java.math.MathContext
import com.foursquare.rogue.ObjectIdKey
import com.foursquare.index.IndexedRecord
import net.pawel.config.SessionsMongoIdentifier

class LiftStateMongo extends BsonRecord[LiftStateMongo] {
  def meta = LiftStateMongo

  object oneRepMax extends DecimalField(this, MathContext.UNLIMITED, 2)
  object increment extends DecimalField(this, MathContext.UNLIMITED, 2)
  object sessionType extends StringField(this, 10)
  object exercise extends StringField(this, 10)
}

object LiftStateMongo extends LiftStateMongo with BsonMetaRecord[LiftStateMongo]
