/**
 * Taken from the phong illumination example
 */
package Assignment3;

/**
 * A 4-component vector for single-precision floating point data.
 */
public class Vector4f {
  public float x;
  public float y;
  public float z;
  public float w;

  /**
   * Initialises the vector to (0,0,0,0).
   */
  public Vector4f() {
    x = y = z = w = 0.f;
  }

  public Vector4f(final float xval, final float yval, final float zval,
                  final float wval) {
    x = xval;
    y = yval;
    z = zval;
    w = wval;
  }

  public Vector4f(final Vector4f v) {
    x = v.x;
    y = v.y;
    z = v.z;
    w = v.w;
  }

  public Vector4f(final Vector3f v) {
    x = v.x;
    y = v.y;
    z = v.z;
    w = 1.f;
  }

  /**
   * Subtract v from this vector.
   *
   * @param rhs The vector to subtract.
   * @return A new vector with the result.
   */
  public Vector4f minus(final Vector4f rhs) {
    return minus(rhs, new Vector4f());
  }

  /**
   * Subtract v from this vector.
   *
   * @param rhs The vector to subtract.
   * @param out The result vector.
   * @return The vector given in out.
   */
  public Vector4f minus(final Vector4f rhs, Vector4f out) {
    out.x = x - rhs.x;
    out.y = y - rhs.y;
    out.z = z - rhs.z;
    out.w = w - rhs.w;
    return out;
  }

  /**
   * Homogenises this vector.
   *
   * @return A reference to this.
   */
  public Vector4f homogenise() {
    if (w == 0.f) return this;

    x /= w;
    y /= w;
    z /= w;
    w = 1.f;
    return this;
  }
}
