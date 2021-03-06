package com.tweeter.module.tweet.mentionstimeline

import akka.actor.{ActorRef, ActorRefFactory}
import com.tweeter.module.tweet.{TweetModule, TweetMessage}
import com.tweeter.module.{Envelope, Message, ModuleActor, Module}
import com.typesafe.config.{ConfigFactory, Config}

/**
 * The MentionsTimeline Module will provide the functionality to Tweeter that allows a user to have a Mentions Timeline
 * (ie, a Timeline that contains any tweet in which the said user is mentioned).
 * Created by Carlos on 12/18/2014.
 */
object MentionsTimeline extends Module
{
  /**
   * Returns the list of modules that this Module loads by default
   * @return  The list of modules that this Module loads by default
   */
  override protected def defaultModules(): List[Module] = List[Module]()

  /**
   * Returns the class of the ModuleActor used by this Module
   * @return   The class of MentionsTimeline
   */
  override protected def getModule(): Class[_ <: ModuleActor] = classOf[MentionsTimeline]

  /**
   * Returns the routing information used by Spray for this Module.
   */
  override def getRoute: Unit = ???

  /**
   * Returns the config for this Module
   * @return  The config for "mentionstimeline.conf"
   */
  override protected def config(): Config = ConfigFactory.load("mentionstimeline")

  /**
   * Returns a List[String] of the roles this Module handles in a cluster.
   * @return   a List[String] of the roles this Module handles in a cluster.
   */
  override def roles(): List[String] = List[String]()

  /**
   * Returns the name that this Module is expected to run under when an actor is created. This should be used by
   * the Module's creator to know the path that a particular Actor will have when it is created.
   * @return "mentionstimeline"
   */
  override def name(): String = "mentionstimeline"

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
      case x:MentionsTimelineMessage => classOf[MentionsTimelineMessage].getCanonicalName
      case x:TweetMessage => TweetModule.getTopic(x)
      case x => ""
    }
  }
}

/**
 * This ModuleActor will provide support for the MentionsTimeline Module. The actual work will be done by this
 * ModuleActor and it's supporting actors. TweetMessage will be received via a subscription to mediator.
 * MentionsTimeline will keep track of the tweet ids associated to the tweets that contains mentions of the particular
 * user.
 * @param modules
 */
class MentionsTimeline(modules: List[Module] = List[Module]()) extends ModuleActor(modules)
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
