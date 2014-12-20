package com.tweeter.module.tweet.tweets

import akka.actor.{ActorRef, ActorRefFactory}
import com.tweeter.module.tweet.{Tweet, TweetMessage}
import com.tweeter.module.{Envelope, Message, ModuleActor, Module}
import com.typesafe.config.{ConfigFactory, Config}

/**
 * The Tweets Module will provide concrete Tweets to the Tweet module. This is the module that will effectively handle
 * every Tweet and then publish them for any other interested Modules.
 * Created by Carlos on 12/18/2014.
 */
object Tweets extends Module
{
  /**
   * Returns the list of modules that this Module loads by default
   * @return  The list of modules that this Module loads by default
   */
  override protected def defaultModules(): List[Module] = List[Module]()

  /**
   * Returns the class of the ModuleActor used by this Module
   * @return   The class of Tweets
   */
  override protected def getModule(): Class[_ <: ModuleActor] = classOf[Tweets]

  /**
   * Returns the routing information used by Spray for this Module.
   */
  override def getRoute: Unit = ???

  /**
   * Returns the config for this Module
   * @return  The config for "tweets.conf"
   */
  override protected def config(): Config = ConfigFactory.load("tweets")

  /**
   * Returns a List[String] of the roles this Module handles in a cluster.
   * @return   a List[String] of the roles this Module handles in a cluster.
   */
  override def roles(): List[String] = List[String]("tweets")

  /**
   * Returns the name that this Module is expected to run under when an actor is created. This should be used by
   * the Module's creator to know the path that a particular Actor will have when it is created.
   * @return "tweets"
   */
  override def name(): String = "tweets"

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
      case x:TweetsMessage => classOf[TweetsMessage].getCanonicalName
      case x:TweetMessage => Tweet.getTopic(x)
      case x => ""
    }
  }
}

/**
 * Tweets will be forwarded any TweetMessage obtained by the Tweet Module. The only Module this ModuleActor will
 * potentially forward tweets to is the Retweets Module. Any other Modules will need to subscribe to the TweetMessage
 * Message in order to obtain the Tweets processed by this Module. This ModuleActor or sub-Modules will take in a Tweet,
 * assign it a tweet id, and then store it in a database/cache of tweets.
 * @param modules The modules loaded by this Module
 */
class Tweets(modules: List[Module] = List[Module]()) extends ModuleActor(modules)
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
      case x => this.unknownMessage(x)
    }
  }
}
