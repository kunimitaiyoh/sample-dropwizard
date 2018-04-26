package com.example.sample.api

import java.time.Instant

case class Article(id: Int, userId: Int, title: String, body: String, created: Instant)
