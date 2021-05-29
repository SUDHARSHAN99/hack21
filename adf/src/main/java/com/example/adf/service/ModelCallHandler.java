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

import com.example.adf.Helper.DataHelper;
import com.example.adf.dto.InvestorDecisionRule;
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
	private DataHelper dataHelper;
	
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
			dataHelper.buildLeadEntity(result);
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
		LeadBidStatus leadBidStatus = new LeadBidStatus();
		
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
		NarModelRequest narModelRequest = buildNarModelRequest();
		String request = "{\r\n" + 
				"\"credit_bureau_values_transunion_indexed\": {\r\n" + 
				"\"fico_score\": \"700-719\",\r\n" + 
				"\"credit_report_date\": \"2021-02-23 16:16:28 +0000\",\r\n" + 
				"\"at02s_open_accounts\": 7,\r\n" + 
				"\"g041s_accounts_30_or_more_days_past_due_ever\": 0,\r\n" + 
				"\"g093s_number_of_public_records\": 0,\r\n" + 
				"\"g094s_number_of_public_record_bankruptcies\": -4,\r\n" + 
				"\"g095s_months_since_most_recent_public_record\": -4,\r\n" + 
				"\"g102s_months_since_most_recent_inquiry\": 2,\r\n" + 
				"\"g218b_number_of_delinquent_accounts\": 0,\r\n" + 
				"\"g980s_inquiries_in_the_last_6_months\": 2,\r\n" + 
				"\"re20s_age_of_oldest_revolving_account_in_months\": 157,\r\n" + 
				"\"s207s_months_since_most_recent_public_record_bankruptcy\": -4,\r\n" + 
				"\"re33s_balance_owed_on_all_revolving_accounts\": 19338,\r\n" + 
				"\"at57s_amount_delinquent\": 0,\r\n" + 
				"\"g099s_public_records_last_24_months\": -4,\r\n" + 
				"\"at20s_oldest_trade_open_date\": 213,\r\n" + 
				"\"at03s_current_credit_lines\": 7,\r\n" + 
				"\"re101s_revolving_balance\": 19338,\r\n" + 
				"\"bc34s_bankcard_utilization\": 71,\r\n" + 
				"\"at01s_credit_lines\": 24\r\n" + 
				"},\r\n" + 
				"\"credit_bureau_values_transunion\": {\r\n" + 
				"\"fico_score\": \"700-719\",\r\n" + 
				"\"credit_report_date\": \"2021-02-23 16:16:28 +0000\",\r\n" + 
				"\"at01s\": 24,\r\n" + 
				"\"at02s\": 7,\r\n" + 
				"\"at03s\": 7,\r\n" + 
				"\"at06s\": 2,\r\n" + 
				"\"at09s\": 3,\r\n" + 
				"\"at12s\": 7,\r\n" + 
				"\"at20s\": 213,\r\n" + 
				"\"at21s\": 2,\r\n" + 
				"\"at24s\": 5,\r\n" + 
				"\"at25s\": 4,\r\n" + 
				"\"at27s\": 4,\r\n" + 
				"\"at28a\": 114675,\r\n" + 
				"\"at28b\": 95597,\r\n" + 
				"\"at29s\": 7,\r\n" + 
				"\"at32s\": 38671,\r\n" + 
				"\"at34a\": 80,\r\n" + 
				"\"at34b\": 75,\r\n" + 
				"\"at35a\": 13028,\r\n" + 
				"\"at35b\": 12020,\r\n" + 
				"\"at36s\": 999,\r\n" + 
				"\"at57s\": 0,\r\n" + 
				"\"at101s\": 91196,\r\n" + 
				"\"at101b\": 72117,\r\n" + 
				"\"au01s\": 6,\r\n" + 
				"\"au02s\": 3,\r\n" + 
				"\"au03s\": 3,\r\n" + 
				"\"au06s\": 1,\r\n" + 
				"\"au09s\": 2,\r\n" + 
				"\"au12s\": 3,\r\n" + 
				"\"au20s\": 162,\r\n" + 
				"\"au21s\": 2,\r\n" + 
				"\"au24s\": 2,\r\n" + 
				"\"au25s\": 1,\r\n" + 
				"\"au27s\": 1,\r\n" + 
				"\"au28s\": 68297,\r\n" + 
				"\"au29s\": 3,\r\n" + 
				"\"au32s\": 38671,\r\n" + 
				"\"au34s\": 77,\r\n" + 
				"\"au35s\": 17593,\r\n" + 
				"\"au36s\": 999,\r\n" + 
				"\"au57s\": 0,\r\n" + 
				"\"au101s\": 52779,\r\n" + 
				"\"bc01s\": 5,\r\n" + 
				"\"bc02s\": 3,\r\n" + 
				"\"bc03s\": 3,\r\n" + 
				"\"bc06s\": 0,\r\n" + 
				"\"bc09s\": 0,\r\n" + 
				"\"bc12s\": 3,\r\n" + 
				"\"bc20s\": 157,\r\n" + 
				"\"bc21s\": 62,\r\n" + 
				"\"bc24s\": 3,\r\n" + 
				"\"bc25s\": 3,\r\n" + 
				"\"bc27s\": 3,\r\n" + 
				"\"bc28s\": 27300,\r\n" + 
				"\"bc29s\": 3,\r\n" + 
				"\"bc32s\": 13198,\r\n" + 
				"\"bc34s\": 71,\r\n" + 
				"\"bc35s\": 6446,\r\n" + 
				"\"bc36s\": 999,\r\n" + 
				"\"bc57s\": 0,\r\n" + 
				"\"bc101s\": 19338,\r\n" + 
				"\"bc102s\": 9100,\r\n" + 
				"\"bc103s\": 6446,\r\n" + 
				"\"bc106s\": 0,\r\n" + 
				"\"bc107s\": 0,\r\n" + 
				"\"bc108s\": 0,\r\n" + 
				"\"bc110s\": 0,\r\n" + 
				"\"co01s\": 0,\r\n" + 
				"\"co02s\": -1,\r\n" + 
				"\"co03s\": -1,\r\n" + 
				"\"co04s\": -1,\r\n" + 
				"\"co05s\": -1,\r\n" + 
				"\"co06s\": -1,\r\n" + 
				"\"co07s\": -1,\r\n" + 
				"\"g001b\": 0,\r\n" + 
				"\"g002b\": 0,\r\n" + 
				"\"g003s\": 0,\r\n" + 
				"\"g020s\": 12,\r\n" + 
				"\"g041s\": 0,\r\n" + 
				"\"g042s\": 0,\r\n" + 
				"\"g043s\": 0,\r\n" + 
				"\"g051s\": 0,\r\n" + 
				"\"g057s\": 0,\r\n" + 
				"\"g058s\": 0,\r\n" + 
				"\"g059s\": 0,\r\n" + 
				"\"g061s\": 0,\r\n" + 
				"\"g063s\": 0,\r\n" + 
				"\"g064s\": 0,\r\n" + 
				"\"g066s\": 0,\r\n" + 
				"\"g068s\": 0,\r\n" + 
				"\"g069s\": 0,\r\n" + 
				"\"g071s\": 0,\r\n" + 
				"\"g093s\": 0,\r\n" + 
				"\"g094s\": -4,\r\n" + 
				"\"g095s\": -4,\r\n" + 
				"\"g099s\": -4,\r\n" + 
				"\"g100s\": 0,\r\n" + 
				"\"g102s\": 2,\r\n" + 
				"\"g103s\": 2,\r\n" + 
				"\"g104s\": -1,\r\n" + 
				"\"g105s\": 2,\r\n" + 
				"\"g209s\": -1,\r\n" + 
				"\"g210s\": -1,\r\n" + 
				"\"g211s\": -1,\r\n" + 
				"\"g212s\": -1,\r\n" + 
				"\"g217s\": 0,\r\n" + 
				"\"g218b\": 0,\r\n" + 
				"\"g218d\": 0,\r\n" + 
				"\"g219b\": 0,\r\n" + 
				"\"g219d\": 0,\r\n" + 
				"\"g220b\": 0,\r\n" + 
				"\"g220d\": 0,\r\n" + 
				"\"g221b\": 0,\r\n" + 
				"\"g221d\": 0,\r\n" + 
				"\"g222s\": -1,\r\n" + 
				"\"g223s\": -1,\r\n" + 
				"\"g225s\": -1,\r\n" + 
				"\"g226s\": -1,\r\n" + 
				"\"g227s\": -1,\r\n" + 
				"\"g228s\": -1,\r\n" + 
				"\"g230s\": -1,\r\n" + 
				"\"g231s\": 0,\r\n" + 
				"\"g232s\": 10,\r\n" + 
				"\"g233s\": -1,\r\n" + 
				"\"g234s\": 0,\r\n" + 
				"\"g235s\": -1,\r\n" + 
				"\"g236s\": 10,\r\n" + 
				"\"g237s\": 6,\r\n" + 
				"\"g238s\": 9,\r\n" + 
				"\"g239s\": -1,\r\n" + 
				"\"g240s\": 4,\r\n" + 
				"\"g241s\": 2,\r\n" + 
				"\"g960s\": 5,\r\n" + 
				"\"g980s\": 2,\r\n" + 
				"\"g990s\": 4,\r\n" + 
				"\"hi01s\": 2,\r\n" + 
				"\"hi02s\": 0,\r\n" + 
				"\"hi03s\": -3,\r\n" + 
				"\"hi06s\": 0,\r\n" + 
				"\"hi09s\": 0,\r\n" + 
				"\"hi12s\": -3,\r\n" + 
				"\"hi20s\": 164,\r\n" + 
				"\"hi21s\": 164,\r\n" + 
				"\"hi24s\": -3,\r\n" + 
				"\"hi25s\": -3,\r\n" + 
				"\"hi27s\": -3,\r\n" + 
				"\"hi28s\": -3,\r\n" + 
				"\"hi29s\": -3,\r\n" + 
				"\"hi32s\": -3,\r\n" + 
				"\"hi34s\": -3,\r\n" + 
				"\"hi35s\": -3,\r\n" + 
				"\"hi36s\": 999,\r\n" + 
				"\"hi57s\": -3,\r\n" + 
				"\"hi101s\": -2,\r\n" + 
				"\"hr01s\": 0,\r\n" + 
				"\"hr02s\": -1,\r\n" + 
				"\"hr03s\": -1,\r\n" + 
				"\"hr06s\": -1,\r\n" + 
				"\"hr09s\": -1,\r\n" + 
				"\"hr12s\": -1,\r\n" + 
				"\"hr20s\": -1,\r\n" + 
				"\"hr21s\": -1,\r\n" + 
				"\"hr24s\": -1,\r\n" + 
				"\"hr25s\": -1,\r\n" + 
				"\"hr27s\": -1,\r\n" + 
				"\"hr28s\": -1,\r\n" + 
				"\"hr29s\": -1,\r\n" + 
				"\"hr32s\": -1,\r\n" + 
				"\"hr34s\": -1,\r\n" + 
				"\"hr35s\": -1,\r\n" + 
				"\"hr36s\": -1,\r\n" + 
				"\"hr57s\": -1,\r\n" + 
				"\"hr101s\": -1,\r\n" + 
				"\"in01s\": 9,\r\n" + 
				"\"in02s\": 3,\r\n" + 
				"\"in03s\": 3,\r\n" + 
				"\"in06s\": 1,\r\n" + 
				"\"in09s\": 2,\r\n" + 
				"\"in12s\": 3,\r\n" + 
				"\"in20s\": 162,\r\n" + 
				"\"in21s\": 2,\r\n" + 
				"\"in24s\": 2,\r\n" + 
				"\"in25s\": 1,\r\n" + 
				"\"in27s\": 1,\r\n" + 
				"\"in28s\": 68297,\r\n" + 
				"\"in29s\": 3,\r\n" + 
				"\"in32s\": 38671,\r\n" + 
				"\"in34s\": 77,\r\n" + 
				"\"in35s\": 17593,\r\n" + 
				"\"in36s\": 999,\r\n" + 
				"\"in57s\": 0,\r\n" + 
				"\"in101s\": 52779,\r\n" + 
				"\"mt01s\": 3,\r\n" + 
				"\"mt02s\": 1,\r\n" + 
				"\"mt03s\": 1,\r\n" + 
				"\"mt06s\": 1,\r\n" + 
				"\"mt09s\": 1,\r\n" + 
				"\"mt12s\": 1,\r\n" + 
				"\"mt20s\": 213,\r\n" + 
				"\"mt21s\": 2,\r\n" + 
				"\"mt24s\": -6,\r\n" + 
				"\"mt25s\": -6,\r\n" + 
				"\"mt27s\": -6,\r\n" + 
				"\"mt28s\": 19078,\r\n" + 
				"\"mt29s\": 1,\r\n" + 
				"\"mt32s\": 19079,\r\n" + 
				"\"mt34s\": 100,\r\n" + 
				"\"mt34b\": -0.5,\r\n" + 
				"\"mt34c\": -3,\r\n" + 
				"\"mt35s\": 19079,\r\n" + 
				"\"mt36s\": 999,\r\n" + 
				"\"mt47s\": 0,\r\n" + 
				"\"mt57s\": 0,\r\n" + 
				"\"mt101s\": 19079,\r\n" + 
				"\"of01s\": 5,\r\n" + 
				"\"re01s\": 10,\r\n" + 
				"\"re02s\": 3,\r\n" + 
				"\"re03s\": 3,\r\n" + 
				"\"re06s\": 0,\r\n" + 
				"\"re09s\": 0,\r\n" + 
				"\"re12s\": 3,\r\n" + 
				"\"re20s\": 157,\r\n" + 
				"\"re21s\": 39,\r\n" + 
				"\"re24s\": 3,\r\n" + 
				"\"re25s\": 3,\r\n" + 
				"\"re27s\": 3,\r\n" + 
				"\"re28s\": 27300,\r\n" + 
				"\"re29s\": 3,\r\n" + 
				"\"re32s\": 13198,\r\n" + 
				"\"re34s\": 71,\r\n" + 
				"\"re35s\": 6446,\r\n" + 
				"\"re36s\": 999,\r\n" + 
				"\"re57s\": 0,\r\n" + 
				"\"re101s\": 19338,\r\n" + 
				"\"re102s\": 9100,\r\n" + 
				"\"rt01s\": 4,\r\n" + 
				"\"rt02s\": 0,\r\n" + 
				"\"rt03s\": -3,\r\n" + 
				"\"rt06s\": 0,\r\n" + 
				"\"rt09s\": 0,\r\n" + 
				"\"rt12s\": -3,\r\n" + 
				"\"rt20s\": 140,\r\n" + 
				"\"rt21s\": 39,\r\n" + 
				"\"rt24s\": -3,\r\n" + 
				"\"rt25s\": -3,\r\n" + 
				"\"rt27s\": -3,\r\n" + 
				"\"rt28s\": -3,\r\n" + 
				"\"rt29s\": -3,\r\n" + 
				"\"rt32s\": -3,\r\n" + 
				"\"rt34s\": -3,\r\n" + 
				"\"rt35s\": -3,\r\n" + 
				"\"rt36s\": 999,\r\n" + 
				"\"rt57s\": -3,\r\n" + 
				"\"rt101s\": 0,\r\n" + 
				"\"rt201s\": 0,\r\n" + 
				"\"s004s\": 95,\r\n" + 
				"\"s061s\": 999,\r\n" + 
				"\"s062s\": 999,\r\n" + 
				"\"s064a\": -4,\r\n" + 
				"\"s068a\": 0,\r\n" + 
				"\"s071a\": -4,\r\n" + 
				"\"st01s\": 0,\r\n" + 
				"\"st02s\": -1,\r\n" + 
				"\"st03s\": -1,\r\n" + 
				"\"st06s\": -1,\r\n" + 
				"\"st09s\": -1,\r\n" + 
				"\"st12s\": -1,\r\n" + 
				"\"st20s\": -1,\r\n" + 
				"\"st21s\": -1,\r\n" + 
				"\"st24s\": -1,\r\n" + 
				"\"st25s\": -1,\r\n" + 
				"\"st27s\": -1,\r\n" + 
				"\"st28s\": -1,\r\n" + 
				"\"st29s\": -1,\r\n" + 
				"\"st32s\": -1,\r\n" + 
				"\"st34s\": -1,\r\n" + 
				"\"st35s\": -1,\r\n" + 
				"\"st36s\": -1,\r\n" + 
				"\"st45s\": -1,\r\n" + 
				"\"st50s\": -1,\r\n" + 
				"\"st57s\": -1,\r\n" + 
				"\"st99s\": -1,\r\n" + 
				"\"st101s\": -1,\r\n" + 
				"\"atap01\": 1539,\r\n" + 
				"\"hiap01\": -3,\r\n" + 
				"\"hrap01\": -1,\r\n" + 
				"\"inap01\": 1047,\r\n" + 
				"\"mtap01\": 68,\r\n" + 
				"\"reap01\": 424,\r\n" + 
				"\"s063s\": -4,\r\n" + 
				"\"s063a\": -4,\r\n" + 
				"\"s207s\": -4,\r\n" + 
				"\"s207a\": -1,\r\n" + 
				"\"g106s\": 269,\r\n" + 
				"\"g099a\": -1,\r\n" + 
				"\"s206s\": -4,\r\n" + 
				"\"s209s\": -4,\r\n" + 
				"\"s204s\": -4,\r\n" + 
				"\"g242s\": 6,\r\n" + 
				"\"g243s\": 6,\r\n" + 
				"\"g244s\": 9,\r\n" + 
				"\"cv01\": -1,\r\n" + 
				"\"cv04\": -6,\r\n" + 
				"\"cv10\": 0,\r\n" + 
				"\"cv11\": 0,\r\n" + 
				"\"cv12\": 0,\r\n" + 
				"\"cv13\": 0,\r\n" + 
				"\"cv14\": 5,\r\n" + 
				"\"cv15\": 2,\r\n" + 
				"\"cv16\": 3,\r\n" + 
				"\"cv17\": -1,\r\n" + 
				"\"cv18\": -1,\r\n" + 
				"\"cv19\": 91196,\r\n" + 
				"\"cv20\": 1539,\r\n" + 
				"\"cv21\": 843,\r\n" + 
				"\"cv22\": 19338,\r\n" + 
				"\"cv23\": 424,\r\n" + 
				"\"cv24\": 300,\r\n" + 
				"\"re33s\": 19338\r\n" + 
				"},\r\n" + 
				"\"credit_bureau_values_experian\": {\r\n" + 
				"\"scorex\": \"NA\",\r\n" + 
				"\"fico_score\": \"NA\"\r\n" + 
				"},\r\n" + 
				"\"listing_start_date\": \"2021-02-24 22:01:04 +0000\",\r\n" + 
				"\"listing_duration\": 14,\r\n" + 
				"\"months_employed\": 244,\r\n" + 
				"\"listing_number\": 100000119,\r\n" + 
				"\"investment_product_id\": 1,\r\n" + 
				"\"decision_bureau\": \"TransUnion\",\r\n" + 
				"\"member_key\": \"866B40204426991CBC92\",\r\n" + 
				"\"listing_creation_date\": \"2021-02-23 16:16:26 +0000\",\r\n" + 
				"\"listing_status\": 2,\r\n" + 
				"\"listing_status_reason\": \"Active\",\r\n" + 
				"\"verification_stage\": 3,\r\n" + 
				"\"listing_amount\": 5000,\r\n" + 
				"\"amount_funded\": 0,\r\n" + 
				"\"amount_remaining\": 35000,\r\n" + 
				"\"percent_funded\": 0,\r\n" + 
				"\"partial_funding_indicator\": false,\r\n" + 
				"\"funding_threshold\": 1,\r\n" + 
				"\"prosper_rating\": \"A\",\r\n" + 
				"\"lender_yield\": 0.0974,\r\n" + 
				"\"borrower_rate\": 0.1074,\r\n" + 
				"\"borrower_apr\": 0.14318,\r\n" + 
				"\"listing_term\": 36,\r\n" + 
				"\"listing_monthly_payment\": 1141.55,\r\n" + 
				"\"prosper_score\": 9,\r\n" + 
				"\"listing_category_id\": 13,\r\n" + 
				"\"listing_title\": \"Household Expenses\",\r\n" + 
				"\"income_range\": 5,\r\n" + 
				"\"income_range_description\": \"$75,000-99,999\",\r\n" + 
				"\"stated_monthly_income\": 7333.33,\r\n" + 
				"\"income_verifiable\": true,\r\n" + 
				"\"dti_wprosper_loan\": 0.3563,\r\n" + 
				"\"employment_status_description\": \"Employed\",\r\n" + 
				"\"occupation\": \"Executive\",\r\n" + 
				"\"borrower_state\": \"WA\",\r\n" + 
				"\"prior_prosper_loans_active\": 0,\r\n" + 
				"\"prior_prosper_loans\": 1,\r\n" + 
				"\"prior_prosper_loans_principal_borrowed\": 25000,\r\n" + 
				"\"prior_prosper_loans_principal_outstanding\": 0,\r\n" + 
				"\"prior_prosper_loans_balance_outstanding\": 0,\r\n" + 
				"\"prior_prosper_loans_cycles_billed\": 0,\r\n" + 
				"\"prior_prosper_loans_ontime_payments\": 0,\r\n" + 
				"\"prior_prosper_loans_late_cycles\": 0,\r\n" + 
				"\"prior_prosper_loans_late_payments_one_month_plus\": 0,\r\n" + 
				"\"max_prior_prosper_loan\": 25000,\r\n" + 
				"\"min_prior_prosper_loan\": 25000,\r\n" + 
				"\"lender_indicator\": 0,\r\n" + 
				"\"channel_code\": \"80000\",\r\n" + 
				"\"amount_participation\": 0,\r\n" + 
				"\"investment_typeid\": 2,\r\n" + 
				"\"investment_type_description\": \"Whole\",\r\n" + 
				"\"whole_loan_start_date\": \"2021-02-24 21:28:41 +0000\",\r\n" + 
				"\"last_updated_date\": \"2021-02-24 22:53:22 +0000\",\r\n" + 
				"\"invested\": false,\r\n" + 
				"\"biddable\": true,\r\n" + 
				"\"has_mortgage\": true,\r\n" + 
				"\"historical_return\": 0.04796,\r\n" + 
				"\"historical_return_10th_pctl\": 0.03565,\r\n" + 
				"\"historical_return_90th_pctl\": 0.06584,\r\n" + 
				"\"estimated_monthly_housing_expense\": 68,\r\n" + 
				"\"co_borrower_application\": false\r\n" + 
				"}";//JsonMapper.bindObjectToString(narModelRequest);
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
	
	public void saveInvestorDecisionRules(long leadId) {
		try {
			if (DataHelper.getDecisionRules(leadId) != null) {
				System.out.println("Decision rules for leadId " + leadId + ":" + DataHelper.getDecisionRules(leadId));
				List<InvestorDecisionRule> decisionRuleServices = DataHelper.getDecisionRules(leadId);
				System.out.println("Save these decision rules for " + leadId + " is  " + decisionRuleServices);
				repoHelper.saveAffilcateDecisionRulesList(decisionRuleServices);
				System.out.println("decision rules saved for " + leadId);
			}
		} catch (Exception e) {
			System.out.println("Excepption occured while storing the decision rules " + e);
		}

	}

}
