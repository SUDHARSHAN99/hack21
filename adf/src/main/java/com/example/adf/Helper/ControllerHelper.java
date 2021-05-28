/**
 * 
 */
package com.example.adf.Helper;

/**
 * @author sudharshanreddy
 *
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
@Component
public class ControllerHelper {

  private final Logger LOG = LoggerFactory.getLogger(ControllerHelper.class);
  
  @Autowired
  private ObjectMapper objectMapper;
  

  public <T> T bindJsonToObj(String request, Class<T> clazz) {
    LOG.info("Request Body --> " + request);
    try {
      T t = objectMapper.readValue(request, clazz);
      return t;
    } catch (IOException e) {
      e.printStackTrace();
    }
	return null;
  }
  @Async
  public <T> T bindRespJsonToObj(String request, Class<T> clazz) {
	  
	    try {
	    	Thread.sleep(10000);
	      T t = objectMapper.readValue(request, clazz);
	      LOG.info("Response Body --> " + request);
	      return t;
	    } catch (IOException | InterruptedException e) {
	      e.printStackTrace();
	    }
		return null;
	  }

  public <T> T bindJsonToObj(String request, Class<T> clazz, boolean log) {
    if(log) {
      LOG.info("Request Body --> " + request);
    }
    try {
      T t = objectMapper.readValue(request, clazz);
      return t;
    } catch (IOException e) {
    	e.printStackTrace();
    }
	return null;
  }

  public <T> List<T> bindJsonArrayToList(String request, Class<T> clazz) {
    LOG.info("Request Body --> " + request);
    try {
      List<T> t = objectMapper.readValue(request,
          TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, clazz));
      return t;
    } catch (IOException e) {
    	e.printStackTrace();
    }
	return null;
  }

  public String generateJsonFromObj(Object obj) {
    if(String.class.isInstance(obj)) {
    //  LOG.info("Response Body --> ");
      //LOG.info("" + obj);
      return (String) obj;
    }
    String response="";
    try {
      response = objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      LOG.info("Error while createing response, ", e);
      e.printStackTrace();
    }
    //LOG.info("Response Body --> " + response);
    System.out.println("Response Body --> " + response);
    return response;
  }

  
}
