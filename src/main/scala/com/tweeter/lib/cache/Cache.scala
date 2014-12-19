package com.tweeter.lib.cache

import com.tweeter.lib.CachedObject

import scala.collection.concurrent.{Map, TrieMap}
import scala.collection.immutable.List

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
class Cache[K,V <: CachedObject](val cacheSize:Int = 100)
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
      else found = false
    }
    return found
  }

  /**
   * Adds element to the front of the Sequence mapped to at key. It also removes the element at the back of the
   * sequence if its size surpasses cacheSize in order to maintain the size of the cache.
   * @param kv      The key value pair
   * @return        A reference to this object
   */
  def += (kv:(K,V)): Cache[K, V] =
  {
    val oldSeq= delegate.get(kv._1)
    if(oldSeq != None)
    {
      var newSeq = kv._2 +: oldSeq.get
      if(newSeq.size > cacheSize) newSeq = newSeq.dropRight(1)
      compareAndSet(kv._1, oldSeq.get, newSeq)
    }
    else compareAndSet(kv._1, null, Cache.getSeq[V](kv._2))
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
   * Returns the number of key,sequence pairs.
   * @return  The number of key,sequence pairs.
   */
  def size():Int = delegate.size

  /**
   * A locked compare and set operation on the sequence that is mapped to by k. Every write operation on Cache should
   * use this compareAndSet method in order to enforce thread-safety. While we may not know if oldSeq is the current
   * sequence mapped to on the delegate with key k, we do know that in order for it to be replaced, it must be the
   * current sequence mapped to in the delegate by k. Hence, if oldKey is the currentSeq, we will have no write
   * contentions in the delegate because we are locked on the currentSeq. If we were locked on an older sequence, this
   * method would never write to the delegate and hence we have achieved write-safety. Furthermore, because the delegate
   * maps to an immutable structure, we have gained read-safety for free.
   *
   * Note: If the current sequence mapped to by k is null, null should be passed in for oldSeq. In this case, this is
   * used to synchronized on the compare and set operation. This should rarely happen (only on the initial write for
   * a key) and for the most part still allows one write multiple read to any sequence assigned to a key except for the
   * special case that no such sequence yet exists. Even in the special case, we maintain thread-safety because the write
   * will still only happen if there is no sequence mapped to the key. Should another compareAndSet start and complete
   * during the middle of this compareAndSet, this operation will fail because even should this operation read the map
   * for the given key at the same time as another operation, both will see a different Seq and hence at least one
   * should fail.
   *
   * The key to the enforcement of thread-safety in this operation is not in that two threads cannot read the map for
   * a particular key at the same time (in fact they can), but that two threads cannot read the same Seq mapped to by
   * key and change it concurrently.
   * @param key      The key of the sequence that is being replaced
   * @param oldSeq   The sequence being replaced
   * @param newSeq   The sequence that is replacing oldSeq
   * @return         False if compareAndSet failed and true if compareAndSet succeeded.
   */
  private final def compareAndSet(key:K, oldSeq:Seq[V], newSeq:Seq[V]):Boolean =
  {
    var replaced = false
    var lock:AnyRef = oldSeq

    /* Use self as lock in the case that no List is mapped to the key.  */
    if(oldSeq == null) lock = this
    lock.synchronized
    {
      val currentSeq = delegate.get(key)
      if(currentSeq != None || (currentSeq == None && oldSeq == null))
      {
        if(currentSeq == None || currentSeq.get == oldSeq)
        {
          delegate += (key -> newSeq)
          replaced = true
        }
      }
    }
    return replaced
  }
}