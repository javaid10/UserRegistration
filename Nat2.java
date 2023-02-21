package com.mora.userreg;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.Json;
import com.konylabs.middleware.api.OperationData;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.exceptions.MiddlewareException;
import com.mora.util.ErrorCode;
import com.mora.util.Utilities;
//import com.temenos.onboarding.crypto.PasswordGenerator;

import netscape.javascript.JSObject;

public class NationalIdVerification implements JavaService2 {

	private static final Logger logger = LogManager.getLogger(NationalIdVerification.class);

	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		boolean istemp = false;
		Result result = new Result();
		JSONObject JsonResponse = null; // new JSONObject();

		String res = "";
		Map<String, String> customerResponse = new HashMap<>();

		if (request.getParameter("NationalID").toString().equals("")) {
			ErrorCode.ERR_66008.updateResultObject(result);

			// result.addParam("ResponseCode", ErrorCode.ERR_66008.toString());
			// result.addParam("Message", ErrorCode.ERR_66008.getErrorMessage());
		} else if (request.getParameter("Mobile").toString().equals("")) {
			ErrorCode.ERR_66005.updateResultObject(result);
			// result.addParam("ResponseCode", ErrorCode.ERR_66005.toString());
			// result.addParam("Message", ErrorCode.ERR_66005.getErrorMessage());
		} else if (request.getParameter("DOB").toString().equals("")) {
			ErrorCode.ERR_66009.updateResultObject(result);
			// result.addParam("ResponseCode", ErrorCode.ERR_66009.toString());
			// result.addParam("Message", ErrorCode.ERR_66009.getErrorMessage());
		} else if (request.getParameter("LoanPurpose").toString().equals("")) {
			ErrorCode.ERR_660010.updateResultObject(result);
			// result.addParam("ResponseCode", ErrorCode.ERR_660010.toString());
			// result.addParam("Message", ErrorCode.ERR_660010.getErrorMessage());
		} else if (request.getParameter("IBAN").toString().equals("")) {
			ErrorCode.ERR_660011.updateResultObject(result);
			// result.addParam("ResponseCode", ErrorCode.ERR_660011.toString());
			// result.addParam("Message", ErrorCode.ERR_660011.getErrorMessage());
		}

