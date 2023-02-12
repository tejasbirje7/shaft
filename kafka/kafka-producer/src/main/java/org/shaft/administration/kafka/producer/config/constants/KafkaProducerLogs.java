package org.shaft.administration.kafka.producer.config.constants;

public class KafkaProducerLogs {
  // #TODO Convert this to enum
  public static String ERROR_PUBLISHING_EVENTS_KAFKA = "Error publishing events to producer {}";
  public static String SENDING_MESSAGE_TO_KAFKA_TOPIC = "Sending message='{}' to topic='{}'";
  public static String BOOTSTRAP_SERVERS_INFO = "Bootstrap servers information: {}";
  public static String CLOSING_KAFKA_PRODUCER = "Closing kafka producer!";
  public static String UNABLE_TO_SEND_MESSAGE_TO_TOPIC = "Error while sending message {} to topic {}";
  public static String RECORD_METADATA = "Record Metadata - Topic: {}; Partition {}; Offset {}; Timestamp {}, at time {}";
}
