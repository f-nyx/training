package com.jediterm.terminal.model;

import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TerminalMode;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.emulator.charset.CharacterSet;
import com.jediterm.terminal.emulator.charset.GraphicSet;
import com.jediterm.terminal.emulator.charset.GraphicSetState;
import com.jediterm.terminal.ui.SettingsProvider;
import com.jediterm.terminal.util.CharUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.*;

public class PtyTerminal {
  private static final Logger LOG = LoggerFactory.getLogger(PtyTerminal.class.getName());

  volatile private int myCursorX = 0;
  volatile private int myCursorY = 1;

  private final int myTerminalWidth;
  private final int myTerminalHeight;

  private final TerminalTextBuffer myTerminalTextBuffer;

  private final StyleState myStyleState;

  private StoredCursor myStoredCursor = null;

  private final EnumSet<TerminalMode> myModes = EnumSet.noneOf(TerminalMode.class);

  private final Tabulator myTabulator;
  private final GraphicSetState myGraphicSetState;
  private final SettingsProvider mySettingsProvider;

  public PtyTerminal(SettingsProvider settingsProvider, final TerminalTextBuffer buf, final StyleState initialStyleState) {
    mySettingsProvider = settingsProvider;
    myTerminalTextBuffer = buf;
    myStyleState = initialStyleState;

    myTerminalWidth = buf.getWidth();
    myTerminalHeight = buf.getHeight();
    myTabulator = new DefaultTabulator(myTerminalWidth);

    myGraphicSetState = new GraphicSetState();

    reset();
  }

  public TerminalTextBuffer buffer() {
    return myTerminalTextBuffer;
  }

  public void setModeEnabled(TerminalMode mode, boolean enabled) {
    if (enabled) {
      myModes.add(mode);
    } else {
      myModes.remove(mode);
    }
  }

  public void writeCharacters(String string) {
    writeDecodedCharacters(decodeUsingGraphicalState(string));
  }

  private void writeDecodedCharacters(char[] string) {
    myTerminalTextBuffer.lock();
    try {
      if (myCursorY < 1) {
        myCursorY = 1;
      }

      if (string.length != 0) {
        CharBuffer characters = newCharBuf(string);
        myTerminalTextBuffer.writeString(myCursorX, myCursorY, characters);
        myCursorX += characters.length();
      }
    } finally {
      myTerminalTextBuffer.unlock();
    }
  }

  private char[] decodeUsingGraphicalState(String string) {
    StringBuilder result = new StringBuilder();
    for (char c : string.toCharArray()) {
      result.append(myGraphicSetState.map(c));
    }

    return result.toString().toCharArray();
  }

  public void newLine() {
    myCursorY += 1;

    if (isAutoNewLine()) {
      carriageReturn();
    }
  }

  public void mapCharsetToGL(int num) {
    myGraphicSetState.setGL(num);
  }

  public void mapCharsetToGR(int num) {
    myGraphicSetState.setGR(num);
  }

  public void designateCharacterSet(int tableNumber, char charset) {
    GraphicSet gs = myGraphicSetState.getGraphicSet(tableNumber);
    myGraphicSetState.designateGraphicSet(gs, charset);
  }

  public void singleShiftSelect(int num) {
    myGraphicSetState.overrideGL(num);
  }

  public void setAnsiConformanceLevel(int level) {
    if (level == 1 || level == 2) {
      myGraphicSetState.designateGraphicSet(0, CharacterSet.ASCII); //ASCII designated as G0
      myGraphicSetState
              .designateGraphicSet(1, CharacterSet.DEC_SUPPLEMENTAL); //TODO: not DEC supplemental, but ISO Latin-1 supplemental designated as G1
      mapCharsetToGL(0);
      mapCharsetToGR(1);
    } else if (level == 3) {
      designateCharacterSet(0, 'B'); //ASCII designated as G0
      mapCharsetToGL(0);
    } else {
      throw new IllegalArgumentException();
    }
  }

