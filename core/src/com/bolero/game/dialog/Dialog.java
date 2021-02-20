package com.bolero.game.dialog;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class Dialog extends AbstractText {
  @Getter @Setter private List<Choice> choices;
}
