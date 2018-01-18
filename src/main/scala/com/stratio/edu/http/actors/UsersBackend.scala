package com.stratio.edu.http.actors

import com.stratio.edu.http.utils.ConfigComponent
import com.stratio.edu.http.{ActionPerformed, User, Users}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class UsersBackend() extends ConfigComponent {

  private var usersCache = Set.empty[User]

  def getAll(): Future[List[User]] =
    Future {
      usersCache.toList
    }


  def getByName(name: String): Future[Option[User]] = Future {
    usersCache.find(usr => usr.name == name)
  }

  def postOrPut(user: User): Future[ActionPerformed] = Future {
    usersCache = usersCache.takeWhile(u => u.id != user.id) + user
    ActionPerformed(s"User ${user.name} upserted")
  }

  def delete(id: String) = Future {
    val user = usersCache.find(u => u.id == id)
    user match {
      case Some(usr) => ActionPerformed(s"User ${usr.name} deleted")
      case None => ActionPerformed(s"User with id $id not found")
    }

  }

  def paginated(page: Int, offset: Int, limit: Int): Future[Users] = Future {
    Users(Seq.empty[User])
  }
}
