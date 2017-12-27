package com.stratio.edu.http

case class Service(id: String, name: String, version: String, `type`: String)

case class Services(services: Seq[Service])

case class UpgradeService(id: String, version: String)

case class User(id: String, name: String, lastName: String, email: String)

case class Users(users: Seq[User])

case class ActionPerformed(description: String)