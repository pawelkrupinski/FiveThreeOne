package net.pawel.mongo

import com.foursquare.rogue.LiftRogue._
import net.pawel.domain.Session

trait SessionFetcher {
  def sessions(): List[Session] = SessionMongo.findAll.map(_.toSession)
}

trait SessionPersister {
  def insert(session: Session) = SessionMongo.fromSession(session).save
  def delete(session: Session) = SessionMongo.where(_.date eqs session.date.toDate).findAndDeleteOne()
}

class SessionRepository extends SessionFetcher with SessionPersister

