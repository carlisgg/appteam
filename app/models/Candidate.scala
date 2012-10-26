package models

import play.api.db.DB
import play.api.Play.current
import anorm._


case class Candidate(email: String, profile: String, mainTechnologies: String, otherTechnologies: Option[String],
                     twitterAccount: Option[String], portfolio: Option[String])

object Candidate extends {

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

}
