package dependencies

import com.timushev.sbt.updates.UpdatesPlugin.autoImport._
import com.timushev.sbt.updates.versions.Version
import github4s.free.domain.Issue
import sbt.Keys._
import sbt._

import scala.collection.immutable.SortedSet
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object DependenciesPlugin extends AutoPlugin {

  object autoImport extends DependenciesKeys

  import DependenciesPlugin.autoImport._

  def readUpdates(data: Map[ModuleID, SortedSet[Version]], log: Logger)(
      f: (List[DependencyUpdate]) => Unit) =
    Updates.readUpdates(data) match {
      case Nil  => log.info("\nNo dependency updates found\n")
      case list => f(list)
    }

  def createIssueForDep(dep: DependencyUpdate,
                        issues: Map[String, Issue],
                        githubClient: GithubClient,
                        log: Logger): Future[Issue] = {
    log.info(s"Preparing issue for module `${dep.moduleName}`\n")
    issues.get(dep.moduleName) match {
      case Some(issue) =>
        log.info(s"Found existing open issue (#${issue.number}), updating it\n")
        githubClient.updateIssue(issue, dep) map { issue =>
          log.info(s"Issue updated at: ${issue.html_url}")
          issue
        }
      case None =>
        log.info("Existing issue not found, creating a new one\n")
        githubClient.createIssue(dep) map { issue =>
          log.info(s"Issue created at: ${issue.html_url}")
          issue
        }
    }
  }

  lazy val defaultSettings = Seq(
    showDependencyUpdates := {
      readUpdates(dependencyUpdatesData.value, streams.value.log) {
        list =>
          val fullTable = Seq("Module", "Revision", "Patch", "Minor", "Major") +:
              list.map(
                dep =>
                  Seq(dep.moduleName,
                      dep.revision,
                      dep.patch.getOrElse(""),
                      dep.minor.getOrElse(""),
                      dep.major.getOrElse("")))
          streams.value.log.info("\nFound some dependency updates:\n")
          streams.value.log.info(TablePrinter.format(fullTable))
          streams.value.log.info("Execute `updateDependencyIssues` to update your issues\n")
      }
    },
    updateDependencyIssues := {
      readUpdates(dependencyUpdatesData.value, streams.value.log) {
        list =>
          streams.value.log.info("Reading GitHub issues\n")
          sys.props.get("githubToken") match {
            case Some(accessToken) =>
              val githubClient = GithubClient(githubOwner.value, githubRepo.value, accessToken)
              val result = for {
                issues <- githubClient.findIssuesByModuleName()
                createdIssues <- Future.sequence(
                  list map (dep =>
                              createIssueForDep(dep, issues, githubClient, streams.value.log)))
              } yield createdIssues
              result onComplete {
                case Success(createdIssues) => streams.value.log.info("GitHub issues created or updated\n")
                case Failure(e) =>
                  streams.value.log.error(s"Error creating issues")
                  e.printStackTrace()
              }
            case None =>
              streams.value.log.info(
                "Can't read the access token, please set the GitHub token with the property 'githubToken' (for ex. `sbt -DgithubToken=XXXXXX`)\n")
          }

      }
    },
    dependencyUpdatesExclusions := moduleFilter(organization = "org.scala-lang")
  )

  override val projectSettings = defaultSettings

}
