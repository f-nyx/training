package com.jediterm.terminal;

import com.jediterm.terminal.model.PtyTerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum TerminalMode {
  Null,
  ANSI,
  WideColumn {
    @Override
    public void setEnabled(PtyTerminal terminal, boolean enabled) {
      // Skip resizing as it would require to resize parent container.
      // Other terminal emulators (iTerm2, Terminal.app, GNOME Terminal) ignore it too.
//      terminal.clearScreen();
//      terminal.resetScrollRegions();
    }
  },
  SmoothScroll,
  ReverseVideo,
  AutoWrap {
    @Override
    public void setEnabled(PtyTerminal terminal, boolean enabled) {
      //we do nothing just switching the mode
    }
  },
  AutoRepeatKeys,
  Interlace,
  Keypad {
    @Override
    public void setEnabled(PtyTerminal terminal, boolean enabled) {
//      terminal.setApplicationKeypad(enabled);
    }
  },
  StoreCursor {
    @Override
    public void setEnabled(PtyTerminal terminal, boolean enabled) {
      if (enabled) {
        terminal.saveCursor();
      }
      else {
        terminal.restoreCursor();
      }
    }
  }, 
  AllowWideColumn,
  ReverseWrapAround, 
  KeyboardAction,
  InsertMode,
  SendReceive,
  EightBitInput
  ;

  private static final Logger LOG = LoggerFactory.getLogger(TerminalMode.class);
  
  public void setEnabled(PtyTerminal terminal, boolean enabled) {
    LOG.error("Mode " + name() + " is not implemented, setting to " + enabled);
  }
}