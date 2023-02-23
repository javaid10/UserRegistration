package com.mora.userreg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.konylabs.middleware.api.OperationData;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.mora.util.ErrorCode;

public class Expenses implements JavaService2 {

	private static final Logger logger = LogManager.getLogger(Expenses.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		Result result = new Result();
		JSONObject JsonResponse = null;
		String res = "";
		String nationalId = "";
		String message = "";

		try {
			JSONArray jarr = new JSONArray(request.getParameter("Expenses"));
			JsonResponse = new JSONObject();
			
			
			
			
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject job = jarr.getJSONObject(i);
				HashMap<String, Object> imap = new HashMap();
				imap.put("NationalID", job.getString("NationalID").toString());
				imap.put("ExpenseID", job.getString("ExpenseID").toString());
				imap.put("Amount", job.getString("Amount"));

				if (i == 0) {
					nationalId = job.getString("NationalID");
					String resultOfGetReApplyDate = getReApplyDate(nationalId);
					if (resultOfGetReApplyDate.equalsIgnoreCase("true")) {
						String resultOfGetCustomerApplicationJourneyDate = getCustomerApplicationJourneyDate(
								nationalId);
						if (resultOfGetCustomerApplicationJourneyDate.equalsIgnoreCase("true")) {
							String getSanadIterationDate = getSanadIterationDate(nationalId);
							if (getSanadIterationDate.equalsIgnoreCase("true")) {
								continue;
							} else {
								message = getMessage("660028");
								message = message.length() == 0 ? String.format(message, getSanadIterationDate)
										: getSanadIterationDate;
								result.addParam("ResponseCode", "660028");
								result.addParam("Message", message);
								return result;
							}
						} else {
							message = getMessage("660028");
							message = message.length() == 0
									? String.format(message, resultOfGetCustomerApplicationJourneyDate)
									: resultOfGetCustomerApplicationJourneyDate;
							result.addParam("ResponseCode", "660028");
							result.addParam("Message", message);
							return result;
						}
					} else {
						message = getMessage("660028");
						message = message.length() == 0 ? String.format(message, resultOfGetReApplyDate)
								: resultOfGetReApplyDate;
						result.addParam("ResponseCode", "660028");
						result.addParam("Message", message);
						return result;
					}
				}

				res = DBPServiceExecutorBuilder.builder().withServiceId("MooraJsonServices")
						.withOperationId("createCustomerExpenses").withRequestParameters(imap).build().getResponse();
				JsonResponse.append("res", new JSONObject(res));
			}
			JSONObject JsonResponse1 = new JSONObject();
			for (int j = 0; j < JsonResponse.getJSONArray("res").length(); j++) {
				if (JsonResponse.getJSONArray("res").getJSONObject(j).has("errmsg")) {
					ErrorCode.ERR_61002.updateResultObject(result);
					break;
				} else {
					result.addParam("ResponseCode", ErrorCode.ERR_60000.toString());
					result.addParam("Message", "" + ErrorCode.ERR_60000.getErrorMessage());
				}
			}
		} catch (Exception e) {
			ErrorCode.ERR_660021.updateResultObject(result);
			result.addParam("ResponseCode", ErrorCode.ERR_660028.toString());
			result.addParam("Message", "" + ErrorCode.ERR_660028.getErrorMessage());
		}
		customerApplicationJourneyIncrementDBCall(nationalId, result);
		return result;
	}

	private void customerApplicationJourneyIncrementDBCall(String nationalId, Result result) {
		try {
			Map<String, Object> inputParams = new HashMap<>();
			inputParams.put("nationalId", nationalId);
			DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
					.withOperationId("dbxdb_sp_increment_customer_application_journey_count")
					.withRequestParameters(inputParams).build().getResponse();
		} catch (Exception ex) {
			result.addParam("ResponseCode", ErrorCode.ERR_660028.toString());
			result.addParam("Message", "" + ErrorCode.ERR_660028.getErrorMessage());
			logger.error("ERROR customerApplicationJourneyIncrementDBCall :: " + ex);
		}
	}

	private String getReApplyDate(String nationalId) {
		try {
			Map<String, Object> inputParams = new HashMap<>();
			inputParams.put("nationalId", nationalId);
			String reApplyDateResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
					.withOperationId("dbxdb_sp_get_reapply_date").withRequestParameters(inputParams).build()
					.getResponse();
			JSONObject reApplyDateJsonObject = new JSONObject(reApplyDateResponse);
			if (reApplyDateJsonObject.getJSONArray("records").length() > 0) {
				String reApplyDate = reApplyDateJsonObject.getJSONArray("records").getJSONObject(0)
						.optString("reApplyDate");
				if (isReapplyDateValid(reApplyDate)) {
					return reApplyDate;
				}
			}
			return "true";
		} catch (Exception ex) {
			logger.error("ERROR getReApplyDate :: " + ex);
			return "true";
		}
	}

	private String getCustomerApplicationJourneyDate(String nationalId) {
		try {
			Map<String, Object> inputParams = new HashMap<>();
			inputParams.put("nationalId", nationalId);
			String customerApplicationJourneyDateResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId("DBMoraServices").withOperationId("dbxdb_sp_get_customer_application_journey_date")
					.withRequestParameters(inputParams).build().getResponse();
			JSONObject customerApplicationJourneyReApplyDateJsonObject = new JSONObject(
					customerApplicationJourneyDateResponse);
			if (customerApplicationJourneyReApplyDateJsonObject.getJSONArray("records").length() > 0) {
				String reApplyCustomerApplicationJourney = customerApplicationJourneyReApplyDateJsonObject
						.getJSONArray("records").getJSONObject(0).optString("reApplyCustomerApplicationJourney");
				if (isReapplyDateValid(reApplyCustomerApplicationJourney)) {
					return reApplyCustomerApplicationJourney;
				}
			}
			return "true";
		} catch (Exception ex) {
			logger.error("ERROR getCustomerApplicationJourneyDate :: " + ex);
			return "true";
		}
	}

	private String getSanadIterationDate(String nationalId) {
		try {
			Map<String, Object> inputParams = new HashMap<>();
			inputParams.put("nationalId", nationalId);
			String customerApplicationJourneyDateResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId("DBMoraServices").withOperationId("dbxdb_sp_get_sanad_iteration_date")
					.withRequestParameters(inputParams).build().getResponse();
			JSONObject sanadRetryDateJsonObject = new JSONObject(customerApplicationJourneyDateResponse);
			if (sanadRetryDateJsonObject.getJSONArray("records").length() > 0) {
				String sanadRetryDate = sanadRetryDateJsonObject.getJSONArray("records").getJSONObject(0)
						.optString("retrySanadSignDate");
				if (isReapplyDateValid(sanadRetryDate)) {
					return sanadRetryDate;
				}
			}
			return "true";
		} catch (Exception ex) {
			logger.error("ERROR getSanadIterationDate :: " + ex);
			return "true";
		}
	}

	private String getMessage(String code) {
		try {
			Map<String, Object> inputParams = new HashMap<>();
			inputParams.put("code", code);
			String fetchMessageFromDB = DBPServiceExecutorBuilder.builder()
					.withServiceId("DBMoraServices").withOperationId("dbxdb_sp_get_message_by_code")
					.withRequestParameters(inputParams).build().getResponse();
			JSONObject msgObj = new JSONObject(fetchMessageFromDB);
			if (msgObj.getJSONArray("records").length() > 0) {
				String msg = msgObj.getJSONArray("records").getJSONObject(0)
						.optString("msg_en");
				return msg;
			}
			return "";
		} catch (Exception ex) {
			logger.error("ERROR getSanadIterationDate :: " + ex);
			return "";
		}
	}

	public static boolean isReapplyDateValid(String reApplyDate) throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date inputDate = dateFormat.parse(reApplyDate);
		Date currentDate = new Date();
		return inputDate.compareTo(currentDate) >= 0;
	}
}
