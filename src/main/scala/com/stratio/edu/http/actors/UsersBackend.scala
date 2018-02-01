package com.stratio.edu.http.actors

import com.stratio.edu.http.{ActionPerformed, User}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UsersBackend() {

  private var usersCache = Set.empty[User]

  def getAll(): Future[List[User]] =
    Future {
      usersCache.toList
    }

  def postOrPut(user: User): Future[ActionPerformed] = Future {
    usersCache = usersCache.takeWhile(u => u.id != user.id) + user
    ActionPerformed(s"User ${user.name} upserted")
  }

  def delete(id: String) = Future {
    val user = usersCache.find(u => u.id == id)
    usersCache = usersCache -- user
    user match {
      case Some(usr) => ActionPerformed(s"User ${usr.name} deleted")
      case None => ActionPerformed(s"User with id $id not found")
    }

  }

}
