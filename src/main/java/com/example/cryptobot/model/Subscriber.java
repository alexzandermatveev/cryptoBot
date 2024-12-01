package com.example.cryptobot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "subscribers")
public class Subscriber {
    @Id
    private UUID uuid;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "price_subscribed_on", nullable = true)
    private Double priceSubscribedOn;
    @Column(name = "last_notified")
    private Instant lastNotified;

}
