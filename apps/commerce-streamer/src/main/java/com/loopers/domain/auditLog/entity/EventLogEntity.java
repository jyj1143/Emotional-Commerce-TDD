package com.loopers.domain.auditLog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "event_log")
public class EventLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "group_id", nullable = false)
    private String groupId;

    @Column(name = "partition_key", nullable = false)
    private String partitionKey;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "published_at", nullable = false)
    private ZonedDateTime publishedAt;

    @Column(name = "version")
    private String version;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    private EventLogEntity(String eventId, String eventType, String topic, String groupId, String partitionKey, String payload,
                           ZonedDateTime publishedAt, String version) {
        this.topic = topic;
        this.groupId = groupId;
        this.partitionKey = partitionKey;
        this.eventId = eventId;
        this.eventType = eventType;
        this.payload = payload;
        this.publishedAt = publishedAt;
        this.version = version;
        this.createdAt = ZonedDateTime.now();
    }

    public static EventLogEntity create(String eventId, String eventType, String topic, String groupId, String partitionKey,
                                        String payload,
                                        ZonedDateTime publishedAt, String version) {
        return new EventLogEntity(eventId, topic, groupId, partitionKey, eventType, payload, publishedAt, version);
    }

}
