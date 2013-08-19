package net.pawel.mongo

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import com.foursquare.rogue.ObjectIdKey
import com.foursquare.index.IndexedRecord
import net.liftweb.mongodb.record.field.BsonRecordField
import net.liftweb.record.field.IntField
import net.pawel.config.SessionsMongoIdentifier

class StateMongo extends MongoRecord[StateMongo] with ObjectIdKey[StateMongo] with IndexedRecord[StateMongo] {
  def meta = StateMongo

  object squat extends BsonRecordField(this, LiftStateMongo)
  object deadLift extends BsonRecordField(this, LiftStateMongo)
  object press extends BsonRecordField(this, LiftStateMongo)
  object bench extends BsonRecordField(this, LiftStateMongo)
  object currentLiftIndex extends IntField(this)
}

object StateMongo extends StateMongo with MongoMetaRecord[StateMongo] {
  override def collectionName = "state"
  override def mongoIdentifier = SessionsMongoIdentifier
}