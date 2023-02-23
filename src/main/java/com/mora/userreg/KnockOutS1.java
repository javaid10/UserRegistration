package com.mora.userreg;

import java.time.LocalDate;
import java.time.chrono.HijrahDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konylabs.middleware.api.OperationData;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.exceptions.MiddlewareException;
import com.konylabs.middleware.session.Session;
import com.mora.util.ErrorCode;

public class KnockOutS1 implements JavaService2 {
	private static final Logger logger = LogManager.getLogger(KnockOutS1.class);

	private String ageInMonths = "";

	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		boolean istemp = false;
		String inOUT = "";
		String customerId = "";

		String firstName = "";
		String secondName = "";
		String lastName = "";

		Result result = new Result();
		JSONObject JsonResponse = null; // new JSONObject();

		String res = "";

		Map<String, String> customerResponse = new HashMap<>();

		if (request.getParameter("Mobile").toString().equals("")) {
			ErrorCode.ERR_66005.updateResultObject(result);
			// result.addParam("ResponseCode", ErrorCode.ERR_66005.toString());
			// result.addParam("Message", ErrorCode.ERR_66005.getErrorMessage());
		} else if (request.getParameter("NationalID").toString().equals("")) {
			ErrorCode.ERR_66008.updateResultObject(result);
			// result.addParam("ResponseCode", ErrorCode.ERR_66008.toString());
			// result.addParam("Message", ErrorCode.ERR_66008.getErrorMessage());
		} else if (request.getParameter("Product").toString().equals("")) {
			ErrorCode.ERR_660025.updateResultObject(result);
			// result.addParam("ResponseCode", ErrorCode.ERR_660025.toString());
			// result.addParam("Message", ErrorCode.ERR_660025.getErrorMessage());
		} else if (request.getParameter("Age").toString().equals("")) {
			ErrorCode.ERR_660026.updateResultObject(result);
			// result.addParam("ResponseCode", ErrorCode.ERR_660026.toString());
			// result.addParam("Message", ErrorCode.ERR_660026.getErrorMessage());
		}

// product
		else if (request.getParameter("ProductID").toString().equals("")) {
			ErrorCode.ERR_660026.updateResultObject(result);
			// result.addParam("ResponseCode", ErrorCode.ERR_660026.toString());
			// result.addParam("Message", ErrorCode.ERR_660026.getErrorMessage());
		}

		else if (request.getParameter("LoanAmount").toString().equals("")) {
			ErrorCode.ERR_660035.updateResultObject(result);

			// result.addParam("ResponseCode", ErrorCode.ERR_660035.toString());
			// result.addParam("Message", ErrorCode.ERR_660035.getErrorMessage());
		}

		else if (request.getParameter("Tenor").toString().equals("")) {
			ErrorCode.ERR_660036.updateResultObject(result);
			// result.addParam("ResponseCode", ErrorCode.ERR_660036.toString());
			// result.addParam("Message", ErrorCode.ERR_660036.getErrorMessage());
		}

		else if (request.getParameter("MonthlyRepayment").toString().equals("")) {
			ErrorCode.ERR_660037.updateResultObject(result);
			// result.addParam("ResponseCode", ErrorCode.ERR_660037.toString());
			// result.addParam("Message", ErrorCode.ERR_660037.getErrorMessage());
		}

		else if (request.getParameter("Approx").toString().equals("")) {
			ErrorCode.ERR_660038.updateResultObject(result);
			// result.addParam("ResponseCode", ErrorCode.ERR_660038.toString());
			// result.addParam("Message", ErrorCode.ERR_660038.getErrorMessage());
		}

