package com.washplatform.repository;

import com.washplatform.entity.PaymentEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentEvidenceRepository extends JpaRepository<PaymentEvidence, Integer> {
    List<PaymentEvidence> findByOrderId(Integer orderId);
    List<PaymentEvidence> findByUserId(Integer userId);
}
