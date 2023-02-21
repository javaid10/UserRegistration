
package com.mora.userreg;

import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.session.Session;
import com.mora.util.ErrorCode;
//import com.temenos.dbx.party.javaservice.CreatePartyOperation;
import com.temenos.onboarding.crypto.PasswordGenerator;

public class UserRegistration implements JavaService2 {

	private static final Logger logger = LogManager.getLogger(UserRegistration.class);

	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		boolean istemp = false;
		Result result = new Result();
		// new JSONObject();
		
		
		
		Map<String, String> customerResponse = new HashMap<>();

		if (request.getParameter("Mobile").toString().equals("")) {
			ErrorCode.ERR_66005.updateResultObject(result);
		} else if (request.getParameter("Email").toString().equals("")) {
			ErrorCode.ERR_66006.updateResultObject(result);
		} else if (request.getParameter("Password").toString().equals("")) {
			ErrorCode.ERR_66007.updateResultObject(result);
		} else {

			if (!isUserRegistered(request, result)) {
				JSONObject jobj = saveTempUser(request);
				if (jobj.getString("success").equals("true")) {
					result.addParam("ResponseCode", ErrorCode.ERR_60000.toString());
					result.addParam("Message", ErrorCode.ERR_66002.getErrorMessage());
					result.addParam("SecurityKey", jobj.getString("securityKey"));
				} else {
					ErrorCode.ERR_66003.updateResultObject(result);
				}
			}else
				ErrorCode.ERR_66001.updateResultObject(result);

		}
		return result;

	}

	private boolean isUserRegistered(DataControllerRequest request, Result result) {
		boolean flag = false;
		JSONObject jsonResponse = null;
		try {
			HashMap<String, Object> imap = new HashMap();
			imap.put("Mobile", request.getParameter("Mobile"));

			String res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
					.withOperationId("checkCustomerByMobile").withRequestParameters(imap).build().getResponse();

			jsonResponse = new JSONObject(res);

			if (jsonResponse.optInt("opstatus") == 0) {
				if (jsonResponse.getJSONArray("records").length() > 0) {
					flag = true;
				}
			} else
				ErrorCode.ERR_66004.updateResultObject(result);

		} catch (Exception e) {
			logger.error("============  isUserREgistered Exception ========= " + e);
			result.addParam("Error", "" + e);
		}

		return flag;

	}

	private JSONObject saveTempUser(DataControllerRequest request) {
		JSONObject JsonOTP = null;
		JsonOTP = new JSONObject();
		try {
			PasswordGenerator passwordGenerator = new PasswordGenerator();
			String encPassword = passwordGenerator.hashPassword(request.getParameter("Password"));

			Session session = request.getSession();
			if (session != null) {
				logger.error("++++============ Session ID =====" + session.getId());
				logger.error("++++============ Input Mobile Number =====" + request.getParameter("Mobile"));
				session.setAttribute("Session-Temp-Number", request.getParameter("Mobile"));
				session.setAttribute("Session-Encrypt-Password", encPassword);
				session.setAttribute("Session-Normal-Password", request.getParameter("Password"));
				session.setAttribute("Session-Temp-Email", request.getParameter("Email"));
				session.setAttribute("Session-ID", session.getId());
				JsonOTP = sendOTP(request);
				JsonOTP.put("success", "true");
			} else {
				JsonOTP.put("success", "false");
			}

		} catch (Exception e) {
			logger.error("============  saveTempUser Exception ========= " + e);
			JsonOTP.put("success", "false");
		}
		logger.error("============  saveTempUser JsonOTP ========= " + JsonOTP);
		return JsonOTP;
	}

	private JSONObject sendOTP(DataControllerRequest request) {

		String res = "";
		JSONObject jsonResponse = null;
		JSONObject jsonResult = new JSONObject();

		HashMap<String, Object> imap = new HashMap();
		imap.put("baseEncode", true);
		imap.put("Phone", request.getParameter("Mobile"));
		imap.put("Email", request.getParameter("Email"));
		imap.put("otpType", "MORA_REGISTRATION");
		imap.put("Recipient", request.getParameter("Mobile"));
		imap.put("UserName", request.getParameter("Mobile"));

		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("OriginationSmsOTP").withOperationId("sendSMSOTP")
					.withRequestParameters(imap).build().getResponse();

			jsonResponse = new JSONObject(res);
			if (jsonResponse.optString("success").equals("true")) {
				jsonResult.put("message", "success");
				jsonResult.put("securityKey", jsonResponse.getString("securityKey"));
			} else
				jsonResult.put("message", "false");

		} catch (Exception e) {
			logger.error("============  User Registration Exception ========= " + e);
			jsonResult.put("message", "false");

		}
		logger.error("============  sendOTP jsonResult ========= " + jsonResult);
		return jsonResult;

	}

}
