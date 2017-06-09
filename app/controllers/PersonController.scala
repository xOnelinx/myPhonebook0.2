package controllers

import play.api._
import play.api.mvc._
import play.api.i18n._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.libs.json.Json
import models._
import dal._

import scala.concurrent.{ExecutionContext, Future}
import javax.inject._

import play.api.data.validation.Constraints

class PersonController @Inject() (repo: PersonRepository, val messagesApi: MessagesApi)
                                 (implicit ec: ExecutionContext) extends Controller with I18nSupport{

  /**
    * The mapping for the person form.
    */

  val phoneFormat = """^(\+)?\d{5,15}$""".r


  val personForm: Form[CreatePersonForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "phone" -> nonEmptyText.verifying(Constraints.pattern(phoneFormat))
    )(CreatePersonForm.apply)(CreatePersonForm.unapply)
  }

  /**
    * The index action.
    */
//  def index = Action { implicit request =>
//    Ok(views.html.index(personForm))
//  }
  def index (filter: String) = Action.async { implicit request =>
    repo.list() map { persons =>
      Ok(views.html.index(personForm,persons,filter))
    }
  }

  /**
    * The add person action.
    *
    * This is asynchronous, since we're invoking the asynchronous methods on PersonRepository.
    */
  def addPerson = Action.async { implicit request =>
    // Bind the form first, then fold the result, passing a function to handle errors, and a function to handle succes.
    personForm.bindFromRequest.fold(
      // The error function. We return the index page with the error form, which will render the errors.
      // We also wrap the result in a successful future, since this action is synchronous, but we're required to return
      // a future because the person creation function returns a future.
      errorForm => {
        Future.successful(Ok(views.html.index(errorForm,Seq.empty[Person],"")))
      },
      // There were no errors in the from, so create the person.
      person => {
        repo.create(person.name, person.phone).map { _ =>
          // If successful, we simply redirect to the index page.
          Redirect(routes.PersonController.index(""))
        }
      }
    )
  }

  def delete (id: Long) = Action.async{implicit request =>
    repo.delete(id) map{res =>
      Redirect(routes.PersonController.index())
    }
  }

}

/**
  * The create person form.
  *
  * Generally for forms, you should define separate objects to your models, since forms very often need to present data
  * in a different way to your models.  In this case, it doesn't make sense to have an id parameter in the form, since
  * that is generated once it's created.
  */
case class CreatePersonForm(name: String, phone: String)