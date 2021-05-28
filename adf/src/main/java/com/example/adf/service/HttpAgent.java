package com.example.adf.service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.apache.http.client.ClientProtocolException;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import org.springframework.stereotype.Service;

@Service
public class HttpAgent {

	private AsyncHttpClient httpClient = null;

	@PostConstruct
	public void init() {
		try {
			DefaultAsyncHttpClientConfig.Builder clientBuilder = Dsl.config().setConnectTimeout(60000);
			httpClient = Dsl.asyncHttpClient(clientBuilder);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	public String hitEndPointForGet(String url) throws ClientProtocolException, IOException, InterruptedException, ExecutionException {
	    BoundRequestBuilder requestBuilder = httpClient.prepareGet(url);
	    Future<Response> whenResponse = requestBuilder.execute();
	    Response response = whenResponse.get();
	    System.out.println(" Response ="+ response);
	    return response.getResponseBody();
	}
	
	public String hitEndPoint(String post, String url, String contentType) throws InterruptedException, ExecutionException {
		System.out.println("hit end point: " +url);
	    Request request = httpClient.preparePost(url)
	    .setHeader("Content-Type",contentType)
	    .setBody(post)
	    .build();
	    System.out.println("hit end point request: " +request);
	    Future<Response> whenResponse = httpClient.executeRequest(request);
	    Response response = whenResponse.get();
	    System.out.println("hit end point response: " +response);
	    return response.getResponseBody();
	  }
}
