package models


import play.api.Play
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation._
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.Messages

import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Денис on 05.06.2017.
  */
case class Contact(id: Long, name: String, pnumber: String)

case class ContactForm (name: String, pnumber: String)

class ContactTable(tag: Tag) extends Table[Contact](tag, "Contact"){
  def id = column [Long]("id",O.PrimaryKey,O.AutoInc)
  def name = column[String]("name")
  def pnumber = column[String]("pnumber")

  override def * = (id,name,pnumber)<>(Contact.tupled, Contact.unapply)
}

object Contacts{
  val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  val contacts = TableQuery[ContactTable]

  def add(contact: Contact):Future[String] = {
    dbConfig.db.run(contacts += contact).map(res => "Contact added").recover{
      case ex: Exception => ex.getCause.getMessage
    }
  }
  def delete (id: Long): Future[Int] = {
    dbConfig.db.run(contacts.filter(_.id === id).delete)
  }
  def get(id: Long): Future[Option[Contact]] = {
    dbConfig.db.run(contacts.filter(_.id === id).result.headOption)
  }

  def listAll: Future[Seq[Contact]] = {
    dbConfig.db.run(contacts.result)
  }

  def findByPhone(pnumber: String): Future[Option[Contact]] = {
    dbConfig.db.run(contacts.filter(_.pnumber === pnumber).result.headOption)
  }
  def findByFilter(filter: String): Future[Seq[Contact]] = {
    dbConfig.db.run(contacts.filter{ _.name.like(filter)}.result)
  }

  def isPhoneFree(pnumber: String): Boolean = findByPhone(pnumber).value.isEmpty
}