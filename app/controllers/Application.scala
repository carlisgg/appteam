package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models.Candidate

object Application extends Controller {

  val myForm = Form(
    mapping (
      "email" -> email,
      "profile" -> nonEmptyText,
      "mainTechnologies" -> nonEmptyText,
      "otherTechnologies" -> optional(text),
      "twitterAccount" -> optional(text),
      "portfolio" -> optional(text),
      "password" -> optional(text),
      "team" -> optional(text)
    ) (Candidate.apply) (Candidate.unapply)
  )
  
  def index = Action { implicit request =>
    Auth.getLoggedUser(request)
    Ok(views.html.index())
  }

  def faq = Action { implicit request =>
    Ok(views.html.faq())
  }

  def register = Action { implicit request =>
    Ok(views.html.register(myForm))
  }

  def apply = Action { implicit request =>
    myForm.bindFromRequest.fold(
      form => BadRequest(views.html.register(form)),
      candidate => {
          Candidate.create(candidate)
          Ok(views.html.joined(candidate, Candidate.numApuntados))
      }
    )
  }
}