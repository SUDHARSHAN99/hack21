package com.example.adf.model;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapper {

  static final ObjectMapper mapper = new ObjectMapper();

  public static <T> T bindStringToObject(String str, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
    return mapper.readValue(str, clazz);
  }

  public static <T> T bindStringToObject(String str, TypeReference<T> type) throws
          JsonParseException, JsonMappingException, IOException {
    return mapper.readValue(str, type);
  }

  public static <T> String bindObjectToString(T obj) throws JsonProcessingException {
    return mapper.writeValueAsString(obj);
  }
  public static JsonNode getJsonNode(String content)throws Exception {
    return mapper.readTree(content);
  }
  public static JsonNode getJsonNode(InputStream inputStream)throws Exception {
    return mapper.readTree(inputStream);
  }
}