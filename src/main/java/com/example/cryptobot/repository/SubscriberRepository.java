package com.example.cryptobot.repository;

import com.example.cryptobot.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, UUID> {
    Boolean existsByUserId(Long userId);
    Subscriber findByUserId(Long userId);
    List<Subscriber> findAllByPriceSubscribedOnLessThanEqual(Double priceSubscribedOn);

}
