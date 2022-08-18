package com.decagon.decapay.constants;

public class ResponseMessageConstants {
    //SUCCESS MESSAGES
    public static final String RESOURCE_CREATED_SUCCESSFULLY = "Resource Created Successfully";
    public static final String RESOURCE_RETRIEVED_SUCCESSFULLY = "Resource Retrieved Successfully";
    public static final String RESOURCE_UPDATED_SUCCESSFULLY = "Resource Updated Successfully";
    public static final String RESOURCE_DEACTIVATED_SUCCESSFULLY = "Resource Deactivated Successfully";
    public static final String RESOURCE_ACTIVATED_SUCCESSFULLY = "Resource Activated Successfully";
    public static final String EMAIL_SENT_SUCCESSFULLY = "Email Sent Successfully";
    public static final String FORGOT_PASSWORD_INITIATED_SUCCESSFULLY = "Forgot Password Initiated Successfully";
    public static final String PASSWORD_CHANGED_SUCCESSFULLY = "Password Changed Successfully";
    public static final String PASSWORD_CREATED_SUCCESSFULLY = "Password Created Successfully";
    public static final String USER_SUCCESSFULLY_REGISTERED = "User successfully registered";
    public static final String SIGN_IN_SUCCESSFULLY = "Sign In Successfully";
    public static final String PASSWORD_RESET_CODE_VERIFIED_SUCCESSFULLY = "Password Reset Code Verified Successfully";
    public static final String SIGN_OUT_SUCCESSFULLY = "Sign Out Successfully";
    public static final String RETURN_BUDGET_LISTS_SUCCESSFULLY = "Returns list of a user's budget successfully if the user has budget " +
                                                                  "or empty list if the user does not have budget";

    public static final String BUDGET_SUCCESSFULLY_CREATED = "Budget successfully created";
    public static final String BUDGET_CATEGORY_SUCCESSFULLY_CREATED = "Budget category successfully created";
    public static final String BUDGET_UPDATED_SUCCESSFULLY = "Budget Updated Successfully";
    public static final String BUDGET_CATEGORY_UPDATED_SUCCESSFULLY = "Budget Category Updated Successfully";
    public static final String LINE_ITEM_CREATED_SUCCESSFULLY = "Budget Line Item Created Successfully";

    //FAILURE MESSAGES
    public static final String UNABLE_TO_PROCESS_TEMPLATE = "Unable to process template";
    public static final String INVALID_REQUEST = "Invalid Request!";
    public static final String NOT_AUTHORIZED = "Not Authorized!";
    public static final String REQUEST_FORBIDDEN = "Request Forbidden!";
    public static final String NOT_FOUND = "Not Found!";
    public static final String USER_NOT_FOUND = "User Not Found!";
    public static final String UNABLE_TO_SEND_EMAIL = "Unable to send email";
    public static final String EMAIL_IS_EMPTY = "Email is Empty";
    
    public static final String USER_EMAIL_ALREADY_EXISTS = "User email already exists";

    public static final String NO_RESET_CODE_OR_EMAIL_PROVIDED = "No Reset Code/Email Provided";
    public static final String PASSWORD_RESET_CODE_DOES_NOT_EXIST = "Reset Code Does Not Exist";
    public static final String INVALID_PASSWORD_RESET_CODE = "Reset Code Is Invalid";
    public static final String PASSWORD_RESET_CODE_HAS_EXPIRED = "Reset Code Has Expired";
    public static final String PASSWORD_RESET_CODE_ALREADY_USED = "Reset Code Already Used";
    public static final String PASSWORD_RESET_TOKEN_HAS_EXPIRED = "Reset Token Has Expired";
    public static final String PASSWORD_RESET_CODE_IS_UNVERIFIED = "Reset Code Is Unverified";
    public static final String TOKEN_DOES_NOT_EXIST = "Token Does Not Exist";
    public static final String PASSWORD_SHOULD_NOT_BE_EMPTY = "Password Should Not be Empty";
    public static final String PASSWORDS_DONT_MATCH = "Passwords Dont Match";
    public static final String UNEXPECTED_VALUE = "Unexpected value: ";
    public static final String BUDGET_FETCHED_SUCCESSFULLY = "Budget fetched sucessfully";
}
