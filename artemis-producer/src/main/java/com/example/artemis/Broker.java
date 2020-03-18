package com.example.artemis;

import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.spi.core.security.ActiveMQJAASSecurityManager;

public class Broker {

	public static void main(String[] args) {
		EmbeddedActiveMQ embedded = new EmbeddedActiveMQ();

		ActiveMQJAASSecurityManager securityManager = new ActiveMQJAASSecurityManager("PropertiesLogin", "CertLogin");

		embedded.setSecurityManager(securityManager);
	
		try {
			embedded.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
