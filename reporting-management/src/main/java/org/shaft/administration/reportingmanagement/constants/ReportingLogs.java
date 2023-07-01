package org.shaft.administration.reportingmanagement.constants;

public class ReportingLogs {
  // #TODO Convert this to enum
  public static String JSON_PARSING_FAILED = "Json parsing failed {} for account {}";
  public static String ENCODED_QUERY_EVALUATION_FAILED = "Failed evaluating encoded query {} for account {}";
  public static String ENCODED_QUERY_EVALUATION_EXCEPTION = "Exception evaluating encoded query {} for account {}";
  public static String FAILED_PROCESSING_QUERY = "Failed processing query {} for account {}";
  public static String PROCESSING_QUERY_EXCEPTION = "Exception processing query {} for account {}";
  public static String BAD_QUERY_REQUEST = "Bad request for encoded query {} for account {}";
  public static String BAD_QUERY = "Bad query request {} for account {}";
  public static String EXCEPTION_FETCHING_SEGMENT = "Exception fetching segment {} for account {}";
  public static String SEGMENT_JSON_PARSING_EXCEPTION= "Exception parsing json {} for account {}";
  public static String EXCEPTION_SAVING_SEGMENT= "Exception saving segment {} for account {}";
  public static String BAD_SAVE_SEGMENT_REQUEST = "Bad request for saving segment {} for account {}";
}
