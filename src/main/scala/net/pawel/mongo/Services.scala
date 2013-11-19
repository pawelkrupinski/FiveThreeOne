package net.pawel.mongo

import com.foursquare.rogue.LiftRogue._
import net.pawel.domain.Session
import org.bson.types.ObjectId

trait SessionFetcher {
  def sessions(): List[Session] = SessionMongo.findAll.map(_.toSession)
}

trait SessionPersister {
  def insert(session: Session) = SessionMongo.fromSession(session).save
  def update(session: Session) = {
    SessionMongo
      .where(_._id eqs new ObjectId(session.id.get))
      .modify(_.workingSets setTo session.sets.workingSets.map(SetMongo.fromSet))
      .modify(_.warmupSets setTo session.sets.warmup.map(SetMongo.fromSet))
      .upsertOne()
  }
  def delete(session: Session) = SessionMongo.where(_._id eqs new ObjectId(session.id.get)).findAndDeleteOne()
}

class SessionRepository extends SessionFetcher with SessionPersister

