package com.tweeter.lib

import scala.collection.concurrent.{TrieMap, Map}
import scala.collection.immutable.{List}

/**
 * The Cache object will simply act as a Factory for the Cache class. It will provide the Seq used by the Cache class
 */
object Cache
{
  /**
   * Returns a List with the provided element
   * @param element The element being created in the sequence
   * @tparam T      The type of element
   * @return        A List with the provided element
   */
  def getSeq[T <: CachedObject](element:T):Seq[T] = List[T](element)

  /**
   * Returns a List of type T
   * @tparam T  The type of the List
   * @return    A List of type T
   */
  def getSeq[T <: CachedObject]():Seq[T] = List[T]()
}

/**
 * Cache Maps a key K to a Seq of objects of type V and provides thread-safe reads and writes. Cache has a one writer
 * all reader policy for accessing the value mapped to by a key. In order to achieve thread-safety, all writes must
 * utilize the compareAndSet method to perform any write operations on this datastructure. The newest elements are added
 * to the front of the Seq mapped to by a key.
 *
 * Cache uses List as the Seq. While the use of a List and putting newest elements to the front of the List makes writes
 * slow, this also makes reads fast. Cache focuses on fast reads and slow writes.
 *
 * Note: Any CachedObject mapped to a value k is expected to have a unique id for the given mapping.
 * Created by Carlos on 12/18/2014.
 */
class Cache[K,V <: CachedObject](cacheSize:Int = 100)
{
  /**
   * delegate will be the delegate used to perform the operations.
   */
  private val delegate:Map[K,Seq[V]] = new TrieMap[K, Seq[V]]()

  /**
   * Removes an element from the sequence mapped to by key with and id matching id
   * @param key The mapping to the sequence for which an object with id is being removed
   * @param id  The id of the CachedObject being removed from the sequence mapped to by key
   * @return    True if the operation performs a remove or False if the operation does not perform a remove.
   */
  def remove(key: K, id:Int): Boolean =
  {
    var found = true
    var removed = false
    val seq = delegate.get(key)

    while(found && !removed)
    {
      if(seq != None)
      {
        found = seq.get.exists(_.id == id)
        if(found)
        {
          val newSeq = seq.get.filter(_.id == id)
          removed = compareAndSet(key, seq.get, newSeq)
        }
      }
    }
    return found
  }

  /**
   * Adds element to the front of the Sequence mapped to at key. It also removes the element at the back of the
   * sequence if its size surpasses cacheSize in order to maintain the size of the cache.
   * @param key     The key of the element being added
   * @param element The object being added to the Seq at key
   * @return        A reference to this object
   */
  def += (key:K, element:V): Cache[K, V] =
  {
    val oldSeq= delegate.get(key)
    if(oldSeq != None)
    {
      var newSeq = element +: oldSeq.get
      if(newSeq.size > cacheSize) newSeq = newSeq.dropRight(1)
      compareAndSet(key, oldSeq.get, newSeq)
    }
    else compareAndSet(key, null, Cache.getSeq[V](element))
    return this;
  }

  /**
   * Returns the sequence mapped at the provided key
   * @param key The key at which the sought after sequence is mapped at
   * @return    The sequence mapped at the provided key.
   */
  def get(key: K): Option[Seq[V]] = delegate.get(key)

  /**
   * Returns an iterator over all the keys
   * @return  An iterator over all the keys
   */
  def iterator: Iterator[(K, Seq[V])] = delegate.iterator

  /**
   * A locked compare and set operation on the sequence that is mapped to by k. Every write operation on Cache should
   * use this compareAndSet method in order to enforce thread-safety. While we may not know if oldSeq is the current
   * sequence mapped to on the delegate with key k, we do know that in order for it to be replaced, it must be the
   * current sequence mapped to in the delegate by k. Hence, if oldKey is the currentSeq, we will have no write
   * contentions in the delegate because we are locked on the currentSeq. If we were locked on an older sequence, this
   * method would never write to the delegate and hence we have achieved write-safety. Furthermore, because the delegate
   * maps to an immutable structure, we have gained read-safety for free.
   *
   * Note: If the current sequence mapped to by k is null, null should be passed in for oldSeq
   * @param key      The key of the sequence that is being replaced
   * @param oldSeq   The sequence being replaced
   * @param newSeq   The sequence that is replacing oldSeq
   * @return         False if compareAndSet failed and true if compareAndSet succeeded.
   */
  private final def compareAndSet(key:K, oldSeq:Seq[V], newSeq:Seq[V]):Boolean =
  {
    var replaced = false
    oldSeq.synchronized
    {
      val currentSeq = delegate.get(key)
      if(currentSeq != None || (currentSeq == None && oldSeq == null))
      {
        if(currentSeq == oldSeq)
        {
          delegate.replace(key, newSeq)
          replaced = true
        }
      }
    }
    return replaced
  }
}