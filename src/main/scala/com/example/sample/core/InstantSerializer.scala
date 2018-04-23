package com.example.sample.core
import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneOffset}

import com.example.sample.core.InstantSerializer.FORMATTER
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}

class InstantSerializer extends JsonSerializer[Instant]{
  override def serialize(value: Instant, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
    gen.writeObject(FORMATTER.format(value))
  }
}

object InstantSerializer {
  val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")
    .withZone(ZoneOffset.UTC)
}
