

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
import com.mora.util.ErrorCode;
//import com.temenos.dbx.party.javaservice.CreatePartyOperation;
import com.temenos.onboarding.crypto.PasswordGenerator;

public class UserRegistration implements JavaService2 {

	private static final Logger logger = LogManager.getLogger(UserRegistration.class);

	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		boolean istemp=false;
		Result result=new Result();
		 JSONObject JsonResponse= null; //new JSONObject();
		 
		 String res="";
		 Map<String, String> customerResponse = new HashMap<>();
		 
		 if(request.getParameter("Mobile").toString().equals(""))
		 {
			ErrorCode.ERR_66005.updateResultObject(result);
			//  result.addParam("ResponseCode",ErrorCode.ERR_66005.toString());
			// //	result.addParam("Error",ErrorCode.ERR_66005.toString());
			// 	 result.addParam("Message",ErrorCode.ERR_66005.getErrorMessage());  
		 }
		 else if(request.getParameter("Email").toString().equals(""))
		 {
			ErrorCode.ERR_66006.updateResultObject(result);
			//  result.addParam("ResponseCode",ErrorCode.ERR_66006.toString());
			// //	result.addParam("Error",ErrorCode.ERR_66006.toString());
			// 	 result.addParam("Message",ErrorCode.ERR_66006.getErrorMessage()); 	 
		 }
		 else if(request.getParameter("Password").toString().equals(""))
		 {
			ErrorCode.ERR_66007.updateResultObject(result);
			//  result.addParam("ResponseCode",ErrorCode.ERR_66007.toString());
			// //	result.addParam("Error",ErrorCode.ERR_66007.toString());
			// 	 result.addParam("Message",ErrorCode.ERR_66007.getErrorMessage()); 		 
		 }
		 else
		 {
			 
 
		try {
			
			
			HashMap<String,Object> imap =new HashMap();
			imap.put("Mobile",request.getParameter("Mobile"));
//			 OperationData serviceData = request.getServicesManager()
//	 				.getOperationDataBuilder()
//	 				.withServiceId("DBXDBServices")
//	 				.withOperationId("dbxdb_customer_get")
//	 				.build();
			 
			  res = DBPServiceExecutorBuilder.builder().withServiceId("MooraJsonServices").withOperationId("checkCustomerByMobile").withRequestParameters(imap).build().getResponse();
			 

			  JsonResponse= new JSONObject(res);
			 if(JsonResponse.getJSONArray("customer").length()>0)
			 {
				 ErrorCode.ERR_66001.updateResultObject(result);
				//  result.addParam("ResponseCode",ErrorCode.ERR_66001.toString());
				// //result.addParam("Error",ErrorCode.ERR_66001.toString());
				//  result.addParam("Message",ErrorCode.ERR_66001.getErrorMessage()); 
			 }
			 else
			 {
				 //result.addParam("ResponseCode",ErrorCode.ERR_66000.toString());
				// result.addParam("Message","You can registered this user"); 
				
						 JSONObject jobj=saveTempUser(request);
				 
				 if(jobj.getString("success").equals("true"))
				 {
					 result.addParam("ResponseCode",ErrorCode.ERR_60000.toString());
					 result.addParam("Message",ErrorCode.ERR_66002.getErrorMessage());  
					 result.addParam("SecurityKey",jobj.getString("securityKey"));  
				 }
				 else
				 {
					ErrorCode.ERR_66003.updateResultObject(result);
				// 	 result.addParam("ResponseCode",ErrorCode.ERR_66003.toString());
				// //	result.addParam("Error",ErrorCode.ERR_66003.toString());
				// 	 result.addParam("Message",ErrorCode.ERR_66003.getErrorMessage()+" "+jobj.getString("message"));  
				 }
			 }
		}	
		catch(Exception e) {
			result.addParam("Error",""+e);
			//result.addParam(new Param("Error" ,""+e));
		}
		 }
		return result;
	