		else {
			try {

				HashMap<String, Object> imap = new HashMap();
				imap.put("$filter", "UserName eq " + request.getParameter("NationalID"));
				OperationData serviceData = request.getServicesManager().getOperationDataBuilder()
						.withServiceId("DBXDBServices").withOperationId("dbxdb_customer_get").build();

				res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
						.withOperationId("dbxdb_customer_get").withRequestParameters(imap).build().getResponse();

				JsonResponse = new JSONObject(res);
				if (JsonResponse.has("customer")) {
					if (JsonResponse.getJSONArray("customer").length() <= 0) {
						ErrorCode.ERR_660040.updateResultObject(result);
						// result.addParam("ResponseCode", ErrorCode.ERR_660040.toString());
						// result.addParam("Message", ErrorCode.ERR_660040.getErrorMessage());
					} else {
						customerId = JsonResponse.getJSONArray("customer").getJSONObject(0).getString("id");
						String dob = JsonResponse.getJSONArray("customer").getJSONObject(0).getString("DateOfBirth");
						if (JsonResponse.getJSONArray("customer").getJSONObject(0).has("AddressValidationStatus")) {
							inOUT = JsonResponse.getJSONArray("customer").getJSONObject(0)
									.getString("AddressValidationStatus");
						}

						firstName = JsonResponse.getJSONArray("customer").getJSONObject(0).getString("FirstName");
						secondName = JsonResponse.getJSONArray("customer").getJSONObject(0).getString("MiddleName");
						lastName = JsonResponse.getJSONArray("customer").getJSONObject(0).getString("LastName");

						HashMap<String, Object> imapDb = new HashMap();

						ageInMonths = convertGeogToHijri(dob);

						imapDb.put("dataType", request.getParameter("Product"));
						imapDb.put("customerAge", convertGeogToHijri(dob));
						imapDb.put("insideKsa", inOUT);
						imapDb.put("loanAmount", request.getParameter("LoanAmount"));
						imapDb.put("nationality", "KSA");
						// imapDb.put("loanAmount", request.getParameter("Tenor"));

						String resDB = DBPServiceExecutorBuilder.builder().withServiceId("KnockoutService")
								.withOperationId("CalculateScoreCardS1").withRequestParameters(imapDb).build()
								.getResponse();
						String requestJson = (new ObjectMapper()).writeValueAsString(imap);
						String apiHost = "Scorecard:S1";
						try {
							auditLogData(request, response, requestJson, resDB, apiHost);
						} catch (DBPApplicationException | MiddlewareException e) {
							e.printStackTrace();
						}
						JSONObject jobj = new JSONObject(resDB);
						String status = getStatus(jobj.getJSONObject("body").getInt("applicationCategory"));
						logger.error(
								"NAtional id before create applicaiton stage = " + request.getParameter("NationalID"));

						JSONObject job = createApplicationStage(request.getParameter("Mobile"), customerId,
								request.getParameter("ProductID"), request.getParameter("Product"), status,
								request.getParameter("LoanAmount"), request.getParameter("Tenor"),
								request.getParameter("MonthlyRepayment"), request.getParameter("Approx"),
								jobj.getString("id"), request.getParameter("NationalID"), request.getParameter("Age"),
								inOUT);

						if (job.has("knockoutStatus")) {
							if (job.getString("knockoutStatus").equals("PASS")) {
//								 checking aml
								HashMap<String, Object> imapaml = new HashMap();
								imapaml.put("firstname", firstName);
								imapaml.put("fourthname", lastName);
								String resAML = DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
										.withOperationId("AMLCheckProc").withRequestParameters(imapaml).build()
										.getResponse();
								JSONObject jsonAMLStat = new JSONObject(resAML);
								String amlStatus = jsonAMLStat.getJSONArray("records").getJSONObject(0)
										.getString("status");
								if (amlStatus.equals("0")) {

									// StringBuilder sb = new StringBuilder();
									// sb.append("firstname").append(" eq ").append(firstName).append(" and ")
									// .append("fourthname").append(" eq ").append(lastName);

									// HashMap<String, Object> imapAml = new HashMap();
									// imapAml.put("$filter", sb);

									// String amlResponse
									// =DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices").withOperationId("dbxdb_customer_get")
									// .withRequestParameters(imapAml).build().getResponse();

									HashMap<String, Object> updateParam = new HashMap();
									updateParam.put("id", customerId);
									updateParam.put("currentAppId", job.get("applicationID"));

									String cusUpdateResp = DBPServiceExecutorBuilder.builder()
											.withServiceId("DBMoraServices").withOperationId("applicationIdUpdate")
											.withRequestParameters(updateParam).build().getResponse();

									JSONObject cusObj = new JSONObject(cusUpdateResp);
//									if(cusObj.getString("updatedRecords").equals("1")) {
//										logger.error("ApplicationID updated in customer table -----");
//									}

									logger.error("job for output" + job);

									Session session = request.getSession();
									session.setAttribute("ApplicationID", job.getString("applicationID"));

									result.addParam("ResponseCode", ErrorCode.ERR_60000.toString());
									result.addParam("Message", ErrorCode.ERR_660030.getErrorMessage() + " "
											+ job.getString("applicationID"));
									result.addParam("ApplicationID", job.getString("applicationID"));
									// JSONObject customerName=new JSONObject();

									result.addParam("FirstName", firstName);
									result.addParam("SecondName", secondName);
									result.addParam("LastName", lastName);

									// result.addParam("CustomerName",customerName);
								} else if (amlStatus.equals("1")) {
									ErrorCode.ERR_660044.updateResultObject(result);
								}
							} else if (job.getString("knockoutStatus").equalsIgnoreCase("fail")) {
								ErrorCode.ERR_660028.updateResultObject(result);
								
								HashMap<String, Object> updateParam = new HashMap();
								updateParam.put("id", customerId);
								updateParam.put("currentAppId", job.get("applicationID"));

								String cusUpdateResp = DBPServiceExecutorBuilder.builder()
										.withServiceId("DBMoraServices").withOperationId("applicationIdUpdate")
										.withRequestParameters(updateParam).build().getResponse();

								JSONObject cusObj = new JSONObject(cusUpdateResp);

							}
						} else if (job.has("isError")) {
							if (job.getString("isError").equals("true")) {
								ErrorCode.ERR_660027.updateResultObject(result);
								// result.addParam("ResponseCode", ErrorCode.ERR_660027.toString());
								// // result.addParam("Error",ErrorCode.ERR_66003.toString());
								// result.addParam("Message", ErrorCode.ERR_660027.getErrorMessage());
							}
						}

					}
				} else {
					ErrorCode.ERR_660040.updateResultObject(result);
					// result.addParam("ResponseCode", ErrorCode.ERR_660040.toString());
					// result.addParam("Message", ErrorCode.ERR_660040.getErrorMessage());
				}

			} catch (MiddlewareException e) {
				// result.addParam("Error",""+e);
				ErrorCode.ERR_660027.updateResultObject(result);
				// result.addParam("ResponseCode", ErrorCode.ERR_660027.toString());
				// result.addParam("Message", ErrorCode.ERR_660027.getErrorMessage() + " " +
				// e.getMessage());
			}

		}

