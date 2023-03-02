package com.mora.util;

import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public enum ErrorCode {
    ERR_60000(60000, " Success"),
    ERR_61002(61002, " Record Not Inserted"),
    ERR_61004(61004, " Record Not Found"),
    ERR_66001(66001, "Mobile Number you have provided is already registered"),
    ERR_66000(66000, "Mobile Number has registered successfuly"),
    ERR_66002(66002, "OTP has sent to provided Mobile Number"),
    ERR_66003(66003, "Error Occured while sending OTP"),
    ERR_66004(66004, "Error Occured while registration of Mobile Number"),

    ERR_66005(66005, "Mobile Number is required"),
    ERR_66006(66006, "Email is required"),
    ERR_66007(66007, "Password is required"),

    ERR_66008(66008, "ID is required"),
    ERR_66009(66009, "Date of Birth is required"),
    ERR_660010(660010, "Loan Purpose is required"),
    ERR_660011(660011, "IBAN is required"),
    ERR_660012(660012, "National ID you have provided is already registered, please Login with Registered Mobile Number"),

    ERR_660013(660013, "Mobile Number is not registered with National ID"),
    ERR_660014(660014, "Error Occured while Mobile Number verification"),
    ERR_660078(660078, "prospect customer creation failed"),
    ERR_660015(660015, "The ID Is Not Found at NIC"), // 001
    ERR_660016(660016, "The Iqama Number Is Not Found at NIC"), // 002
    ERR_660017(660017, "Not Authorized"), // 003
    ERR_660018(660018, "The National ID format is not valid"), // 005
    ERR_660019(660019, "Date Of Birth does not match NIC records"), // 006, 008, 009
    ERR_660020(660020, "The Iqama Number format is not valid"), // 007
    ERR_660021(660021, "Error Occured while National ID verification"), // 100
    ERR_660022(660022, "Customer Life status is not Alive, Your Registration Process has Canceled"),
    ERR_660023(660023, "Customer Legal status is not Good, Your Registration Process has Suspended"),
    ERR_660024(660024, "ChargeCode is require"),
    ERR_660025(660025, "Product is require"),
    ERR_660026(660026, "Age is require"),
    ERR_660027(660027, "Error Occurred while performing Knockout stage"),
    // Error Occured while registration of Mobile Number
    ERR_660028(660028, "Knockout Failed, Application has Declined"),
    ERR_660029(660029, "Knockout Success"),
    ERR_660030(660030, "Application Successfully created. Application ID: "),
    ERR_660031(660031, "Product ID is require"),
    ERR_660032(660032, "National ID/Iqama is required"),
    ERR_660033(660033, "Employement Status is not Active"),
    ERR_660034(660034, "Provided Mobile Number is not Registered"),

    ERR_660035(660035, "Loan Amount is required"),
    ERR_660036(660036, "Tenor is required"),
    ERR_660037(660037, "Monthly Repayment is required"),
    ERR_660038(660038, "Approx is required"),
    ERR_660039(660039, "Voucher ID is required"),
    ERR_660040(660040, "Provided National ID is not Found in the System"),
    ERR_660041(660041, "National Address Check Failed"),
    ERR_660042(660042, "ApplicationID is required"),
    ERR_660043(660043, "Login Failed"),

    ERR_660044(660044, "AML Check failed"),
    ERR_660045(660045, "T24 service failed creating customer");

    private int errCode;

    private String errMsg;

    ErrorCode(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public int getErrorCode() {
        return this.errCode;
    }

    public String getErrorMessage() {
        return this.errMsg;
    }

    public String getErrorCodeAsString() {
        return String.valueOf(this.errCode);
    }

    public void appendToErrorMessage(String stringToBeAppended) {
        this.errMsg += ". " + stringToBeAppended;
    }

    public Result constructResultObject() {
        Result result = new Result();
        return addAttributesToResultObject(result);
    }

    public Result updateResultObject(Result result) {
        if (result == null)
            return constructResultObject();
        return addAttributesToResultObject(result);
    }

//  public FabricResponseManager updateResultObject(FabricResponseManager fabricResponseManager) {
//    return addAttributesToResultObject(fabricResponseManager);
//  }

    private Result addAttributesToResultObject(Result result) {
        result.addParam(new Param("dbpErrCode", getErrorCodeAsString()));
        result.addParam(new Param("dbpErrMsg", this.errMsg));
        return result;
    }

//  private FabricResponseManager addAttributesToResultObject(FabricResponseManager result) {
//    JsonObject responseJson = new JsonObject();
//    responseJson.addProperty("dbpErrCode", getErrorCodeAsString());
//    responseJson.addProperty("dbpErrMsg", getErrorMessage());
//    result.getPayloadHandler().updatePayloadAsJson((JsonElement)responseJson);
//    return result;
//  }

    private Result addAttributesToResultObject(Result result, String customErrorMessageToBeAppended) {
        result.addParam(new Param("dbpErrCode", getErrorCodeAsString()));
        result.addParam(new Param("dbpErrMsg", this.errMsg + ". " + customErrorMessageToBeAppended));
        return result;
    }
}
