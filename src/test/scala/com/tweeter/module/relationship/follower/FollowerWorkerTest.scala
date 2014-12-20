package com.tweeter.module.relationship.follower

import akka.actor.{ActorSystem}
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
    val cache = mock[Cache[Int,User]]
  }

  override def afterAll { TestKit.shutdownActorSystem(system) }

  "A FollowerWorker" should "return an empty list when a new FollowerWorker is sent a getFollowers message" in
    {
      val f = fixture
      (f.cache.get _) expects(10) returning(None)
      val worker = TestActorRef(new FollowerWorker(f.cache))
      worker ! Envelope(GetFollowers(User(10)), null, f.handler.ref)
      f.handler.expectMsg(Envelope(Followers(User(10), List[User]()), null, f.handler.ref))
    }
  it should "add User(10) as a follower of User(5) when it is sent AddFollower(User(5),User(10))" in
    {
      val f = fixture
      (f.cache.+= _) expects(5,User(10))
      val worker = TestActorRef(new FollowerWorker(f.cache))
      worker ! Envelope(AddFollower(User(5),User(10)), null, f.handler.ref)
    }
  it should "remove User(10) as a follower of User(5) when it is sent RemoveFollower(User(5),User(10))" in
    {
      val f = fixture
      (f.cache.remove _) expects(5,10)
      val worker = TestActorRef(new FollowerWorker(f.cache))
      worker ! Envelope(RemoveFollower(User(5),User(10)), null, f.handler.ref)
    }
}
