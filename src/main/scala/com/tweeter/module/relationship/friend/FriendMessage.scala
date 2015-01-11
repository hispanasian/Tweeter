package com.tweeter.module.relationship.friend

import com.tweeter.module.relationship.{User, RelationshipMessage}

/**
 * FriendMessage will be handled by the Friend Module and any Message intended for the Friend Module should extend the
 * FriendMessage
 * Created by Carlos on 12/18/2014.
 */
trait FriendMessage extends RelationshipMessage

/**
 * Message requesting the friends for the user with the given uid
 * @param uid The user who's friends are being queried.
 */
case class GetFriend(uid:User) extends FriendMessage

/**
 * Message requesting the addition of friend as a friend of user
 * @param user    The user who is gaining a friend
 * @param friend  The friend who is becoming a friend of user
 */
case class AddFriend(user:User, friend:User) extends FriendMessage

/**
 * Message requesting the removal of friend from the set of friends belonging to user.
 * @param user    The user who is losing a friend
 * @param friend  The friend who is being removed from user
 */
case class RemoveFriend(user:User, friend:Friend) extends FriendMessage

/**
 * Message containing the sequence of friends whoa re following user
 * @param user    The user whose friends are being queried
 * @param friends The friends of user
 */
case class Friends(user:User, friends:Seq[User]) extends FriendMessage