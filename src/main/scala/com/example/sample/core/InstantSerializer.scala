package com.example.sample.core
import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneOffset}

import com.example.sample.core.InstantSerializer.FORMATTER
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

/**
  * A serializer that serializes Instant values with ISO 8601 format.
  */
class InstantSerializer extends StdSerializer[Instant](classOf[Instant]){
  override def serialize(value: Instant, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
    gen.writeObject(FORMATTER.format(value))
  }
}

object InstantSerializer {
  val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX")
    .withZone(ZoneOffset.UTC)
}
