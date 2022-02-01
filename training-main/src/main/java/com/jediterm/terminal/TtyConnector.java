package com.jediterm.terminal;

import java.io.IOException;

/**
 * Interface to tty.
 */
public interface TtyConnector {
  void close();

  String getName();

  int read(char[] buf, int offset, int length) throws IOException;

  void write(byte[] bytes) throws IOException;

  boolean isConnected();

  void write(String string) throws IOException;

  int waitFor() throws InterruptedException;

  boolean ready() throws IOException;
}
