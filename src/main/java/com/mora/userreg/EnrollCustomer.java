package com.mora.userreg;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.konylabs.middleware.api.OperationData;
import com.konylabs.middleware.api.ServiceRequest;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.mora.util.ErrorCode;

public class EnrollCustomer implements JavaService2 {

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		// TODO Auto-generated method stub
		
		Result result=new Result();
		// result.addParam("ResponseCode",ErrorCode.ERR_66004.toString());
result.addParam("Message","This Service is not in use, please use National ID Verification directly");
return result;
	}

//	@Override
//	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
//			DataControllerResponse response) throws Exception {
//		boolean istemp=false;
//		String tempRegID="";
//		Result result=new Result();
//		 JSONObject JsonResponse= null; //new JSONObject();
//		 
//		 String res="";
//		 Map<String, String> customerResponse = new HashMap<>();
//		try {
//			
//			
//			HashMap<String,Object> imap =new HashMap();
//			imap.put("Mobile", request.getParameter("Mobile"));
//			
//			 OperationData serviceData = request.getServicesManager()
//	 				.getOperationDataBuilder()
//	 				.withServiceId("DBXDBServices")
//	 				.withOperationId("dbxdb_customer_get")
//	 				.build();
//			 
//			  res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices").withOperationId("dbxdb_gettempRegisterUser").withRequestParameters(imap).build().getResponse();
//			 
//			  JsonResponse= new JSONObject(res);
//			 if(JsonResponse.getJSONArray("records").length()>0)
//			 {
//				 Random rand = new Random(); //instance of random class
//			      int upperbound = 99999;
//			        //generate random values from 0-24
//			      int int_random = rand.nextInt(upperbound); 
//				 
//				 JSONArray records =(JsonResponse.getJSONArray("records"));
//				 JSONObject jObj =records.getJSONObject(0);
//				 
//				 HashMap<String,Object> imapReq =new HashMap();
//				 
//				 imapReq.put("Stan", int_random);
//				 imapReq.put("UserName", jObj.getString("UserName"));
//				 imapReq.put("Password", jObj.getString("Password"));
//				 imapReq.put("CreatedBy", "SYSTEM");
//				 imapReq.put("ModifiedBy", "SYSTEM");
//				 imapReq.put("FirstName", "");
//				 imapReq.put("LastName", "");
//				 imapReq.put("MiddleName", "");
//				 imapReq.put("DateOfBirth", jObj.getString("DateOfBirth"));
//				 imapReq.put("Ssn", "");
//				 imapReq.put("CustomerType_id", "TYPE_ID_PROSPECT");
//				 imapReq.put("coreCustomerId", "000");
//				 imapReq.put("Type_id", "PHONE,EMAIL");
//				 imapReq.put("Value", jObj.getString("UserName")+","+jObj.getString("Email"));
//				 
//				 tempRegID=jObj.getString("id");
//				
//				 String createRes = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices").withOperationId("dbxdb_sp_createcustomer").withRequestParameters(imapReq).build().getResponse();
//
//				JSONObject JsonResponseCreateCus= new JSONObject(createRes);
//				 if(JsonResponseCreateCus.getJSONArray("records").length()>0)
//				 {
//					 result.addParam("ResponseCode",ErrorCode.ERR_60000.toString());
//					//	result.addParam("Error",ErrorCode.ERR_66000.toString());
//						 result.addParam("Message",ErrorCode.ERR_66000.getErrorMessage());
//						 
//					 HashMap<String,Object> imapdltReq =new HashMap();
//					 
//					 imapdltReq.put("UserName", request.getParameter("UserName"));
//				 String dltRes = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices").withOperationId("dbxdb_sp_delete_tempregistration").withRequestParameters(imapdltReq).build().getResponse();
// 
//				 }
//				 else
//				 {
//					 result.addParam("ResponseCode",ErrorCode.ERR_66004.toString());
//				//	result.addParam("Error",ErrorCode.ERR_66004.toString());
//					result.addParam("Message",ErrorCode.ERR_66004.getErrorMessage());  
//				 }
//				 
//				 
//			 }
//			 else
//			 {
//				 
//				 result.addParam("ResponseCode",ErrorCode.ERR_66004.toString());
//				//	result.addParam("Error",ErrorCode.ERR_66004.toString());
//					result.addParam("Message",ErrorCode.ERR_66004.getErrorMessage()); 
//			 }
//		}	
//		catch(Exception e) {
//			
//			result.addParam("ResponseCode",ErrorCode.ERR_66004.toString());
//		//	result.addParam("Error",ErrorCode.ERR_66004.toString());
//			result.addParam("Message",e.getMessage()); 
//			//result.addParam(new Param("Error" ,""+e));
//		}
//		return result;
//	
//	//return null;
//	}

}
