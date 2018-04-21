package com.example.sample.dao

import javax.persistence.{Inheritance, InheritanceType, Table}

import com.example.sample.api.User
import io.dropwizard.hibernate.AbstractDAO
import org.hibernate.SessionFactory


class UserDao(session: SessionFactory) extends AbstractDAO[User](session) {
  def create(user: User): User = {
    persist(user)
  }

  def find(id: Int): Option[User] = {
    Option(get(id))
  }
}
