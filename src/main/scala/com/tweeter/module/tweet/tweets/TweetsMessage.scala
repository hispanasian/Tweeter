package com.tweeter.module.tweet.tweets

import com.tweeter.module.relationship.User
import com.tweeter.module.tweet.TweetMessage

/**
 * TweetsMessage will be handled by the Tweets Module and any Message intended for the Tweets Module should extend the
 * TweetsMessage
 * Created by Carlos on 12/18/2014.
 */
trait TweetsMessage extends TweetMessage

/**
 * A TweetModule is simply a unique id called tid that maps to a HydratedTweet which contains the contents of the tweet
 */
case class Tweet(tid:Int) extends TweetsMessage

/**
 * A HydratedTweet is composed of a tid, string body, author, primaryMention, mention, retweet, and hashtags
 */
case class HydratedTweet(tid:Int, body:String, author:User, primaryMention:User, mentions:List[User], retweet:Tweet, hashtags:String) extends TweetsMessage

/**
 * Returns the HydratedTweet associated with tid
 * @param tid The unique ID associated with the TweetModule
 */
case class HydrateTweet(tid:Int) extends TweetsMessage

/**
 * A message requesting the insertion of tweet into the Tweets module. This will generate a unique tid for tweet, hence
 * any tid assigned to tweet will be overridden.
 * @param tweet The tweet being stored
 */
case class AddTweet(tweet:HydratedTweet)