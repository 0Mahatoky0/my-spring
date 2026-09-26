package util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SpringJsonParser {

    private final ObjectMapper mapper;

    public SpringJsonParser(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String toJson(Object object) throws JsonProcessingException {
        return this.mapper.writeValueAsString(object);
    }
}