package com.bolero.game.mixins;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bolero.game.data.Tuple;
import lombok.val;

public interface SmoothMovement {

  /* Calculate a position towards a target position.
   * For any kind of smooth movement (like characters & camera), we can't just set one position to
   * another, since that would cause choppy movement.
   * To ensure smooth translation, we scale out position so that the result is
   * old_position * ispeed + target * speed.
   * We also return ispeed back in the tuple for any centering calculations.
   * From: https://stackoverflow.com/a/24048501/3894455
   */
  default Tuple<Vector3, Float> getSmoothMovement(
      float speed, Vector2 targetPos, Vector2 currentPos) {
    val ispeed = 1.0f - speed;

    val target = new Vector3(targetPos, 0);
    val current = new Vector3(currentPos, 0);
    current.scl(ispeed);
    target.scl(speed);
    current.add(target);

    return new Tuple<>(current, ispeed);
  }
}
