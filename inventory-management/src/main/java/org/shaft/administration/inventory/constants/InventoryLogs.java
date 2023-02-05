package org.shaft.administration.inventory.constants;

public class InventoryLogs {
  // #TODO Convert this to enum
  public static String UNABLE_TO_FETCH_ORDERS = "Unable to fetch orders {} for account {}";
  public static String EXCEPTION_POPULATING_ITEMS_IN_ORDER = "Exception populating items in order {} for account {}";
  public static String PRODUCT_API_FAILED = "Failed to invoke product API {} for account {}";
  public static String UNABLE_TO_FETCH_ORDERS_FOR_I = "Failed to get orders for I {} for account {}";
  public static String INVALID_CART_API_RESPONSE = "Invalid cart API Response {} for account {}";
  public static String PRODUCT_API_FAILED_CODE = "Product api failed with code {} for account {}";
  public static String CART_API_FAILED_CODE = "Cart api failed with code {} for account {}";
  public static String CART_API_ERROR = "Cart API error {} for account {}";
  public static String EXCEPTION_SAVING_ORDER = "Exception saving order : {} , for account : {}";
  public static String BULK_PRODUCT_API_FAILED = "Bulk product API response {} for account {}";
  public static String BULK_PRODUCT_API_INVALID_RESPONSE = "Bulk product API invalid response {} for account {}";
  public static String EXCEPTION_FETCHING_BULK_ITEMS = "Exception fetching bulk items in order {} account {}";
  public static String BULK_PRODUCT_API_ERROR = "Exception in bulk product api {} account {}";
  public static String FAILED_TO_UPDATE_ORDER_STAGE = "Failed to update order stage for account {}";
  public static String EXCEPTION_UPDATING_ORDER_STAGE = "Exception {} updating order stage for account {}";
  public static String BAD_REQUEST_FOR_UPDATING_ORDER_STAGE = "Exception {} updating order stage for account {}";
}
