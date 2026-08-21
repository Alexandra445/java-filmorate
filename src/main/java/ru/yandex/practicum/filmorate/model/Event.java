package ru.yandex.practicum.filmorate.model;

import java.util.Objects;

@com.fasterxml.jackson.annotation.JsonAutoDetect(fieldVisibility = com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY)
public class Event {

    private Integer eventId;
    private Long timestamp;
    private Integer userId;
    private EventType eventType;
    private Operation operation;
    private Integer entityId;

    public Event() {
    }

    public Event(Integer eventId, Long timestamp, Integer userId, EventType eventType, Operation operation, Integer entityId) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("eventId")
    public Integer getEventId() {
        return eventId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("userId")
    public Integer getUserId() {
        return userId;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("eventType")
    public EventType getEventType() {
        return eventType;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("operation")
    public Operation getOperation() {
        return operation;
    }

    @com.fasterxml.jackson.annotation.JsonProperty("entityId")
    public Integer getEntityId() {
        return entityId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(eventId, event.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
}