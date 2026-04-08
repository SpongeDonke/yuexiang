package com.washplatform.controller;

import com.washplatform.entity.PaymentEvidence;
import com.washplatform.service.PaymentEvidenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment-evidence")
public class PaymentEvidenceController {

    @Autowired
    private PaymentEvidenceService paymentEvidenceService;

    @PostMapping("/upload")
    public PaymentEvidence uploadPaymentEvidence(
            @RequestParam("orderId") Integer orderId,
            @RequestParam("userId") Integer userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/payment-evidence/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            String filePath = uploadDir + uniqueFilename;

            file.transferTo(new File(filePath));

            PaymentEvidence paymentEvidence = new PaymentEvidence();
            paymentEvidence.setOrderId(orderId);
            paymentEvidence.setUserId(userId);
            paymentEvidence.setFilePath(filePath);
            paymentEvidence.setFileName(originalFilename);
            paymentEvidence.setFileSize((int) file.getSize());
            paymentEvidence.setFileType(file.getContentType());

            return paymentEvidenceService.save(paymentEvidence);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/order/{orderId}")
    public List<PaymentEvidence> findByOrderId(@PathVariable Integer orderId) {
        return paymentEvidenceService.findByOrderId(orderId);
    }

    @GetMapping("/user/{userId}")
    public List<PaymentEvidence> findByUserId(@PathVariable Integer userId) {
        return paymentEvidenceService.findByUserId(userId);
    }

    @GetMapping("/{id}")
    public PaymentEvidence findById(@PathVariable Integer id) {
        return paymentEvidenceService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        paymentEvidenceService.deleteById(id);
    }
}
