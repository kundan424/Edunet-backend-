import os
path = 'src/test/java/com/edtech/platform/payment/service/StripeWebhookIntegrationTest.java'
with open(path, 'r', encoding='utf-8') as f:
    c = f.read()
c = c.replace('@org.junit.jupiter.api.Disabled\n    @Test\n    void testSuccessfulPaymentAndDuplicateWebhook()', '@Test\n    void testSuccessfulPaymentAndDuplicateWebhook()')
with open(path, 'w', encoding='utf-8') as f:
    f.write(c)
