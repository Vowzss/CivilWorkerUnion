package com.oneliferp.cwu.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.oneliferp.cwu.utils.SimpleDateTime;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class SimpleDateDeserializer extends JsonDeserializer<SimpleDateTime> {
    @Override
    public SimpleDateTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        final JsonNode node = p.getCodec().readTree(p);
        final LocalDate date = LocalDate.parse(node.get("date").asText(), SimpleDateTime.DATE_FORMATTER);
        final LocalTime time = node.has("time") ? LocalTime.parse(node.get("time").asText(), SimpleDateTime.TIME_FORMATTER) : null;

        return new SimpleDateTime(date, time);
    }
}
