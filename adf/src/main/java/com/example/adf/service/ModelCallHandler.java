package com.example.adf.service;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.adf.model.JsonMapper;
import com.example.adf.model.NarModelRequest;
import com.example.adf.model.NarModelResponse;
import com.example.adf.model.RiskModelRequest;
import com.example.adf.model.RiskModelResponse;


@Service
public class ModelCallHandler {

	private final ExecutorService executorService = Executors.newFixedThreadPool(200);
	private static final String contentType = "application/json";
	 @Value("${risk.model.url}")
	private String riskModelUrl;
	 @Value("${nar.model.url}")
	private String narModelUrl;
	
	@Autowired
	private HttpAgent httpAgent;

	private boolean executeNarAndRiskModelUW() {
		AtomicBoolean flag = new AtomicBoolean();
		try {
			CountDownLatch countDownLatch = new CountDownLatch(2);
			executorService.execute(() -> {
				try {
					callNarModel();
				} catch (Exception e) {
					flag.set(true);
					e.printStackTrace();
				} finally {
					countDownLatch.countDown();
				}
			});
			executorService.execute(() -> {
				try {
					callRiskModel();
				} catch (Exception e) {
					flag.set(true);
					e.printStackTrace();
				} finally {
					countDownLatch.countDown();
				}
			});
			countDownLatch.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (flag.get())
			return false;
		else {
			return executeModelSocreValidation();
		}
	}

	private void callRiskModel() throws InterruptedException, ExecutionException, IOException {
		RiskModelRequest riskModelRequest = buildRiskModelRequest();
		String request = JsonMapper.bindObjectToString(riskModelRequest);
		String response = httpAgent.hitEndPoint(request, riskModelUrl, contentType);
		RiskModelResponse	riskModelResponse =	JsonMapper.bindStringToObject(response, RiskModelResponse.class);
	}

	private void callNarModel() throws InterruptedException, ExecutionException, IOException {
		NarModelRequest narModelRequest = buildNarModelRequest();
		String request = JsonMapper.bindObjectToString(narModelRequest);
		String response = httpAgent.hitEndPoint(request, riskModelUrl, contentType);
		NarModelResponse narModelResponse =	JsonMapper.bindStringToObject(response, NarModelResponse.class);
	}

	private boolean executeModelSocreValidation() {
		return false;
	}

	private RiskModelRequest buildRiskModelRequest() {
		return null;
	}

	private NarModelRequest buildNarModelRequest() {
		return null;
	}
}
