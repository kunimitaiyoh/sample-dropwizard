package com.example.sample.api

import java.time.Instant
import java.util.UUID

case class AccessToken(id: UUID, userId: Int, created: Instant, lastAccess: Instant) {
}
