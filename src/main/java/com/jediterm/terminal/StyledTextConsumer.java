package com.jediterm.terminal;

import com.jediterm.terminal.model.CharBuffer;

public class StyledTextConsumer {

  public void consume(int x, int y, TextStyle style, CharBuffer characters, int startRow) {
    // to override
  }

  public void consumeNul(int x, int y, int nulIndex, TextStyle style, CharBuffer characters, int startRow) {
    // to override
  }

  public void consumeQueue(int x, int y, int nulIndex, int startRow) {
    // to override
  }

}
