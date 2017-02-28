package io.github.frigidrain.examples;

import io.github.frigidrain.MessageServer;
import io.github.frigidrain.ServerWorker;
import io.github.frigidrain.ServerWorkerFactory;
import io.github.frigidrain.SimpleMessageOuterClass.SimpleMessage;
import java.io.IOException;

/** A server that receives a message from a client and sends back the same message. */
public class EchoServer {

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.format("Usage: %s <port number>%n", EchoServer.class.getSimpleName());
      System.exit(1);
    }
    int port = Integer.parseInt(args[0]);
    MessageServer<SimpleMessage, SimpleMessage> server = MessageServer
        .create(SimpleMessage.parser(), port);
    server.start(new EchoServerWorkerFactory());
  }

  private static class EchoServerWorkerFactory implements
      ServerWorkerFactory<SimpleMessage, SimpleMessage> {
    private static int id = 0;

    @Override
    public ServerWorker<SimpleMessage, SimpleMessage> create() {
      return new EchoServerWorker(++id);
    }
  }

  public static class EchoServerWorker extends ServerWorker<SimpleMessage, SimpleMessage> {
    private final int id;

    EchoServerWorker(int id) {
      this.id = id;
    }

    @Override
    public void whenConnected() {
      System.out.format("Established connection with client #%d.%n", id);
    }

    @Override
    public SimpleMessage process(SimpleMessage input) {
      System.out.format("Client #%d: %s%n", id, input.getContent());
      return input;
    }

    @Override
    public void whenDisconnected() {
      System.out.format("Disconnected from client #%d.%n", id);
    }
  }
}
