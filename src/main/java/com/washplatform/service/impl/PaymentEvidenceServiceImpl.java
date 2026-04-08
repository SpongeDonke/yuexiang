package com.washplatform.service.impl;

import com.washplatform.entity.PaymentEvidence;
import com.washplatform.repository.PaymentEvidenceRepository;
import com.washplatform.service.PaymentEvidenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;

@Service
public class PaymentEvidenceServiceImpl implements PaymentEvidenceService {

    @Autowired
    private PaymentEvidenceRepository paymentEvidenceRepository;

    @Override
    public PaymentEvidence save(PaymentEvidence paymentEvidence) {
        if (paymentEvidence.getCreatedAt() == null) {
            paymentEvidence.setCreatedAt(new Date());
        }
        return paymentEvidenceRepository.save(paymentEvidence);
    }

    @Override
    public List<PaymentEvidence> findByOrderId(Integer orderId) {
        return paymentEvidenceRepository.findByOrderId(orderId);
    }

    @Override
    public List<PaymentEvidence> findByUserId(Integer userId) {
        return paymentEvidenceRepository.findByUserId(userId);
    }

    @Override
    public PaymentEvidence findById(Integer id) {
        return paymentEvidenceRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Integer id) {
        paymentEvidenceRepository.deleteById(id);
    }
}