		return result;
	}

	private String getStatus(int status) {
		int i = status;
		if (i > 0) {
			return "PASS";
		} else
			return "FAIL";
	}

// convert age into hijri and calculate age in months
	private String convertGeogToHijri(String date) {

		logger.error("DB DOB = " + date);

		String[] abc = date.split("-");
		System.out.print(abc[0]);

		LocalDate gregorianDateToday = LocalDate.now();
		HijrahDate dobHijriDate = HijrahDate.of(Integer.parseInt(abc[0]), Integer.parseInt(abc[1]),
				Integer.parseInt(abc[2]));
		HijrahDate hijradateToday = HijrahDate.from(gregorianDateToday);

		double numberOfMonths = ChronoUnit.MONTHS.between(dobHijriDate, hijradateToday);

		logger.error("Age In Hijri Months = " + numberOfMonths);

		return String.valueOf(Math.round(numberOfMonths));

	}

	private void updateVerifyStatusKnockoutS1(String mobileNumber, String status) {

		String res = "";

		HashMap<String, Object> imap = new HashMap();

		imap.put("Mobile", mobileNumber);
		imap.put("ResponseStatus", status);

		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
					.withOperationId("dbxdb_updatecus_knockoutS1").withRequestParameters(imap).build().getResponse();

		} catch (Exception e) {

			String errorMsg = "Error in Knockout, updateVerifyStatusKnockoutS1 : " + e.toString();
			logger.error(errorMsg);
		}

	}

