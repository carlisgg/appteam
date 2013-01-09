package models

import play.api.db.DB
import play.api.Play.current
import anorm._
import anorm.SqlParser._
import play.api.libs.Codecs


case class Candidate(email: String, profile: String, mainTechnologies: String, otherTechnologies: Option[String],
                     twitterAccount: Option[String], portfolio: Option[String], password: Option[String],
                     team: Option[String])

object Candidate {

  /**
   * Parse a Candidate from a ResultSet
   */
  val simple = {
    get[String]("candidate.email") ~
    get[String]("candidate.profile") ~
    get[String]("candidate.mainTechnologies") ~
    get[Option[String]]("candidate.otherTechnologies") ~
    get[Option[String]]("candidate.twitterAccount") ~
    get[Option[String]]("candidate.portfolio") ~
    get[Option[String]]("candidate.password") ~
    get[Option[String]]("candidate.team") map {
      case email~profile~mainTechnologies~otherTechnologies~twitterAccount~portfolio~password~team =>
        Candidate(email, profile, mainTechnologies, otherTechnologies, twitterAccount, portfolio, password, team)
    }
  }

  def create(candidate: Candidate) {
    DB.withConnection(implicit c =>
      SQL(
        """
        insert into candidate (email, profile, mainTechnologies, otherTechnologies, twitterAccount, portfolio)
        values ({email}, {profile}, {mainTechnologies}, {otherTechnologies}, {twitterAccount}, {portfolio})
        """
      ).on(
        'email -> candidate.email, 'profile -> candidate.profile, 'mainTechnologies -> candidate.mainTechnologies,
        'otherTechnologies -> candidate.otherTechnologies, 'twitterAccount -> candidate.twitterAccount,
        'portfolio -> candidate.portfolio
      ).executeUpdate()
    )
  }

  def numApuntados: Long = {
    DB.withConnection(implicit c => {
      val firstRow = SQL(
        """
          select count(*) as apuntados from candidate
        """).apply().head

      firstRow[Long]("apuntados")
    }
    )
  }

  def updatePassword(email: String, passwordHash: String) = {
    DB.withConnection { implicit connection =>
      SQL("update candidate set password = {passwordHash} where email = {email}").on(
        'email -> email,
        'passwordHash -> passwordHash
      ).executeUpdate()
    }
  }

  def existsWithCredentials(email: String, password: String) = {

    val found = DB.withConnection { implicit connection =>
      SQL("select * from candidate where email = {email} and team is not null limit 1").on(
        'email -> email
      ).as(Candidate.simple.singleOpt)
    }

    val passwordHash = Codecs.sha1(password)

    found match {
      case Some(candidate) =>
        candidate.password match {
          case Some(`passwordHash`) => true
          case None => {
            updatePassword(email, passwordHash)
            true
          }
          case _ => false
        }
      case None => false
    }
  }

  def findByEmail(email: String) = {
    DB.withConnection { implicit connection =>
      SQL("select * from candidate where email = {email} limit 1").on(
        'email -> email
      ).as(Candidate.simple.singleOpt)
    }
  }

  def findByTeam(team: String) = {
    DB.withConnection { implicit connection =>
      SQL("select * from candidate where team = {team}").on(
        'team -> team
      ).as(Candidate.simple *)
    }
  }

}