  public @Nullable TerminalColor getWindowForeground() {
    Color windowForeground = getForeground();
    return new TerminalColor(windowForeground.getRed(), windowForeground.getGreen(), windowForeground.getBlue());
  }

  public @Nullable TerminalColor getWindowBackground() {
    Color windowBackground = getBackground();

    // Return RGB color because we don't have palette information outside of TerminalPanel.
    return new TerminalColor(windowBackground.getRed(), windowBackground.getGreen(), windowBackground.getBlue());
  }

  public Color getBackground() {
    return getPalette().getBackground(myStyleState.getBackground());
  }

  public Color getForeground() {
    return getPalette().getForeground(myStyleState.getForeground());
  }

  private ColorPalette getPalette() {
    return mySettingsProvider.getTerminalColorPalette();
  }

  public void backspace() {
    myCursorX -= 1;
    if (myCursorX < 0) {
      myCursorY -= 1;
      myCursorX = myTerminalWidth - 1;
    }
    adjustXY(-1);
  }

  public void carriageReturn() {
    myCursorX = 0;
  }

  public void horizontalTab() {
    if (myCursorX >= myTerminalWidth) {
      return;
    }
    int length = myTerminalTextBuffer.getLine(myCursorY - 1).getText().length();
    int stop = myTabulator.nextTab(myCursorX);
    myCursorX = Math.max(myCursorX, length);
    if (myCursorX < stop) {
      char[] chars = new char[stop - myCursorX];
      Arrays.fill(chars, CharUtils.EMPTY_CHAR);
      writeDecodedCharacters(chars);
    } else {
      myCursorX = stop;
    }
    adjustXY(+1);
  }

  public void eraseInDisplay(final int arg) {
    myTerminalTextBuffer.lock();
    try {
      int beginY;
      int endY;

      switch (arg) {
        case 0:
          // Initial line
          if (myCursorX < myTerminalWidth) {
            myTerminalTextBuffer.eraseCharacters(myCursorX, -1, myCursorY - 1);
          }
          // Rest
          beginY = myCursorY;
          endY = myTerminalHeight - 1;

          break;
        case 1:
          // initial line
          myTerminalTextBuffer.eraseCharacters(0, myCursorX + 1, myCursorY - 1);

          beginY = 0;
          endY = myCursorY - 1;
          break;
        case 2:
          beginY = 0;
          endY = myTerminalHeight - 1;
          break;
        default:
          LOG.error("Unsupported erase in display mode:" + arg);
          beginY = 1;
          endY = 1;
          break;
      }
      // Rest of lines
      if (beginY != endY) {
        clearLines(beginY, endY);
      }
    } finally {
      myTerminalTextBuffer.unlock();
    }
  }

  public void clearLines(final int beginY, final int endY) {
    myTerminalTextBuffer.lock();
    try {
      myTerminalTextBuffer.clearLines(beginY, endY);
    } finally {
      myTerminalTextBuffer.unlock();
    }
  }

  public void eraseInLine(int arg) {
    myTerminalTextBuffer.lock();
    try {
      switch (arg) {
        case 0:
          if (myCursorX < myTerminalWidth) {
            myTerminalTextBuffer.eraseCharacters(myCursorX, -1, myCursorY - 1);
          }
          // delete to the end of line : line is no more wrapped
          myTerminalTextBuffer.getLine(myCursorY - 1).setWrapped(false);
          break;
        case 1:
          final int extent = Math.min(myCursorX + 1, myTerminalWidth);
          myTerminalTextBuffer.eraseCharacters(0, extent, myCursorY - 1);
          break;
        case 2:
          myTerminalTextBuffer.eraseCharacters(0, -1, myCursorY - 1);
          break;
        default:
          LOG.error("Unsupported erase in line mode:" + arg);
          break;
      }
    } finally {
      myTerminalTextBuffer.unlock();
    }
  }

  public void deleteCharacters(int count) {
    myTerminalTextBuffer.lock();
    try {
      myTerminalTextBuffer.deleteCharacters(myCursorX, myCursorY - 1, count);
    } finally {
      myTerminalTextBuffer.unlock();
    }
  }

