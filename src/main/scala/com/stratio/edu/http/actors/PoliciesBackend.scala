package com.stratio.edu.http.actors


import com.stratio.edu.http.Services
import com.stratio.edu.http.utils.ConfigComponent

class PoliciesBackend extends ConfigComponent {

  private var policiesCache = Set()

  def getAll(): Services = {
    Services(services = policiesCache.toSeq)
  }
}
