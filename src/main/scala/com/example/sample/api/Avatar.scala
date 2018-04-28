package com.example.sample.api

import java.time.Instant

case class Avatar(name: String, userId: Int, data: Array[Byte], width: Int, height: Int, created: Instant)
