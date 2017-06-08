package models

import play.api.libs.json._

case class Person(id: Long, name: String, phone: String)

object Person {

  implicit val personFormat = Json.format[Person]
}
