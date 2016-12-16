addSbtPlugin("com.fortysevendeg" % "sbt-catalysts-extras" % "0.1.2")
addSbtPlugin("com.geirsson"      % "sbt-scalafmt"         % "0.4.7")
libraryDependencies <+= sbtVersion("org.scala-sbt" % "scripted-plugin" % _)
