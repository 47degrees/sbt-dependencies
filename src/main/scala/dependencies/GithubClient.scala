package dependencies

import cats.MonadError
import cats.instances.future
import github4s.Github
import github4s.Github._
import github4s.jvm.Implicits._
import github4s.free.domain._

import scala.concurrent.{ExecutionContext, Future}
import scalaj.http.HttpResponse

class GithubClient(owner: String, repo: String, accessToken: String)(implicit ec: ExecutionContext) {

  val issueTitle = "Update dependency"
  val issueLabel = "update-dependencies"

  implicit val monadError: MonadError[Future, Throwable] = catsStdInstancesForFuture

  def findIssuesByModuleName(): Future[Map[String, Issue]] = {

    def readIssues(issues: List[Issue]): Map[String, Issue] =
      issues.flatMap { issue =>
        if (issue.title.startsWith(issueTitle) && issue.title.length > issueTitle.length + 1) {
          val moduleName = issue.title.substring(issueTitle.length + 1)
          Option(moduleName -> issue)
        } else {
          None
        }
      }.toMap

    val searchParams = List(OwnerParamInRepository(s"$owner/$repo"),
                            IssueTypeIssue,
                            IssueStateOpen,
                            SearchIn(Set(SearchInTitle)),
                            LabelParam(issueLabel))

    Github(Some(accessToken)).issues
      .searchIssues(issueTitle, searchParams)
      .execFuture[HttpResponse[String]](Map("user-agent" -> "sbt-dependencies")) map {
      case Right(response) =>
        readIssues(response.result.items)
      case Left(e) => throw e
    }
  }

  def createIssue(dependencyUpdate: DependencyUpdate): Future[Issue] =
    Github(Some(accessToken)).issues
      .createIssue(owner = owner,
                   repo = repo,
                   title = title(dependencyUpdate),
                   body = body(dependencyUpdate),
                   labels = List(issueLabel),
                   assignees = List.empty)
      .execFuture[HttpResponse[String]](Map("user-agent" -> "sbt-dependencies")) map {
      case Right(response) => response.result
      case Left(e)         => throw e
    }

  def updateIssue(issue: Issue, dependencyUpdate: DependencyUpdate): Future[Issue] =
    Github(Some(accessToken)).issues
      .editIssue(owner = owner,
                 repo = repo,
                 issue = issue.number,
                 state = "open",
                 title = title(dependencyUpdate),
                 body = body(dependencyUpdate),
                 milestone = None,
                 labels = List(issueLabel),
                 assignees = issue.assignee.toList.map(_.login))
      .execFuture[HttpResponse[String]](Map("user-agent" -> "sbt-dependencies")) map {
      case Right(response) => response.result
      case Left(e)         => throw e
    }

  def title(dependencyUpdate: DependencyUpdate): String =
    s"$issueTitle ${dependencyUpdate.moduleName}"

  def body(dependencyUpdate: DependencyUpdate): String =
    s"""
       | Actual version: ${dependencyUpdate.revision}
       | Patch: ${dependencyUpdate.patch.getOrElse("-")}
       | Minor: ${dependencyUpdate.minor.getOrElse("-")}
       | Major: ${dependencyUpdate.major.getOrElse("-")}
         """.stripMargin

}

object GithubClient {

  def apply(owner: String, repo: String, accessToken: String)(
      implicit ec: ExecutionContext): GithubClient =
    new GithubClient(owner, repo, accessToken)

}
