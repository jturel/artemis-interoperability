package com.example.artemis;

import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;

public class Consumer {

	public static void main (String[] args) {

		try {
			ServerLocator locator = ActiveMQClient.createServerLocator("tcp://localhost:61617?sslEnabled=true&trustStorePath=client-side-truststore.jks&trustStorePassword=changeme&keyStorePath=client-side-keystore.jks&keyStorePassword=changeme");
			ClientSessionFactory factory =  locator.createSessionFactory();

			ClientSession session = factory.createSession("consumerUser", "sekret", false, true, true, false, 0);
			ClientConsumer consumer = session.createConsumer("core_messages");
	
			session.start();
	
			ClientMessage msg;
	
			while (true) {
				msg = consumer.receive(10);
		
				if (msg == null) {
					break;
				}
				
				System.out.println(msg.toString());

				try {
					try {
						System.out.println("Body with readString: " + msg.getBodyBuffer().duplicate().readString());
					} catch (Exception e) {
						System.out.println("Invalid body for readString");
					}
					
					try {
						System.out.println("Body with readNullableString: " + msg.getBodyBuffer().duplicate().readNullableString());
					} catch (Exception e) {
						System.out.println("Invalid body for readNullableString");
					}

					try {
						System.out.println("Body with readNullableSimpleString: " + msg.getBodyBuffer().duplicate().readNullableSimpleString());
					} catch (Exception e) {
						System.out.println("Invalid body for readNullableSimpleString");
					}
					
					try {
						System.out.println("Body with readSimpleString: " + msg.getBodyBuffer().duplicate().readSimpleString());
					} catch (Exception e) {
						System.out.println("Invalid body for readSimpleString");
					}
					
					try {
			            int bodySize = msg.getBodySize();
			            byte[] bodyBytes = new byte[bodySize];
			            msg.getBodyBuffer().duplicate().readBytes(bodyBytes);
			            String body = new String(bodyBytes, 0, bodySize);
						System.out.println("Body with readBytes: " + body);
					} catch (Exception e) {
						System.out.println("Invalid body for readBytes");
					}
	
					System.out.println("\n");
				} catch (Exception e) {
					System.out.println("Error receiving message");
				} finally {
					msg.acknowledge();
				}
			}

			System.out.println("All messages received");
			session.close();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}
}
