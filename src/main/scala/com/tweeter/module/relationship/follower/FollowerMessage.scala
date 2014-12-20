package com.tweeter.module.relationship.follower

import com.tweeter.module.relationship.{User, RelationshipMessage}

/**
 * FollowerMessage will be handled by the Follower Module and any Message intended for the Follower Module should
 * extend the FollowerMessage
 * Created by Carlos on 12/18/2014.
 */
trait FollowerMessage extends RelationshipMessage

/**
 * Message requesting the followers for the user with the given uid
 * @param uid The user who's follower we are looking for
 */
case class GetFollowers(uid:User) extends FollowerMessage

/**
 * Message requesting the addition of follower as a follower of user
 * @param user      The user who is gaining a follower
 * @param follower  The follower who is becoming a follower of user
 */
case class AddFollower(user:User, follower:User) extends FollowerMessage

/**
 * Message requesting the removal of follower as a follower of user
 * @param user      The user who is losing a follower
 * @param follower  The follower who stopped following user
 */
case class RemoveFollower(user:User, follower:User) extends FollowerMessage

/**
 * Message containing the sequence of followers who are following user
 * @param user      The user being followed
 * @param followers The followers following user
 */
case class Followers(user:User, followers:Seq[User]) extends FollowerMessage