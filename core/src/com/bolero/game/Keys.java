package com.bolero.game;

import com.badlogic.gdx.Input;

public class Keys {
  public String up;
  public String down;
  public String left;
  public String right;
  public String interact;
  public String reload;
  public String debug;
  public String zoomIn;
  public String zoomOut;

  public int getUp() {
    return Input.Keys.valueOf(up);
  }

  public int getDown() {
    return Input.Keys.valueOf(down);
  }

  public int getLeft() {
    return Input.Keys.valueOf(left);
  }

  public int getRight() {
    return Input.Keys.valueOf(right);
  }

  public int getInteract() {
    return Input.Keys.valueOf(interact);
  }

  public int getReload() {
    return Input.Keys.valueOf(reload);
  }

  public int getDebug() {
    return Input.Keys.valueOf(debug);
  }

  public int getZoomIn() {
    return Input.Keys.valueOf(zoomIn);
  }

  public int getZoomOut() {
    return Input.Keys.valueOf(zoomOut);
  }
}
