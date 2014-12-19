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
}
