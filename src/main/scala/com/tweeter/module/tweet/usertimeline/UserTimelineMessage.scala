package com.tweeter.module.tweet.usertimeline

import com.tweeter.module.tweet.TweetMessage

/**
 * UserTimelineMessage will be handled by the UserTimeline Module and any Message intended for the UserTimeline Module
 * should extend UserTimelineMessage
 * Created by Carlos on 12/18/2014.
 */
trait UserTimelineMessage extends TweetMessage
