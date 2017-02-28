package io.github.frigidrain;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.net.Socket;

/** A worker that handles a single connection from a client. */
public abstract class ServerWorker<T extends Message, U extends Message> implements Runnable {

  private Parser<T> parser;
  private Socket connection;

  /** Override with logic to run when the client is connected. */
  public void whenConnected() {
  }

  /** Override with logic to process input T and return U to the client. */
  public abstract U process(T input);

  /** Override with logic to run when the client is disconnected. */
  public void whenDisconnected() {
  }

  /** Initializes this worker with the given required params. */
  ServerWorker<T, U> init(Parser<T> parser, Socket connection) {
    this.parser = parser;
    this.connection = connection;
    return this;
  }

  @Override
  public void run() {
    whenConnected();
    try {
      MessageClient<U, T> client = MessageClient.create(parser, connection);
      T message;
      U response;
      while ((message = client.receive()) != null) {
        if ((response = process(message)) != null) {
          client.send(response);
        }
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    whenDisconnected();
  }

}
