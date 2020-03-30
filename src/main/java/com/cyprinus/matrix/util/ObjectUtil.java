package com.cyprinus.matrix.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class ObjectUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public HashMap object2map(Object object) throws JsonProcessingException {
        return objectMapper.readValue(objectMapper.writeValueAsString(object), HashMap.class);
    }
}
