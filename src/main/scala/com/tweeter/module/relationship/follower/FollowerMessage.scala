package com.tweeter.module.relationship.follower

import com.tweeter.module.relationship.RelationshipMessage

/**
 * FollowerMessage will be handled by the Follower Module and any Message intended for the Follower Module should
 * extend the FollowerMessage
 * Created by Carlos on 12/18/2014.
 */
trait FollowerMessage extends RelationshipMessage
