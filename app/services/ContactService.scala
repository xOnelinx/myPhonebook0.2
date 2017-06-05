package services

import models.{Contact, Contacts}

import scala.concurrent.Future
/**
  * Created by Денис on 05.06.2017.
  */
object ContactService {

  def addContact(user: Contact): Future[String] = {
    Contacts.add(user)
  }

  def deleteContact(id: Long): Future[Int] = {
    Contacts.delete(id)
  }

  def getContact(id: Long): Future[Option[Contact]] = {
    Contacts.get(id)
  }

  def listAllContact: Future[Seq[Contact]] = {
    Contacts.listAll
  }
}
