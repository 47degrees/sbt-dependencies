import de.heikoseeberger.sbtheader.license.Apache2_0
import catext.Dependencies._

val dev  = Seq(Dev("47 Degrees (twitter: @47deg)", "47 Degrees"))
val gh   = GitHubSettings("com.fortysevendeg", "github4s", "47 Degrees", apache)
val vAll = Versions(versions, libraries, scalacPlugins)

lazy val artifactSettings = Seq(
  name := "sbt-dependencies",
  organization := "com.fortysevendeg",
  organizationName := "47 Degrees",
  homepage := Option(url("http://www.47deg.com")),
  organizationHomepage := Some(new URL("http://47deg.com"))
)

lazy val pluginSettings = Seq(
    sbtPlugin := true,
    scalaVersion in ThisBuild := "2.10.6",
    resolvers ++= Seq(
      Resolver.sonatypeRepo("releases"),
      "jgit-repo" at "http://download.eclipse.org/jgit/maven"
    ),
    libraryDependencies ++= Seq(
      "com.lihaoyi"   %% "scalatags" % "0.6.0",
      "org.scalactic" %% "scalactic" % "3.0.0",
      "org.scalatest"  %% "scalatest"  % versions("scalatest")  % "test",
      "org.scalacheck" %% "scalacheck" % versions("scalacheck") % "test"
    ),
    scalafmtConfig in ThisBuild := Some(file(".scalafmt"))
  ) ++ reformatOnCompileSettings

lazy val `sbt-dependency-updates` = (project in file("."))
  .settings(moduleName := "sbt-dependencies")
  .settings(artifactSettings: _*)
  .settings(pluginSettings: _*)
  .settings(addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.0"))
