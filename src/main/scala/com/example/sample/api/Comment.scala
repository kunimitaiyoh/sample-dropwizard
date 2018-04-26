package com.example.sample.api

import java.time.Instant

case class Comment(id: Int, userId: Int, articleId: Int, body: String, created: Instant)
