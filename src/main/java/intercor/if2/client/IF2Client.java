package intercor.if2.client;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownSignalException;

/**

 * @author Igor Passchier
 * @copyright (c) Tass International BV
 * */
public class IF2Client {

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
	protected static String EXCHANGE = "DENM";

	protected Connection connection;
	protected Channel channel;

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

}
