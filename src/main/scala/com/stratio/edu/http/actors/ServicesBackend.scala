package com.stratio.edu.http.actors

import com.stratio.edu.http.UpgradeService
import com.stratio.edu.http.utils.ConfigComponent

class ServicesBackend extends ConfigComponent {

  def upgrade(service: UpgradeService) = {
    val upgradeService = service.copy(version = s"SeviceUpgraded ${service.version}")
    upgradeService
  }
}
