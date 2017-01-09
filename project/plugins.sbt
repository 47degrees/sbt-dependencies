addSbtPlugin("com.fortysevendeg" % "sbt-catalysts-extras" % "0.1.2")
addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "0.4.10")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")
libraryDependencies <+= sbtVersion("org.scala-sbt" % "scripted-plugin" % _)
