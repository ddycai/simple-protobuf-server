package io.github.frigidrain.examples;

import io.github.frigidrain.MessageClient;
import io.github.frigidrain.SimpleMessageOuterClass.SimpleMessage;
import java.io.IOException;
import java.util.Scanner;

/**
 * A client that sends a {@link SimpleMessage} based on user input stdin and receives a {@link
 * SimpleMessage} from the server.
 */
public class SimpleMessageClient {
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.err
          .format("Usage: %s <host name> <port number>%n", MessageClient.class.getSimpleName());
      System.exit(1);
    }
    String hostname = args[0];
    Integer port = Integer.parseInt(args[1]);
    MessageClient<SimpleMessage, SimpleMessage> client = MessageClient
        .create(SimpleMessage.parser(), hostname, port);

    System.out.println("Client started.");
    String userInput;
    Scanner scanner = new Scanner(System.in);
    while ((userInput = scanner.nextLine()) != null) {
      System.out.println("Sending message...");
      SimpleMessage message = SimpleMessage.newBuilder().setContent(userInput).build();
      client.send(message);
      System.out.println("received: " + client.receive().getContent());
    }
  }

}
