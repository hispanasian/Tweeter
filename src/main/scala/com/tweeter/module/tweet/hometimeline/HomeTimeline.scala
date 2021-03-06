package com.tweeter.module.tweet.hometimeline

import akka.actor.{ActorRef, ActorRefFactory}
import com.tweeter.module.tweet.{TweetModule, TweetMessage}
import com.tweeter.module.{Envelope, Message, ModuleActor, Module}
import com.typesafe.config.{ConfigFactory, Config}

/**
 * The HomeTimeline Module will get a TweetModule from a users Followers and update that users home timeline with the new
 * tweet. The HomeTimeline Module will get the TweetModule via it's subscription to the mediator
 * Created by Carlos on 12/18/2014.
 */
object HomeTimeline extends Module
{
  /**
   * Returns the list of modules that this Module loads by default
   * @return  The list of modules that this Module loads by default
   */
  override protected def defaultModules(): List[Module] = List[Module]()

  /**
   * Returns the class of the ModuleActor used by this Module
   * @return   The class of HomeTimeline
   */
  override protected def getModule(): Class[_ <: ModuleActor] = classOf[HomeTimeline]

  /**
   * Returns the routing information used by Spray for this Module.
   */
  override def getRoute: Unit = ???

  /**
   * Returns the config for this Module
   * @return  The config for "hometimeline"
   */
  override protected def config(): Config = ConfigFactory.load("hometimeline")

  /**
   * Returns a List[String] of the roles this Module handles in a cluster.
   * @return   a List[String] of the roles this Module handles in a cluster.
   */
  override def roles(): List[String] = List[String]()

  /**
   * Returns the name that this Module is expected to run under when an actor is created. This should be used by
   * the Module's creator to know the path that a particular Actor will have when it is created.
   * @return "hometimeline"
   */
  override def name(): String = "hometimeline"

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
      case x:HomeTimelineMessage => classOf[HomeTimelineMessage].getCanonicalName
      case x:TweetMessage => TweetModule.getTopic(x)
      case x => ""
    }
  }
}

/**
 * This ModuleActor will provide support for the HomeTimeline Module. The actual work will be done by this ModuleActor
 * and it's supporting actors. It will obtain the tweets via a subscription to the mediator. HomeTimeline will keep
 * track of the tweet ids associated with any tweets owned by a user that the User is following and not privately
 * tweeted.
 * @param modules
 */
class HomeTimeline(modules: List[Module] = List[Module]()) extends ModuleActor(modules)
{
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
      case x => unknownMessage(x)
    }
  }
}
