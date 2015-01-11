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
      case GetFriend(user)
      case x => unknownMessage()
    }
  }
}
