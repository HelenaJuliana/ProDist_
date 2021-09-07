package br.edu.ifpb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveLogDirect {
    public static String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        String queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName, EXCHANGE_NAME, "number");
        channel.queueBind(queueName, EXCHANGE_NAME, "string");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback callback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(
                    String.format(" [x] Received '%s': %s",
                            delivery.getEnvelope().getRoutingKey(),
                            message
                    )
            );
        };

        channel.basicConsume(queueName, true, callback, consumerTag -> {});
    }
}
