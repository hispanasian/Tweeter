package com.tweeter.module.tweet.tweets

import akka.actor.ActorRef
import com.tweeter.module.{Envelope, ClusteredActor}

/**
 * TweetsWorker receives HydratedTweets and assigns a unique guid to the HydratedTweet which it then stores in a
 * cache (a Map). The unique guid is an int that is used to map to the HydratedTweet in the map. Once the Tweet is
 * stored, TweetsWorker advertises the new Tweet to any listening subscribers. TweetsWorker requires a reference to the
 * GUIDGenerator that will be supplying the unique GUIDBlock that will be used to designate a new guid for each
 * HydratedTweet.
 *
 * cache should be a thread-safe Map if multiple TweetsWorker Actors are used.
 * Created by Carlos on 1/13/2015.
 *
 * @param cache         The thread-safe Map that will map the tids to the HydratedTweet objects
 * @param guidGenerator The ActorRef to the unique GUIDGenerator in the cluster
 */
class TweetsWorker(cache:Map[Int,HydratedTweet], guidGenerator:ActorRef) extends ClusteredActor
{
  override def receive =
  {
    case x:Envelope => process(x)
    case x => unknownMessage(x)
  }

  /**
   * Processes envelope and sends the response to handler. The final response should be sent back to client. If the
   * receiving Actor does not know who the new handler should be when sending a response to handler, set the new
   * handler to the current handler.
   * @param envelope  The envelope that needs to be processed
   */
  override def process(envelope: Envelope): Unit =
  {
    envelope.mssg match
    {
      case HydratedTweet(tid, body, author, primaryMention, mentions, retweet, hashtags) =>
      {

      }
      case x => unknownMessage(x)
    }
  }
}
