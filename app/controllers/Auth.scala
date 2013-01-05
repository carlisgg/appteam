package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import models.Candidate

object Auth extends Controller {

  val loginForm = Form(
    tuple (
      "email" -> nonEmptyText,
      "password" -> nonEmptyText
    ) verifying ("El email o password proporcionado no es válido", result => result match {
      case (email, password) => check(email, password)
    })
  )

  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      value => Redirect(routes.Teams.details).withSession("email" -> value._1)
    )
  }

  def check(email: String, password: String) = {
    Candidate.existsWithCredentials(email, password)
  }
}

trait Secured {

  def username(request: RequestHeader) = request.session.get("email")

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login)

  def withAuth(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }
}
