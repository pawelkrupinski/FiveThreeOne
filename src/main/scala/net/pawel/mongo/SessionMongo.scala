package net.pawel.mongo

import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import com.foursquare.rogue.ObjectIdKey
import com.foursquare.index.IndexedRecord
import net.liftweb.mongodb.record.field.{BsonRecordListField, DateField}
import net.liftweb.record.field.StringField
import net.pawel.config.SessionsMongoIdentifier
import net.pawel.domain.{Lift, Sets, Session}
import org.joda.time.DateTime

class SessionMongo extends MongoRecord[SessionMongo] with ObjectIdKey[SessionMongo] with IndexedRecord[SessionMongo] {
  def meta = SessionMongo

  object date extends DateField(this)
  object warmupSets extends BsonRecordListField(this, SetMongo)
  object workingSets extends BsonRecordListField(this, SetMongo)
  object lift extends StringField(this, 10)

  def toSession: Session = {
    Session(
      new DateTime(date.get),
      Sets(
        warmupSets.get.map(_.toSet),
        workingSets.get.map(_.toSet)
      ),
      Lift.forName(lift.get)
    )
  }
}

object SessionMongo extends SessionMongo with MongoMetaRecord[SessionMongo] {
  def fromSession(session: Session) = createRecord
    .date(session.date.toDate)
    .lift(session.exercise.name)
    .warmupSets(session.sets.warmup.map(SetMongo.fromSet))
    .workingSets(session.sets.workingSets.map(SetMongo.fromSet))

  override def collectionName = "session"
  override def mongoIdentifier = SessionsMongoIdentifier
}

