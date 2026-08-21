package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Event {
    private final Integer eventId;
    private final Long timestamp;
    private final Integer userId;
    private final EventType eventType;
    private final Operation operation;
    private final Integer entityId;

    @JsonCreator
    public Event(
            @JsonProperty("eventId") Integer eventId,
            @JsonProperty("timestamp") Long timestamp,
            @JsonProperty("userId") Integer userId,
            @JsonProperty("eventType") EventType eventType,
            @JsonProperty("operation") Operation operation,
            @JsonProperty("entityId") Integer entityId
    ) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Integer getUserId() {
        return userId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Operation getOperation() {
        return operation;
    }

    public Integer getEntityId() {
        return entityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Event event = (Event) o;
        return Objects.equals(eventId, event.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
}
