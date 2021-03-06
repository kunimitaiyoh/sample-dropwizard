package com.example.sample.resources

import java.time.Instant
import javax.annotation.security.PermitAll
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.ws.rs.core.{MediaType, Response}
import javax.ws.rs.{BeanParam, FormParam, GET, POST, Path, PathParam, Produces, QueryParam, WebApplicationException}

import com.example.sample.api.{Article, Comment, User}
import com.example.sample.dao.{ArticleDao, CommentDao, UserDao}
import com.example.sample.resources.ArticlesResource.{ArticleParams, ArticleResponse, CommentParams}
import io.dropwizard.auth.Auth
import org.hibernate.validator.constraints.NotEmpty

import scala.beans.BeanProperty

@Path("/articles")
@Produces(Array(MediaType.APPLICATION_JSON))
class ArticlesResource(val articles: ArticleDao, val comments: CommentDao, val users: UserDao) {
  @GET
  @PermitAll
  def getList(@Auth user: User): Response = {
    val items = articles.searchByUserId(user.id)
    Response.ok(Map("articles" -> items)).build()
  }

  @POST
  @PermitAll
  def create(@Auth user: User, @Valid @BeanParam params: ArticleParams): Article = {
    val article = params.toArticle(user, Instant.now())
    val id = this.articles.create(article)
    article.copy(id = id)
  }

  @GET
  @Path("/{id}")
  @PermitAll
  def get(@PathParam("id") @NotNull id: Int): ArticleResponse = {
    this.articles.find(id) match {
      case Some(article) =>
        val comments = this.comments.findManyByArticleId(id)
        val users = this.users.findByArticleId(id)
        ArticleResponse(article, comments, users)
      case None =>
        throw new WebApplicationException("No such article.")
    }
  }

  @POST
  @Path("/{articleId}/comments")
  @PermitAll
  def createComment(@Auth user: User, @PathParam("articleId") @NotEmpty articleId: Int,
    @Valid @BeanParam params: CommentParams): Comment = {

    val comment = params.toComment(user, articleId, Instant.now())
    val id = this.comments.create(comment)
    comment.copy(id = id)
  }
}

object ArticlesResource {
  class ArticleParams {
    var id: Int = 0

    @FormParam("title")
    @NotEmpty
    var title: String = _

    @FormParam("body")
    @NotEmpty
    var body: String = _

    def toArticle(user: User, created: Instant): Article = {
      Article(0, user.id, this.title, this.body, created)
    }
  }

  class CommentParams {
    var id: Int = 0

    @FormParam("articleId")
    @NotEmpty
    var articleId: String = _

    @FormParam("body")
    @NotEmpty
    var body: String = _

    def toComment(user: User, articleId: Int, created: Instant): Comment = {
      Comment(0, user.id, articleId, this.body, created)
    }
  }

  case class ArticleResponse (
    @BeanProperty article: Article,
    @BeanProperty comments: Seq[Comment],
    @BeanProperty users: Seq[User]
  )
}