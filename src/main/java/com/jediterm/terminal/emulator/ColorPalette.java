package com.jediterm.terminal.emulator;

import com.jediterm.terminal.TerminalColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class ColorPalette {

  private static final Color[] XTERM_COLORS = new Color[]{
    new Color(0x000000), //Black
    new Color(0xcd0000), //Red
    new Color(0x00cd00), //Green
    new Color(0xcdcd00), //Yellow
    new Color(0x1e90ff), //Blue
    new Color(0xcd00cd), //Magenta
    new Color(0x00cdcd), //Cyan
    new Color(0xe5e5e5), //White
    //Bright versions of the ISO colors
    new Color(0x4c4c4c), //Black
    new Color(0xff0000), //Red
    new Color(0x00ff00), //Green
    new Color(0xffff00), //Yellow
    new Color(0x4682b4), //Blue
    new Color(0xff00ff), //Magenta
    new Color(0x00ffff), //Cyan
    new Color(0xffffff), //White
  };

  public static final ColorPalette XTERM_PALETTE = new ColorPalette(XTERM_COLORS);

  private static final Color[] WINDOWS_COLORS = new Color[]{
    new Color(0x000000), //Black
    new Color(0x800000), //Red
    new Color(0x008000), //Green
    new Color(0x808000), //Yellow
    new Color(0x000080), //Blue
    new Color(0x800080), //Magenta
    new Color(0x008080), //Cyan
    new Color(0xc0c0c0), //White
    //Bright versions of the ISO colors
    new Color(0x808080), //Black
    new Color(0xff0000), //Red
    new Color(0x00ff00), //Green
    new Color(0xffff00), //Yellow
    new Color(0x4682b4), //Blue
    new Color(0xff00ff), //Magenta
    new Color(0x00ffff), //Cyan
    new Color(0xffffff), //White
  };

  public static final ColorPalette WINDOWS_PALETTE = new ColorPalette(WINDOWS_COLORS);

  private final Color[] myColors;

  private ColorPalette(@NotNull Color[] colors) {
    myColors = colors;
  }

  public @NotNull Color getForeground(@NotNull TerminalColor color) {
    if (color.isIndexed()) {
      int colorIndex = color.getColorIndex();
      assertColorIndexIsLessThan16(colorIndex);
      return getForegroundByColorIndex(colorIndex);
    }
    return color.toAwtColor();
  }

  public @NotNull Color getBackground(@NotNull TerminalColor color) {
    if (color.isIndexed()) {
      int colorIndex = color.getColorIndex();
      assertColorIndexIsLessThan16(colorIndex);
      return getBackgroundByColorIndex(colorIndex);
    }
    return color.toAwtColor();
  }

  @NotNull
  public Color getForegroundByColorIndex(int colorIndex) {
    return myColors[colorIndex];
  }

  @NotNull
  protected Color getBackgroundByColorIndex(int colorIndex) {
    return myColors[colorIndex];
  }


  private void assertColorIndexIsLessThan16(int colorIndex) {
    if (colorIndex < 0 || colorIndex >= 16) {
      throw new AssertionError("Color index is out of bounds [0,15]: " + colorIndex);
    }
  }

  public static @Nullable TerminalColor getIndexedTerminalColor(int colorIndex) {
    return colorIndex < 16 ? TerminalColor.index(colorIndex) : getXTerm256(colorIndex);
  }

  private static @Nullable TerminalColor getXTerm256(int colorIndex) {
    return colorIndex < 256 ? COL_RES_256[colorIndex - 16] : null;
  }

  //The code below is translation of xterm's 256colres.pl
  private static final TerminalColor[] COL_RES_256 = new TerminalColor[240];

  static {
    // colors 16-231 are a 6x6x6 color cube
    for (int red = 0; red < 6; red++) {
      for (int green = 0; green < 6; green++) {
        for (int blue = 0; blue < 6; blue++) {
          COL_RES_256[36 * red + 6 * green + blue] = new TerminalColor(getCubeColorValue(red),
                  getCubeColorValue(green),
                  getCubeColorValue(blue));
        }
      }
    }

    // colors 232-255 are a grayscale ramp, intentionally leaving out
    // black and white
    for (int gray = 0; gray < 24; gray++) {
      int level = 10 * gray + 8;
      COL_RES_256[216 + gray] = new TerminalColor(level, level, level);
    }
  }

  private static int getCubeColorValue(int value) {
    return value == 0 ? 0 : (40 * value + 55);
  }
}