		else if (request.getParameter("ChargeCode").toString().equals("")) {
			ErrorCode.ERR_660011.updateResultObject(result);
			// result.addParam("ResponseCode", ErrorCode.ERR_660011.toString());
			// result.addParam("Message", ErrorCode.ERR_660011.getErrorMessage());
		} else {

			JSONObject JresponseEnroll = new JSONObject();
			JresponseEnroll = enrollCustomer(request, response);
			// result.addParam("ResponseCode",ErrorCode.ERR_66004.toString());
			// result.addParam("msg"," "+JresponseEnroll);

			if (JresponseEnroll.getString("ResponseCode").equals("00")) {
				// result.addParam("ResponseCode",ErrorCode.ERR_66004.toString());
				// result.addParam("Error"," "+JresponseEnroll.getString("ResponseCode"));
//				 

				try {

					HashMap<String, Object> imap = new HashMap();
					imap.put("$filter", "Ssn eq " + request.getParameter("NationalID"));
					OperationData serviceData = request.getServicesManager().getOperationDataBuilder()
							.withServiceId("DBXDBServices").withOperationId("dbxdb_customer_get").build();

					res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
							.withOperationId("dbxdb_customer_get").withRequestParameters(imap).build().getResponse();

//						
					JsonResponse = new JSONObject(res);
					// if(JsonResponse.has("customer"))
					{
						if (JsonResponse.getJSONArray("customer").length() > 0) {
							ErrorCode.ERR_660012.updateResultObject(result);
							// result.addParam("ResponseCode", ErrorCode.ERR_660012.toString());
							// result.addParam("Message", ErrorCode.ERR_660012.getErrorMessage());
						} else {
							createCitizenVerifyStatus(request.getParameter("Mobile"),
									request.getParameter("NationalID"));

							JSONObject jobj = verifyMobileOwner(request, response);

							if (jobj.has("isOwnerVerify")) {
								if (jobj.getString("isOwnerVerify").equals("false")) {
									ErrorCode.ERR_660013.updateResultObject(result);
									// result.addParam("ResponseCode", ErrorCode.ERR_660013.toString());
									// // result.addParam("Error",ErrorCode.ERR_66003.toString());
									// result.addParam("Message", ErrorCode.ERR_660013.getErrorMessage());

								//	updateMobileVerifyStatus(request.getParameter("Mobile"), "Failed");
								}
							} else if (jobj.has("isOwnerVerifyError")) {
								if (jobj.getString("isOwnerVerifyError").equals("true")) {
									ErrorCode.ERR_660014.updateResultObject(result);
									// result.addParam("ResponseCode", ErrorCode.ERR_660014.toString());
									// // result.addParam("Error",ErrorCode.ERR_66003.toString());
									// result.addParam("Message", ErrorCode.ERR_660014.getErrorMessage() + " "
									// 		+ jobj.getString("OvEmessage"));
								}
							}

							else if (jobj.has("isCitizenSuccess")) {
								if (jobj.getString("isCitizenSuccess").equals("false")) {

									switch (jobj.getString("errorCode").toString()) {

									case "1":
										ErrorCode.ERR_660015.updateResultObject(result);
										// result.addParam("ResponseCode", ErrorCode.ERR_660015.toString());
										// result.addParam("Message", ErrorCode.ERR_660015.getErrorMessage());
										break;

									case "2":
										ErrorCode.ERR_660016.updateResultObject(result);
										// result.addParam("ResponseCode", ErrorCode.ERR_660016.toString());
										// result.addParam("Message", ErrorCode.ERR_660016.getErrorMessage());
										break;

									case "3":
										ErrorCode.ERR_660017.updateResultObject(result);
										// result.addParam("ResponseCode", ErrorCode.ERR_660017.toString());
										// result.addParam("Message", ErrorCode.ERR_660017.getErrorMessage());
										break;

									case "5":
										ErrorCode.ERR_660018.updateResultObject(result);
										// result.addParam("ResponseCode", ErrorCode.ERR_660018.toString());
										// result.addParam("Message", ErrorCode.ERR_660018.getErrorMessage());
										break;

									case "6":
										ErrorCode.ERR_660019.updateResultObject(result);
										// result.addParam("ResponseCode", ErrorCode.ERR_660019.toString());
										// result.addParam("Message", ErrorCode.ERR_660019.getErrorMessage());
										break;

									case "7":
										ErrorCode.ERR_660020.updateResultObject(result);
										// result.addParam("ResponseCode", ErrorCode.ERR_660020.toString());
										// result.addParam("Message", ErrorCode.ERR_660020.getErrorMessage());
										break;

									case "8":
										ErrorCode.ERR_660019.updateResultObject(result);
										// result.addParam("ResponseCode", ErrorCode.ERR_660019.toString());
										// result.addParam("Message", ErrorCode.ERR_660019.getErrorMessage());
										break;

									case "9":
										ErrorCode.ERR_660019.updateResultObject(result);
										// result.addParam("ResponseCode", ErrorCode.ERR_660019.toString());
										// result.addParam("Message", ErrorCode.ERR_660019.getErrorMessage());
										break;

									case "10":
										ErrorCode.ERR_660021.updateResultObject(result);
										// result.addParam("ResponseCode", ErrorCode.ERR_660021.toString());
										// result.addParam("Message", ErrorCode.ERR_660021.getErrorMessage());
										break;

									default:
										ErrorCode.ERR_660021.updateResultObject(result);
										// result.addParam("ResponseCode", ErrorCode.ERR_660021.toString());
										// result.addParam("Message", ErrorCode.ERR_660021.getErrorMessage());
										break;

									}

								} else {
									if (jobj.getString("customerStatus").equals("SID_CANCELLED")) {
										ErrorCode.ERR_660022.updateResultObject(result);
										// result.addParam("ResponseCode", ErrorCode.ERR_660022.toString());
										// result.addParam("Message", ErrorCode.ERR_660022.getErrorMessage());
									} else if (jobj.getString("customerStatus").equals("SID_CUS_ACTIVE")) {
										result.addParam("ResponseCode", ErrorCode.ERR_60000.toString());
										result.addParam("Message", ErrorCode.ERR_60000.getErrorMessage());
									} else if (jobj.getString("customerStatus").equals("SID_CUS_SUSPENDED")) {
										ErrorCode.ERR_660023.updateResultObject(result);
										// result.addParam("ResponseCode", ErrorCode.ERR_660023.toString());
										// result.addParam("Message", ErrorCode.ERR_660023.getErrorMessage());
									}

								}
							}

							else if (jobj.has("isCitizenExc")) {
								if (jobj.getString("isCitizenExc").equals("true")) {
									ErrorCode.ERR_660021.updateResultObject(result);
									// result.addParam("ResponseCode", ErrorCode.ERR_660021.toString());
									// // result.addParam("Error",ErrorCode.ERR_66003.toString());
									// result.addParam("Message",
									// 		ErrorCode.ERR_660021.getErrorMessage() + " " + jobj.getString("message"));
								}
							}

						}
					}
////						  else
////						  {
////							  result.addParam("ResponseCode",ErrorCode.ERR_660034.toString());
////								 result.addParam("Message",ErrorCode.ERR_660034.getErrorMessage());  
////						  }

				} catch (Exception e) {
					ErrorCode.ERR_660021.updateResultObject(result);
					// result.addParam("ResponseCode", ErrorCode.ERR_660021.toString());
					// result.addParam("Error", "" + e);
					// result.addParam(new Param("Error" ,""+e));
				}
//				 

			} else {
				ErrorCode.ERR_66004.updateResultObject(result);
				// result.addParam("ResponseCode", ErrorCode.ERR_66004.toString());
				String msg = "";
				if (JresponseEnroll.getString("ResponseCode") == "20") {
					msg = ErrorCode.ERR_660018.getErrorMessage();
				}
				if (JresponseEnroll.getString("ResponseCode") == "10") {
					msg = "Record not inserted";
				}
				if (JresponseEnroll.getString("ResponseCode") == "-11") {
					msg = ErrorCode.ERR_660014.getErrorMessage();
				}
				result.addParam("Error", " " + msg);
			}

		}
		return result;

