package com.jediterm.terminal.model;

import com.google.common.collect.Maps;
import com.jediterm.terminal.StyledTextConsumer;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.model.TerminalLine.TextEntry;
import com.jediterm.terminal.util.CharUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Buffer for storing styled text data.
 * Stores only text that fit into one screen XxY, but has scrollBuffer to save history lines and screenBuffer to restore
 * screen after resize. ScrollBuffer stores all lines before the first line currently shown on the screen. TextBuffer
 * stores lines that are shown currently on the screen and they have there(in TextBuffer) their initial length (even if
 * it doesn't fit to screen width).
 * <p/>
 */
public class TerminalTextBuffer {
  private static final Logger LOG = LoggerFactory.getLogger(TerminalTextBuffer.class);

  @NotNull
  private final StyleState myStyleState;
  private final LinesBuffer myScreenBuffer;

  private final int myWidth;
  private final int myHeight;
  private final Lock myLock = new ReentrantLock();
  private final List<TerminalModelListener> myListeners = new CopyOnWriteArrayList<>();

  public TerminalTextBuffer(final int width, final int height, @NotNull StyleState styleState) {
    myStyleState = styleState;
    myWidth = width;
    myHeight = height;
    myScreenBuffer = new LinesBuffer(-1);
  }

  public void addModelListener(TerminalModelListener listener) {
    myListeners.add(listener);
  }

  private void fireModelChangeEvent() {
    for (TerminalModelListener modelListener : myListeners) {
      modelListener.modelChanged();
    }
  }

  private TextStyle createEmptyStyleWithCurrentColor() {
    return myStyleState.getCurrent().createEmptyWithColors();
  }

  private TextEntry createFillerEntry() {
    return new TextEntry(createEmptyStyleWithCurrentColor(), new CharBuffer(CharUtils.NUL_CHAR, myWidth));
  }

  public void deleteCharacters(final int x, final int y, final int count) {
    if (y > myHeight - 1 || y < 0) {
      LOG.error("attempt to delete in line " + y + "\n" +
              "args were x:" + x + " count:" + count);
    } else if (count < 0) {
      LOG.error("Attempt to delete negative chars number: count:" + count);
    } else if (count > 0) {
      myScreenBuffer.deleteCharacters(x, y, count, createEmptyStyleWithCurrentColor());

      fireModelChangeEvent();
    }
  }

  public void insertBlankCharacters(final int x, final int y, final int count) {
    if (y > myHeight - 1 || y < 0) {
      LOG.error("attempt to insert blank chars in line " + y + "\n" +
              "args were x:" + x + " count:" + count);
    } else if (count < 0) {
      LOG.error("Attempt to insert negative blank chars number: count:" + count);
    } else if (count > 0) { //nothing to do
      myScreenBuffer.insertBlankCharacters(x, y, count, myWidth, createEmptyStyleWithCurrentColor());

      fireModelChangeEvent();
    }
  }

  public void writeString(final int x, final int y, @NotNull final CharBuffer str) {
    writeString(x, y, str, myStyleState.getCurrent());
  }

  private void writeString(int x, int y, @NotNull CharBuffer str, @NotNull TextStyle style) {
    myScreenBuffer.writeString(x, y - 1, str, style);

    fireModelChangeEvent();
  }

  public String getStyleLines() {
    final Map<Integer, Integer> hashMap = Maps.newHashMap();
    myLock.lock();
    try {
      final StringBuilder sb = new StringBuilder();
      myScreenBuffer.processLines(0, myHeight, new StyledTextConsumer() {
        int count = 0;

        @Override
        public void consume(int x, int y, @NotNull TextStyle style, @NotNull CharBuffer characters, int startRow) {
          if (x == 0) {
            sb.append("\n");
          }
          int styleNum = style.getId();
          if (!hashMap.containsKey(styleNum)) {
            hashMap.put(styleNum, count++);
          }
          sb.append(String.format("%02d ", hashMap.get(styleNum)));
        }
      });
      return sb.toString();
    } finally {
      myLock.unlock();
    }
  }

  /**
   * Returns terminal lines. Negative indexes are for history buffer. Non-negative for screen buffer.
   *
   * @param index index of line
   * @return history lines for index<0, screen line for index>=0
   */
  public TerminalLine getLine(int index) {
    if (index >= getHeight()) {
      LOG.error("Attempt to get line out of bounds: " + index + " >= " + getHeight());
      return TerminalLine.createEmpty();
    }
    return myScreenBuffer.getLine(index);
  }

  public String getScreenLines() {
    myLock.lock();
    try {
      final StringBuilder sb = new StringBuilder();
      for (int row = 0; row < myHeight; row++) {
        StringBuilder line = new StringBuilder(myScreenBuffer.getLine(row).getText());

        for (int i = line.length(); i < myWidth; i++) {
          line.append(' ');
        }
        if (line.length() > myWidth) {
          line.setLength(myWidth);
        }

        sb.append(line);
        sb.append('\n');
      }
      return sb.toString();
    } finally {
      myLock.unlock();
    }
  }

  public void lock() {
    myLock.lock();
  }

  public void unlock() {
    myLock.unlock();
  }

  public int getWidth() {
    return myWidth;
  }

  public int getHeight() {
    return myHeight;
  }

  public int getHistoryLinesCount() {
    return 0;
  }

  public char getBuffersCharAt(int x, int y) {
    return getLine(y).charAt(x);
  }

  public TextStyle getStyleAt(int x, int y) {
    return getLine(y).getStyleAt(x);
  }

  public char getCharAt(int x, int y) {
    synchronized (myScreenBuffer) {
      TerminalLine line = getLine(y);
      return line.charAt(x);
    }
  }

  public LinesBuffer getHistoryBuffer() {
    return myScreenBuffer;
  }

  public void insertLines(int y, int count, int scrollRegionBottom) {
    myScreenBuffer.insertLines(y, count, scrollRegionBottom - 1, createFillerEntry());

    fireModelChangeEvent();
  }

  // returns deleted lines
  public LinesBuffer deleteLines(int y, int count, int scrollRegionBottom) {
    LinesBuffer linesBuffer = myScreenBuffer.deleteLines(y, count, scrollRegionBottom - 1, createFillerEntry());
    fireModelChangeEvent();
    return linesBuffer;
  }

  public void clearLines(int startRow, int endRow) {
    myScreenBuffer.clearLines(startRow, endRow, createFillerEntry());
    fireModelChangeEvent();
  }

  public void eraseCharacters(int leftX, int rightX, int y) {
    TextStyle style = createEmptyStyleWithCurrentColor();
    if (y >= 0) {
      myScreenBuffer.clearArea(leftX, y, rightX, y + 1, style);
      fireModelChangeEvent();
    } else {
      LOG.error("Attempt to erase characters in line: " + y);
    }
  }

  public void clearAll() {
    myScreenBuffer.clearAll();
    fireModelChangeEvent();
  }

  /**
   * @param scrollOrigin row where a scrolling window starts, should be in the range [-history_lines_count, 0]
   */
  public void processHistoryAndScreenLines(int scrollOrigin, int maximalLinesToProcess, StyledTextConsumer consumer) {
    if (maximalLinesToProcess<0) {
      //Process all lines in this case
      
      maximalLinesToProcess = myScreenBuffer.getLineCount();
    }

    int linesFromHistory = Math.min(-scrollOrigin, maximalLinesToProcess);
    myScreenBuffer.processLines(0, maximalLinesToProcess - linesFromHistory, consumer, -linesFromHistory);
  }

  @NotNull
  LinesBuffer getHistoryBufferOrBackup() {
    return null;
  }

  @NotNull
  LinesBuffer getScreenBufferOrBackup() {
    return myScreenBuffer;
  }
}
