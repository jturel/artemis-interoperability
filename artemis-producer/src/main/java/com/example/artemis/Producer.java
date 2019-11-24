package com.example.artemis;

import java.nio.charset.StandardCharsets;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.activemq.artemis.api.core.ActiveMQQueueExistsException;
import org.apache.activemq.artemis.api.core.Message;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.client.ActiveMQClient;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientProducer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.client.ClientSessionFactory;
import org.apache.activemq.artemis.api.core.client.ServerLocator;


public class Producer 
{
	public static void main(String[] args) {
		
		try {
			ServerLocator locator = ActiveMQClient.createServerLocator("tcp://localhost:61616");

			ClientSessionFactory factory =  locator.createSessionFactory();
			ClientSession session = factory.createSession();

			try {
				session.createQueue("interop_test", RoutingType.MULTICAST, "core_messages", true);
			} catch (ActiveMQQueueExistsException e) {
			}

			ClientProducer producer = session.createProducer("interop_test");

			JsonObject messageBody = Json.createObjectBuilder()
					.add("foo", "bar").build();
			
			/**
			 * This is the method Candlepin uses to send messages to Artemis
			 * STOMP clients will not receive the message body.
			 * The QPid Proton client (AMQP) also does not get the body.
			 */
			ClientMessage message = session.createMessage(true);
			message.getBodyBuffer().writeString(messageBody.toString());
			message.putStringProperty("MESSAGE_TYPE", "default");
			message.putStringProperty("METHOD", "writeString");
			producer.send(message);
			
			message = session.createMessage(true);
			message.getBodyBuffer().writeSimpleString(SimpleString.toSimpleString(messageBody.toString()));
			message.putStringProperty("MESSAGE_TYPE", "default");
			message.putStringProperty("METHOD", "writeSimpleString");
			producer.send(message);

			message = session.createMessage(Message.BYTES_TYPE, true);
			message.getBodyBuffer().writeBytes(messageBody.toString().getBytes());
			message.putStringProperty("MESSAGE_TYPE", "bytes");
			message.putStringProperty("METHOD", "writeBytes");
			producer.send(message);
			
			message = session.createMessage(Message.BYTES_TYPE, true);
			message.getBodyBuffer().writeNullableSimpleString(new SimpleString(messageBody.toString()));
			message.putStringProperty("MESSAGE_TYPE", "bytes");
			message.putStringProperty("METHOD", "writeNullableSimpleString");
			producer.send(message);

			message = session.createMessage(true);
			message.getBodyBuffer().writeNullableSimpleString(SimpleString.toSimpleString(messageBody.toString()));
			message.putStringProperty("MESSAGE_TYPE", "default");
			message.putStringProperty("METHOD", "writeNullableSimpleString");
			producer.send(message);

			message = session.createMessage(Message.TEXT_TYPE, true);
			message.getBodyBuffer().writeString(messageBody.toString());
			message.putStringProperty("METHOD", "writeString");
			message.putStringProperty("MESSAGE_TYPE", "text");
			producer.send(message);
			
			message = session.createMessage(Message.BYTES_TYPE, true);
			message.getBodyBuffer().writeBytes(messageBody.toString().getBytes(StandardCharsets.UTF_8));
			message.putStringProperty("METHOD", "writeBytes");
			message.putStringProperty("MESSAGE_TYPE", "bytes");
			producer.send(message);

			/**
			 * This one explodes on AMQP ruby client
			 * "\xE0" from ASCII-8BIT to UTF-8 (Encoding::UndefinedConversionError)
			 */
			/*
			message = session.createMessage(true);
			message.getBodyBuffer().writeNullableString(messageBody.toString());
			message.putStringProperty("METHOD", "writeNullableString");
			message.putStringProperty("MESSAGE_TYPE", "text");
			message.setType(Message.TEXT_TYPE);
			producer.send(message);
			*/

			message = session.createMessage(Message.TEXT_TYPE, true);
			message.getBodyBuffer().writeSimpleString(SimpleString.toSimpleString(messageBody.toString()));
			message.putStringProperty("METHOD", "writeSimpleString");
			message.putStringProperty("MESSAGE_TYPE", "text");
			producer.send(message);

			// This seems to be the most compatible approach for different clients
			message = session.createMessage(Message.TEXT_TYPE, true);
			message.getBodyBuffer().writeNullableSimpleString(SimpleString.toSimpleString((messageBody.toString())));
			message.putStringProperty("METHOD", "writeNullableSimpleString");
			message.putStringProperty("MESSAGE_TYPE", "text");
			producer.send(message);

			System.out.println("All messages delivered, closing");

			session.close();

			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
