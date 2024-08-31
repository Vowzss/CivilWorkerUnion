package com.oneliferp.cwu.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.oneliferp.cwu.utils.SimpleDuration;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SimpleDurationDeserializer extends JsonDeserializer<SimpleDuration> {
    @Override
    public SimpleDuration deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        final JsonNode node = p.getCodec().readTree(p);
        final String time = node.get("time").asText();

        return new SimpleDuration(LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm")));
    }
}