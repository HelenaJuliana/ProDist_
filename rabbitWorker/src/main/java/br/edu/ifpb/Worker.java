package br.edu.ifpb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;


public class Worker {
    private static String QUEUE_NAME = "task_queue";

    public static void main(String[] args) throws IOException, TimeoutException, IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        Boolean durable = true;
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
        System.out.println(" [*] Aguardando mensagens. Para sair, pressione CTRL + C");

        channel.basicQos(1);

        DeliverCallback callback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

            System.out.println(" [x] Recebido '" + message + "'");

            try {
                doWork(message);
            } finally {
                System.out.println(" [x] Feito");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };

        Boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, callback, consumerTag -> {});
    }

    private static void doWork(String task) {
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
