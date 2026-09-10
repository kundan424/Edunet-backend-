import com.stripe.model.Event;
import com.stripe.net.Webhook;

public class TestStripe {
    public static void main(String[] args) throws Exception {
        String payload = "{\"id\": \"evt_test_123\",\"type\": \"checkout.session.completed\",\"data\": {\"object\": {\"id\": \"cs_test_123\",\"object\": \"checkout.session\",\"payment_intent\": \"pi_test_123\"}}}";
        Event event = com.stripe.model.Event.GSON.fromJson(payload, Event.class);
        System.out.println(event.getDataObjectDeserializer().getObject().isPresent());
    }
}
