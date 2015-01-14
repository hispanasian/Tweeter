package com.tweeter.module.tweet.tweets

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.tweeter.lib.tests.AkkaSpec
import com.typesafe.config.ConfigFactory

/**
 * TweetsWorkerTest will Unit test the TweetsWorker for correct behavior
 * Created by Carlos on 1/13/2015.
 */
class TweetsWorkerTest(_system:ActorSystem) extends AkkaSpec(_system)
{
  def this() = this(ActorSystem("TweetsWorkerTestSystem", ConfigFactory.parseString("""akka.remote.netty.tcp.port="3000"""").withFallback(ConfigFactory.load("test").withFallback(ConfigFactory.load("application")))))

  override def afterAll { TestKit.shutdownActorSystem(system); system.shutdown() }

  def fixture = new
    {
      val probe = TestProbe()
      val handler = TestProbe()
      val client = TestProbe()
      val cache = mock[Map[Int,HydratedTweet]]
    }

  "A TweetsWorker" should "queue a Tweet to be stored/cached if it has not received a GUIDBlock (it was just initialized)" in
    {

    }
  it should "queue a Tweet to be stored/cached if it has consumed a GUIDBlock" in
    {

    }
  it should "dequeue and store/cache any queued Tweets when it receives a valid GUIDBlock" in
    {

    }
  it should "store/cache a Tweet if it has an available GUIDBlock" in
    {

    }
  it should "request a new GUIDBlock when it is half way through it's current GUIDBlock" in
    {

    }
}
