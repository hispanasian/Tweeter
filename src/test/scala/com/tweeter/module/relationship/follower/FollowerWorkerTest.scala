package com.tweeter.module.relationship.follower

import akka.actor.{ActorSystem}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import com.tweeter.lib.cache.Cache
import com.tweeter.lib.tests.{AkkaSpec}
import com.tweeter.module.{Message, Envelope}
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
      val replyMessage = mockFunction[Message, Message]
      (f.cache.get _) expects(10) returning(None)
      replyMessage expects Followers(User(10), List[User]()) returning Followers(User(10), List[User]())
      val worker = TestActorRef(new FollowerWorker(f.cache))
      worker ! Envelope(GetFollowers(User(10)), f.client.ref, f.handler.ref, replyMessage)
      val expectedMssg = Followers(User(10), List[User]())
      f.handler.expectMsgPF(hint="Message is an Envelope with mssg=expectedMssg, client=client.ref, handler=handler.ref and reply should return a Message")
      {
        case Envelope(mssg,client, handler, reply) =>
          mssg should equal (expectedMssg)
          client should be (f.client.ref)
          handler should be (f.handler.ref)
          reply should be (_:Message)
      }
      //f.handler.expectMsg(Envelope(Followers(User(10), List[User]()), null, f.handler.ref)) // this fails on matching replyMessage of Envelope
    }
  it should "add User(10) as a follower of User(5) when it is sent AddFollower(User(5),User(10))" in
    {
      val f = fixture
      (f.cache.+= _) expects(5,User(10))
      val worker = TestActorRef(new FollowerWorker(f.cache))
      worker ! Envelope(AddFollower(User(5),User(10)), f.client.ref, f.handler.ref)
    }
  it should "remove User(10) as a follower of User(5) when it is sent RemoveFollower(User(5),User(10))" in
    {
      val f = fixture
      (f.cache.remove _) expects(5,10)
      val worker = TestActorRef(new FollowerWorker(f.cache))
      worker ! Envelope(RemoveFollower(User(5),User(10)), f.client.ref, f.handler.ref)
    }
}
