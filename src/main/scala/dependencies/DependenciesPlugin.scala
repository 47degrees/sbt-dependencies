package dependencies

import com.timushev.sbt.updates.UpdatesPlugin.autoImport._
import com.timushev.sbt.updates.versions.Version
import sbt.Keys._
import sbt._

import scala.collection.immutable.SortedSet

object DependenciesPlugin extends AutoPlugin {

  object autoImport extends DependenciesKeys

  lazy val defaultSettings = Seq(
    dependencyUpdatesExclusions := moduleFilter(organization = "org.scala-lang"),
    compile <<= (compile in Compile) dependsOn dependencyUpdates
  )

  override val projectSettings = defaultSettings

}
