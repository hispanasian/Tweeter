package com.tweeter.lib.guidgenerator

/**
 * The trait by which legal GUIDGenerator messages will be derived from for communication
 * Created by Carlos on 1/12/2015.
 */
trait GUIDMessage

/**
 * Requests a block of GUIDs from the GUIDGenerator
 */
case class GetGUIDBlock() extends GUIDMessage

/**
 * A message containing the start of a given GUID and the number of incremental GUIDs that are valid from this block
 * @param start       The beginning of a valid GUID
 * @param blockSize   The number of valid incremental GUIDs that can be obtained with start(including start)
 */
case class GUIDBlock(start:Int, blockSize:Int) extends GUIDMessage
