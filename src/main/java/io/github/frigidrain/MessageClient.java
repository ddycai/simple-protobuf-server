package io.github.frigidrain;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * A client that sends and receives {@link Message}.
 */
public class MessageClient<T extends Message, U extends Message> {

  private final Parser<U> parser;
  private final Socket socket;
  private OutputStream out;
  private InputStream in;

  /**
   * Create a new client.
   *
   * @param parser parser for messages that the client receives
   * @param hostname the hostname of the server
   * @param port the port on the host to connect to
   * @param <T> the message that the client sends
   * @param <U> the message that the client expects from the server
   */
  public static <T extends Message, U extends Message> MessageClient<T, U> create(
      Parser<U> parser, String hostname, int port) throws IOException {
    return create(parser, new Socket(hostname, port));
  }

  /**
   * Create a new client.
   *
   * @param parser parser for messages that the client receives
   * @param socket socket that the client sends/receives from
   * @param <T> the message that the client sends
   * @param <U> the message that the client expects from the server
   */
  public static <T extends Message, U extends Message> MessageClient<T, U> create(
      Parser<U> parser, Socket socket) throws IOException {
    return new MessageClient<>(parser, socket);
  }

  private MessageClient(Parser<U> parser, Socket socket) throws IOException {
    this.parser = parser;
    this.socket = socket;
    this.out = socket.getOutputStream();
    this.in = socket.getInputStream();
  }

  public void send(T message) throws IOException {
    message.writeDelimitedTo(out);
  }

  public U receive() throws IOException {
    return parser.parseDelimitedFrom(in);
  }
}
