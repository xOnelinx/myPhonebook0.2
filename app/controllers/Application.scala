package controllers

import models.{Contact,ContactForm}
import play.api.mvc._
import scala.concurrent.Future
import services.ContactService
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Денис on 01.06.2017.
  */
class Application extends Controller{

  def index = Action.async { implicit request =>
    ContactService.listAllContact map { contacts =>
      Ok(views.html.index(ContactForm.form,contacts))
    }
  }

  def addContact() = Action.async {implicit request =>
    ContactForm.form.bindFromRequest.fold(
      errorForm => Future.successful(Ok(views.html.index(errorForm,Seq.empty[Contact]))),
      data => {
        val newContact = Contact(0,data.name,data.pnumber)
        ContactService.addContact(newContact).map(res =>
        Redirect(routes.Application.index()))
      }
    )
  }

  def deleteContact(id:Long) = Action.async{ implicit request =>
    ContactService.deleteContact(id) map{ res =>
      Redirect(routes.Application.index)
    }
  }
}
