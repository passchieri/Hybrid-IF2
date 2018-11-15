package intercor.if2.client;

/**
 * @author Igor Passchier
 * @copyright (c) Tass International BV
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;

public class IF2Producer extends IF2Client {

	public IF2Producer() {
		super();
	}

	public IF2Producer(Map<String, Object> properties) {
		super(properties);
	}

	/**
	 * Publish a message to an previously opened connection and channel.
	 * 
	 * @param data
	 */
	public void publishMessage(Datum data) {
		// Get the routing key
		String key = data.getRoutingKey();

		// As message, we transmit a string representation of the fake data. Normally,
		// this would be the ASN.1 encoded message
		String message = data.toString();

		// Use the message type as exchange name to publish the message to.
		String exchange = data.messageType;
		try {
			if (channel != null && channel.isOpen()) {
				// fill the properties that go along with the message
				BasicProperties.Builder builder = new Builder();
				HashMap<String, Object> headers = new HashMap<>();
				headers.put("lat", data.lat);
				headers.put("lon", data.lon);
				BasicProperties props = builder.headers(headers).expiration("10000").build();
				channel.basicPublish(exchange, key, props, message.getBytes());
				System.out.println("published message " + message);
			} else {
				System.out.println("Cannot publish, no channel available");
			}
		} catch (IOException e) {
			e.printStackTrace();
			disconnect();
		}
	}
}
