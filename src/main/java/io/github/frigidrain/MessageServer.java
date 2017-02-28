package io.github.frigidrain;

import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple server that receives and sends protocol buffers.
 *
 * @param <T> type of {@link Message} to receive
 * @param <U> type of {@link Message} to send as a response
 */
public class MessageServer<T extends Message, U extends Message> {

  private static final int DEFAULT_THREAD_POOL_SIZE = 10;

  private final Parser<T> parser;
  private final ServerSocket serverSocket;
  private final ExecutorService executor;

  private ServerWorkerFactory<T, U> factory;

  /**
   * Same as {@link #create(Parser, int, int)}, but with the thread pool size set to {@link
   * #DEFAULT_THREAD_POOL_SIZE}.
   */
  public static <T extends Message, U extends Message> MessageServer<T, U> create(
      Parser<T> parser, int port) throws IOException {
    return new MessageServer<>(parser, port, DEFAULT_THREAD_POOL_SIZE);
  }

  /**
   * Create a server.
   *
   * @param parser parser for messages that the client receives
   * @param port the port to start the server on
   * @param threadPoolSize size of the thread pool for workers
   * @param <T> the message that the server receives
   * @param <U> the message that the server sends back to the client
   */
  public static <T extends Message, U extends Message> MessageServer<T, U> create(
      Parser<T> parser, int port, int threadPoolSize) throws IOException {
    return new MessageServer<>(parser, port, threadPoolSize);
  }

  private MessageServer(Parser<T> parser, int port, int threadPoolSize) throws IOException {
    this.parser = parser;
    this.serverSocket = new ServerSocket(port);
    this.executor = Executors.newFixedThreadPool(threadPoolSize);
  }

  /**
   * Start the server.
   *
   * @param factory creates new instances of workers to process messages received from clients
   */
  public void start(ServerWorkerFactory<T, U> factory) throws IOException {
    this.factory = factory;
    System.out.println("Server started and accepting connections.");
    Socket connection;
    while ((connection = serverSocket.accept()) != null) {
      executor.submit(factory.create().init(parser, connection));
    }
  }
}
