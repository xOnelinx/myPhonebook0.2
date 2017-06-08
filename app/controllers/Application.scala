package controllers

import javax.inject.Inject

import models._
import play.api.data.Form
import play.api.data.Forms.{ignored, mapping, nonEmptyText}
import play.api.data.validation._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import play.api.i18n._
import services.ContactService
/**
  * Created by Денис on 01.06.2017.
  */

class Application @Inject()( val messagesApi: MessagesApi, contactService: ContactService
                           )(implicit ec: ExecutionContext) extends Controller with I18nSupport{

  implicit val app = play.api.Play.current


    val phoneUniqueConstraint: Constraint[String] = Constraint("constraints.uniquePhone")({
      plainText =>
        val errors = plainText match {
          case phone if !Contacts.isPhoneFree(phone) => Seq(ValidationError("phone number not unique!"))
          case _ => Nil
        }
        if (errors.isEmpty) {
          Valid
        } else {
          Invalid(errors)
        }
    })
    val phoneFormat = """^(\+)?\d{5,15}$""".r
    val form = Form(
      mapping(
        "name" -> nonEmptyText,
        "pnumber" -> nonEmptyText.verifying(Constraints.pattern(phoneFormat),phoneUniqueConstraint)
      )(ContactForm.apply)(ContactForm.unapply)
    )



  def index(filter: String) = Action.async { implicit request =>
    contactService.listContact(filter) map { contacts =>
      Ok(views.html.index(form,contacts,filter))
    }
  }

  def addContact() = Action.async {implicit request =>
    form.bindFromRequest.fold(
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
