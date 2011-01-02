package blender;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class FPCamera {
  private Vector3f _pos;

  private float _yaw;

  private float _pitch;


  public FPCamera(float x, float y, float z) {
    _pos = new Vector3f(x, y, z);
  }


  /**
   * Increment the camera's yaw rotation.
   */
  public void yawInc(float amount) {
    _yaw += amount;
  }


  /**
   * Increment the camera's pitch rotation.
   */
  public void pitchInc(float amount) {
    _pitch += amount;
  }


  /**
   * Moves the camera forward relative to its current rotation (yaw).
   */
  public void walkForward(float distance) {
    _pos.x -= distance * (float) Math.sin(Math.toRadians(_yaw));
    _pos.z += distance * (float) Math.cos(Math.toRadians(_yaw));
  }


  /**
   * Moves the camera backward relative to its current rotation (yaw).
   */
  public void walkBackward(float distance) {
    _pos.x += distance * (float) Math.sin(Math.toRadians(_yaw));
    _pos.z -= distance * (float) Math.cos(Math.toRadians(_yaw));
  }


  /**
   * Strafes the camera left relative to its current rotation (yaw).
   */
  public void strafeLeft(float distance) {
    _pos.x -= distance * (float) Math.sin(Math.toRadians(_yaw - 90));
    _pos.z += distance * (float) Math.cos(Math.toRadians(_yaw - 90));
  }


  /**
   * Strafes the camera right relative to its current rotation (yaw).
   */
  public void strafeRight(float distance) {
    _pos.x -= distance * (float) Math.sin(Math.toRadians(_yaw + 90));
    _pos.z += distance * (float) Math.cos(Math.toRadians(_yaw + 90));
  }


  /**
   * Translates and rotates the matrix so that it looks through the camera.
   */
  public void lookThrough() {
    // rotate the pitch around the X axis
    GL11.glRotatef(_pitch, 1.0f, 0.0f, 0.0f);
    // rotate the yaw around the Y axis
    GL11.glRotatef(_yaw, 0.0f, 1.0f, 0.0f);
    // translate to the position vector's location
    GL11.glTranslatef(_pos.x, _pos.y, _pos.z);
  }
}
