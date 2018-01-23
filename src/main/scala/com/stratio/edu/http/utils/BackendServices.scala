package com.stratio.edu.http.utils

import com.stratio.edu.http.actors.{ServicesBackend, UsersBackend}

object BackendServices {

  lazy val serviceBackend: ServicesBackend = new ServicesBackend()
  lazy val userBackend: UsersBackend = new UsersBackend()


}
