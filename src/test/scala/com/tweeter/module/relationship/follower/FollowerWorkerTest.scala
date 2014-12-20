package com.tweeter.module.relationship.follower

import akka.actor.{Actor, ActorSystem}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import com.tweeter.lib.cache.Cache
import com.tweeter.lib.tests.{AkkaSpec}
import com.tweeter.module.Envelope
import com.tweeter.module.relationship.User
import com.typesafe.config.ConfigFactory

/**
 * Created by Carlos on 12/20/2014.
 */
class FollowerWorkerTest(_system:ActorSystem) extends AkkaSpec(_system)
{
  def this() = this(ActorSystem("FollowerWorkerTestSystem", ConfigFactory.load("test").withFallback(ConfigFactory.load("application"))))

  def fixture = new
  {
    val probe = TestProbe()
    val handler = TestProbe()
    val client = TestProbe()
  }

  override def afterAll { TestKit.shutdownActorSystem(system) }

  "A FollowerWorker" should "return an empty list when  a new FollowerWorker is sent a getFollowers message" in
    {
      val f = fixture
      //val m = mock[Cache[Int,CachedObject]]
      val worker = TestActorRef(new FollowerWorker(new Cache[Int, User]()))
      worker ! Envelope(GetFollowers(User(10)), null, f.handler.ref)
      f.handler.expectMsg(Envelope(Followers(User(10), List[User]()), null, f.handler.ref))
    }
}
