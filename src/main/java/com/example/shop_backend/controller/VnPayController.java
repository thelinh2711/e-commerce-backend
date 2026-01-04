
package com.example.shop_backend.controller;

import com.example.shop_backend.dto.request.PaymentRequest;
import com.example.shop_backend.model.Order;
import com.example.shop_backend.model.Payment;
import com.example.shop_backend.model.enums.OrderStatus;
import com.example.shop_backend.model.enums.PaymentStatus;
import com.example.shop_backend.repository.OrderRepository;
import com.example.shop_backend.repository.PaymentRepository;
import com.example.shop_backend.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class VnPayController {

    private final VnPayService vnPayService;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public VnPayController(VnPayService vnPayService,
                           PaymentRepository paymentRepository,
                           OrderRepository orderRepository) {
        this.vnPayService = vnPayService;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody PaymentRequest request,
                                           HttpServletRequest httpRequest) throws Exception {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String clientIp = httpRequest.getRemoteAddr();

        String paymentUrl = vnPayService.createPaymentUrl(request, clientIp);

        // Lấy txnRef từ URL
        String txnRef = paymentUrl.split("vnp_TxnRef=")[1].split("&")[0];

        // Kiểm tra xem Order đã có Payment chưa
        Payment payment = order.getPayment();

        if (payment == null) {
            // Chưa có payment → Tạo mới
            payment = Payment.builder()
                    .order(order)
                    .amount(order.getTotalAmount())
                    .paymentMethod(order.getPaymentMethod())
                    .status(PaymentStatus.UNPAID)
                    .transactionId(txnRef)
                    .build();
            paymentRepository.save(payment);
        } else {
            // Đã có payment → Cập nhật transaction_id và status
            payment.setTransactionId(txnRef);
            payment.setStatus(PaymentStatus.UNPAID);
            payment.setAmount(order.getTotalAmount());
            paymentRepository.save(payment);
        }

        return ResponseEntity.ok(Map.of(
                "paymentUrl", paymentUrl,
                "paymentId", payment.getId(),
                "transactionId", txnRef,
                "message", "Copy paymentUrl vào trình duyệt để thanh toán"
        ));
    }

    /**
     * Endpoint này nhận callback từ VNPay sau khi user thanh toán
     * VNPay sẽ gọi GET với các query params
     */
    @GetMapping("/return")
    public ResponseEntity<?> vnpReturn(@RequestParam Map<String, String> params) throws Exception {
        System.out.println("===== VNPAY CALLBACK RECEIVED =====");
        System.out.println("All params: " + params);

        // Kiểm tra chữ ký trả về
        boolean valid = vnPayService.validateReturn(params);

        if (!valid) {
            System.out.println("❌ INVALID SIGNATURE");
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid checksum"
            ));
        }

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");
        String bankCode = params.get("vnp_BankCode");

        Payment payment = paymentRepository.findByTransactionId(txnRef);
        if (payment == null) {
            System.out.println("❌ PAYMENT NOT FOUND: " + txnRef);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Payment not found"
            ));
        }

        if ("00".equals(responseCode)) {
            System.out.println("✅ PAYMENT SUCCESS");
            payment.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);

            Order order = payment.getOrder();
            //order.setStatus(OrderStatus.CONFIRMED);
            //orderRepository.save(order);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Payment success",
                    "orderId", order.getId(),
                    "amount", payment.getAmount(),
                    "transactionId", txnRef,
                    "vnpayTransactionNo", transactionNo,
                    "bankCode", bankCode
            ));
        }

        System.out.println("❌ PAYMENT FAILED - Code: " + responseCode);
        return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Payment failed",
                "responseCode", responseCode,
                "transactionId", txnRef
        ));
    }

    /**
     * API để kiểm tra trạng thái thanh toán theo transactionId
     * Dùng để test sau khi thanh toán
     */
    @GetMapping("/status/{transactionId}")
    public ResponseEntity<?> checkPaymentStatus(@PathVariable String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "status", payment.getStatus(),
                "amount", payment.getAmount(),
                "orderId", payment.getOrder().getId(),
                "orderStatus", payment.getOrder().getStatus(),
                "transactionId", payment.getTransactionId(),
                "paymentMethod", payment.getPaymentMethod()
        ));
    }
}
