package services

import models.{Contact, Contacts}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
/**
  * Created by Денис on 05.06.2017.
  */

class ContactService {

  def addContact(user: Contact): Future[String] = {
    Contacts.add(user)
  }

  def deleteContact(id: Long): Future[Int] = {
    Contacts.delete(id)
  }

  def getContact(id: Long): Future[Option[Contact]] = {
    Contacts.get(id)
  }

  def listContact(filter: String): Future[Seq[Contact]] = {
    if (filter.equals(""))Contacts.listAll else Contacts.findByFilter(filter)
  }
}
