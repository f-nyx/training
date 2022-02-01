package com.jediterm.terminal;

import com.jediterm.terminal.util.CharUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Takes data from and sends it back to TTY input and output streams via {@link TtyConnector}
 */
public class TerminalDataStream {
  private final TtyConnector myTtyConnector;
  private final @Nullable Runnable myOnBeforeBlockingWait;
  protected char[] myBuf;
  protected int myOffset;
  protected int myLength;

  public TerminalDataStream(final TtyConnector ttyConnector) {
    myBuf = new char[1024];
    myOffset = 0;
    myLength = 0;
    myTtyConnector = ttyConnector;
    myOnBeforeBlockingWait = null;
  }

  private void fillBuf() throws IOException {
    myOffset = 0;

    if (!myTtyConnector.ready() && myOnBeforeBlockingWait != null) {
      myOnBeforeBlockingWait.run();
    }
    myLength = myTtyConnector.read(myBuf, myOffset, myBuf.length);

    if (myLength <= 0) {
      myLength = 0;
      throw new EOF();
    }
  }

  public char getChar() throws IOException {
    if (myLength == 0) {
      fillBuf();
    }
    if (myLength == 0) {
      throw new EOF();
    }

    myLength--;

    return myBuf[myOffset++];
  }

  public void pushChar(final char c) {
    if (myOffset == 0) {
      // Pushed back too many... shift it up to the end.

      char[] newBuf;
      if (myBuf.length - myLength == 0) {
        newBuf = new char[myBuf.length + 1];
      }
      else {
        newBuf = myBuf;
      }
      myOffset = newBuf.length - myLength;
      System.arraycopy(myBuf, 0, newBuf, myOffset, myLength);
      myBuf = newBuf;
    }

    myLength++;
    myBuf[--myOffset] = c;
  }

  public String readNonControlCharacters(int maxChars) throws IOException {
    if (myLength == 0) {
      fillBuf();
    }
    String nonControlCharacters = CharUtils.getNonControlCharacters(maxChars, myBuf, myOffset, myLength);

    myOffset += nonControlCharacters.length();
    myLength -= nonControlCharacters.length();

    return nonControlCharacters;
  }

  public void pushBackBuffer(final char[] bytes, final int length) {
    for (int i = length - 1; i >= 0; i--) {
      pushChar(bytes[i]);
    }
  }

  public boolean isEmpty() {
    return myLength == 0;
  }

  @Override
  public String toString() {
    return CharUtils.toHumanReadableText(new String(myBuf, myOffset, myLength));
  }

  public static class EOF extends IOException {
    public EOF() {
      super("EOF: There is no more data or connection is lost");
    }
  }
}
