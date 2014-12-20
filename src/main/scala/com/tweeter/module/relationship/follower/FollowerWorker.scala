package com.tweeter.module.relationship.follower

import akka.actor.ActorRef
import com.tweeter.lib.cache.Cache
import com.tweeter.module.relationship.User
import com.tweeter.module.{Envelope, Message, ClusteredActor}

/**
 * FollowerWorker will maintain a Cache that is shared between all the FollowerWorkers on the ActorSystem. It is capable
 * of returning the followers of a user as well as adding and removing followers for a given user. Follower maps the
 * user uid to some sequence of type Int where the sequence contains the uids of the followers.
 * Created by Carlos on 12/19/2014.
 */
class FollowerWorker(cache:Cache[Int, User]) extends ClusteredActor
{
  /**
   * Processes mssg and sends the response to handler. The final response should be sent back to client.
   * @param mssg    The mssg that is being processed
   * @param client  The originator of the request to whom the final response should be sent
   * @param handler The Actor who should handle the response for mssg
   */
  override def process(mssg: Message, client: ActorRef, handler: ActorRef): Unit =
  {
    mssg match
    {
      case GetFollowers(user) =>
      {
        val option = cache.get(user.id)
        var followers:Seq[User] = null
        if(option != null) followers = option.get else followers = Cache.getSeq[User]()
        handler ! Envelope(Followers(user, followers), client, handler)
      }
      case AddFollower(user, follower) =>
      {
        cache += (user.id -> follower)
        // TODO: Respond to handler or client
      }
      case RemoveFollower(user, follower) =>
      {
        cache.remove(user.id, follower.id)
        // TODO: Respond to handler or client
      }
      case x => unknownMessage(x)
    }
  }
}