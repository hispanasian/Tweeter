package com.tweeter.lib.cache

import com.tweeter.lib.CachedObject
import com.tweeter.lib.tests.UnitSpec

/**
 * A Test for the basic operations provided by the Cache class and Cache object. These tests will not test the
 * thread-safety of the class but will test the methods in a serial manner for serial consistency.
 * Created by Carlos on 12/18/2014.
 */
class CacheTest extends UnitSpec
{
  class SomeCachedObject(id:Int) extends CachedObject(id)

  def fixture = new
  {
    val cache = new Cache[Int, SomeCachedObject]()
  }

  "The Cache objects' getSeq method" should "return a List" in
  {
    assert(Cache.getSeq[SomeCachedObject]().getClass == List[SomeCachedObject]().getClass)
    assert(Cache.getSeq[SomeCachedObject](new SomeCachedObject(1)).getClass == List[SomeCachedObject](new SomeCachedObject(1)).getClass)
  }
  it should "return an empty List when no arguments are provided" in
    {
      assert(Cache.getSeq[SomeCachedObject]().size == 0)
    }
  it should "produce NoSuchElementException when head is invoked on a List returned when no arguments are provided" in
    {
      intercept[NoSuchElementException] { Cache.getSeq[SomeCachedObject]().head }
    }

  "Cache" should "default to a cache size of 100" in
  {
    val f = fixture
    assert(f.cache.cacheSize == 100)
  }
  it should "return false when trying to remove an object from an empty Cache" in
    {
      val f = fixture
      assert(f.cache.remove(5,5) == false)
    }
  it should "return an option of a Seq of size one with the only element inserted into the Cache (given the correct key)" in
    {
      val f = fixture
      val element = new SomeCachedObject(5)
      f.cache += (10 -> element)
      val test = f.cache.get(10)
      assert(f.cache.size == 1)
      assert(test != None)
      assert(test.get.size == 1)
      assert(test.get.apply(0) == element)
    }
  it should "iterate through only one element when only one element is inserted into the Cache" in
    {
      val f = fixture
      val element = new SomeCachedObject(150)
      f.cache.+=(10 -> element)
      val iter = f.cache.iterator
      assert(f.cache.size == 1)
      assert(iter.size == 1)
    }
}
