package com.oneliferp.cwu.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.oneliferp.cwu.utils.SimpleDate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class SimpleDateDeserializer extends JsonDeserializer<SimpleDate> {
    @Override
    public SimpleDate deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        final JsonNode node = p.getCodec().readTree(p);
        final String date = node.get("date").asText();
        final String time = node.get("time").asText();

        return new SimpleDate(
                LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
        );
    }
}
