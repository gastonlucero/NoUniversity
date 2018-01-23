package com.stratio.edu.http.actors

import com.stratio.edu.http.Service

class ServicesBackend {

  def upgrade(service: Service) = {
    val upgradeService = service.copy(version = s"Service ${service.version}")
    upgradeService
  }
}
