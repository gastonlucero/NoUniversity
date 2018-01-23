package com.stratio.edu.http.actors

import com.stratio.edu.http.UpgradeService

class ServicesBackend {

  def upgrade(service: UpgradeService) = {
    val upgradeService = service.copy(version = s"SeviceUpgraded ${service.version}")
    upgradeService
  }
}
