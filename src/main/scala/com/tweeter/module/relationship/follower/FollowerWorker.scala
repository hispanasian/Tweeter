package com.tweeter.module.relationship.follower

import com.tweeter.lib.cache.Cache
import com.tweeter.module.relationship.User
import com.tweeter.module.{Envelope, ClusteredActor}

/**
 * FollowerWorker will maintain a Cache that is shared between all the FollowerWorkers on the ActorSystem. It is capable
 * of returning the followers of a user as well as adding and removing followers for a given user. Follower maps the
 * user uid to some sequence of type Int where the sequence contains the uids of the followers.
 * Created by Carlos on 12/19/2014.
 */
class FollowerWorker(cache:Cache[Int, User]) extends ClusteredActor
{
  /**
   * Processes envelope and sends the response to handler. The final response should be sent back to client. If the
   * receiving Actor does not know who the new handler should be when sending a response to handler, set the new
   * handler to the current handler.
   * @param e  The envelope that needs to be processed
   */
  override def process(e:Envelope):Unit =
  {
    e.mssg match
    {
      case GetFollowers(user) =>
      {
        val option = cache.get(user.id)
        var followers:Seq[User] = null
        if(option != None) followers = option.get else followers = Cache.getSeq[User]()
        e.handler ! Envelope(e.replyMessage(Followers(user, followers)), e.client, e.handler)
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