// request.getParameter("LoanAmount"),request.getParameter("Tenor"),request.getParameter("MonthlyRepayment"),request.getParameter("Approx")
	private JSONObject createApplicationStage(String mobileNumber, String cusID, String productID, String productName,
			String status, String loanAmount, String tenor, String monthlyRepay, String approx, String scoreId,
			String nanId, String age, String inout) {

		String res = "";
		String resApplicationID = "";
		String resStatus = "";
		JSONObject JsonResponse = new JSONObject();
		JSONObject JsonResult = new JSONObject();
		String statusCheck = "";

		HashMap<String, Object> imap = new HashMap();

		imap.put("mobile", mobileNumber);
		imap.put("knockoutStatus", status);
		imap.put("isKnockouts1", true);

		imap.put("Customer_id", cusID);
		imap.put("productId", productID);
		imap.put("scoredCardId", scoreId);
		imap.put("productName", productName);
		String applicationID = getRandomNumberString();
		imap.put("applicationID", applicationID);

		if (status.equalsIgnoreCase("pass")) {
			imap.put("applicationStatus", "SID_PRO_ACTIVE");
			statusCheck = "PASS";
		} else if (status.equalsIgnoreCase("fail")) {
			imap.put("applicationStatus", "SID_SUSPENDED");
			statusCheck = "FAIL";
		}

		imap.put("createdby", "SYSTEM");

		imap.put("loanAmount", loanAmount);
		imap.put("tenor", tenor);
		imap.put("monthlyRepay", monthlyRepay);
		imap.put("approx", approx);
		imap.put("insideKsa", inout);
		imap.put("customerAge", ageInMonths);
		imap.put("nationalId", nanId);

		logger.error("NAtional id create applicaiton stage = " + nanId);

		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
					.withOperationId("dbxdb_tbl_customerapplication_create").withRequestParameters(imap).build()
					.getResponse();

			JsonResponse = new JSONObject(res);

			if (JsonResponse.getJSONArray("tbl_customerapplication").length() > 0) {

				// JsonResponse.
				resApplicationID = JsonResponse.getJSONArray("tbl_customerapplication").getJSONObject(0)
						.getString("applicationID");
				resStatus = JsonResponse.getJSONArray("tbl_customerapplication").getJSONObject(0)
						.getString("knockoutStatus");

				JsonResult.put("knockoutStatus", resStatus);
				JsonResult.put("applicationID", resApplicationID);

			} else {
				JsonResult.put("isError", "true");
				JsonResult.put("message", "record not saved");
			}
			
			if (status.equalsIgnoreCase("fail")) {
			    customerBlockingDBCall(nanId, applicationID, "S1", "Application Declined");
			}

		} catch (Exception e) {

			String errorMsg = "Error in Knockout, : " + e.toString();
			logger.error(errorMsg);
			JsonResult.put("isError", "true");
			JsonResult.put("message", e.getMessage());
		}
		
		saveMISReportData(createRequestForMISReportDBCall(nanId, applicationID, statusCheck));
		return JsonResult;
	}

	public static String getRandomNumberString() {

		Random rnd = new Random();
		int number = rnd.nextInt(9999999);
		
		String applicationId = "M" + String.format("%07d", number);
		
		if(checkApplicationIdExist(applicationId)) {
			getRandomNumberString();
		}
		
		return applicationId;
	}
	
	public static boolean checkApplicationIdExist(String applicationID) {
		boolean check = false;
		
		try {
			HashMap<String, Object> imap = new HashMap();

			imap.put("applicationid", applicationID);
			String res = DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
					.withOperationId("dbxdb_sp_check_applicationid_exist").withRequestParameters(imap).build().getResponse();
			
			logger.error("ERROR checkApplicationIdExistresult  :: " + res);
			
			JSONObject jsonObject = new JSONObject(res);
			
			if(jsonObject.optJSONArray("records").length() > 0 
					&& jsonObject.optJSONArray("records").optJSONObject(0).optString("message").equalsIgnoreCase("1")) {
				check = true;
			}
			
		}catch(Exception e) {
			logger.error("ERROR checkApplicationIdExist :: " + e);
		}
		
		return check;
	}
	
	

	public boolean auditLogData(DataControllerRequest request, DataControllerResponse response, String req, String res,
			String apiHost) throws DBPApplicationException, MiddlewareException {
		UUID uuid = UUID.randomUUID();
		String uuidAsString = uuid.toString();

		String cusId = request.getParameter("NationalID");
		String logResponse = null;
		String channelDevice = "Mobile";

		String ipAddress = request.getRemoteAddr();

		HashMap<String, Object> logdataRequestMap = new HashMap<String, Object>();
		logdataRequestMap.put("id", uuidAsString);
		logdataRequestMap.put("Customer_id", cusId);
		logdataRequestMap.put("Application_id", "");
		logdataRequestMap.put("channelDevice", channelDevice);
		logdataRequestMap.put("apihost", apiHost);
		logdataRequestMap.put("request_payload", req);
		logdataRequestMap.put("reponse_payload", res);
		logdataRequestMap.put("ipAddress", ipAddress);

		logResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
				.withOperationId("dbxlogs_auditlog_create").withRequestParameters(logdataRequestMap).build()
				.getResponse();
		if (logResponse != null && logResponse.length() > 0) {

			return true;
		}
		return false;
	}
	
	private void customerBlockingDBCall(String nationalId, String applicationID, String scoreStage, String FailureReason) throws DBPApplicationException {

	    Map<String, Object> inputParams = new HashMap<>();
	    inputParams.put("nationalid", nationalId);
	    inputParams.put("applicationid", applicationID);
	    inputParams.put("scorestage", scoreStage);
	    inputParams.put("failurereason", FailureReason);
	    DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
	            .withOperationId("dbxdb_sp_create_update_customer_blocking").withRequestParameters(inputParams).build()
	            .getResponse();
	}
	
	private Map<String, Object> createRequestForMISReportDBCall(String nationalId, String applicationID, String status) {

	    Map<String, Object> inputParam = new HashMap<>();
	    inputParam.put("applicationID", applicationID);
	    inputParam.put("nationalId", nationalId);
	    inputParam.put("knockoutStatusS1", status);
	    return inputParam;
	}

	private void saveMISReportData(Map<String, Object> inputParams) {
	    try {
	        DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
	                .withOperationId("dbxdb_mis_report_create").withRequestParameters(inputParams).build()
	                .getResponse();
	    } catch (Exception ex) {
	        logger.error("ERROR saveMISReportData :: " + ex);
	    }
	}

}
