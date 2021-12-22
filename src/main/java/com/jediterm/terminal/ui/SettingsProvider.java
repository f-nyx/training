package com.jediterm.terminal.ui;

import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.model.LinesBuffer;
import com.jediterm.terminal.util.UIUtil;

import java.awt.*;

public class SettingsProvider {

  public ColorPalette getTerminalColorPalette() {
    return UIUtil.isWindows ? ColorPalette.WINDOWS_PALETTE : ColorPalette.XTERM_PALETTE;
  }

  public Font getTerminalFont() {
    String fontName;
    if (UIUtil.isWindows) {
      fontName = "Consolas";
    } else if (UIUtil.isMac) {
      fontName = "Menlo";
    } else {
      fontName = "Monospaced";
    }
    return new Font(fontName, Font.PLAIN, (int)getTerminalFontSize());
  }

  public float getTerminalFontSize() {
    return 14;
  }

  public TextStyle getDefaultStyle() {
    return new TextStyle(TerminalColor.BLACK, TerminalColor.WHITE);
    // return new TextStyle(TerminalColor.WHITE, TerminalColor.rgb(24, 24, 24));
  }

  public TextStyle getSelectionColor() {
    return new TextStyle(TerminalColor.WHITE, TerminalColor.rgb(82, 109, 165));
  }

  public TextStyle getFoundPatternColor() {
    return new TextStyle(TerminalColor.BLACK, TerminalColor.rgb(255, 255, 0));
  }

  public TextStyle getHyperlinkColor() {
    return new TextStyle(TerminalColor.awt(Color.BLUE), TerminalColor.WHITE);
  }

  public boolean useInverseSelectionColor() {
    return true;
  }

  public boolean copyOnSelect() {
    return emulateX11CopyPaste();
  }

  public boolean pasteOnMiddleMouseClick() {
    return emulateX11CopyPaste();
  }

  public boolean emulateX11CopyPaste() {
    return false;
  }

  public boolean useAntialiasing() {
    return true;
  }

  public int maxRefreshRate() {
    return 50;
  }

  public boolean audibleBell() {
    return true;
  }

  public boolean enableMouseReporting() {
    return true;
  }

  public int caretBlinkingMs() {
    return 505;
  }

  public boolean scrollToBottomOnTyping() {
    return true;
  }

  public boolean DECCompatibilityMode() {
    return true;
  }

  public boolean forceActionOnMouseReporting() {
    return false;
  }

  public int getBufferMaxLinesCount() {
    return LinesBuffer.DEFAULT_MAX_LINES_COUNT;
  }

  public boolean altSendsEscape() {
    return true;
  }

  public boolean ambiguousCharsAreDoubleWidth() {
    return false;
  }

  /**
   * @return vertical scaling factor
   */
  public float getLineSpacing() {
    return 1.0f;
  }
}
