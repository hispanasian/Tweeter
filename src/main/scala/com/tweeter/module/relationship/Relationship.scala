package com.tweeter.module.relationship

import akka.actor._
import com.tweeter.module.relationship.follower.Follower
import com.tweeter.module.relationship.friend.Friend
import com.tweeter.module.{ModuleActor, Module, Message}
import com.typesafe.config.{ConfigFactory, Config}

/**
 * The Relationship Module will handle all relationship-related data and requests for the Tweeter application. This
 * means it will handle any friendship or follower related requests. In order to do these things, the Relationship
 * Module makes use of the following Modules:
 * Friendship
 * Follower
 * Created by Carlos on 12/17/2014.
 */
object Relationship extends Module
{
  def main(args:Array[String]):Unit =
  {

  }

  /**
   * Returns the routing information used by Spray for this Module.
   */
  override def getRoute: Unit = ???

  /**
   * Returns the config for this Module
   * @return  The config for this Module
   */
  override protected def config(): Config = ConfigFactory.load("relationship")

  /**
   * Returns a List[String] of the roles this Module handles in a cluster.
   * @return   a List with "relationship"
   */
  override def roles(): List[String] = List[String]("relationship")

  /**
   * Returns the name that this Module is expected to run under when an actor is created. This should be used by
   * the Module's creator to know the path that a particular Actor will have when it is created.
   * @return "relationship"
   */
  override def name(): String = "relationship"

  /**
   * Returns an ActorRef that knows how to send a Message using REST.
   * @param context The ActorContext in which the router is created
   * @return        An ActorRef that knows how to send a Message using REST.
   */
  override protected def getRESTRouter(context: ActorRefFactory): ActorRef = ???

  /**
   * Returns the list of modules that this Module loads by default
   * @return  The list of modules that this Module loads by default
   */
  override protected def defaultModules(): List[Module] = List[Module](Friend, Follower)

  /**
   * Returns an ActorRef that knows how to route a Message to the correct Actor
   * param context The ActorContext in which the router is created
   * @return        an ActorRef that can route a Message using Akka Actors
   */
  override protected def getAkkaRouter(context: ActorRefFactory): ActorRef = ???

  /**
   * Returns the 'topic' for which the message is categorized when being sent by this Module to the
   * DistributedPubSubMediator if it is a Message handled by this Module. If the Message is not handled by this
   * Module, the default behavior is to return an empty string. The suggested topic for a Message is the full classpath.
   * @param message The Message for whose 'topic' is being searched
   * @return        The 'topic' associated with Message
   */
  override def getTopic(message: Message): String =
  {
    message match
    {
      case x:RelationshipMessage => "com.tweeter.module.relationship.RelationshipMessage"
      case _ => ""
    }
  }

  /**
   * Returns the ModuleActor that will be used by this Module
   * @return  the class of Relationship
   */
  override protected def getModule(): Class[_<:ModuleActor] = classOf[Relationship]
}

/**
 * Relationship will act as a Router that will route messages to the Follower Module or the Friend Module.
 * @param modules  The modules used by Relationship
 */
class Relationship(modules: List[Module] = List[Module]()) extends ModuleActor(modules)
{
  override def preStart() =
  {

  }

  override def postStop() =
  {

  }

  override def receive: Receive =
  {
    case x => log.debug(s"$self received unknown message: $x")
  }
}
