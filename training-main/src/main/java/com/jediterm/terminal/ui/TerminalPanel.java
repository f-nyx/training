package com.jediterm.terminal.ui;

import com.jediterm.terminal.StyledTextConsumer;
import com.jediterm.terminal.TerminalCopyPasteHandler;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.TextStyle.Option;
import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.emulator.charset.CharacterSets;
import com.jediterm.terminal.model.*;
import com.jediterm.terminal.util.CharUtils;
import com.jediterm.terminal.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

public class TerminalPanel extends JComponent {
  private static final Logger LOG = LoggerFactory.getLogger(TerminalPanel.class);
  private static final long serialVersionUID = -1048763516632093014L;

  /*font related*/
  private Font myNormalFont;
  private Font myItalicFont;
  private Font myBoldFont;
  private Font myBoldItalicFont;
  private int myDescent = 0;
  private int mySpaceBetweenLines = 0;
  protected Dimension myCharSize = new Dimension();
  protected Dimension myTermSize = new Dimension(80, 24);
  private Point mySelectionStartPoint = null;
  private TerminalSelection mySelection = null;

  private final TerminalCopyPasteHandler myCopyPasteHandler;

  private final SettingsProvider mySettingsProvider;
  private final TerminalTextBuffer myTerminalTextBuffer;
  final private StyleState myStyleState;

  private Timer myRepaintTimer;
  private final AtomicBoolean needRepaint = new AtomicBoolean(true);

  private final int myMaxFPS;

  public TerminalPanel(@NotNull SettingsProvider settingsProvider, @NotNull TerminalTextBuffer terminalTextBuffer, @NotNull StyleState styleState) {
    mySettingsProvider = settingsProvider;
    myTerminalTextBuffer = terminalTextBuffer;
    myStyleState = styleState;
    myTermSize.width = terminalTextBuffer.getWidth();
    myTermSize.height = terminalTextBuffer.getHeight();
    myMaxFPS = mySettingsProvider.maxRefreshRate();
    myCopyPasteHandler = new TerminalCopyPasteHandler();
    terminalTextBuffer.addModelListener(this::repaint);
  }

  private void doRepaint() {
    super.repaint();
  }

  @Override
  public void repaint() {
    needRepaint.set(true);
  }

