/*
 * Copyright 2018 CJWW Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package common

import java.security.cert.X509Certificate

import com.cjwwdev.auth.frontend.AuthorisedAction
import com.cjwwdev.filters.RequestLoggingFilter
import com.cjwwdev.frontendUI.builders.NavBarLinkBuilder
import com.cjwwdev.http.exceptions.ForbiddenException
import com.cjwwdev.views.html.templates.errors.{NotFoundView, ServerErrorView, StandardErrorView}
import com.kenshoo.play.metrics.MetricsFilter
import com.typesafe.config.ConfigFactory
import controllers.login.{routes => loginRoutes}
import controllers.redirect.{routes => redirectRoutes}
import controllers.register.{routes => registerRoutes}
import controllers.user.{routes => userRoutes}
import controllers.{routes => assetRoutes}
import filters.{HeadersFilter, IPWhitelistFilter}
import javax.inject.{Inject, Provider, Singleton}
import org.slf4j.{Logger, LoggerFactory}
import play.api.http.Status._
import play.api.http.{DefaultHttpFilters, HttpErrorHandler}
import play.api.i18n.{I18NSupportLowPriorityImplicits, I18nSupport, MessagesApi}
import play.api.mvc.Results._
import play.api.mvc._
import play.api.routing.Router
import play.api.{Environment, OptionalSourceMapper}

import scala.concurrent.Future

trait ApplicationConfiguration {
  //FeedServiceConfig
  val EDIT_PROFILE              = "edit-profile"
  val TITLE                     = "Your profile has been updated"

  val LOGIN_CALLBACK            = controllers.login.routes.LoginController.show(None)

  val LOGIN_REDIRECT            = "/account-services/login"

  //Account types
  val ORGANISATION              = "organisation"
  val INDIVIDUAL                = "individual"

  def buildServiceUrl(service: String): String = ConfigFactory.load.getString(s"microservice.external-services.$service.domain")

  //routes
  val accountsMicroservice      = buildServiceUrl("accounts-microservice")
  val authMicroservice          = buildServiceUrl("auth-microservice")
  val sessionStore              = buildServiceUrl("session-store")
  val authService               = buildServiceUrl("auth-service")
  val diagnosticsFrontend       = buildServiceUrl("diagnostics-frontend")
  val deversityFrontend         = buildServiceUrl("deversity-frontend")
  val deversityMicroservice     = buildServiceUrl("deversity")
  val hubFrontend               = buildServiceUrl("hub-frontend")

  implicit def serviceLinks(implicit requestHeader: RequestHeader): Seq[NavBarLinkBuilder] = Seq(
    NavBarLinkBuilder("/", "glyphicon-home", "Home", "home"),
    NavBarLinkBuilder(redirectRoutes.RedirectController.redirectToDiagnostics().absoluteURL(), "glyphicon-wrench", "Diagnostics", "diagnostics"),
    NavBarLinkBuilder(redirectRoutes.RedirectController.redirectToDeversity().absoluteURL(), "glyphicon-education", "Deversity", "deversity"),
    NavBarLinkBuilder("/", "glyphicon-asterisk", "Hub", "the-hub")
  )

  implicit def standardNavBarRoutes(implicit requestHeader: RequestHeader): Map[String, Call] = Map(
    "navBarLogo"    -> assetRoutes.Assets.versioned("images/logo.png"),
    "globalAssets"  -> assetRoutes.Assets.versioned("stylesheets/global-assets.css"),
    "favicon"       -> assetRoutes.Assets.versioned("images/favicon.ico"),
    "userRegister"  -> Call("GET", registerRoutes.UserRegisterController.show().absoluteURL()),
    "orgRegister"   -> Call("GET", registerRoutes.OrgRegisterController.show().absoluteURL()),
    "login"         -> Call("GET", loginRoutes.LoginController.show(None).absoluteURL()),
    "dashboard"     -> Call("GET", userRoutes.DashboardController.show().absoluteURL()),
    "signOut"       -> Call("GET", loginRoutes.LoginController.signOut().absoluteURL())
  )
}

trait Logging {
  val logger: Logger = LoggerFactory.getLogger(getClass)
}

trait FrontendController
  extends Controller
    with ApplicationConfiguration
    with I18NSupportLowPriorityImplicits
    with I18nSupport
    with UrlParser
    with AuthorisedAction {
  implicit val messagesApi: MessagesApi

  implicit def requestHeaderFromRequest(implicit request: Request[_]): RequestHeader = request.copy()

  override def unauthorisedRedirect: Call = LOGIN_CALLBACK

  override val logger: Logger = LoggerFactory.getLogger(getClass)
}

trait UrlParser extends ApplicationConfiguration with Logging {
  def serviceDirector(implicit request: Request[_]) : String = {
    logger.info(s"[UrlParser] - [serviceDirector] ${request.getQueryString("redirect").getOrElse("Redirecting to service selector")}")
    request.getQueryString("redirect") match {
      case Some("diagnostics") => diagnosticsFrontend
      case Some("deversity")   => deversityFrontend
      case Some("hub")         => hubFrontend
      case _                   => controllers.redirect.routes.RedirectController.chooseService().url
    }
  }
}

trait RequestBuilder {
  def buildNewRequest[T](requestHeader : RequestHeader, requestBody : T) : Request[T] = new Request[T] {
    override def body: T                                              = requestBody
    override def secure: Boolean                                      = requestHeader.secure
    override def uri: String                                          = requestHeader.uri
    override def queryString: Map[String, Seq[String]]                = requestHeader.queryString
    override def remoteAddress: String                                = requestHeader.remoteAddress
    override def method: String                                       = requestHeader.method
    override def headers: Headers                                     = requestHeader.headers
    override def path: String                                         = requestHeader.path
    override def clientCertificateChain: Option[Seq[X509Certificate]] = requestHeader.clientCertificateChain
    override def version: String                                      = requestHeader.version
    override def tags: Map[String, String]                            = requestHeader.tags
    override def id: Long                                             = requestHeader.id
  }
}

@Singleton
class ErrorHandler @Inject()(env: Environment,
                             sm: OptionalSourceMapper,
                             router: Provider[Router],
                             implicit val messagesApi: MessagesApi) extends HttpErrorHandler with RequestBuilder with ApplicationConfiguration with Logging {

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    logger.error(s"[ErrorHandler] - [onClientError] - Url: ${request.uri}, status code: $statusCode")
    implicit val req: Request[String] = buildNewRequest[String](request, "")
    statusCode match {
      case NOT_FOUND  => Future.successful(NotFound(NotFoundView()))
      case FORBIDDEN  => Future.successful(Redirect(LOGIN_REDIRECT).withNewSession)
      case _          => Future.successful(Status(statusCode)(StandardErrorView(messagesApi("errors.standard-error.message"))))
    }
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    logger.error(s"[ErrorHandler] - [onServerError] - exception : $exception")
    exception.printStackTrace()
    implicit val req: Request[String] = buildNewRequest[String](request, "")
    exception match {
      case _: ForbiddenException => Future.successful(Redirect(LOGIN_REDIRECT).withNewSession)
      case _                     => Future.successful(InternalServerError(ServerErrorView()))
    }
  }
}

class EnabledFilters @Inject()(ipWhitelistFilter: IPWhitelistFilter,
                               loggingFilter: RequestLoggingFilter,
                               metricsFilter: MetricsFilter,
                               headersFilter: HeadersFilter)
  extends DefaultHttpFilters(
    ipWhitelistFilter,
    loggingFilter,
    metricsFilter,
    headersFilter
  )

class MissingBasicDetailsException(msg: String) extends Exception(msg)
class MissingOrgDetailsException(msg: String) extends Exception(msg)

sealed trait UpdatedPasswordResponse
case object InvalidOldPassword extends UpdatedPasswordResponse
case object PasswordUpdated extends UpdatedPasswordResponse
