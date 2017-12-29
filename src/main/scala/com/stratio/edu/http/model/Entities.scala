package com.stratio.edu.http

case class UpgradeService(id: String, version: String)

case class User(id: String, name: String, lastName: String, email: String)

case class Users(users: Seq[User])

case class ActionPerformed(description: String)