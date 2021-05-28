package com.example.adf.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.adf.model.BidRequest;
import com.example.adf.model.BidRequestList;
import com.example.adf.model.BidResponse;
import com.example.adf.model.JsonMapper;
import com.example.adf.model.NarModelRequest;
import com.example.adf.model.NarModelResponse;
import com.example.adf.model.Result;
import com.example.adf.model.ResultList;
import com.example.adf.model.RiskModelRequest;
import com.example.adf.model.RiskModelResponse;
import java.util.stream.Stream;



@Service
public class ModelCallHandler {

	private final ExecutorService executorService = Executors.newFixedThreadPool(200);
	private static final String contentType = "application/json";
	 @Value("${risk.model.url}")
	private String riskModelUrl;
	 @Value("${nar.model.url}")
	private String narModelUrl;
	 @Value("${bid.url}")
	 private String bidUrl;
	
	@Autowired
	private HttpAgent httpAgent;
	
	@Autowired
	private UwExecutionHelper uwExecutionHelper;
	
	public void processResultList(ResultList resultList ) {
		resultList.getResult().parallelStream().forEach(e -> processLead(e));
	}
	
	public void processLead(Result result) {
		boolean validateRequest = true;//uwExecutionHelper.validateRequest(result);
		System.out.println(" validateRequest result=" +validateRequest +" " + result.getListing_number() );
		if(validateRequest) {
			boolean ruleResult = true ;//buildAndExcecuteBasicChecks(result);
			System.out.println(" ruleResult = " + ruleResult + "  " + result.getListing_number());
			if(ruleResult) {
				boolean modelResult = executeNarAndRiskModelUW();
				if(modelResult) {
					System.out.println(" Lead is eligible for bid" + result.getListing_number());
					makeBidRequest(result);
				}
			}
		}
	}
	
	private void makeBidRequest(Result result) {
		BidRequest bidRequest = new BidRequest(result.listing_number, 1.0);
		List<BidRequest> bidList = new ArrayList<BidRequest>();
		bidList.add(bidRequest);
		BidRequestList bidRequestList = new BidRequestList();
		bidRequestList.setBid_requests(bidList);
		try {
			String request = JsonMapper.bindObjectToString(bidRequestList);
			String response = httpAgent.hitEndPoint(request, bidUrl, contentType);
			System.out.println(" Response from market place" + response);
			BidResponse bidResponse = JsonMapper.bindStringToObject(response, BidResponse.class);
		} catch (Exception e) {
			System.out.println(" Biding got failed");
			e.printStackTrace();
		}
		System.out.println(" Bid placed successfully");
	}

	private boolean executeNarAndRiskModelUW() {
		AtomicBoolean flag = new AtomicBoolean();
		try {
			CountDownLatch countDownLatch = new CountDownLatch(1);
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
			/*
			 * executorService.execute(() -> { try { callRiskModel(); } catch (Exception e)
			 * { flag.set(true); e.printStackTrace(); } finally {
			 * countDownLatch.countDown(); } });
			 */
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
		RiskModelResponse riskModelResponse =	JsonMapper.bindStringToObject(response, RiskModelResponse.class);
	}

	private void callNarModel() throws InterruptedException, ExecutionException, IOException {
		NarModelRequest narModelRequest = buildNarModelRequest();
		String request = JsonMapper.bindObjectToString(narModelRequest);
		String response = httpAgent.hitEndPoint(request, riskModelUrl, contentType);
		System.out.println("risk and nar response"+ response);
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
	
	private boolean buildAndExcecuteBasicChecks(Result result) {
		List<Callable<Boolean>> callableList = new ArrayList<>();
		callableList.add(() -> uwExecutionHelper.validateState(result.getBorrower_state()));
		callableList.add(() -> uwExecutionHelper.validateTerm(result.getListing_term()));
		callableList.add(() -> uwExecutionHelper.validateLoan(result.getWhole_loan()));
		callableList.add(() -> uwExecutionHelper.validateCoBorrowerApplication(result.isCo_borrower_application()));
		CompletionService<Boolean> completionService = new ExecutorCompletionService<Boolean>(executorService);
		
		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
	    for (Callable<Boolean> callable : callableList) {
	      futures.add(completionService.submit(callable));
	    }
	    
	    for (int i = 0; i < callableList.size(); i++) {
			try {
				if (!completionService.take().get())
					return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	    return true;

	}

}
