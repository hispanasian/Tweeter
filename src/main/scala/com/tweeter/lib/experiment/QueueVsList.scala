package com.tweeter.lib.experiment

import com.tweeter.module.relationship.follower.{AddFollower, FollowerMessage}

import scala.collection.concurrent.TrieMap
import scala.collection.immutable.Queue

/**
 * Created by Carlos on 12/18/2014.
 */
object QueueVsList
{
  def main(args:Array[String]):Unit =
  {
    var q = Queue[Int](1)
    var l = List[Int](1)

    // Append 2 to both
    q = q :+ 2
    l = l :+ 2
    println(s"queue: $q")
    println(s"list: $l")

    println(s"queue: element at pos 0: %s".format(q(0)))
    println(s"list: element at pos 0: %s".format(l(0)))

    q = q :+ 2 :+ 5
    l = l :+ 2 :+ 5

    println(s"queue: length: %s with elements %s".format(q.length, q))
    println(s"list: length: %s with elements %s".format(l.length, l))

    val map = new TrieMap[Int, String]()
    println(s"map has size %s".format(map.size))
    map.replace(5, "a")
    println(s"map has size %s".format(map.size))
    map += (5 ->"a")
    println(s"map has size %s".format(map.size))
    map += (5 -> "b")
    println(s"map has size %s".format(map.size))
    println("map has element %s".format(map.get(5)))

    println(classOf[FollowerMessage].getCanonicalName())
    println(classOf[AddFollower])
  }

}
