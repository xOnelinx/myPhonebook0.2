package dal

import javax.inject.{Inject, Singleton}

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import models.Person

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * A repository for people.
  *
  * @param dbConfigProvider The Play db config provider. Play will inject this for you.
  */
@Singleton
class PersonRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  /**
    * отображение таблицы
    */
  private class PeopleTable(tag: Tag) extends Table[Person](tag, "PEOPLE") {

    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

    def name = column[String]("NAME")

    def phone = column[String]("PHONE")


    def * = (id, name, phone) <> ((Person.apply _).tupled, Person.unapply)
  }


  private val people = TableQuery[PeopleTable]


  def create(name: String, phone: String): Future[Person] = db.run {
    (people.map(p => (p.name, p.phone))
      returning people.map(_.id)
      into ((nameAge, id) => Person(id, nameAge._1, nameAge._2))
      ) += (name, phone)
  }


  def list(): Future[Seq[Person]] = db.run {
    people.result
  }

  def delete(id: Long): Future[Int] = {
    dbConfig.db.run(people.filter(_.id === id).delete)
  }

  def findByPhone(phone: String): Future[Option[Person]] = {
    dbConfig.db.run(people.filter(_.phone.like(phone)).result.headOption)
  }
  def findByFilter(filter: String): Future[Seq[Person]] = {
    dbConfig.db.run(people.filter{ _.name.like(filter)}.result)
  }
  val duration = Duration(500, "millis")
  def isPhoneFree(phone: String): Boolean = Await.result(findByPhone(phone),duration ).isEmpty //здесь кончается асинхронность

}
