package com.tweeter.module.relationship.friend

import akka.actor.{Props, ActorRef, ActorRefFactory}
import akka.routing.FromConfig
import com.tweeter.lib.cache.Cache
import com.tweeter.module.relationship.{User, Relationship, RelationshipMessage}
import com.tweeter.module.{Envelope, Message, Module, ModuleActor}
import com.typesafe.config.{Config, ConfigFactory}

/**
 * The Friend Module will provide functionality to keep track of the friendships between users. Friendships are a two-
 * way relationship and require both users to concede to the friendship.
 * Created by Carlos on 12/18/2014.
 */
object Friend extends Module
{
  /**
   * Returns the list of modules that this Module loads by default
   * @return  The list of modules that this Module loads by default
   */
  override protected def defaultModules(): List[Module] = List[Module]()

  /**
   * Returns the class of the ModuleActor used by this Module
   * @return   The class of Friend
   */
  override protected def getModule(): Class[_ <: ModuleActor] = classOf[Friend]

  /**
   * Returns the routing information used by Spray for this Module.
   */
  override def getRoute: Unit = ???

  /**
   * Returns the config for this Module
   * @return  The config for this Module
   */
  override protected def config(): Config = ConfigFactory.load("friend")

  /**
   * Returns a List[String] of the roles this Module handles in a cluster.
   * @return   a List[String] of the roles this Module handles in a cluster.
   */
  override def roles(): List[String] = List[String]("friend")

  /**
   * Returns the name that this Module is expected to run under when an actor is created. This should be used by
   * the Module's creator to know the path that a particular Actor will have when it is created.
   * @return "friend"
   */
  override def name(): String = "friend"

  /**
   * Returns an ActorRef that knows how to send a Message using REST.
   * @param context The ActorContext in which the router is created
   * @return        An ActorRef that knows how to send a Message using REST.
   */
  override protected def getRESTRouter(context: ActorRefFactory): ActorRef = ???

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
      case x:FriendMessage => classOf[FriendMessage].getCanonicalName
      case x:RelationshipMessage => Relationship.getTopic(x)
      case _ => ""
    }
  }
}

/**
 * The Friend ModuleActor will handle any Friend requests. It will essentially create a mapping between two friends and
 * support queries related to friendships.
 * @param modules
 */
class Friend(modules: List[Module] = List[Module]()) extends ModuleActor(modules)
{
  val cache = new Cache[Int,User]()
  val workers = context.actorOf(FromConfig.props(Props(classOf[FriendWorker], cache)), name = "worker")

  /**
   * Processes envelope and sends the response to handler. The final response should be sent back to client. If the
   * receiving Actor does not know who the new handler should be when sending a response to handler, set the new
   * handler to the current handler.
   * @param envelope  The envelope that needs to be processed
   */
  override def process(envelope:Envelope):Unit =
  {
    envelope.mssg match
    {
      case x:FriendMessage => workers ! x
      case x => unknownMessage(x)
    }
  }
}
