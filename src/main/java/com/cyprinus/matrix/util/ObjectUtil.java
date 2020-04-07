package com.cyprinus.matrix.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
public class ObjectUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public HashMap object2map(Object object) throws JsonProcessingException {
        return objectMapper.readValue(objectMapper.writeValueAsString(object), HashMap.class);
    }

    public <T> T map2object(HashMap map, Class<T> classType) throws JsonProcessingException {
        return objectMapper.readValue(objectMapper.writeValueAsString(map), classType);
    }

    public void copyNullProperties(Object source, Object target) {
        BeanUtils.copyProperties(source, target, getNullField(source));
    }

    String obj2json(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    private static String[] getNullField(Object target) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(target);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
        Set<String> notNullFieldSet = new HashSet<>();
        if (propertyDescriptors.length > 0) {
            for (PropertyDescriptor p : propertyDescriptors) {
                String name = p.getName();
                Object value = beanWrapper.getPropertyValue(name);
                if (Objects.isNull(value)) {
                    notNullFieldSet.add(name);
                }
            }
        }
        String[] notNullField = new String[notNullFieldSet.size()];
        return notNullFieldSet.toArray(notNullField);
    }
}
