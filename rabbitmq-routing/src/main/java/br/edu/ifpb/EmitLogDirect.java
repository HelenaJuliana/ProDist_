package br.edu.ifpb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class EmitLogDirect {
    private static String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");

        System.out.println("Escreva um número ou um nome: ");
        Scanner keyboard = new Scanner(System.in);
        String text = keyboard.nextLine();
        String bindingKey = "";

        try {
            Integer.parseInt(text);
            bindingKey = "number";
        } catch (Exception err) {
            bindingKey = "string";
        }

        try (
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
        ) {
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            channel.basicPublish(EXCHANGE_NAME, bindingKey, null, text.getBytes("UTF-8"));
            System.out.println(String.format("Enviado '%s' para %s.", text, bindingKey));
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
}
