package com.washplatform.service;

import com.washplatform.entity.PaymentEvidence;

import java.util.List;

public interface PaymentEvidenceService {
    PaymentEvidence save(PaymentEvidence paymentEvidence);
    List<PaymentEvidence> findByOrderId(Integer orderId);
    List<PaymentEvidence> findByUserId(Integer userId);
    PaymentEvidence findById(Integer id);
    void deleteById(Integer id);
}