		// return null;
	}

	private JSONObject verifyMobileOwner(DataControllerRequest request, DataControllerResponse response) {
		String res = "";
		JSONObject JsonResponse = null;
		JSONObject JsonIdentity = null;
		boolean flag = false;

		HashMap<String, Object> imap = new HashMap();
		imap.put("id", request.getParameter("NationalID"));
		imap.put("mobileNumber", request.getParameter("Mobile"));

		HashMap<String, Object> imapHeader = new HashMap();
		imapHeader.put("app-id", "c445edda");
		imapHeader.put("app-key", "3a171d308e025b6d7a46e93ad7b0bbb3");
		imapHeader.put("SERVICE_KEY", "9f5786c8-640c-4390-bde3-b952ef397145");
		imapHeader.put("content-type", "application/json");

		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("MobileOwnerVerificationAPI")
					.withOperationId("VerifyMobileNumber").withRequestParameters(imap).withRequestHeaders(imapHeader)
					.build().getResponse();
			String apiHost = "MobileOwnerVerification";
			String requestJson = new ObjectMapper().writeValueAsString(imapHeader);
			if (res != null) {
				try {
					if (auditLogData(request, response, requestJson, res, apiHost)) {

					} else {

					}
				} catch (DBPApplicationException | MiddlewareException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			JsonResponse = new JSONObject(res);
			if (JsonResponse.has("isOwner")) // JsonResponse.getJSONArray("res").getJSONObject(j).has("errmsg"))
			{
				if (JsonResponse.getBoolean("isOwner") == (true)) {

				//	updateMobileVerifyStatus(request.getParameter("Mobile"), "Passed");
//				  /flag=true;
					if (request.getParameter("NationalID").startsWith("1")) {
						JsonIdentity = callYakeenCitizen(request, response);
					} else if (request.getParameter("NationalID").startsWith("2")) {
						JsonIdentity = callYakeenAlien(request, response);
					}

				} else {
					flag = false;
					JsonIdentity = new JSONObject();
					JsonIdentity.put("isOwnerVerify", "false");
				}
			} else if (JsonResponse.has("errmsg")) // JsonResponse.getJSONArray("res").getJSONObject(j).has("errmsg"))
			{

				JsonIdentity = new JSONObject();
				JsonIdentity.put("isOwnerVerifyError", "true");
				JsonIdentity.put("OvEmessage", JsonResponse.getString("message").toString());

			}

		} catch (Exception e) {

			JsonIdentity = new JSONObject();
			JsonIdentity.put("isOwnerVerifyError", "true");
			JsonIdentity.put("OvEmessage", e.getMessage());
		}
		return JsonIdentity;
	}

	private JSONObject callYakeenCitizen(DataControllerRequest request, DataControllerResponse response) {
		boolean flag = false;

		String res = "";
		String operation = "";
		String errorCode = "";

		JSONObject JsonResponse = null;
		JSONObject JsonResult = new JSONObject();
		JSONObject JsonCitizenError = new JSONObject();

		if (request.getParameter("ChargeCode").toString().toUpperCase().equals("PROD")) {
			operation = "getCitizenInfoOrch";
		} else {
			operation = "getCitizenInfoOrchMock";
		}

		HashMap<String, Object> imap = new HashMap();

		imap.put("nin", request.getParameter("NationalID"));
		imap.put("dateOfBirth", request.getParameter("DOB"));
		imap.put("referenceNumber", "");
		imap.put("password", "Ejarah@76511");
		imap.put("userName", "USR_Ejarah_PROD");

		imap.put("chargeCode", "PROD");
		imap.put("Mobile", request.getParameter("Mobile"));

		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("YakeenOrch").withOperationId(operation)
					.withRequestParameters(imap).build().getResponse();

			String apiHost = "YAKEEN_ORCH";
			String requestJson = new ObjectMapper().writeValueAsString(imap);
			if (res != null) {
				try {
					if (auditLogData(request, response, requestJson, res, apiHost)) {

					} else {

					}
				} catch (DBPApplicationException | MiddlewareException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			JsonResponse = new JSONObject(res);

			if (JsonResponse.has("faultdetail")) {
				// JsonCitizenError=Utilities.xmltojson(JsonResponse.getString("faultdetail"));
				JsonCitizenError = Utilities.xmlParser(JsonResponse.getString("faultdetail"));
				// errorCode=JsonCitizenError.getJSONObject("Yakeen4EjarahFault").getJSONObject("commonErrorObject").getJSONObject("ErrorCode").toString();

				errorCode = JsonCitizenError.getJSONObject("commonErrorObject").getString("ErrorCode").toString();

				JsonResult.put("isCitizenSuccess", "false");
				JsonResult.put("errorCode", errorCode);

				// updateCitizenVerifyStatus(request.getParameter("Mobile"), "Failed");
				
			}

			else {
				HashMap<String, Object> imapDb = new HashMap();

				imapDb.put("NationalID", request.getParameter("NationalID"));
				imapDb.put("Mobile", request.getParameter("Mobile"));
				imapDb.put("IBAN", request.getParameter("IBAN"));

				String resDB = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
						.withOperationId("dbxdb_updatecustomer_citizeninfo").withRequestParameters(imapDb).build()
						.getResponse();

				JsonResponse = new JSONObject(resDB);

				if (JsonResponse.getJSONArray("records").length() > 0) {

					// JsonResponse.
					String saveCitizenRes = JsonResponse.getJSONArray("records").getJSONObject(0)
							.getString("Status_id");

					JsonResult.put("customerStatus", saveCitizenRes);
					JsonResult.put("isCitizenSuccess", "true");

					// updateCitizenVerifyStatus(request.getParameter("Mobile"), "Passed");
				}
			}

		} catch (Exception e) {

			JsonResult.put("message", e.getMessage());
			JsonResult.put("isCitizenExc", "true");
		}
		return JsonResult;

	}

	public void updateLoanContract(DataControllerRequest request, DataControllerResponse respone,JSONObject yakData) 	{
	
		if(yakData.has("hijYear") && yakData.has("nationalID") && yakData.has("engFirstname") && yakData.has("engSecondname") && yakData.has("engFamName")&& yakData.has("arFirstName") && yakData.has("arSecondName")&& yakData.has("arThridName") && yakData.has("arFamName")) {



			HashMap<String, String> inputParams = new HashMap<String, String>();

			


			// T	Yakeen	ID Type if cusomert start from 1 then T or Q
			// 1077543260	Yakeen	Customer ID number
			// 23/03/2038		ID Expiry Date in format DD/MM/YYYY
			// 23	Yakeen	DD
			// 3	Yakeen	MM
			// 20228	Yakeen	YYYY
			// 01/01/1980	Yakeen	Date of Birth in format DD/MM/YYYY
			// 1	Yakeen	Date
			// 1	Yakeen	Month
			// 1980	Yakeen	YYYY
			// M	Syetem	Gender code
			// S	System	Marital status
			// SAU	Yakeen	Nationality code
			// سعيد	Yakeen	Family Name – Arabic
			// محمد	Yakeen	First name - Arabic
			// محمد	Yakeen	Second name - Arabic
			// محمد	Yakeen	Third name – Arabic
			// EID	Yakeen	Family name - English
			// MOHAMMED	Yakeen	First name - English
			// ALMALKI	Yakeen	Second name - English
			// ALMALKI	Yakeen	Third name – English
			


		}

	}
	private JSONObject callYakeenCitizenForCore(DataControllerRequest request, DataControllerResponse response)
			throws JsonProcessingException {
		boolean flag = false;

		String res = "";
		String operation = "";
		String errorCode = "";

		JSONObject JsonResponse = null;
		JSONObject JsonResult = new JSONObject();
		JSONObject JsonCitizenError = new JSONObject();

		HashMap<String, Object> imap = new HashMap();

		imap.put("nin", request.getParameter("NationalID"));
		imap.put("dateOfBirth", request.getParameter("DOB"));
		imap.put("referenceNumber", "");
		imap.put("password", "Ejarah@76511");
		imap.put("userName", "USR_Ejarah_PROD");

		imap.put("chargeCode", "PROD");
		imap.put("Mobile", request.getParameter("Mobile"));

		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("YakeenSoapAPI").withOperationId("getCitizenInfo")
					.withRequestParameters(imap).build().getResponse();

			String apiHost = "YAKEEN_CITIZEN";
			String requestJson = new ObjectMapper().writeValueAsString(imap);
			if (res != null) {
				try {
					if (auditLogData(request, response, requestJson, res, apiHost)) {

					} else {

					}
				} catch (DBPApplicationException | MiddlewareException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			JsonResponse = new JSONObject(res);

		} catch (DBPApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return JsonResponse;

	}

	// need to work on this
	private JSONObject callYakeenAlien(DataControllerRequest request, DataControllerResponse response) {
		boolean flag = false;

		String res = "";
		String operation = "";
		String errorCode = "";

		JSONObject JsonResponse = null;
		JSONObject JsonResult = new JSONObject();
		JSONObject JsonCitizenError = new JSONObject();

		if (request.getParameter("ChargeCode").toString().toUpperCase().equals("PROD")) {
			operation = "getAlienInfoByIqama";
		} else {
			operation = "getAlienInfoByIqamaMock";
		}

		HashMap<String, Object> imap = new HashMap();

		imap.put("iqamaNumber", request.getParameter("NationalID"));
		imap.put("dateOfBirth", request.getParameter("DOB"));
		imap.put("referenceNumber", "");
		imap.put("password", "Ejarah@76511");
		imap.put("userName", "USR_Ejarah_PROD");

		imap.put("chargeCode", "PROD");
		imap.put("Mobile", request.getParameter("Mobile"));

		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("YakeenOrch").withOperationId(operation)
					.withRequestParameters(imap).build().getResponse();
			String apiHost = "YAKEEN_API";
			String requestJson = new ObjectMapper().writeValueAsString(imap);
			if (res != null) {
				try {
					if (auditLogData(request, response, requestJson, res, apiHost)) {

					} else {

					}
				} catch (DBPApplicationException | MiddlewareException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			JsonResponse = new JSONObject(res);

			if (JsonResponse.has("faultdetail")) {

				JsonCitizenError = Utilities.xmltojson(JsonResponse.getString("faultdetail"));
				errorCode = JsonCitizenError.getJSONObject("Yakeen4EjarahFault").getJSONObject("commonErrorObject")
						.getJSONObject("ErrorCode").toString();

				JsonResult.put("isCitizenSuccess", "false");
				JsonResult.put("errorCode", errorCode);
				updateIqamaVerifyStatus(request.getParameter("Mobile"), "Failed");
			} else {
				HashMap<String, Object> imapDb = new HashMap();

				imapDb.put("NationalID", request.getParameter("NationalID"));
				imapDb.put("Mobile", request.getParameter("Mobile"));
				imapDb.put("IBAN", request.getParameter("IBAN"));
				imapDb.put("LoanPurpose", request.getParameter("LoanPurpose"));

				String resDB = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
						.withOperationId("dbxdb_updatecustomer_iqamainfo").withRequestParameters(imapDb).build()
						.getResponse();

				JsonResponse = new JSONObject(resDB);

				if (JsonResponse.getJSONArray("records").length() > 0) {

					// JsonResponse.
					String saveCitizenRes = JsonResponse.getJSONArray("records").getJSONObject(0)
							.getString("Status_id");

					JsonResult.put("customerStatus", saveCitizenRes);
					JsonResult.put("isCitizenSuccess", "true");

					updateIqamaVerifyStatus(request.getParameter("Mobile"), "Passed");
					JsonResponse = new JSONObject();
					JsonResult = callYakeenAlienStatus(request, response);
				}
			}

		} catch (Exception e) {

			JsonResult.put("message", e.getMessage());
			JsonResult.put("isCitizenExc", "true");
		}
		return JsonResult;

	}

	private JSONObject callYakeenAlienForCore(DataControllerRequest request, DataControllerResponse response)
			throws JsonProcessingException {
		boolean flag = false;

		String res = "";
		String operation = "";
		String errorCode = "";

		JSONObject JsonResponse = null;

		HashMap<String, Object> imap = new HashMap();

		imap.put("iqamaNumber", request.getParameter("NationalID"));
		imap.put("dateOfBirth", request.getParameter("DOB"));
		imap.put("referenceNumber", "");
		imap.put("password", "Ejarah@76511");
		imap.put("userName", "USR_Ejarah_PROD");

		imap.put("chargeCode", "PROD");
		imap.put("Mobile", request.getParameter("Mobile"));

		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("YakeenSoapAPI")
					.withOperationId("getAlienInfoByIqama").withRequestParameters(imap).build().getResponse();
			String apiHost = "YAKEEN_ALIEN";
			String requestJson = new ObjectMapper().writeValueAsString(imap);
			if (res != null) {
				try {
					if (auditLogData(request, response, requestJson, res, apiHost)) {

					} else {

					}
				} catch (DBPApplicationException | MiddlewareException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			JsonResponse = new JSONObject(res);

		} catch (DBPApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return JsonResponse;

	}
	// ---------------

	private JSONObject callYakeenAlienStatus(DataControllerRequest request, DataControllerResponse response) {
		boolean flag = false;

		String res = "";
		String operation = "";
		String errorCode = "";

		JSONObject JsonResponse = null;
		JSONObject JsonResult = new JSONObject();
		JSONObject JsonCitizenError = new JSONObject();

		if (request.getParameter("ChargeCode").toString().toUpperCase().equals("PROD")) {
			operation = "getAlienInfoByIqamaAndStatusOrch";
		} else {
			operation = "getAlienInfoByIqamaAndStatusOrchMock";
		}

		HashMap<String, Object> imap = new HashMap();

		imap.put("iqamaNumber", request.getParameter("NationalID"));
		imap.put("dateOfBirth", request.getParameter("DOB"));
		imap.put("referenceNumber", "");
		imap.put("password", "Ejarah@76511");
		imap.put("userName", "USR_Ejarah_PROD");

		imap.put("chargeCode", "PROD");
		imap.put("Mobile", request.getParameter("Mobile"));

		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("YakeenOrch").withOperationId(operation)
					.withRequestParameters(imap).build().getResponse();
			String apiHost = "YAKEEN_ALIEN";
			String requestJson = new ObjectMapper().writeValueAsString(imap);
			if (res != null) {
				try {
					if (auditLogData(request, response, requestJson, res, apiHost)) {

					} else {

					}
				} catch (DBPApplicationException | MiddlewareException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			JsonResponse = new JSONObject(res);

			if (JsonResponse.has("faultdetail")) {

				JsonCitizenError = Utilities.xmltojson(JsonResponse.getString("faultdetail"));
				errorCode = JsonCitizenError.getJSONObject("Yakeen4EjarahFault").getJSONObject("commonErrorObject")
						.getJSONObject("ErrorCode").toString();

				JsonResult.put("isCitizenSuccess", "false");
				JsonResult.put("errorCode", errorCode);

				updateIqamaStVerifyStatus(request.getParameter("Mobile"), "Failed");
			} else {
				HashMap<String, Object> imapDb = new HashMap();

				imapDb.put("NationalID", request.getParameter("NationalID"));
				imapDb.put("Mobile", request.getParameter("Mobile"));
				imapDb.put("IBAN", request.getParameter("IBAN"));

				String resDB = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
						.withOperationId("dbxdb_updatecustomer_iqamastatus").withRequestParameters(imapDb).build()
						.getResponse();

				JsonResponse = new JSONObject(resDB);

				if (JsonResponse.getJSONArray("records").length() > 0) {

					// JsonResponse.
					String saveCitizenRes = JsonResponse.getJSONArray("records").getJSONObject(0)
							.getString("Status_id");

					JsonResult.put("customerStatus", saveCitizenRes);
					JsonResult.put("isCitizenSuccess", "true");

					//
					updateIqamaStVerifyStatus(request.getParameter("Mobile"), "Passed");
				}
			}

		} catch (Exception e) {

			JsonResult.put("message", e.getMessage());
			JsonResult.put("isCitizenExc", "true");
		}
		return JsonResult;

	}

	private void updateMobileVerifyStatus(String mobileNumber, String status) {

		String res = "";

		HashMap<String, Object> imap = new HashMap();

		imap.put("Mobile", mobileNumber);
		imap.put("ResponseStatus", status);

		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
					.withOperationId("dbxdb_updatecus_mobverify").withRequestParameters(imap).build().getResponse();

		} catch (Exception e) {

			String errorMsg = "Error in NationalIdVerification, updateMobileVerifyStatus : " + e.toString();
			logger.error(errorMsg);
		}

	}

	private void updateCitizenVerifyStatus(String mobileNumber, String status) {

		String res = "";

		HashMap<String, Object> imap = new HashMap();

		imap.put("Mobile", mobileNumber);
		imap.put("ResponseStatus", status);

		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
					.withOperationId("dbxdb_updatecus_citizenverify").withRequestParameters(imap).build().getResponse();

		} catch (Exception e) {

			String errorMsg = "Error in NationalIdVerification, updateCitizenVerifyStatus : " + e.toString();
			logger.error(errorMsg);
		}

	}

	private void updateIqamaVerifyStatus(String mobileNumber, String status) {

		String res = "";

		HashMap<String, Object> imap = new HashMap();

		imap.put("Mobile", mobileNumber);
		imap.put("ResponseStatus", status);

		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
					.withOperationId("dbxdb_updatecus_iqamaverify").withRequestParameters(imap).build().getResponse();

		} catch (Exception e) {

			String errorMsg = "Error in NationalIdVerification, updateIqamaVerifyStatus : " + e.toString();
			logger.error(errorMsg);
		}

	}

	private void updateIqamaStVerifyStatus(String mobileNumber, String status) {

		String res = "";

		HashMap<String, Object> imap = new HashMap();

		imap.put("Mobile", mobileNumber);
		imap.put("ResponseStatus", status);

		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
					.withOperationId("dbxdb_updatecus_iqamaverifystatus").withRequestParameters(imap).build()
					.getResponse();

		} catch (Exception e) {

			String errorMsg = "Error in NationalIdVerification, updateIqamaStVerifyStatus : " + e.toString();
			logger.error(errorMsg);
		}

	}

	private void createCitizenVerifyStatus(String mobileNumber, String nationalID) {

		String res = "";

		HashMap<String, Object> imap = new HashMap();

		imap.put("Mobile", mobileNumber);
		imap.put("IqamaNumber", nationalID);

		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
					.withOperationId("dbxdb_customerverify_status_create").withRequestParameters(imap).build()
					.getResponse();

		} catch (Exception e) {

			String errorMsg = "Error in NationalIdVerification, createCitizenVerifyStatus : " + e.toString();
			logger.error(errorMsg);
		}

	}

	// -------------enroll------------------------------------------------------\\

	private JSONObject enrollCustomer(DataControllerRequest request, DataControllerResponse response) throws Exception {

		boolean istemp = false;
		String tempRegID = "";

		String nationalID = request.getParameter("NationalID");

		Result result = new Result();
		JSONObject JsonResponse = null; // new JSONObject();
		JSONObject jsonSend = null;

		String res = "";
		Map<String, String> customerResponse = new HashMap<>();

		try {

			HashMap<String, Object> imap = new HashMap();
			imap.put("Mobile", request.getParameter("Mobile"));

			OperationData serviceData = request.getServicesManager().getOperationDataBuilder()
					.withServiceId("DBXDBServices").withOperationId("dbxdb_customer_get").build();

			res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
					.withOperationId("dbxdb_gettempRegisterUser").withRequestParameters(imap).build().getResponse();

			JsonResponse = new JSONObject(res);
			if (JsonResponse.getJSONArray("records").length() > 0) {
				Random rand = new Random(); // instance of random class
				int upperbound = 99999;
				// generate random values from 0-24
				int int_random = rand.nextInt(upperbound);

				JSONArray records = (JsonResponse.getJSONArray("records"));
				JSONObject jObj = records.getJSONObject(0);

				HashMap<String, Object> imapReq = new HashMap();

				imapReq.put("Stan", int_random);
				imapReq.put("UserName", nationalID);
				imapReq.put("Password", jObj.getString("Password"));
				imapReq.put("CreatedBy", "SYSTEM");
				imapReq.put("ModifiedBy", "SYSTEM");
				imapReq.put("FirstName", "");
				imapReq.put("LastName", "");
				imapReq.put("MiddleName", "");
				imapReq.put("DateOfBirth", jObj.getString("DateOfBirth"));
				imapReq.put("Ssn", "");
				imapReq.put("CustomerType_id", "TYPE_ID_PROSPECT");
				imapReq.put("coreCustomerId", "000");
				imapReq.put("Type_id", "PHONE,EMAIL");
				imapReq.put("Value", request.getParameter("Mobile") + "," + jObj.getString("Email"));

				tempRegID = jObj.getString("id");

				String createRes = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
						.withOperationId("dbxdb_sp_createcustomer").withRequestParameters(imapReq).build()
						.getResponse();

				JSONObject JsonResponseCreateCus = new JSONObject(createRes);
				if (JsonResponseCreateCus.getJSONArray("records").length() > 0) {
					logger.error("Customer stored procedure executed ===>");
					// result.addParam("ResponseCode",ErrorCode.ERR_60000.toString());
					// result.addParam("Error",ErrorCode.ERR_66000.toString());
					// result.addParam("Message",ErrorCode.ERR_66000.getErrorMessage());
					String cusId = JsonResponseCreateCus.getJSONArray("records").getJSONObject(0)
							.getString("@MaxCustomerId");
					try {
						if (createCoreProspect(request, response, cusId)) {
							logger.error("PRospect customer created 24334=====>");
						} else {
							logger.error("Prospect create failed");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					jsonSend = new JSONObject();
					jsonSend.put("ResponseCode", "00");

					HashMap<String, Object> imapdltReq = new HashMap();

					imapdltReq.put("UserName", request.getParameter("Mobile"));
					String dltRes = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
							.withOperationId("dbxdb_sp_delete_tempregistration").withRequestParameters(imapdltReq)
							.build().getResponse();

				} else {
					// result.addParam("ResponseCode",ErrorCode.ERR_66004.toString());
					// result.addParam("Error",ErrorCode.ERR_66004.toString());
					// result.addParam("Message",ErrorCode.ERR_66004.getErrorMessage());

					jsonSend = new JSONObject();
					jsonSend.put("ResponseCode", "10");

				}

			} else {

				// result.addParam("ResponseCode",ErrorCode.ERR_66004.toString());
				// result.addParam("Message",ErrorCode.ERR_66004.getErrorMessage());
				jsonSend = new JSONObject();
				jsonSend.put("ResponseCode", "20");
			}
		} catch (Exception e) {

			// result.addParam("ResponseCode",ErrorCode.ERR_66004.toString());
			// result.addParam("Error",ErrorCode.ERR_66004.toString());
			// result.addParam("Message",e.getMessage());
			jsonSend = new JSONObject();
			jsonSend.put("ResponseCode", "-11");
			// result.addParam(new Param("Error" ,""+e));
		}
		return jsonSend;

		// return null;
	}

	public boolean createCoreProspect(DataControllerRequest request, DataControllerResponse response, String cusID)
			throws DBPApplicationException, JsonProcessingException {
		HashMap<String, Object> prospectParam = new HashMap();
		String res = null;
		String prosRes = null;

		JSONObject jsonResponse = null;
		if (request.getParameter("NationalID").startsWith("1")) {
			jsonResponse = callYakeenCitizenForCore(request, response);
			logger.error("calling yakeenforcore");
		} else if (request.getParameter("NationalID").startsWith("2")) {
			jsonResponse = callYakeenAlienForCore(request, response);
		}

//		getCitizenAddressInfo
		if (jsonResponse != null) {
			String dateH = jsonResponse.getString("dateOfBirthG");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			prospectParam.put("dateOfBirth", LocalDate.parse(dateH, formatter).format(formatter2));
			prospectParam.put("displayName", jsonResponse.getString("englishFirstName"));
			prospectParam.put("customerName",
					jsonResponse.getString("englishFirstName") + " " + jsonResponse.getString("englishLastName"));
			prospectParam.put("phoneNumber", request.getParameter("Mobile"));
			prospectParam.put("customerMnemonic", getMnemonic());
			prospectParam.put("gender", "");
			prospectParam.put("title", "");
			prospectParam.put("lastName", jsonResponse.getString("englishLastName"));
			prospectParam.put("givenName", jsonResponse.getString("englishFirstName"));
			prospectParam.put("street", "");
			prospectParam.put("street", "");
			prosRes = DBPServiceExecutorBuilder.builder().withServiceId("MoraT24Service")
					.withOperationId("ProspectCustomer").withRequestParameters(prospectParam).build().getResponse();

			JSONObject JsonResponse = new JSONObject(prosRes);
			if (JsonResponse.getString("status").equals("success")) {
				String partyId = JsonResponse.getString("id");
				logger.error("T24 prospect created");
				// update customer
				HashMap<String, Object> cusUpdate = new HashMap();
				cusUpdate.put("id", cusID);
				cusUpdate.put("FirstName", jsonResponse.getString("englishFirstName"));
				cusUpdate.put("MiddleName", jsonResponse.getString("englishSecondName"));
				cusUpdate.put("LastName", jsonResponse.getString("englishLastName"));
				cusUpdate.put("FullName",
						jsonResponse.getString("englishFirstName") + " " + jsonResponse.getString("englishLastName"));
				cusUpdate.put("partyId", partyId);

				HashMap<String, Object> tempupdate = new HashMap();
				String requestJson = new ObjectMapper().writeValueAsString(prospectParam);
				tempupdate.put("partyId", partyId);

//				String reqCsv = prospectParam.get("customerMnemonic").toString() + ","
//						+ prospectParam.get("dateOfBirth").toString() + ","
//						+ prospectParam.get("englishFirstName").toString() + ","
//						+ prospectParam.get("englishLastName").toString() + ","
//						+ prospectParam.get("phoneNumber").toString();
//				tempupdate.put("requestJson", reqCsv);

				res = DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
						.withOperationId("customerProfileUpdate").withRequestParameters(cusUpdate).build()
						.getResponse();
				if (res != null) {
					logger.error("customer profiled updated");
//					String resTemp = DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
//							.withOperationId("dbxdb_tempPros_create").withRequestParameters(tempupdate).build()
//							.getResponse();
//					if (resTemp.isEmpty()) {
//						return false;
//					} else {
//						
//					}
					return true;
				}
			} else {
				return false;
			}
		}
		return false;

	}

	public String getMnemonic() {
		String mnemonic = "";

		Random random = new Random();
		int num = random.nextInt(100000);
		String formatted = String.format("%05d", num);
		mnemonic = "C" + formatted;
		return mnemonic;
	}

	public boolean updateCustomer(DataControllerRequest request, DataControllerResponse response) {

		return false;
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
}
