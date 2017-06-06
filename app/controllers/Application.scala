package controllers

import javax.inject.Inject

import models.{Contact, ContactForm}
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.i18n._
import services.ContactService
/**
  * Created by Денис on 01.06.2017.
  */

@Singleton
class Application @Inject()( val messagesApi: MessagesApi, contactService: ContactService, contactForm: ContactForm
                           ) extends Controller with I18nSupport{


  def index(filter: String) = Action.async { implicit request =>
    contactService.listContact(filter) map { contacts =>
      Ok(views.html.index(ContactForm.form,contacts,filter))
    }
  }

  def addContact() = Action.async {implicit request =>
    ContactForm.form.bindFromRequest.fold(
      errorForm => Future.successful(BadRequest(views.html.index(errorForm,Seq.empty[Contact],""))),
      data => {
        val newContact = Contact(0,data.name,data.pnumber)
        contactService.addContact(newContact).map(res =>
        Redirect(routes.Application.index()))
      }
    )
  }

  def deleteContact(id:Long) = Action.async{ implicit request =>
    contactService.deleteContact(id) map{ res =>
      Redirect(routes.Application.index(""))
    }
  }
}
