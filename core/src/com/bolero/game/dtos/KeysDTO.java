package com.bolero.game.dtos;

import com.badlogic.gdx.Input;
import lombok.Data;

@Data
public class KeysDTO {
  private String up;
  private String down;
  private String left;
  private String right;
  private String interact;
  private String reload;
  private String debug;
  private String zoomIn;
  private String zoomOut;
  private String quit;

  public int getUpInput() {
    return Input.Keys.valueOf(up);
  }

  public int getDownInput() {
    return Input.Keys.valueOf(down);
  }

  public int getLeftInput() {
    return Input.Keys.valueOf(left);
  }

  public int getRightInput() {
    return Input.Keys.valueOf(right);
  }

  public int getInteractInput() {
    return Input.Keys.valueOf(interact);
  }

  public int getReloadInput() {
    return Input.Keys.valueOf(reload);
  }

  public int getDebugInput() {
    return Input.Keys.valueOf(debug);
  }

  public int getZoomInInput() {
    return Input.Keys.valueOf(zoomIn);
  }

  public int getZoomOutInput() {
    return Input.Keys.valueOf(zoomOut);
  }

  public int getQuitInput() {
    return Input.Keys.valueOf(quit);
  }
}
