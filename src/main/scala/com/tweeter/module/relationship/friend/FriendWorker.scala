package com.tweeter.module.relationship.friend

import com.tweeter.lib.cache.Cache
import com.tweeter.module.{Envelope, ClusteredActor}
import com.tweeter.module.relationship.User

/**
 * FriendWorker will maintain a Cache that is shared between all the FriendWorkers on the ActorSystem. It is capable
 * of returning the friends of a user as well as adding and removing friends for a given user. FriendWorker maps the
 * user uid to some sequence of type Int where the sequence contains the uids of the friends.
 * Created by Carlos on 1/11/2015.
 */
class FriendWorker(cache:Cache[Int, User]) extends ClusteredActor
{
  override def process(e:Envelope):Unit =
  {
    e.mssg match
    {
      case GetFriends(user) =>
      {
        val option = cache.get(user.id)
        var friends:Seq[User] = Seq[User]()
        if(option != None) friends = option.get
        e.handler ! Envelope(e.replyMessage(Friends(user, friends)), e.client, e.handler)
      }
      case AddFriend(user, friend) =>
      {
        cache += (user.id -> friend)
        // TODO: Respond to handler or client
      }
      case RemoveFriend(user, friend) =>
      {
        cache.remove(user.id, friend.id)
        // TODO: Respond to handler or client
      }
      case x => unknownMessage()
    }
  }
}
