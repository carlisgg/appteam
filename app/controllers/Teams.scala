package controllers

import play.api.mvc.Controller
import models.Candidate

object Teams extends Controller with Secured{

  def details = withAuth { username => implicit request =>
    val found = Candidate.findByEmail(username)
    found match {
      case Some(candidate) => Ok(views.html.team(candidate, Candidate.findByTeam(candidate.team.getOrElse(""))))
      case None => Forbidden
    }
  }

}
