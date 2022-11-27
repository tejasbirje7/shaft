package org.shaft.administration.eventingestion.entity;

import lombok.Data;

@Data
public class EventAction {
    private Long userId;
    private Long id;
    private String text;
    private Long createdAt;

}
