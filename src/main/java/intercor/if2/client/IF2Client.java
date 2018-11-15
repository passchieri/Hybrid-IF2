package intercor.if2.client;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 
 * @author Igor Passchier
 * @copyright (c) Tass International BV
 */
abstract public class IF2Client {

	/**
	 * User name used to connect to the AMQP broker. Defaults to prosumer
	 */
	private static final String USER = "USER";

	/**
	 * Password to connect to the AMQP broker. Defaults to prosumerpw
	 */
	private static final String PASSWORD = "PASSWORD";

	/**
	 * Virtual host to connect to. Defaults to test
	 */
	public static final String VIRTUALHOST = "VIRTUALHOST";

	/**
	 * Hostname or IP address of the broker. Defaults to localhost
	 */
	public static final String HOST = "HOST";

	/**
	 * TCP port of the broker. Defaults to 5671 when ssl is used, and 5672 when not.
	 * Note, that this should be an Integer
	 */
	public static final String PORT = "PORT";

	/**
	 * Exchange to connect to. This is only used by the consumer, the publisher uses
	 * the message type for publication. Defaults to SPAT
	 */
	public static final String EXCHANGE = "EXCHANGE";

	/**
	 * Use SSL for the connection. Note, that although SSL is used, certificates are
	 * not validated. Defaults to true. It should be a Boolean
	 */
	public static final String USESSL = "USESSL";

	protected Connection connection;
	protected Channel channel;
	protected Map<String, Object> props = new HashMap<>();

	public IF2Client() {
		props.put(HOST, "localhost");
		props.put(PORT, 5671);
		props.put(USER, "prosumer");
		props.put(PASSWORD, "prosumerpw");
		props.put(VIRTUALHOST, "test");
		props.put(EXCHANGE, "SPAT");
		props.put(USESSL, true);
	}

	public IF2Client(Map<String, Object> properties) {
		this();
		/* Change the default port, based on whether SSL is requested */
		if (properties.containsKey(USESSL) && !(Boolean) properties.get(USESSL))
			props.put(PORT, 5672);
		this.props.putAll(properties);
	}

	private String reconstructUri(ConnectionFactory factory) {
		StringBuffer buf = new StringBuffer();
		buf.append(("amqp" + (factory.isSSL() ? "s" : "") + "://"));
		buf.append(factory.getUsername() + ":" + factory.getPassword()+"@");
		buf.append(factory.getHost() + ":" + factory.getPort());
		buf.append("/" + factory.getVirtualHost());
		return buf.toString();
	}

	/**
	 * Connect to the broker, and generate a channel.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public void connect() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(props.get(HOST).toString());
		factory.setUsername(props.get(USER).toString());
		factory.setPassword(props.get(PASSWORD).toString());
		factory.setVirtualHost(props.get(VIRTUALHOST).toString());
		factory.setPort(((Number) props.get(PORT)).intValue());

		String uri = "not defined yet";
		try {
			if ((Boolean) props.get(USESSL)) {
				factory.useSslProtocol();
			}
			final String url = uri = reconstructUri(factory);
			connection = factory.newConnection();
			connection.addShutdownListener((ShutdownSignalException cause) -> {
				System.out.println("Connection closed to " + url);
			});
			System.out.println("Connection opened to " + url);
			channel = connection.createChannel();
			channel.addShutdownListener((ShutdownSignalException cause) -> {
				System.out.println("Channel closed");
			});
			System.out.println("Channel opened");

		} catch (Exception e) {
			System.out.println("Exception in connect to " + uri);
			disconnect();
			throw e;

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
}