package com.example.sample.api

import java.time.Instant
import javax.xml.bind.annotation.XmlRootElement

import scala.beans.BeanProperty

case class User(@BeanProperty id: Int, @BeanProperty name: String, @BeanProperty mail: String, passwordDigest: String, @BeanProperty created: Instant) {

}
