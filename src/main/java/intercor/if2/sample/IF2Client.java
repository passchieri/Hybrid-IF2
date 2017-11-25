package intercor.if2.sample;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;

import quadtree.QuadTreeConverter;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * @author Igor Passchier
 * 
 * 
 *         This is a basic example implementation of the Intercor IF2 interface.
 *         It combines the function to publish a message to the AMQP message
 *         broker (which is formally not part of the IF2 specification, but
 *         required to receive any data via IF2, and a listener that subscribes
 *         with different selection criteria.
 * 
 *         The routing key structure expected is:
 * 
 *         <message type>.<message version>.<provider>.<subtype id>.{quadtree
 *         path}
 * 
 *         A broker should be available to connect to. The connection parameters
 *         are hardcoded, and should be adjusted in line with the actual broker
 *         used.
 *
 */
public class IF2Client {

	/**
	 * A simeple data structure to represent the metadata of a message
	 * 
	 * @author passchieri
	 *
	 */
	static private class FakeData {
		double lat; // latitude of the data
		double lon; // longitude of the data
		int zoom; // zoom level at which the data should be published
		String messageType; // message type, In this example always DENM
		String messageVersion; // message version. The current DENM version is 1.2.1, encoded as 1_2_1
		String provider; // identifier of the organisation that publishes the message
		String subtype; // subtype of the message. In case of DENMs, we use the causecode

		public FakeData(double lat, double lon, int zoom, String messageType, String messageVersion, String provider,
				String subtype) {
			super();
			this.lat = lat;
			this.lon = lon;
			this.zoom = zoom;
			this.messageType = messageType;
			this.messageVersion = messageVersion;
			this.provider = provider;
			this.subtype = subtype;
		}

		public String getRoutingKey() {
			// none of the strings in the routing key should contain ".". This should be
			// checked
			// No extra "." after the subtype, as the quadtree starts with a "."
			return messageType + "." + messageVersion + "." + provider + "." + subtype
					+ QuadTreeConverter.getQuadTree(zoom, lat, lon, ".");
		}

		@Override
		public String toString() {
			return "FakeData [lat=" + lat + ", lon=" + lon + ", zoom=" + zoom + ", key=" + getRoutingKey() + "]";
		}
	}

	/**
	 * User name used to connect to the AMQP broker
	 */
	private static final String USER = "rws";
	/**
	 * Password to connect to the AMQP broker
	 */
	private static final String PASSWORD = "secret";
	/**
	 * Virtual host to connect to
	 */
	private static final String VIRTUALHOST = "test";
	/**
	 * Hostname or IP address of the broker
	 */
	private static String HOST = "localhost";
	/**
	 * Exchange to connect to. This is only used by the consumer, the publisher uses
	 * the message type for publication.
	 */
	private static String EXCHANGE = "DENM";
	/**
	 * Sleep time between in ms publishing messages. No sleep time is required, but
	 * the consumer disconnects after the last message publication. If the consumer
	 * disconnects before all messages are received, then not all messages are
	 * received, and it looks as if mesasges are irronously lost. So SLEEP should be
	 * large enough for the message roundtrip + message handling by the broker.
	 */
	private static int SLEEP = 100;
	private Connection connection;
	private Channel channel;

	/**
	 * Connect to the broker, and generate a channel.
	 */
	public void connect() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(HOST);
		factory.setUsername(USER);
		factory.setPassword(PASSWORD);
		factory.setVirtualHost(VIRTUALHOST);

		try {
			connection = factory.newConnection();
			connection.addShutdownListener((ShutdownSignalException cause) -> {
				System.out.println("Connection closed");
			});
			System.out.println("Connection opened");
			channel = connection.createChannel();
			channel.addShutdownListener((ShutdownSignalException cause) -> {
				System.out.println("Channel closed");
			});
			System.out.println("Channel opened");

		} catch (Exception e) {
			e.printStackTrace();
			disconnect();

		}
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
			channel.queueBind(queue, EXCHANGE, key);
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

	/**
	 * Publish a message to an previously opened connection and channel.
	 * 
	 * @param data
	 */
	public void publishMessage(FakeData data) {
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

	/**
	 * Close the channel, and disconnect
	 */
	public void disconnect() {
		try {
			if (channel != null)
				channel.abort();
			if (connection != null)
				connection.abort();
			System.out.println("Disconnected");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			connection = null;
			channel = null;
		}
	}

	/**
	 * Main routine, implementing publishing and receiving fake data messages.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Vector to store the fake data that will be transmitted
		Vector<FakeData> fake = new Vector<FakeData>();

		// RWW (3) DENM v 1.2.2, located in helmond, from RWS operator
		fake.add(new FakeData(51.481939, 5.640488, 18, "DENM", "1_2_2", "RWS", "3"));

		// Slow vehicle (26) DENM v1.2.2, located at another location in helmond, from
		// province of NorthBrabant
		fake.add(new FakeData(51.467641, 5.667863, 18, "DENM", "1_2_2", "NL_NB", "26"));

		// RWW (3) DENM v 1.2.2, located in eindhoven, from RWS operator
		fake.add(new FakeData(51.437941, 5.472308, 18, "DENM", "1_1_1", "RWS", "3"));

		// quadtree path of helmond at zoom 10. This does not include eindhoven
		String helmond = "1.2.0.2.0.3.0.2.0.0";

		// quadtree path of eindhoven at zoom 10. This does not include helmond
		String eindhoven = "1.2.0.2.0.2.1.3.1.1";

		// zero length quadtree is the whole world
		String theWorld = "";

		// Create a message publisher, and connect to the broker
		IF2Client publisher = new IF2Client();
		publisher.connect();

		// Create the consumer
		IF2Client consumer = new IF2Client();

		// Below, multiple times the same structure: connect the consumer, subscribe
		// with a specific filter, have the publisher publish all the fake messages, and
		// disconnect the consumer again.
		consumer.connect();
		consumer.startListening("*.*.*.*." + helmond + ".#");

		System.out.println("###############\nExpecting 3 messages going out, 2 coming in from Helmond...");
		for (FakeData data : fake) {
			publisher.publishMessage(data);
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
			}
		}

		consumer.disconnect();
		consumer.connect();
		consumer.startListening("#");// dont add an empty string (theWorld) in the routing key
										// selector: that would filter on an empty quadtree,
										// followed by anything
		System.out.println("###############\nExpecting 3 messages going out, 3 coming in from everywhere...");
		for (FakeData data : fake) {
			publisher.publishMessage(data);
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
			}
		}
		consumer.disconnect();

		consumer.connect();
		consumer.startListening("*.*.*.*." + eindhoven + ".#");
		System.out.println("###############\nExpecting 3 messages going out, 1 coming in from Eindhoven...");
		for (FakeData data : fake) {
			publisher.publishMessage(data);
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
			}
		}
		consumer.disconnect();

		consumer.connect();
		consumer.startListening("*.*.*.*." + helmond + ".#");
		consumer.startListening("*.*.*.*." + eindhoven + ".#");

		System.out
				.println("###############\nExpecting 3 messages going out, 3 coming in from Helmond and Eindhoven...");
		for (FakeData data : fake) {
			publisher.publishMessage(data);
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
			}
		}
		consumer.disconnect();

		consumer.connect();
		consumer.startListening("*.1_2_2.*.*.#");
		System.out.println("###############\nExpecting 3 messages going out, 2 coming in with version 1_2_2...");
		for (FakeData data : fake) {
			publisher.publishMessage(data);
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
			}
		}
		consumer.disconnect();

		consumer.connect();
		consumer.startListening("*.*.RWS.*.#");
		System.out.println("###############\nExpecting 3 messages going out, 2 coming in from RWS...");
		for (FakeData data : fake) {
			publisher.publishMessage(data);
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
			}
		}
		consumer.disconnect();

		consumer.connect();
		consumer.startListening("*.*.*.3.#");
		System.out.println("###############\nExpecting 3 messages going out, 2 coming in subtype RWW (3)...");
		for (FakeData data : fake) {
			publisher.publishMessage(data);
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
			}
		}
		consumer.disconnect();
		publisher.disconnect();
	}

}
