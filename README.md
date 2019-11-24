## To Run

Set up a basic artemis broker and run it
If desired, enable DEBUG logging for STOMP in logging.properties:

org.apache.activemq.artemis.core.protocol.stomp.StompConnection

/var/lib/artemis/testbroker/bin/artemis run

cd client
dnf install qpid-proton-cpp-devel
bundle install
ruby stomp_client.rb
ruby proton_client.rb

cd core
mvn clean install
mvn exec:java -Dexec.mainClass="com.example.artemis.Producer"
mvn exec:java -Dexec.mainClass="com.example.artemis.Consumer"
