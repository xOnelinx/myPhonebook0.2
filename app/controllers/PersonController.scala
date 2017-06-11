package controllers

import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import models._
import dal._

import scala.concurrent.{ExecutionContext, Future}
import javax.inject._

import play.api.data.validation._

class PersonController @Inject() (repo: PersonRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  /**
    *патерны для валидации
    */

  val phoneFormat = """^(\+)?\d{5,15}$""".r


  val phoneUniqueConstraint: Constraint[String] = Constraint("the phone number must be unique")({
    plainText =>
      val errors = plainText match {
        case phone if !repo.isPhoneFree(phone) => Seq(ValidationError(Messages("number is not unique")))
        case _ => Nil
      }
      if (errors.isEmpty) {
        Valid
      } else {
        Invalid(errors)
      }
  })

/**
  *форма валидации
  */

  val personForm: Form[CreatePersonForm] = Form {
    mapping(
      "name" -> nonEmptyText(3),
      "phone" -> nonEmptyText.verifying(Constraints.pattern(phoneFormat,"use numbers","use + and numbers"),phoneUniqueConstraint)
    )(CreatePersonForm.apply)(CreatePersonForm.unapply)
  }

  /**
    *отображение списка контактов
    */
  def index (filter: String) = Action.async { implicit request =>
    repo.findByFilter(filter = ("%" + filter + "%")) map { persons =>
      Ok(views.html.index(personForm,persons,filter))
    }
  }

  /**
    *добавление нового контакта
    */
  def addPerson = Action.async { implicit request =>
    personForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.index(errorForm,Seq.empty[Person],"")))
      },
      person => {
        repo.create(person.name, person.phone).map { _ =>
          Redirect(routes.PersonController.index())
        }
      }
    )
  }

  /**
  *удалить контакт
  */
  def delete (id: Long) = Action.async{implicit request =>
    repo.delete(id) map{res =>
      Redirect(routes.PersonController.index())
    }
  }

}

case class CreatePersonForm(name: String, phone: String)