  public void insertBlankCharacters(int count) {
    myTerminalTextBuffer.lock();
    try {
      final int extent = Math.min(count, myTerminalWidth - myCursorX);
      myTerminalTextBuffer.insertBlankCharacters(myCursorX, myCursorY - 1, extent);
    } finally {
      myTerminalTextBuffer.unlock();
    }
  }

  public void eraseCharacters(int count) {
    //Clear the next n characters on the cursor's line, including the cursor's
    //position.
    myTerminalTextBuffer.lock();
    try {
      myTerminalTextBuffer.eraseCharacters(myCursorX, myCursorX + count, myCursorY - 1);
    } finally {
      myTerminalTextBuffer.unlock();
    }
  }

  public void clearTabStopAtCursor() {
    myTabulator.clearTabStop(myCursorX);
  }

  public void clearAllTabStops() {
    myTabulator.clearAllTabStops();
  }

  public void setTabStopAtCursor() {
    myTabulator.setTabStop(myCursorX);
  }

  public void insertLines(int count) {
    myTerminalTextBuffer.lock();
    try {
      myTerminalTextBuffer.insertLines(myCursorY - 1, count, 0);
    } finally {
      myTerminalTextBuffer.unlock();
    }
  }

  public void deleteLines(int count) {
    myTerminalTextBuffer.lock();
    try {
      myTerminalTextBuffer.deleteLines(myCursorY - 1, count, 0);
    } finally {
      myTerminalTextBuffer.unlock();
    }
  }

  public void cursorUp(final int countY) {
    myTerminalTextBuffer.lock();
    try {
//      myCursorYChanged = true;
      myCursorY -= countY;
      adjustXY(-1);
    } finally {
      myTerminalTextBuffer.unlock();
    }
  }

  public void cursorDown(final int dY) {
    myTerminalTextBuffer.lock();
    try {
//      myCursorYChanged = true;
      myCursorY += dY;
      adjustXY(-1);
      
    } finally {
      myTerminalTextBuffer.unlock();
    }
  }

//  public void index() {
//    //Moves the cursor down one line in the
//    //same column. If the cursor is at the
//    //bottom margin, the page scrolls up
//    myTerminalTextBuffer.lock();
//    try {
//      if (myCursorY == myScrollRegionBottom) {
//        scrollArea(myScrollRegionTop, scrollingRegionSize(), -1);
//      } else {
//        myCursorY += 1;
//        adjustXY(-1);
//
//      }
//    } finally {
//      myTerminalTextBuffer.unlock();
//    }
//  }

//  private void scrollArea(int scrollRegionTop, int scrollRegionSize, int dy) {
////    myTerminalTextBuffer.scrollArea(scrollRegionTop, dy, scrollRegionTop + scrollRegionSize - 1);
//    System.out.println("foo");
//  }

  public void nextLine() {
    myTerminalTextBuffer.lock();
    try {
      myCursorX = 0;
      myCursorY += 1;
    } finally {
      myTerminalTextBuffer.unlock();
    }
  }

//  private int scrollingRegionSize() {
//    return myScrollRegionBottom - myScrollRegionTop + 1;
//  }

//  public void reverseIndex() {
//    //Moves the cursor up one line in the same
//    //column. If the cursor is at the top margin,
//    //the page scrolls down.
//    myTerminalTextBuffer.lock();
//    try {
//      if (myCursorY == myScrollRegionTop) {
//        scrollArea(myScrollRegionTop, scrollingRegionSize(), 1);
//      } else {
//        myCursorY -= 1;
//
//      }
//    } finally {
//      myTerminalTextBuffer.unlock();
//    }
//  }

//  private int scrollingRegionTop() {
//    return isOriginMode() ? myScrollRegionTop : 1;
//  }
//
//  private int scrollingRegionBottom() {
//    return isOriginMode() ? myScrollRegionBottom : myTerminalHeight;
//  }

