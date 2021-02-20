package com.bolero.game.dialog;

import lombok.Getter;
import lombok.Setter;

public class Choice extends AbstractText {
  @Getter @Setter private Dialog next;
}
