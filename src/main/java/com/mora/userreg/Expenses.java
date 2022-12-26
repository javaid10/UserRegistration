package com.mora.userreg;

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

public class Expenses implements JavaService2{
	
	private static final Logger logger = LogManager.getLogger(Expenses.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		
		Result result=new Result();
		 JSONObject JsonResponse= null; //new JSONObject();
		 
		 String res="";
		 Map<String, String> customerResponse = new HashMap<>();
		 
		
			 
		try {
				
			 
			 JSONArray jarr =new JSONArray(request.getParameter("Expenses"));
			 JsonResponse= new JSONObject();
				for(int i =0; i<jarr.length();i++)
				{
					JSONObject job =jarr.getJSONObject(i);
					HashMap<String,Object> imap =new HashMap();
					imap.put("NationalID", job.getString("NationalID").toString());
					imap.put("ExpenseID", job.getString("ExpenseID").toString());
					imap.put("Amount", job.getString("Amount"));	
				
					res = DBPServiceExecutorBuilder.builder().withServiceId("MooraJsonServices").withOperationId("createCustomerExpenses").withRequestParameters(imap).build().getResponse();

					// JSONObject JsonResponse1= new JSONObject();
					JsonResponse.append("res", new JSONObject(res) );
				
				}
			
				 JSONObject JsonResponse1= new JSONObject();
				for(int j = 0 ; j<JsonResponse.getJSONArray("res").length();j++)
				{
					if(JsonResponse.getJSONArray("res").getJSONObject(j).has("errmsg"))
					{
						ErrorCode.ERR_61002.updateResultObject(result);
						// result.addParam("ResponseCode",ErrorCode.ERR_61002.toString());
						// result.addParam("Message",""+ErrorCode.ERR_61002.getErrorMessage()+" at index "+j);
						break;
					}
					else
					{
						
						result.addParam("ResponseCode",ErrorCode.ERR_60000.toString());
						result.addParam("Message",""+ErrorCode.ERR_60000.getErrorMessage());
					}
					
				}
			
						 
			
		}	
		catch(Exception e) {
			// result.addParam("ResponseCode",ErrorCode.ERR_660021.toString());

		ErrorCode.ERR_660021.updateResultObject(result);
			
			//result.addParam(new Param("Error" ,""+e));
		}
		 
		return result;
	
	//return null;
	}
	

}
