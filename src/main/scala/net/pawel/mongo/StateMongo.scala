package net.pawel.mongo

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import com.foursquare.rogue.ObjectIdKey
import com.foursquare.index.IndexedRecord
import net.liftweb.mongodb.record.field.BsonRecordField
import net.liftweb.record.field.IntField
import net.pawel.config.SessionsMongoIdentifier
import net.pawel.domain._
import org.bson.types.ObjectId
import net.pawel.domain.State
import net.pawel.domain.Session
import net.pawel.domain.LiftState

class StateMongo extends MongoRecord[StateMongo] with ObjectIdKey[StateMongo] with IndexedRecord[StateMongo] {
  def meta = StateMongo

  object squat extends BsonRecordField(this, LiftStateMongo)
  object deadLift extends BsonRecordField(this, LiftStateMongo)
  object press extends BsonRecordField(this, LiftStateMongo)
  object bench extends BsonRecordField(this, LiftStateMongo)
  object currentLiftIndex extends IntField(this)

  def toState(uuidGenerator: () => String) = State(
    squat.get.toLiftState(uuidGenerator),
    deadLift.get.toLiftState(uuidGenerator),
    press.get.toLiftState(uuidGenerator),
    bench.get.toLiftState(uuidGenerator),
    currentLiftIndex.get)
}

object StateMongo extends StateMongo with MongoMetaRecord[StateMongo] {
  def fromState(state: State): StateMongo =
    createRecord
      .squat(LiftStateMongo.fromLiftState(state.squat))
      .deadLift(LiftStateMongo.fromLiftState(state.deadLift))
      .press(LiftStateMongo.fromLiftState(state.press))
      .bench(LiftStateMongo.fromLiftState(state.bench))
      .currentLiftIndex(state.currentLiftIndex)

  override def collectionName = "state"
  override def mongoIdentifier = SessionsMongoIdentifier
}