  public void init() {
    initFont();

    setPreferredSize(new Dimension(getPixelWidth(), getPixelHeight()));
    setFocusable(true);
    setDoubleBuffered(true);
    setFocusTraversalKeysEnabled(false);

    addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(final MouseEvent e) {
        final Point charCoords = panelToCharCoords(e.getPoint());

        if (mySelection == null) {
          // prevent unlikely case where drag started outside terminal panel
          if (mySelectionStartPoint == null) {
            mySelectionStartPoint = charCoords;
          }
          mySelection = new TerminalSelection(new Point(mySelectionStartPoint));
        }
        repaint();
        mySelection.updateEnd(charCoords);
        handleCopyOnSelect();
      }
    });

    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(final MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          if (e.getClickCount() == 1) {
            mySelectionStartPoint = panelToCharCoords(e.getPoint());
            mySelection = null;
            repaint();
          }
        }
      }

      @Override
      public void mouseReleased(final MouseEvent e) {
        requestFocusInWindow();
        repaint();
      }
    });

    createRepaintTimer();
  }

  private void initFont() {
    myNormalFont = mySettingsProvider.getTerminalFont();
    myBoldFont = myNormalFont.deriveFont(Font.BOLD);
    myItalicFont = myNormalFont.deriveFont(Font.ITALIC);
    myBoldItalicFont = myNormalFont.deriveFont(Font.BOLD | Font.ITALIC);
    establishFontMetrics();
  }

  private void createRepaintTimer() {
    if (myRepaintTimer != null) {
      myRepaintTimer.stop();
    }
    myRepaintTimer = new Timer(1000 / myMaxFPS, new WeakRedrawTimer(this));
    myRepaintTimer.start();
  }

  static class WeakRedrawTimer implements ActionListener {

    private final WeakReference<TerminalPanel> ref;

    public WeakRedrawTimer(TerminalPanel terminalPanel) {
      this.ref = new WeakReference<>(terminalPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      TerminalPanel terminalPanel = ref.get();
      if (terminalPanel != null && terminalPanel.needRepaint.getAndSet(false)) {
        terminalPanel.doRepaint();
      }
    }
  }

  private @NotNull Point panelToCharCoords(final Point p) {
    Cell cell = panelPointToCell(p);
    return new Point(cell.getColumn(), cell.getLine());
  }

  private @NotNull Cell panelPointToCell(@NotNull Point p) {
    int x = Math.min((p.x - getInsetX()) / myCharSize.width, getColumnCount() - 1);
    x = Math.max(0, x);
    int y = Math.min(p.y / myCharSize.height, getRowCount() - 1);
    return new Cell(y, x);
  }

  private void copySelection(@Nullable Point selectionStart,
                             @Nullable Point selectionEnd) {
    if (selectionStart == null || selectionEnd == null) {
      return;
    }
    String selectionText = SelectionUtil.getSelectionText(selectionStart, selectionEnd, myTerminalTextBuffer);
    if (selectionText.length() != 0) {
      myCopyPasteHandler.setContents(selectionText, false);
    }
  }

  protected void drawImage(Graphics2D gfx, BufferedImage image, int x, int y, ImageObserver observer) {
    gfx.drawImage(image, x, y,
            image.getWidth(), image.getHeight(), observer);
  }

  private void establishFontMetrics() {
    final BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    final Graphics2D graphics = img.createGraphics();
    graphics.setFont(myNormalFont);

    final float lineSpacing = mySettingsProvider.getLineSpacing();
    final FontMetrics fo = graphics.getFontMetrics();

    myCharSize.width = fo.charWidth('W');
    int fontMetricsHeight = fo.getHeight();
    myCharSize.height = (int)Math.ceil(fontMetricsHeight * lineSpacing);
    mySpaceBetweenLines = Math.max(0, ((myCharSize.height - fontMetricsHeight) / 2) * 2);
    myDescent = fo.getDescent();
    if (LOG.isDebugEnabled()) {
      // The magic +2 here is to give lines a tiny bit of extra height to avoid clipping when rendering some Apple
      // emoji, which are slightly higher than the font metrics reported character height :(
      int oldCharHeight = fontMetricsHeight + (int) (lineSpacing * 2) + 2;
      int oldDescent = fo.getDescent() + (int)lineSpacing;
      LOG.debug("charHeight=" + oldCharHeight + "->" + myCharSize.height +
        ", descent=" + oldDescent + "->" + myDescent);
    }

    img.flush();
    graphics.dispose();
  }

  protected void setupAntialiasing(Graphics graphics) {
    if (graphics instanceof Graphics2D) {
      Graphics2D myGfx = (Graphics2D) graphics;
      final Object mode = mySettingsProvider.useAntialiasing() ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
              : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
      final RenderingHints hints = new RenderingHints(
              RenderingHints.KEY_TEXT_ANTIALIASING, mode);
      myGfx.setRenderingHints(hints);
    }
  }

  @Override
  public Color getBackground() {
    return getPalette().getBackground(myStyleState.getBackground());
  }

  @Override
  public Color getForeground() {
    return getPalette().getForeground(myStyleState.getForeground());
  }

  @Override
  public void paintComponent(final Graphics g) {
    final Graphics2D gfx = (Graphics2D) g;

    setupAntialiasing(gfx);

    gfx.setColor(getBackground());

    gfx.fillRect(0, 0, getWidth(), getHeight());

    try {
      myTerminalTextBuffer.lock();
      myTerminalTextBuffer.processHistoryAndScreenLines(0, myTermSize.height, new StyledTextConsumer() {
        final int columnCount = getColumnCount();

        @Override
        public void consume(int x, int y, @NotNull TextStyle style, @NotNull CharBuffer characters, int startRow) {
          int row = y - startRow;
          drawCharacters(x, row, style, characters, gfx, false);

          if (mySelection != null) {
            Pair<Integer, Integer> interval = mySelection.intersect(x, row, characters.length());
            if (interval != null) {
              TextStyle selectionStyle = getSelectionStyle(style);
              CharBuffer selectionChars = characters.subBuffer(interval.first - x, interval.second);

              drawCharacters(interval.first, row, selectionStyle, selectionChars, gfx);
            }
          }
        }

        @Override
        public void consumeNul(int x, int y, int nulIndex, @NotNull TextStyle style, @NotNull CharBuffer characters, int startRow) {
          int row = y - startRow;
          if (mySelection != null) {
            // compute intersection with all NUL areas, non-breaking
            Pair<Integer, Integer> interval = mySelection.intersect(nulIndex, row, columnCount - nulIndex);
            if (interval != null) {
              TextStyle selectionStyle = getSelectionStyle(style);
              drawCharacters(x, row, selectionStyle, characters, gfx);
              return;
            }
          }
          drawCharacters(x, row, style, characters, gfx);
        }

        @Override
        public void consumeQueue(int x, int y, int nulIndex, int startRow) {
          if (x < columnCount) {
            consumeNul(x, y, nulIndex, TextStyle.EMPTY, new CharBuffer(CharUtils.EMPTY_CHAR, columnCount - x), startRow);
          }
        }
      });
    } finally {
      myTerminalTextBuffer.unlock();
    }

    drawMargins(gfx, getWidth(), getHeight());
  }

  @NotNull
  private TextStyle getSelectionStyle(@NotNull TextStyle style) {
    if (mySettingsProvider.useInverseSelectionColor()) {
      return getInversedStyle(style);
    }
    TextStyle.Builder builder = style.toBuilder();
    TextStyle mySelectionStyle = mySettingsProvider.getSelectionColor();
    builder.setBackground(mySelectionStyle.getBackground());
    builder.setForeground(mySelectionStyle.getForeground());
    return builder.build();
  }

  @NotNull
  private TextStyle getInversedStyle(@NotNull TextStyle style) {
    TextStyle.Builder builder = new TextStyle.Builder(style);
    builder.setOption(Option.INVERSE, !style.hasOption(Option.INVERSE));
    if (style.getForeground() == null) {
      builder.setForeground(myStyleState.getForeground());
    }
    if (style.getBackground() == null) {
      builder.setBackground(myStyleState.getBackground());
    }
    return builder.build();
  }

  public int getPixelWidth() {
    return myCharSize.width * myTermSize.width + getInsetX();
  }

  public int getPixelHeight() {
    return myCharSize.height * myTermSize.height;
  }

  public int getColumnCount() {
    return myTermSize.width;
  }

  public int getRowCount() {
    return myTermSize.height;
  }

  public String getWindowTitle() {
    String myWindowTitle = "Terminal";
    return myWindowTitle;
  }

  protected int getInsetX() {
    return 4;
  }

  protected void drawImage(Graphics2D g, BufferedImage image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
    g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
  }

  private void drawCharacters(int x, int y, TextStyle style, CharBuffer buf, Graphics2D gfx) {
    drawCharacters(x, y, style, buf, gfx, true);
  }

  private void drawCharacters(int x, int y, TextStyle style, CharBuffer buf, Graphics2D gfx,
                              boolean includeSpaceBetweenLines) {
    int xCoord = x * myCharSize.width + getInsetX();
    int yCoord = y * myCharSize.height + (includeSpaceBetweenLines ? 0 : mySpaceBetweenLines / 2);

    if (xCoord < 0 || xCoord > getWidth() || yCoord < 0 || yCoord > getHeight()) {
      return;
    }

    int textLength = CharUtils.getTextLengthDoubleWidthAware(buf.getBuf(), buf.getStart(), buf.length(), mySettingsProvider.ambiguousCharsAreDoubleWidth());
    int height = Math.min(myCharSize.height - (includeSpaceBetweenLines ? 0 : mySpaceBetweenLines), getHeight() - yCoord);
    int width = Math.min(textLength * TerminalPanel.this.myCharSize.width, TerminalPanel.this.getWidth() - xCoord);

    Color backgroundColor = getPalette().getBackground(myStyleState.getBackground(style.getBackgroundForRun()));
    gfx.setColor(backgroundColor);
    gfx.fillRect(xCoord,
            yCoord,
            width,
            height);

    if (buf.isNul()) {
      return; // nothing more to do
    }

    drawChars(x, y, buf, style, gfx);

    gfx.setColor(getStyleForeground(style));


    if (style.hasOption(Option.UNDERLINED)) {
      int baseLine = (y + 1) * myCharSize.height - mySpaceBetweenLines / 2 - myDescent;
      int lineY = baseLine + 3;
      gfx.drawLine(xCoord, lineY, (x + textLength) * myCharSize.width + getInsetX(), lineY);
    }
  }

  /**
   * Draw every char in separate terminal cell to guaranty equal width for different lines.
   * Nevertheless to improve kerning we draw word characters as one block for monospaced fonts.
   */
  private void drawChars(int x, int y, CharBuffer buf, TextStyle style, Graphics2D gfx) {
    int blockLen = 1;
    int offset = 0;
    int drawCharsOffset = 0;

    // workaround to fix Swing bad rendering of bold special chars on Linux
    // TODO required for italic?
    CharBuffer renderingBuffer;
    if (mySettingsProvider.DECCompatibilityMode() && style.hasOption(Option.BOLD)) {
      renderingBuffer = CharUtils.heavyDecCompatibleBuffer(buf);
    } else {
      renderingBuffer = buf;
    }

    while (offset + blockLen <= buf.length()) {
      if (renderingBuffer.getBuf()[buf.getStart() + offset] == CharUtils.DWC) {
        offset += blockLen;
        drawCharsOffset += blockLen;
        continue; // dont' draw second part(fake one) of double width character
      }

      Font font = getFontToDisplay(buf.charAt(offset + blockLen - 1), style);

      if (offset + 2 <= buf.length() && Character.isSurrogatePair(renderingBuffer.getBuf()[buf.getStart() + offset], renderingBuffer.getBuf()[buf.getStart() + offset + 1])) {
        blockLen = 2;
      }

      gfx.setFont(font);

      int descent = gfx.getFontMetrics(font).getDescent();
      int baseLine = (y + 1) * myCharSize.height - mySpaceBetweenLines / 2 - descent;
      int xCoord = (x + drawCharsOffset) * myCharSize.width + getInsetX();
      int yCoord = y * myCharSize.height + mySpaceBetweenLines / 2;

      gfx.setClip(xCoord,
              yCoord,
              getWidth() - xCoord,
              getHeight() - yCoord);

      gfx.setColor(getStyleForeground(style));
      gfx.drawChars(renderingBuffer.getBuf(), buf.getStart() + offset, blockLen, xCoord, baseLine);

      drawCharsOffset += blockLen;
      offset += blockLen;
      blockLen = 1;
    }
    gfx.setClip(null);
  }

  private @NotNull Color getStyleForeground(@NotNull TextStyle style) {
    Color foreground = getPalette().getForeground(myStyleState.getForeground(style.getForegroundForRun()));
    if (style.hasOption(Option.DIM)) {
      Color background = getPalette().getBackground(myStyleState.getBackground(style.getBackgroundForRun()));
      foreground = new Color((foreground.getRed() + background.getRed()) / 2,
                             (foreground.getGreen() + background.getGreen()) / 2,
                             (foreground.getBlue() + background.getBlue()) / 2,
                             foreground.getAlpha());
    }
    return foreground;
  }

  protected Font getFontToDisplay(char c, TextStyle style) {
    boolean bold = style.hasOption(Option.BOLD);
    boolean italic = style.hasOption(Option.ITALIC);
    // workaround to fix Swing bad rendering of bold special chars on Linux
    if (bold && mySettingsProvider.DECCompatibilityMode() && CharacterSets.isDecBoxChar(c)) {
      return myNormalFont;
    }
    return bold ? (italic ? myBoldItalicFont : myBoldFont)
            : (italic ? myItalicFont : myNormalFont);
  }

  private ColorPalette getPalette() {
    return mySettingsProvider.getTerminalColorPalette();
  }

  private void drawMargins(Graphics2D gfx, int width, int height) {
    gfx.setColor(getBackground());
    gfx.fillRect(0, height, getWidth(), getHeight() - height);
    gfx.fillRect(width, 0, getWidth() - width, getHeight());
  }

  public TerminalSelection getSelection() {
    return mySelection;
  }

  /**
   * Copies selected text to clipboard.
   */
  private void handleCopyOnSelect() {
    if (mySelection != null) {
      Pair<Point, Point> points = mySelection.pointsForRun(myTermSize.width);
      copySelection(points.first, points.second);
    }
  }

  public void dispose() {
    myRepaintTimer.stop();
  }
}
