package controllers

import play.api.mvc.{Action, Controller}
import models.{Idea, Candidate}
import play.api.data.Form
import play.api.data.Forms._
import scala.Some
import anorm.NotAssigned

object Teams extends Controller with Secured {

  val addIdeaForm = Form(
    tuple (
      "title" -> nonEmptyText,
      "description" -> nonEmptyText
    )
  )

  val addRatingForm = Form("rating" -> nonEmptyText)

  def details = withAuth { username => implicit request =>
    val found = Candidate.findByEmail(username)
    found match {
      case Some(candidate) => Ok(views.html.team(candidate, Candidate.findByTeam(candidate.team.getOrElse("")),
        Idea.ideasPerTeam(candidate.team.getOrElse("")), addIdeaForm, addRatingForm))
      case None => Forbidden
    }
  }

  def addIdea = withAuth { username => implicit request =>
    val found = Candidate.findByEmail(username)

    found match {
      case Some(candidate) => {
        addIdeaForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.team(candidate, Candidate.findByTeam(candidate.team.getOrElse("")),
                                                       Idea.ideasPerTeam(candidate.team.getOrElse("")), formWithErrors, addRatingForm)),
          value => {
            Idea.create(Idea(NotAssigned, candidate.email, candidate.team.getOrElse(""), value._1, value._2, 0, false))
            Redirect(routes.Teams.details)
          }
        )
      }
      case None => Forbidden
    }
  }

  def addRating(id_idea: Long) = withAuth { username => implicit request =>
    val found = Candidate.findByEmail(username)

    found match {
      case Some(candidate) => {
        addRatingForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.team(candidate, Candidate.findByTeam(candidate.team.getOrElse("")),
            Idea.ideasPerTeam(candidate.team.getOrElse("")), addIdeaForm, formWithErrors)),
          value => {
            Idea.rate(id_idea, candidate.email, value.toLong)
            Redirect(routes.Teams.details)
          }
        )
      }
      case None => Forbidden
    }
  }


}
