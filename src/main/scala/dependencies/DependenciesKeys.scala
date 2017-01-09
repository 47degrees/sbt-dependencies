package dependencies

import sbt._

trait DependenciesKeys {

  val showDependencyUpdates = taskKey[Unit]("Shows the dependency updates")
  val updateDependencyIssues =
    taskKey[Unit]("Creates and updates issues for all dependency updates")

  val githubOwner  = settingKey[String]("GitHub owner")
  val githubRepo   = settingKey[String]("GitHub repo")
  val githubToken  = settingKey[String]("GitHub token with repo scope")

}
