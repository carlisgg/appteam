package models

import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.Play.current
import anorm.~

case class Idea(id: Pk[Long], candidateEmail: String, team: String, title: String, description: String, rating: Int, shared: Boolean)

object Idea {

  /**
   * Parse an Idea from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("idea.id") ~
    get[String]("idea.candidate_email") ~
    get[String]("idea.team") ~
    get[String]("idea.title") ~
    get[String]("idea.description") ~
    get[Int]("idea.rating") ~
    get[Boolean]("idea.shared") map {
    case id~candidate_email~team~title~description~rating~shared =>
      Idea(id, candidate_email, team, title, description, rating, shared)
    }
  }

  def create(idea: Idea) {
    DB.withConnection(implicit connection =>
      SQL(
        """
        insert into IDEA (candidate_email, team, title, description, rating, shared)
        values ({candidate_email}, {team}, {title}, {description}, 0, {shared})
        """
      ).on(
        'candidate_email -> idea.candidateEmail, 'team -> idea.team, 'title -> idea.title,
        'description -> idea.description, 'shared -> idea.shared
      ).executeUpdate()
    )
  }

  def ideasPerTeam(team: String) = {
    DB.withConnection { implicit connection =>
      SQL("select * from idea where team = {team} or (team != {team} and shared = true) order by shared, rating desc").on(
        'team -> team
      ).as(Idea.simple *)
    }
  }

  def isRatedBy(id_idea: Long, candidate_email: String) = {
    DB.withConnection { implicit connection =>
        SQL(
          """
            select count(*) = 1 from rating where id_idea = {id_idea} and candidate_email = {candidate_email}
          """).on('id_idea -> id_idea, 'candidate_email -> candidate_email).as(scalar[Boolean].single)
      }
  }

  def rate(id_idea: Long, candidate_email: String, rating: Long) = {
    DB.withConnection(implicit connection =>
      SQL(
        """
        insert into rating (id_idea, candidate_email, rating) values ({id_idea}, {candidate_email}, {rating})
        """
      ).on(
        'candidate_email -> candidate_email, 'id_idea -> id_idea, 'rating -> rating
      ).executeUpdate()
    )

    DB.withConnection(implicit connection =>
      SQL(
        """
        update idea set rating = rating + {rating} where id = {id_idea}
        """
      ).on(
        'id_idea -> id_idea, 'rating -> rating
      ).executeUpdate()
    )
  }

}
