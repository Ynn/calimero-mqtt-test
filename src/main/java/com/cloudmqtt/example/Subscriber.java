
package com.cloudmqtt.example;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A sample application that demonstrates how to use the Paho MQTT v3.1 Client blocking API.
 */
public class Subscriber  {

    private final int qos = 1;
    private String topic = "0/0/1";
    private MqttClient client;

    public Subscriber(String uri) throws MqttException, URISyntaxException {
        this(new URI(uri));
    }

    public Subscriber(URI uri) throws MqttException {
    	System.out.println(uri);
        String host = String.format("tcp://%s:%d", uri.getHost(), uri.getPort());
    	System.out.println(host);

        String[] auth = this.getAuth(uri);

        String username = auth[0];
    	System.out.println(username);

        String password = auth[1];
    	System.out.println(password);

        
        String clientId = "MQTT-Java-Example";
        if (!uri.getPath().isEmpty()) {
            this.topic = uri.getPath().substring(1);
        }

        MqttConnectOptions conOpt = new MqttConnectOptions();
        conOpt.setCleanSession(true);
        conOpt.setUserName(username);
        conOpt.setPassword(password.toCharArray());

        this.client = new MqttClient(host, clientId, new MemoryPersistence());
        this.client.setCallback(new Receiver());
        this.client.connect(conOpt);

        this.client.subscribe("#", qos);
    }

    private String[] getAuth(URI uri) {
        String a = uri.getAuthority();
        String[] first = a.split("@");
        return first[0].split(":");
    }

    public void sendMessage(byte[] bytes) throws MqttException {
        MqttMessage message = new MqttMessage(bytes);
        message.setQos(qos);
        this.client.publish(this.topic, message); // Blocking publish
    }


    public static void main(String[] args) throws MqttException, URISyntaxException {
        Subscriber s = new Subscriber("mqtt://user:pass@m21.cloudmqtt.com:18029");
        s.sendMessage("Hello".getBytes());
        s.sendMessage("Hello 2".getBytes());
    }
}