package com.company.foodapp.mappers;

import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class MethodMapper {
    private static Map<HttpMethod, String> mapping = getMapping();

    private static Map<HttpMethod, String> getMapping() {
        var map = new HashMap<HttpMethod, String>();
        map.put(HttpMethod.GET, "GET");
        map.put(HttpMethod.POST, "POST");
        map.put(HttpMethod.PUT, "Put");
        map.put(HttpMethod.DELETE, "Delete");

        return map;
    }

    public static String map(HttpMethod httpMethod){
        return mapping.get(httpMethod);
    }
}
