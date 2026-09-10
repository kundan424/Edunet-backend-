import os
path = 'src/main/java/com/edtech/platform/payment/service/StripeWebhookService.java'
with open(path, 'r', encoding='utf-8') as f:
    c = f.read()

import_gson = "import com.google.gson.JsonObject;\nimport com.google.gson.JsonParser;\n"
if "com.google.gson.JsonObject" not in c:
    c = c.replace("import com.stripe.net.Webhook;", import_gson + "import com.stripe.net.Webhook;")

old_block = '''        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                handleCheckoutSessionCompleted(session);
            } else {
                log.error("Deserialization failed for checkout.session.completed");
            }
        }'''

new_block = '''        if ("checkout.session.completed".equals(event.getType())) {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                handleCheckoutSessionCompleted(session);
            } else {
                log.warn("Deserialization failed for checkout.session.completed. Attempting manual parse...");
                try {
                    String rawJson = event.getDataObjectDeserializer().getRawJson();
                    JsonObject jsonObject = JsonParser.parseString(rawJson).getAsJsonObject();
                    String sessionId = jsonObject.get("id").getAsString();
                    String paymentIntent = jsonObject.has("payment_intent") && !jsonObject.get("payment_intent").isJsonNull() ? jsonObject.get("payment_intent").getAsString() : null;
                    
                    log.info("Manually parsed session ID: {}", sessionId);
                    handleCheckoutSessionCompletedManual(sessionId, paymentIntent);
                } catch (Exception e) {
                    log.error("Failed to manually parse checkout.session.completed", e);
                }
            }
        }'''

c = c.replace(old_block, new_block)

manual_handle = '''
    private void handleCheckoutSessionCompletedManual(String checkoutSessionId, String paymentIntentId) {
        Optional<Payment> optionalPayment = paymentRepository.findByCheckoutSessionId(checkoutSessionId);

        if (optionalPayment.isEmpty()) {
            log.error("Payment not found for checkout session: {}", checkoutSessionId);
            return;
        }

        Payment payment = optionalPayment.get();
        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            log.info("Payment {} already marked as SUCCEEDED", payment.getId());
            return;
        }

        payment.setStatus(PaymentStatus.SUCCEEDED);
        payment.setPaymentIntentId(paymentIntentId);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        try {
            enrollmentService.enrollPaidStudent(payment.getUserId(), payment.getCourseId());
            log.info("Successfully enrolled user {} in course {}", payment.getUserId(), payment.getCourseId());
            
            applicationEventPublisher.publishEvent(new com.edtech.platform.payment.event.PaymentSucceededEvent(
                    payment.getUserId(), payment.getCourseId(), payment.getId(), "Course"));
        } catch (com.edtech.platform.common.exception.EdTechException e) {
            if (e.getErrorCode() == com.edtech.platform.common.exception.ErrorCode.ALREADY_ENROLLED) {
                log.info("User {} is already enrolled in course {}. Payment marked as SUCCEEDED.", payment.getUserId(), payment.getCourseId());
            } else {
                log.error("EdTechException enrolling user {} in course {} after successful payment: {}", payment.getUserId(), payment.getCourseId(), e.getMessage());
                throw e; 
            }
        } catch (Exception e) {
            log.error("Unexpected error enrolling user {} in course {} after successful payment", payment.getUserId(), payment.getCourseId(), e);
            throw new RuntimeException("Failed to create enrollment, triggering rollback", e);
        }
    }
'''

c = c.replace("    private void handleCheckoutSessionCompleted(Session session) {", manual_handle + "\n    private void handleCheckoutSessionCompleted(Session session) {")

with open(path, 'w', encoding='utf-8') as f:
    f.write(c)
