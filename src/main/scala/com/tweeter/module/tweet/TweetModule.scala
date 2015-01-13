package com.tweeter.module.tweet

import akka.actor.{ActorRef, ActorRefFactory}
import com.tweeter.module.tweet.hometimeline.HomeTimeline
import com.tweeter.module.tweet.mentionstimeline.MentionsTimeline
import com.tweeter.module.tweet.retweets.Retweets
import com.tweeter.module.tweet.tweets.Tweets
import com.tweeter.module.tweet.usertimeline.{UserTimeline}
import com.tweeter.module.{Envelope, Message, ModuleActor, Module}
import com.typesafe.config.{ConfigFactory, Config}

/**
 * The TweetModule Module will provide the tweeting feature to the Tweeter application. This includes tweets, retweets,
 * hashtags, etc. This Module loads the following Modules:
 * Tweets
 * Retweets
 * HomeTimeline
 * UserTimeline
 * MentionsTimeline
 * Created by Carlos on 12/18/2014.
 */
object TweetModule extends Module
{
  /**
   * Returns the list of modules that this Module loads by default
   * @return  The list of modules that this Module loads by default
   */
  override protected def defaultModules(): List[Module] = List[Module](Tweets, Retweets, HomeTimeline, UserTimeline, MentionsTimeline)

  /**
   * Returns the class of the ModuleActor used by this Module
   * @return   The class of TweetModule
   */
  override protected def getModule(): Class[_ <: ModuleActor] = classOf[TweetModule]

  /**
   * Returns the routing information used by Spray for this Module.
   */
  override def getRoute: Unit = ???

  /**
   * Returns the config for this Module
   * @return  The config for this Module
   */
  override protected def config(): Config = ConfigFactory.load("tweet")

  /**
   * Returns a List[String] of the roles this Module handles in a cluster.
   * @return   a List[String] of the roles this Module handles in a cluster.
   */
  override def roles(): List[String] = List[String]("tweet")

  /**
   * Returns the name that this Module is expected to run under when an actor is created. This should be used by
   * the Module's creator to know the path that a particular Actor will have when it is created.
   * @return "tweet"
   */
  override def name(): String = "tweet"

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
      case x:TweetMessage => classOf[TweetMessage].getCanonicalName
      case x => ""
    }
  }
}

/**
 * TweetModule will take in a TweetMessage and forward it to the Tweets Module. It will also forward any queries directed at
 * any of the other Modules to said Modules.
 * @param modules The Modules loaded by this ModuleActor
 */
class TweetModule(modules: List[Module] = List[Module]()) extends ModuleActor(modules)
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
