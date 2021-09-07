package br.edu.ifpb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class NewTask {
    private static String QUEUE_NAME = "task_queue";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();
        ) {
            Boolean durable = true;

            channel.queueDeclare(QUEUE_NAME, durable, false, false, null);

            System.out.println("Escreva uma mensagem: ");
            Scanner keyboard = new Scanner(System.in);
            String message = keyboard.nextLine();

            channel.basicPublish("", QUEUE_NAME,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    message.getBytes(StandardCharsets.UTF_8));

            System.out.println("[x] Enviado'" + message + "'");
        } catch (IOException | TimeoutException err) {
            err.printStackTrace();
        }
    }
}