	//return null;
	}
	
	private JSONObject saveTempUser(DataControllerRequest request)
	{
		String res="";
		 JSONObject JsonResponse= null;
		 JSONObject JsonOTP= null;
		boolean flag=false;
		
		PasswordGenerator passwordGenerator=new PasswordGenerator();
		
		String encPassword=passwordGenerator.hashPassword(request.getParameter("Password"));
		
		HashMap<String,Object> imap =new HashMap();
		imap.put("userName", request.getParameter("Mobile"));
		imap.put("password", encPassword);
		imap.put("mobile", request.getParameter("Mobile"));
		imap.put("dob", ""); //request.getParameter("DOB")
		imap.put("email", request.getParameter("Email"));
		imap.put("createdby", "SYSTEM");
		
		try {
		  res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices").withOperationId("dbxdb_sp_tempRegisterUser").withRequestParameters(imap).build().getResponse();
		
		  
		  JsonResponse= new JSONObject(res);
		  if(JsonResponse.getJSONArray("records").length()>0)
			 {
			
//			  /flag=true;
			  JsonOTP=sendOTP(request);
			 
			 }
			 else
			 {
				 flag=false;
				 JsonOTP=new JSONObject();
				 JsonOTP.put("success", "false");
			 }
		  
		}
		catch(Exception e) {
				//result.addParam("Error",""+e);
				//result.addParam(new Param("Error" ,""+e));
			JsonOTP=new JSONObject();
			 JsonOTP.put("success", "false");
			 JsonOTP.put("message",e.getMessage());
			}
		return JsonOTP;
	}
	
	private JSONObject sendOTP(DataControllerRequest request)
	{
		
		boolean flag=false;
		
		String res="";
		String securityKey="";
		 JSONObject JsonResponse= null;
		 JSONObject JsonResult= new JSONObject();
		
		HashMap<String,Object> imap =new HashMap();
		imap.put("baseEncode", true);
		imap.put("Phone", request.getParameter("Mobile"));
		imap.put("Email", request.getParameter("Email"));
		imap.put("otpType","MORA_REGISTRATION");
		// imap.put("Body", "");
		imap.put("Recipient", request.getParameter("Mobile"));
		imap.put("UserName", request.getParameter("Mobile"));
		
		// imap.put("SenderID", "IJARAH");
		// imap.put("serviceKey", "");
		// imap.put("statusCallback", "sent");
		// imap.put("async",false);
		
		// imap.put("AppSid","5LSk7BMeHH39VvwRA3TBr0BbdORaMN");
		// imap.put("responseType","JSON");
		
		logger.error("============  Inside sendOTP ========= ");
		try {
		  res = DBPServiceExecutorBuilder.builder().withServiceId("OriginationSmsOTP").withOperationId("sendSMSOTP").withRequestParameters(imap).build().getResponse();
		
		  logger.error("++++============ Inputparams ====="+imap);  
		  JsonResponse= new JSONObject(res);
		  if(JsonResponse.getString("success").equals("true"))
			 {
			  JsonResult.put("securityKey", JsonResponse.getString("securityKey"));
			  JsonResult.put("message", JsonResponse.getString("message"));
			  JsonResult.put("success", JsonResponse.getString("success"));
			  
			 }
			 else
			 {
				 JsonResult.put("securityKey", JsonResponse.getString("securityKey"));
				  JsonResult.put("message", JsonResponse.getString("message"));
				  JsonResult.put("success", JsonResponse.getString("success"));
			 }
		  
		}
		catch(Exception e) {
			JsonResult.put("securityKey", "");
			  JsonResult.put("message",e.getMessage());
			  JsonResult.put("success", "false");
			}
		return JsonResult;
		
	}

//	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
//			DataControllerResponse response) throws Exception {
//		
//		Result result=new Result();
//		try {
//			
//			HashMap<String,Object> imap =new HashMap();
//			imap.put("accId", inputArray);
//			 OperationData serviceData = request.getServicesManager()
//	 				.getOperationDataBuilder()
//	 				.withServiceId("AccServicePV")
//	 				.withOperationId("getAccountDetailsPV")
//	 				.build();
//			 ServiceRequest serviceRequest = request.getServicesManager().getRequestBuilder (serviceData)
//						.withInputs(imap)
//						.withHeaders(request.getHeaderMap())
//						.build();
//			 result = serviceRequest.invokeServiceAndGetResult();
//		}
//		catch(Exception e) {
//			result.addParam(new Param("Error" ,""+e));
//		}
//		return result;
//	
//	//return null;
//	}

}
