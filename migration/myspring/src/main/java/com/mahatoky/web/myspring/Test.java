package com.mahatoky.web.myspring;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.mahatoky.web.myspring.model.UrlMethod;

public class Test {
    public static void main(String[] args) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        UrlMethod urlMethod  = new UrlMethod("/hello", "Ma methode");

        String otherObject = "{\"url\":\"/hello\",\"method\":\"Ma methode\"}";
        UrlMethod wala =  objectMapper.readValue(otherObject, UrlMethod.class);

        System.out.println(wala.getMethod());
    }
}
