package org.shaft.administration.usermanagement.constants;

public class Log {
  // #TODO Convert this to enum
  public static String FETCHING_USER_EXCEPTION = "Failed to fetch user from database";
  public static String TOKEN_GENERATION_FAILED = "Exception while generating token : ";
  public static String FP_TO_I_UPSERT_FAILED = "Exception while upserting fpToI : ";
  public static String SAVE_IDENTITY_EXCEPTION = "Exception - {} , while saving identity for account {}";
  public static String SAVE_USER_EXCEPTION = "Exception - {} , while saving user for account {}";
  public static String FAILED_TO_UPDATE_FP = "Failed to update fp";
  public static String FP_TO_I_MAPPING_FAILED = "Failed to check fp exists for i {}";
  public static String UNABLE_TO_FETCH_ACCOUNT_META = "Unable to fetch account meta ";
  public static String ERROR_IN_FETCHING_ACCOUNT_META = "Error while account meta {}";
  public static String ERROR_WHILE_UPDATE_FP = "Error while updating fp {}";
  public static String BAD_REQUEST_ACC_META = "Bad Request for fetching account meta, doesn't contains requestTime";
}
