package com.tweeter.module

import akka.actor.{ExtendedActorSystem, ExtensionIdProvider, ExtensionId, Extension}
import com.typesafe.config.Config

/**
 * The Settings class will define the settings that will be used by the Tweeter application so they can be obtained
 * via Config. The supported fields are as follows:
 * tweeter.cluster-name="SomeName"
 * tweeter.address="IPAddress:Port"
 * Created by Carlos on 12/16/2014.
 */
class Settings(config:Config) extends Extension
{
  val clusterName:String = config.getString("tweeter.cluster-name")
  val address:String = config.getString("tweeter.address")
}

object Settings extends ExtensionId[Settings] with ExtensionIdProvider
{
  override def createExtension(system: ExtendedActorSystem): Settings = new Settings(system.settings.config)

  override def lookup(): ExtensionId[_ <: Extension] = Settings
}
