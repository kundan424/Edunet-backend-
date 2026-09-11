package com.edtech.platform.payment.repository;

import com.edtech.platform.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    boolean existsByCourseId(UUID courseId);
    Optional<Payment> findByCheckoutSessionId(String checkoutSessionId);
    List<Payment> findByUserId(UUID userId);
}
