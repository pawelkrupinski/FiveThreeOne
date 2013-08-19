package net.pawel.domain

import org.junit.Test
import net.liftweb.util.Props
import net.liftweb.common.Full
import net.pawel.config.SessionsMongoIdentifier
import net.pawel.mongo.SessionMongo

class PersistenceTest {

  Props.whereToLook = () =>
    List(("local", () => Full(classOf[PersistenceTest].getResourceAsStream("/props/local.props"))))


  @Test
  def bla() {
    SessionsMongoIdentifier.connectToMongo
    SessionMongo.findAll
  }
}
