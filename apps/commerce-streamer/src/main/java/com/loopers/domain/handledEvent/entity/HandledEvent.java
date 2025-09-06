package com.loopers.domain.handledEvent.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "handled_event", uniqueConstraints = {
    @UniqueConstraint(name = "uk_event_consumer", columnNames = {"event_id", "group_id"})
})
public class HandledEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "group_id", nullable = false)
    private String groupId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "published_at", nullable = false)
    private ZonedDateTime publishedAt;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    private HandledEvent(String eventId, String groupId, String payload, ZonedDateTime publishedAt) {
        this.eventId = eventId;
        this.groupId = groupId;
        this.payload = payload;
        this.publishedAt = publishedAt;
        this.createdAt = ZonedDateTime.now();
    }

    public static HandledEvent create(String eventId, String groupId, String payload, ZonedDateTime publishedAt) {
        return new HandledEvent(eventId, groupId, payload, publishedAt);
    }
}
