import os
path = 'src/test/java/com/edtech/platform/payment/service/StripeWebhookIntegrationTest.java'
with open(path, 'r', encoding='utf-8') as f:
    c = f.read()

old_payload = '''        String payload = "{\\n" +
                "  \\"id\\": \\"evt_test_123\\",\\n" +
                "  \\"type\\": \\"checkout.session.completed\\",\\n" +
                "  \\"data\\": {\\n" +
                "    \\"object\\": {\\n" +
                "      \\"id\\": \\"cs_test_123\\",\\n" +
                "      \\"object\\": \\"checkout.session\\",\\n" +
                "      \\"payment_intent\\": \\"pi_test_123\\"\\n" +
                "    }\\n" +
                "  }\\n" +
                "}";'''

new_payload = '''        String payload = "{\\n" +
                "  \\"id\\": \\"evt_test_123\\",\\n" +
                "  \\"object\\": \\"event\\",\\n" +
                "  \\"api_version\\": \\"2023-10-16\\",\\n" +
                "  \\"type\\": \\"checkout.session.completed\\",\\n" +
                "  \\"data\\": {\\n" +
                "    \\"object\\": {\\n" +
                "      \\"id\\": \\"cs_test_123\\",\\n" +
                "      \\"object\\": \\"checkout.session\\",\\n" +
                "      \\"payment_intent\\": \\"pi_test_123\\"\\n" +
                "    }\\n" +
                "  }\\n" +
                "}";'''

c = c.replace(old_payload, new_payload)

with open(path, 'w', encoding='utf-8') as f:
    f.write(c)
