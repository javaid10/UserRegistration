package com.mora.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.json.XML;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class Utilities {
	
	public static JSONObject xmltojson(String xml)
	{

		JSONObject json = XML.toJSONObject(xml); 
		
		return json;
	}

	
	public static JSONObject xmlParser(String xml)
	{
		JSONObject json=new JSONObject();
		try {
		//String xml="<ns1:Yakeen4EjarahFault xmlns:ns1=\"http://yakeen4ejarah.yakeen.elm.com/\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><commonErrorObject xmlns:ns3=\"http://yakeen4ejarah.yakeen.elm.com/\" xsi:type=\"ns3:CommonErrorObject\"><Type>client<\\/Type><ErrorCode>5<\\/ErrorCode><ErrorMessage>The Nin format is not valid<\\/ErrorMessage><\\/commonErrorObject><\\/ns1:Yakeen4EjarahFault>";
		//replace(System.getProperty("file.separator"), "or")
		XmlMapper xmlMapper = new XmlMapper();
		String formated=xml.replace("\\","");
		//System.out.println("formated :: "+formated);
		
		JsonNode jsonNode = xmlMapper.readTree(formated.getBytes());
		ObjectMapper objectMapper = new ObjectMapper();
		String value = objectMapper.writeValueAsString(jsonNode);
		json=new JSONObject(value);
	//	System.out.println("value :: "+value);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		return json;
	}
	
	public static String dateFormat(String date) throws ParseException {
		
		SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
		//String stringDate= DateFor.format(date);
		
		Date date5= DateFor.parse(date);
		System.out.println(date5);
		return date5.toString();
		
		///
		
//		java.util.Date date1 = new Date();
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//		String format = formatter.format(date);
//		System.out.println(format);
		
			
	}
	
//	function convertFromStringToDate(responseDate) 
//	{let dateComponents = responseDate.split('T');let datePieces = dateComponents[0].split("-");let timePieces = dateComponents[1].split(":");return(new Date(datePieces[2], (datePieces[1] - 1), datePieces[0],
//            timePieces[0], timePieces[1], timePieces[2]))}
//	
	public static String splitDate(String dateRec)
	{
		String[] dateParts = dateRec.split("-");
		String year = dateParts[0]; 
		String month = dateParts[1]; 
		String day = dateParts[1]; 
		return day+"/"+month+"/"+year;
	}
	
	public static String splitDateYaqeen(String dateRec)
	{
		if(dateRec!="")
		{
			String[] dateParts = dateRec.split("-");
			String year = dateParts[0]; 
			String month = dateParts[1]; 
			String day = dateParts[1]; 
			return month+"-"+year;
		}
		else {
			return "00-0000";
		}
		
	}
	
}
