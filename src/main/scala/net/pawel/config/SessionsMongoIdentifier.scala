package net.pawel.config

import net.liftweb.mongodb.{MongoDB, MongoIdentifier}
import com.mongodb.{ServerAddress, Mongo}
import net.liftweb.util.Props
import net.liftweb.common.Full

object SessionsMongoIdentifier extends MongoIdentifier {

  override def jndiName = "mongoDb"

  private var mongo: Option[Mongo] = None

  def connectToMongo = {
    val databaseServer = Props.get("databaseServer").openOrThrowException("No database server in properties")
    val databaseName = Props.get("databaseName").openOrThrowException("No database name defined")
    mongo = Some(new Mongo(new ServerAddress(databaseServer)))

    (Props.get("username"), Props.get("password")) match {
      case (Full(username), Full(password)) =>
        MongoDB.defineDbAuth(SessionsMongoIdentifier,
          mongo.get,
          databaseName,
          username,
          password)
      case _ =>
        MongoDB.defineDb(SessionsMongoIdentifier,
          mongo.get,
          databaseName)
    }
  }

  def disconnectFromMongo = {
    mongo.foreach(_.close)
    MongoDB.close
    mongo = None
  }
}
