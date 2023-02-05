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
  public static String REMOVED_CART_ITEMS = "S0008";
  public static String FETCHED_CART_ITEMS = "S0009";
  public static String TRANSACTED_CART_ITEMS = "S0010";
  public static String ADDED_CART_ITEMS = "S0011";
  public static String ORDERS_SAVED = "S0013";
  public static String ORDERS_FETCHED_SUCCESSFULLY = "S0014";
  public static String ORDERS_FETCHED_SUCCESSFULLY_FOR_I = "S0015";
  public static String FETCHED_BULK_ITEMS_IN_ORDER = "S0016";
  public static String ORDER_STAGE_UPDATED = "S0017";
  public static String META_FIELDS_FETCHED = "S0018";
  public static String DASHBOARD_QUERY_PINNED = "S0019";



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
  public static String SHAFT_ITEMS_SERVICE_UNAVAILABLE = "F0018";
  public static String BAD_BULK_ITEMS_REQUEST = "F0019";
  public static String SHAFT_ITEMS_SAVE_EXCEPTION = "F0020";
  public static String SHAFT_FAILED_TO_EMPTY_CART = "F0021";
  public static String FAILED_TO_FETCH_CART_FOR_I = "F0022";
  public static String FAILED_TO_TRANSACT_CART_ITEMS = "F0023";
  public static String FAILED_TO_ADD_CART_ITEM = "F0024";
  public static String UNABLE_TO_FETCH_ORDERS = "F0025";
  public static String NO_ITEMS_IN_ORDER = "F0026";
  public static String INVALID_PRODUCT_API_RESPONSE = "F0027";
  public static String EXCEPTION_POPULATING_ITEMS_IN_ORDER = "F0028";
  public static String PRODUCT_API_ERROR = "F0029";
  public static String UNABLE_TO_FETCH_ORDERS_FOR_I = "F0030";
  public static String NO_I_TO_FETCH_ORDER = "F0031";
  public static String FAILED_TO_REMOVE_CART_ITEMS = "F0032";
  public static String INVALID_CART_API_RESPONSE = "F0033";
  public static String PRODUCT_API_FAILED = "F0034";
  public static String CART_API_FAILED = "F0035";
  public static String CART_API_ERROR = "F0036";
  public static String FAILED_TO_SAVE_ORDER = "F0037";
  public static String BULK_PRODUCT_API_FAILED = "F0038";
  public static String BULK_PRODUCT_API_ERROR = "F0039";
  public static String BULK_PRODUCT_API_INVALID_RESPONSE = "F0040";
  public static String EXCEPTION_WHILE_FETCHING_BULK_ITEMS = "F0041";
  public static String BAD_REQUEST_FOR_BULK_ITEMS = "F0042";
  public static String SHAFT_CATEGORY_SAVE_EXCEPTION = "F0043";
  public static String FAILED_TO_UPDATE_ORDER_STAGE = "F0044";
  public static String EXCEPTION_UPDATING_ORDER_STAGE = "F0045";
  public static String BAD_UPDATE_ORDER_STAGE_REQUEST = "F0046";
  public static String EXCEPTION_FETCHING_META_FIELDS = "F0047";
  public static String BAD_META_REQUEST = "F0048";
  public static String ERROR_CONSTRUCTING_META_FIELDS = "F0049";
  public static String DASHBOARD_QUERIES_LIMIT_EXCEEDED = "F0050";
  public static String FAILED_TO_PINNED_DASHBOARD_QUERY = "F0051";
  public static String INVALID_PIN_TO_DASHBOARD_REQUEST = "F0052";
  public static String EXCEPTION_PINNING_DASHBOARD_QUERY = "F0052";
  public static String EXCEPTION_CONSTRUCTING_PIN_TO_DASHBOARD_QUERY = "F0053";
  public static String ERROR_FETCHING_META_FIELDS = "F0054";
  public static String ERROR_WHILE_FETCHING_BULK_ITEMS = "F0055";
}