  public void cursorForward(final int dX) {
    myCursorX += dX;
    myCursorX = Math.min(myCursorX, myTerminalWidth - 1);
    adjustXY(+1);
    
  }

  public void cursorBackward(final int dX) {
    myCursorX -= dX;
    myCursorX = Math.max(myCursorX, 0);
    adjustXY(-1);
    
  }

  public void cursorHorizontalAbsolute(int x) {
    cursorPosition(x, myCursorY);
  }

  public void linePositionAbsolute(int y) {
    myCursorY = y;
    adjustXY(-1);
  }

  public void cursorPosition(int x, int y) {
    myCursorY = y;
    // avoid issue due to malformed sequence
    myCursorX = Math.max(0, x - 1);
    myCursorX = Math.min(myCursorX, myTerminalWidth - 1);
    myCursorY = Math.max(0, myCursorY);
    adjustXY(-1);
  }

  public void characterAttributes(final TextStyle textStyle) {
    myStyleState.setCurrent(textStyle);
  }

  public void beep() {
    Toolkit.getDefaultToolkit().beep();
  }

  public int distanceToLineEnd() {
    return myTerminalWidth - myCursorX;
  }

  public void saveCursor() {
    myStoredCursor = createCursorState();
  }

  private StoredCursor createCursorState() {
    return new StoredCursor(myCursorX, myCursorY, myStyleState.getCurrent(),
            isAutoWrap(), false, myGraphicSetState);
  }

  public void restoreCursor() {
    if (myStoredCursor != null) {
      restoreCursor(myStoredCursor);
    } else { //If nothing was saved by DECSC
      cursorPosition(1, 1); //Moves the cursor to the home position (upper left of screen).
      myStyleState.reset(); //Turns all character attributes off (normal setting).

      myGraphicSetState.resetState();
    }
  }

  public void restoreCursor(@NotNull StoredCursor storedCursor) {
    myCursorX = storedCursor.getCursorX();
    myCursorY = storedCursor.getCursorY();

    adjustXY(-1);

    myStyleState.setCurrent(storedCursor.getTextStyle());

    setModeEnabled(TerminalMode.AutoWrap, storedCursor.isAutoWrap());

    CharacterSet[] designations = storedCursor.getDesignations();
    for (int i = 0; i < designations.length; i++) {
      myGraphicSetState.designateGraphicSet(i, designations[i]);
    }
    myGraphicSetState.setGL(storedCursor.getGLMapping());
    myGraphicSetState.setGR(storedCursor.getGRMapping());

    if (storedCursor.getGLOverride() >= 0) {
      myGraphicSetState.overrideGL(storedCursor.getGLOverride());
    }
  }

  public void reset() {
    myGraphicSetState.resetState();
    myStyleState.reset();
    myTerminalTextBuffer.clearAll();

    initModes();
    cursorPosition(1, 1);
  }

  private void initModes() {
    myModes.clear();
    setModeEnabled(TerminalMode.AutoWrap, true);
  }

  public boolean isAutoNewLine() {
    return false;
  }

  public boolean isAutoWrap() {
    return myModes.contains(TerminalMode.AutoWrap);
  }

  private void adjustXY(int dirX) {
    if (myCursorY > -myTerminalTextBuffer.getHistoryLinesCount() &&
        Character.isLowSurrogate(myTerminalTextBuffer.getCharAt(myCursorX, myCursorY - 1))) {
      // we don't want to place cursor on the second part of surrogate pair
      if (dirX > 0) { // so we move it into the predefined direction
        if (myCursorX == myTerminalWidth) { //if it is the last in the line we return where we were
          myCursorX -= 1;
        } else {
          myCursorX += 1;
        }
      } else {
        myCursorX -= 1; //low surrogate character can't be the first character in the line
      }
    }
  }

  public int getX() {
    return myCursorX;
  }

  public void setX(int x) {
    myCursorX = x;
    adjustXY(-1);
  }

  public int getY() {
    return myCursorY;
  }

  public void setY(int y) {
    myCursorY = y;
    adjustXY(-1);
  }

