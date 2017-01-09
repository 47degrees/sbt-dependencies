import catext.Dependencies._

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
      Resolver.sonatypeRepo("snapshots"),
      "jgit-repo" at "http://download.eclipse.org/jgit/maven"
    ),
    libraryDependencies ++= Seq(
      "com.fortysevendeg" %% "github4s"   % "0.10.0",
      "org.scalatest"     %% "scalatest"  % versions("scalatest") % "test",
      "org.scalacheck"    %% "scalacheck" % versions("scalacheck") % "test"
    ),
    scalafmtConfig in ThisBuild := Some(file(".scalafmt"))
  ) ++ reformatOnCompileSettings

lazy val gitUrl = "https://github.com/47deg/sbt-dependencies"

lazy val publishSettings = Seq(
  licenses += ("Apache License", url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  scmInfo := Some(ScmInfo(url(gitUrl), s"scm:git:$gitUrl.git")),
  apiURL := Some(url(gitUrl)),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := Function.const(false),
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("Snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("Releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := <developers>
    <developer>
      <name>47 Degrees (twitter: @47deg)</name>
      <email>hello@47deg.com</email>
    </developer>
    <developer>
      <name>47 Degrees</name>
    </developer>
  </developers>
)

lazy val gpgFolder = sys.env.getOrElse("GPG_FOLDER", ".")

lazy val pgpSettings = Seq(
  pgpPassphrase := Some(sys.env.getOrElse("GPG_PASSPHRASE", "").toCharArray),
  pgpPublicRing := file(s"$gpgFolder/pubring.gpg"),
  pgpSecretRing := file(s"$gpgFolder/secring.gpg")
)

lazy val credentialSettings = Seq(
  credentials ++= (for {
    username <- Option(System.getenv().get("SONATYPE_USERNAME"))
    password <- Option(System.getenv().get("SONATYPE_PASSWORD"))
  } yield
    Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq
)

lazy val `sbt-dependencies` = (project in file("."))
  .settings(moduleName := "sbt-dependencies")
  .settings(artifactSettings: _*)
  .settings(pluginSettings: _*)
  .settings(publishSettings: _*)
  .settings(pgpSettings: _*)
  .settings(credentialSettings: _*)
  .settings(addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.0"))
