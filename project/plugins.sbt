addSbtPlugin("com.47deg" % "sbt-org-policies" % "0.2.0")
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "0.4.10")
libraryDependencies <+= sbtVersion("org.scala-sbt" % "scripted-plugin" % _)
