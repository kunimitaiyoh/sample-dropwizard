package com.example.sample

import java.time.Instant
import java.util
import javax.servlet.DispatcherType

import com.codahale.metrics.MetricRegistry
import com.datasift.dropwizard.scala.ScalaApplication
import com.datasift.dropwizard.scala.jdbi.tweak.ProductResultSetMapperFactory
import com.example.sample.Authorizations.SampleOAuthAuthenticator
import com.example.sample.api.User
import com.example.sample.core.{DictionaryValidationExceptionMapper, InstantSerializer}
import com.example.sample.dao.{RawJdbiAccessTokenDao, RawJdbiArticleDao, RawJdbiAvatarDao, RawJdbiCommentDao, RawJdbiUserDao}
import com.example.sample.resources.{ArticlesResource, AuthorizationResource, UsersResource}
import com.fasterxml.jackson.databind.module.SimpleModule
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter
import io.dropwizard.auth.{AuthDynamicFeature, AuthValueFactoryProvider, CachingAuthenticator}
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.jersey.errors.{EarlyEofExceptionMapper, LoggingExceptionMapper}
import io.dropwizard.jersey.jackson.JsonProcessingExceptionMapper
import io.dropwizard.migrations.MigrationsBundle
import io.dropwizard.server.DefaultServerFactory
import io.dropwizard.setup.{Bootstrap, Environment}
import org.eclipse.jetty.servlets.CrossOriginFilter
import org.glassfish.jersey.media.multipart.MultiPartFeature
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature

object SampleApplication extends ScalaApplication[SampleConfig] {
  override def init(bootstrap: Bootstrap[SampleConfig]) {
    bootstrap.addBundle(new MigrationsBundle[SampleConfig] {
      override def getDataSourceFactory(configuration: SampleConfig): DataSourceFactory = configuration.database

      override def getMigrationsFileName: String = "migrations.sql"
    })

    bootstrap.getObjectMapper.registerModule(new SimpleModule().addSerializer(classOf[Instant], new InstantSerializer))
  }

  override def run(config: SampleConfig, environment: Environment) : Unit = {
    val jersey = environment.jersey()
    jersey.register(classOf[MultiPartFeature])

    /**
      * replacing Dropwizard's default exception mappers.
      * @see https://stackoverflow.com/questions/29492359/how-to-change-the-validation-error-behaviour-for-dropwizard
      */
    config.getServerFactory.asInstanceOf[DefaultServerFactory].setRegisterDefaultExceptionMappers(false)
    jersey.register(DictionaryValidationExceptionMapper.withDefault())
    jersey.register(new LoggingExceptionMapper[Throwable]() {})
    jersey.register(new JsonProcessingExceptionMapper(true))
    jersey.register(new EarlyEofExceptionMapper)

    val jdbi = new DBIFactory().build(environment, config.database, "mysql")
    jdbi.registerMapper(new ProductResultSetMapperFactory)

    jersey.register(jdbi)

    val servlets = environment.servlets()
    val cors = servlets.addFilter("CORS", classOf[CrossOriginFilter])
    cors.setInitParameter("allowedOrigins", "*")
    cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin,Authorization")
    cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD")
    cors.addMappingForUrlPatterns(util.EnumSet.allOf(classOf[DispatcherType]), true, "/*")

    val accessTokens = new RawJdbiAccessTokenDao(jdbi)
    val users = new RawJdbiUserDao(jdbi)
    val avatars = new RawJdbiAvatarDao(jdbi)
    val articles = new RawJdbiArticleDao(jdbi)
    val comments = new RawJdbiCommentDao(jdbi)
    jersey.register(users)

    val authFilter = new OAuthCredentialAuthFilter.Builder[User]()
      .setAuthenticator(new CachingAuthenticator(new MetricRegistry(),
        new SampleOAuthAuthenticator(accessTokens, users), config.authenticationCachePolicy))
      .setPrefix("Bearer")
      .buildAuthFilter()

    jersey.register(new AuthDynamicFeature(authFilter))
    jersey.register(classOf[RolesAllowedDynamicFeature])
    jersey.register(new AuthValueFactoryProvider.Binder(classOf[User]))

    jersey.register(new UsersResource(users, avatars))
    jersey.register(new AuthorizationResource(accessTokens, users))
    jersey.register(new ArticlesResource(articles, comments, users))
  }
}
