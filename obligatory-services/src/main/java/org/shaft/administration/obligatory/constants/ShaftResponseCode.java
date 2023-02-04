package org.shaft.administration.obligatory.constants;

public class ShaftResponseCode {
  // #TODO Convert this to enum
  public static String LOGIN_SUCCESS = "S0001";
  public static String USER_REGISTERED = "S0002";
  public static String IDENTITY_FETCHED_SUCCESSFULLY = "S0003";
  public static String CATEGORIES_FETCHED_SUCCESSFULLY = "S0004";
  public static String CATEGORY_SAVED_SUCCESSFULLY = "S0005";
  public static String ITEMS_FETCHED_SUCCESSFULLY = "S0006";
  public static String ITEMS_SAVED_SUCCESSFULLY = "S0007";
  public static String TOKEN_GENERATION_FAILED = "F0001";
  public static String IDENTITY_UPDATE_FAILED = "F0002";
  public static String USER_NOT_FOUND = "F0003";
  public static String UNABLE_TO_FETCH_USER = "F0004";
  public static String BAD_LOGIN_REQUEST = "F0005";
  public static String USER_EXISTS = "F0006";
  public static String SHAFT_IDENTITY_REGISTRATION_ERROR = "F0007";
  public static String SHAFT_REGISTRATION_ERROR = "F0008";
  public static String BAD_REGISTRATION_REQUEST = "F0009";
  public static String SHAFT_FP_UPSERT_FAILED = "F0010";
  public static String SHAFT_FP_TO_I_MAPPING_FAILED = "F0011";
  public static String SHAFT_UNABLE_TO_RETRIEVE_ACCOUNT_META = "F0012";
  public static String ERROR_IN_FETCHING_ACCOUNT_META = "F0013";
  public static String SHAFT_FP_UPSERT_ERROR = "F0014";
  public static String BAD_REQUEST_FOR_FETCHING_ACCOUNT_META = "F0015";
  public static String FAILED_FETCHING_CATEGORIES = "F0016";
  public static String SHAFT_FP_TO_I_FAILED = "F0017";
  public static String SHAFT_CATEGORY_SAVE_EXCEPTION = "F0017";
  public static String SHAFT_ITEMS_SERVICE_UNAVAILABLE = "F0018";
  public static String BAD_BULK_ITEMS_REQUEST = "F0019";
  public static String SHAFT_ITEMS_SAVE_EXCEPTION = "F0020";
}
