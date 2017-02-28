package io.github.frigidrain;

import com.google.protobuf.Message;

/** Factory for creating a {@link ServerWorker}. */
public interface ServerWorkerFactory<T extends Message, U extends Message> {

  public ServerWorker<T, U> create();
}
