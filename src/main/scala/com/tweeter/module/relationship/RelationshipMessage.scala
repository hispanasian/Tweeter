package com.tweeter.module.relationship

import com.tweeter.lib.cache.CachedObject
import com.tweeter.module.Message

/**
 * Any MessAge sent to the Relationship Module or components of Relationship must extend from RelationshipMessage
 * Created by Carlos on 12/17/2014.
 */
trait RelationshipMessage extends Message

/**
 * A User is simply a uid associated with a user. It is bare bones and does not store any other User information.
 * @param uid The unique ID of the user
 */
case class User(uid:Int) extends CachedObject(uid) with RelationshipMessage

/**
 * A HydratedUser contains all the pertinent information related to the user associated with uid
 * @param uid         The unique ID of the user
 * @param followers   The followers of the user with uid
 * @param friends     The friends of the user with uid
 */
case class HydratedUser(uid:Int, followers:Seq[User], friends:Seq[User]) extends CachedObject(uid) with RelationshipMessage