  public void writeString(String s) {
    writeCharacters(s);
  }

  public void fillScreen(final char c) {
    myTerminalTextBuffer.lock();
    try {
      final char[] chars = new char[myTerminalWidth];
      Arrays.fill(chars, c);

      for (int row = 1; row <= myTerminalHeight; row++) {
        myTerminalTextBuffer.writeString(0, row, newCharBuf(chars));
      }
    } finally {
      myTerminalTextBuffer.unlock();
    }
  }

  @NotNull
  private CharBuffer newCharBuf(char[] str) {
    int dwcCount = CharUtils.countDoubleWidthCharacters(str, 0, str.length, mySettingsProvider.ambiguousCharsAreDoubleWidth());

    char[] buf;

    if (dwcCount > 0) {
      // Leave gaps for the private use "DWC" character, which simply tells the rendering code to advance one cell.
      buf = new char[str.length + dwcCount];

      int j = 0;
      for (int i = 0; i < str.length; i++) {
        buf[j] = str[i];
        int codePoint = Character.codePointAt(str, i);
        boolean doubleWidthCharacter = CharUtils.isDoubleWidthCharacter(codePoint, mySettingsProvider.ambiguousCharsAreDoubleWidth());
        if (doubleWidthCharacter) {
          j++;
          buf[j] = CharUtils.DWC;
        }
        j++;
      }
    } else {
      buf = str;
    }
    return new CharBuffer(buf, 0, buf.length);
  }

  public int getTerminalHeight() {
    return myTerminalHeight;
  }

  public StyleState getStyleState() {
    return myStyleState;
  }

  private static class DefaultTabulator implements Tabulator {
    private static final int TAB_LENGTH = 8;

    private final SortedSet<Integer> myTabStops;

    private int myWidth;
    private int myTabLength;

    public DefaultTabulator(int width) {
      this(width, TAB_LENGTH);
    }

    public DefaultTabulator(int width, int tabLength) {
      myTabStops = new TreeSet<Integer>();

      myWidth = width;
      myTabLength = tabLength;

      initTabStops(width, tabLength);
    }

    private void initTabStops(int columns, int tabLength) {
      for (int i = tabLength; i < columns; i += tabLength) {
        myTabStops.add(i);
      }
    }

    public void resize(int columns) {
      if (columns > myWidth) {
        for (int i = myTabLength * (myWidth / myTabLength); i < columns; i += myTabLength) {
          if (i >= myWidth) {
            myTabStops.add(i);
          }
        }
      } else {
        Iterator<Integer> it = myTabStops.iterator();
        while (it.hasNext()) {
          int i = it.next();
          if (i > columns) {
            it.remove();
          }
        }
      }

      myWidth = columns;
    }

    @Override
    public void clearTabStop(int position) {
      myTabStops.remove(Integer.valueOf(position));
    }

    @Override
    public void clearAllTabStops() {
      myTabStops.clear();
    }

    @Override
    public int getNextTabWidth(int position) {
      return nextTab(position) - position;
    }

    @Override
    public int getPreviousTabWidth(int position) {
      return position - previousTab(position);
    }

    @Override
    public int nextTab(int position) {
      int tabStop = Integer.MAX_VALUE;

      // Search for the first tab stop after the given position...
      SortedSet<Integer> tailSet = myTabStops.tailSet(position + 1);
      if (!tailSet.isEmpty()) {
        tabStop = tailSet.first();
      }

      // Don't go beyond the end of the line...
      return Math.min(tabStop, (myWidth - 1));
    }

    @Override
    public int previousTab(int position) {
      int tabStop = 0;

      // Search for the first tab stop before the given position...
      SortedSet<Integer> headSet = myTabStops.headSet(Integer.valueOf(position));
      if (!headSet.isEmpty()) {
        tabStop = headSet.last();
      }

      // Don't go beyond the start of the line...
      return Math.max(0, tabStop);
    }

    @Override
    public void setTabStop(int position) {
      myTabStops.add(Integer.valueOf(position));
    }
  }
}
