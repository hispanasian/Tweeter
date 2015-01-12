package com.tweeter.module.relationship.friend

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestProbe, TestKit}
import com.tweeter.lib.cache.Cache
import com.tweeter.lib.tests.AkkaSpec
import com.tweeter.module.{Envelope, Message}
import com.tweeter.module.relationship.User
import com.typesafe.config.ConfigFactory

/**
 * Tests the FriendWorker class
 * Created by Carlos on 1/11/2015.
 */
class FriendWorkerTest(_system:ActorSystem) extends AkkaSpec(_system)
{
  def this() = this(ActorSystem("FriendWorkerTestSystem", ConfigFactory.load("test").withFallback(ConfigFactory.load("application"))))

  def fixture = new
    {
      val probe = TestProbe()
      val handler = TestProbe()
      val client = TestProbe()
      val cache = mock[Cache[Int,User]]
    }

  override def afterAll { TestKit.shutdownActorSystem(system) }

  "A FriendWorker" should "reply to the handler with Friends(User(10),List[]) when receiving a GetFriends(User(10))" in
    {
      val f = fixture
      (f.cache.get _) expects(10) returning(None)

      val worker = TestActorRef(new FriendWorker(f.cache))
      worker ! Envelope(GetFriends(User(10)), f.client.ref, f.handler.ref)

      val expectedMssg = Friends(User(10), List[User]())
      f.handler.expectMsgPF(hint="Message is an envelope with mssg=expectedMssg, client=client.ref, handler=handler.ref and reply should return a Message")
      {
        case Envelope(mssg, client, handler, reply) =>
          mssg should equal (expectedMssg)
          client should be (f.client.ref)
          handler should be (f.handler.ref)
          reply should be (_:Message)
      }
    }
  it should "map 100 to uid 101 in the cache when it receives a AddFriend(User(101), User(100)) message" in
    {
      val f = fixture
      (f.cache.+= _) expects (101, User(100))
      val worker = TestActorRef(new FriendWorker(f.cache))
      worker ! Envelope(AddFriend(User(101), User(100)), f.client.ref, f.handler.ref)
    }
  it should "map 5 to uid 5 in the cache when it receives a AddFriend(User(5), User(5)) message. This allows for users to friend themselves" in
    {
      val f = fixture
      (f.cache.+= _) expects(5,User(5))
      val worker = TestActorRef(new FriendWorker(f.cache))
      worker ! Envelope(AddFriend(User(5), User(5)), f.client.ref, f.handler.ref)
    }
  it should "remove User(60) from the sequence mapped to User(5) when it receives a RemoveFriend(User(5),User(60)) message" in
    {
      val f = fixture
      (f.cache.remove _) expects(5,60)
      val worker = TestActorRef(new FriendWorker(f.cache))
      worker ! Envelope(RemoveFriend(User(5), User(60)), f.client.ref, f.handler.ref)
    }
}
