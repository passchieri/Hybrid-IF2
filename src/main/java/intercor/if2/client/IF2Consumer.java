package intercor.if2.client;

/**
 * @author Igor Passchier
 * @copyright (c) Tass International BV
 */
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class IF2Consumer extends IF2Client {

	public IF2Consumer() {
		super();
	}

	public IF2Consumer(Map<String, Object> properties) {
		super(properties);
	}

	/**
	 * Based on the connection and channel opened earlier, create a temporary queue,
	 * and bind the queue to the correct exchange with the prescribed key. A simple
	 * handler is connected to the key, that prints every message received.
	 * 
	 * @param key
	 *            The routing key to use
	 */
	public void startListening(String key) {
		Map<String, Object> args = new HashMap<String, Object>();
		// Just to be sure that we do not block the broker, put some limits on queue
		// length and lifetime of the messages
		args.put("x-max-length", 1000); // limit queue length to 1000 elements.
		args.put("x-message-ttl", 60 * 10 * 1000); // limit max time to 10 minutes

		try {
			DeclareOk queueDeclare = channel.queueDeclare("", false, true, true, args);
			final String queue = queueDeclare.getQueue();
			channel.queueBind(queue, props.get(IF2Client.EXCHANGE).toString(), key);
			Consumer consumer = new DefaultConsumer(channel) {

				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
						byte[] body) throws IOException {
					// If real ASN.1 messages would be transmitted, the message could not be
					// converted to a string like done here.
					System.out.println("Message received from exchange " + EXCHANGE + ":" + new String(body));

				}

			};
			String consumertag = channel.basicConsume(queue, true, consumer);
			System.out.println("Waiting for incoming messages...");
		} catch (Exception ex) {
			ex.printStackTrace();
			disconnect();
		}
	}
}
