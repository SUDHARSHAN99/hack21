package com.example.adf.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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

import com.example.adf.Helper.DataHelper;
import com.example.adf.dto.InvestorDecisionRule;
import com.example.adf.dto.Lead;
import com.example.adf.dto.LeadBidStatus;
import com.example.adf.dto.RepoHelper;
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

import java.util.stream.Collectors;
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
	
	@Autowired
	private RepoHelper repoHelper;
	
	public void processResultList(ResultList resultList ) {
		resultList.getResult().parallelStream().forEach(e -> processLead(e));
	}
	
	public void processLead(Result result) {
		boolean validateRequest = true;//uwExecutionHelper.validateRequest(result);
		System.out.println(" validateRequest result=" +validateRequest +" " + result.getListing_number() );
		if(validateRequest) {
			buildLeadEntity(result);
			boolean ruleResult = true ;//buildAndExcecuteBasicChecks(result);
			System.out.println(" ruleResult = " + ruleResult + "  " + result.getListing_number());
			if(ruleResult) {
				boolean modelResult = executeNarAndRiskModelUW(result);
				/*
				 * if(modelResult) { System.out.println(" Lead is eligible for bid" +
				 * result.getListing_number()); makeBidRequest(result); }
				 */
			}
		}
		System.out.println("Call to save DB results");
		executorService.execute(() -> saveToDbForInvestorService(result.getListing_number()));
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
			System.out.println("bidResponse::"+ bidResponse);
			buildBidResponse(bidResponse);
		} catch (Exception e) {
			System.out.println(" Biding got failed");
			e.printStackTrace();
		}
		System.out.println(" Bid placed successfully");
	}

	private void buildBidResponse(BidResponse bidResponse) {
		
		List<BidRequest> bid_requests = bidResponse.getBid_requests();
		//List<LeadBidStatus> LeadBidStatusList = new ArrayList<LeadBidStatus>();
		for (BidRequest bidRequest : bid_requests) {
			LeadBidStatus leadBidStatus = new LeadBidStatus();
			leadBidStatus.setBidStatus(bidRequest.getBid_status());
			leadBidStatus.setListing_id(bidRequest.getListing_id());
			leadBidStatus.setBidAmount(bidRequest.getBid_amount());
			leadBidStatus.setDatestamp(new Date());
			leadBidStatus.setLeadId(bidRequest.getListing_id());
			DataHelper.addLeadBidStatus(bidRequest.getListing_id(), leadBidStatus);
		//	LeadBidStatusList.add(leadBidStatus);
		}
	//	System.out.println("LeadBidStatusList"+ LeadBidStatusList.size());
		//DataHelper.addLeadBidStatus(leadId, LeadBidStatusList);
		//repoHelper.saveLeadBidStatusList(LeadBidStatusList);
	}

	private boolean executeNarAndRiskModelUW(Result result) {
		AtomicBoolean flag = new AtomicBoolean();
		try {
			CountDownLatch countDownLatch = new CountDownLatch(1);
			executorService.execute(() -> {
				try {
					callNarModel(result);
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

	private void callNarModel(Result result) throws InterruptedException, ExecutionException, IOException {
		//NarModelRequest narModelRequest = buildNarModelRequest();
		
		String request = JsonMapper.bindObjectToString(result);
		String response = httpAgent.hitEndPoint(request, riskModelUrl, contentType);
		System.out.println("risk and nar response"+ response);
		DataHelper.addScoreMap(result.getListing_number(), response);
		boolean narAndRisk = uwExecutionHelper.validateModelScore(result.getListing_number(), response);
		if(narAndRisk) {
			System.out.println("Model passed");
			makeBidRequest(result);
		}else
			System.out.println("Model failed");
		//NarModelResponse narModelResponse =	JsonMapper.bindStringToObject(response, NarModelResponse.class);
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
		callableList.add(() -> uwExecutionHelper.validateState(result.getListing_number(), result.getBorrower_state()));
		callableList.add(() -> uwExecutionHelper.validateTerm(result.getListing_number(), result.getListing_term()));
		callableList.add(() -> uwExecutionHelper.validateLoan(result.getListing_number(), result.getWhole_loan()));
		callableList.add(() -> uwExecutionHelper.validateCoBorrowerApplication(result.getListing_number(), result.isCo_borrower_application()));
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
	
	public void saveToDbForInvestorService(long leadId) {
		saveLeadBidStatus(leadId);
		saveInvestorDecisionRules(leadId);
	}
	
	public void saveInvestorDecisionRules(long leadId) {
		try {
			if (DataHelper.getDecisionRules(leadId) != null) {
				System.out.println("Decision rules for leadId " + leadId + ":" + DataHelper.getDecisionRules(leadId));
				List<InvestorDecisionRule> decisionRuleServices = DataHelper.getDecisionRules(leadId);
				System.out.println("Save these decision rules for " + leadId + " is  " + decisionRuleServices);
				repoHelper.saveDecisionRulesList(decisionRuleServices);
				System.out.println("decision rules saved for " + leadId);
			}
		} catch (Exception e) {
			System.out.println("Excepption occured while storing the decision rules " + e);
		}
	}
	
	public void saveLeadBidStatus(long leadId) {
		try {
			if (DataHelper.getLeadBidStatus(leadId) != null) {
				System.out.println("Lead Bid status for leadId " + leadId + ":" + DataHelper.getLeadBidStatus(leadId));
				List<LeadBidStatus> leadBidStatusList = DataHelper.getLeadBidStatus(leadId);
				System.out.println("Save these bid status list for " + leadId + " is  " + leadBidStatusList);
				repoHelper.saveLeadBidStatusList(leadBidStatusList);
				System.out.println("Bid Status saved for " + leadId);
			}
		} catch (Exception e) {
			System.out.println("Excepption occured while storing the Bid status " + e);
		}

	}
	
	public void buildLeadEntity(Result result) {
		Lead lead = new Lead();
		lead.setEmpStatus(result.getEmployment_status_description());
		lead.setEmpType(result.getEmployment_status_description());
		lead.setListingId(result.getListing_number());
		
	}

}
