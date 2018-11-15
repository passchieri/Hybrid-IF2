package intercor.if2.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import intercor.if2.client.Datum;
import intercor.if2.client.IF2Client;
import intercor.if2.client.IF2Consumer;
import intercor.if2.client.IF2Producer;

/**
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
 *         are hard-coded, and should be adjusted in line with the actual broker
 *         used.
 *
 * @author Igor Passchier
 * @copyright (c) Tass International BV
 * */
public class IF2Sample {



	/**
	 * User name used to connect to the AMQP broker
	 */
	private static final String USER = "prosumer";
	/**
	 * Password to connect to the AMQP broker
	 */
	private static final String PASSWORD = "prosumerpw";
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





	/**
	 * Main routine, implementing publishing and receiving fake data messages.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Vector<Datum> fake = createFakeData();
		Map<String, Object> properties = parseCommand(args);

		// quadtree path of helmond at zoom 10. This does not include eindhoven
		String helmond = "1.2.0.2.0.3.0.2.0.0";

		// quadtree path of eindhoven at zoom 10. This does not include helmond
		String eindhoven = "1.2.0.2.0.2.1.3.1.1";

		// Create a message publisher, and connect to the broker
		IF2Producer publisher = new IF2Producer(properties);
		try {
			publisher.connect();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		// Create the consumer
		IF2Consumer consumer = new IF2Consumer(properties);

		// Below, multiple times the same structure: connect the consumer, subscribe
		// with a specific filter, have the publisher publish all the fake messages, and
		// disconnect the consumer again.
		try {
			consumer.connect();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		consumer.startListening("*.*.*.*." + helmond + ".#");

		System.out.println("###############\nExpecting 3 messages going out, 2 coming in from Helmond...");
		for (Datum  data : fake) {
			publisher.publishMessage(data);
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
			}
		}

		consumer.disconnect();
		try {
			consumer.connect();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		consumer.startListening("#");// dont add an empty string (theWorld) in the routing key
										// selector: that would filter on an empty quadtree,
										// followed by anything
		System.out.println("###############\nExpecting 3 messages going out, 3 coming in from everywhere...");
		for (Datum data : fake) {
			publisher.publishMessage(data);
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
			}
		}
		consumer.disconnect();

		try {
			consumer.connect();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		consumer.startListening("*.*.*.*." + eindhoven + ".#");
		System.out.println("###############\nExpecting 3 messages going out, 1 coming in from Eindhoven...");
		for (Datum data : fake) {
			publisher.publishMessage(data);
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
			}
		}
		consumer.disconnect();

		try {
			consumer.connect();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		consumer.startListening("*.*.*.*." + helmond + ".#");
		consumer.startListening("*.*.*.*." + eindhoven + ".#");

		System.out
				.println("###############\nExpecting 3 messages going out, 3 coming in from Helmond and Eindhoven...");
		for (Datum data : fake) {
			publisher.publishMessage(data);
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
			}
		}
		consumer.disconnect();

		try {
			consumer.connect();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		consumer.startListening("*.1_2_2.*.*.#");
		System.out.println("###############\nExpecting 3 messages going out, 2 coming in with version 1_2_2...");
		for (Datum data : fake) {
			publisher.publishMessage(data);
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
			}
		}
		consumer.disconnect();

		try {
			consumer.connect();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		consumer.startListening("*.*.RWS.*.#");
		System.out.println("###############\nExpecting 3 messages going out, 2 coming in from RWS...");
		for (Datum data : fake) {
			publisher.publishMessage(data);
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
			}
		}
		consumer.disconnect();

		try {
			consumer.connect();
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		consumer.startListening("*.*.*.3.#");
		System.out.println("###############\nExpecting 3 messages going out, 2 coming in subtype RWW (3)...");
		for (Datum data : fake) {
			publisher.publishMessage(data);
			try {
				Thread.sleep(SLEEP);
			} catch (InterruptedException e) {
			}
		}
		consumer.disconnect();
		publisher.disconnect();
	}





	private static Map<String,Object> parseCommand(String[] args) {
		HashMap<String, Object> properties=new HashMap<>();
		for(String s: args) {
			int i=-1;
			if ((i=s.indexOf("="))>0) {
				String key=s.substring(0, i).toUpperCase();
				String val=s.substring(i+1);
				if (key.equals(IF2Client.USESSL))  {
					properties.put(key, Boolean.parseBoolean(val));
				} else if (key.equals(IF2Client.PORT)) {
					properties.put(key,Integer.parseInt(val));
				} else {
					properties.put(key,val);
				}
			}
		}
		return properties;
	}





	private static Vector<Datum> createFakeData() {
		// Vector to store the fake data that will be transmitted
		Vector<Datum> fake = new Vector<Datum>();

		// RWW (3) DENM v 1.2.2, located in helmond, from RWS operator
		fake.add(new Datum(51.481939, 5.640488, 18, "DENM", "1_2_2", "RWS", "3"));

		// Slow vehicle (26) DENM v1.2.2, located at another location in helmond, from
		// province of NorthBrabant
		fake.add(new Datum(51.467641, 5.667863, 18, "DENM", "1_2_2", "NL_NB", "26"));

		// RWW (3) DENM v 1.2.2, located in eindhoven, from RWS operator
		fake.add(new Datum(51.437941, 5.472308, 18, "DENM", "1_1_1", "RWS", "3"));
		return fake;
	}

}
