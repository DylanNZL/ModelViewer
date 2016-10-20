package Assignment3;

/**
 * Taken from the phong illumination example
 */

/**
 * A 3-component vector for single-precision floating point data.
 */
class Vector3f {
  public float x;
  public float y;
  public float z;

  /**
   * Initialises the vector to (0,0,0).
   */
  Vector3f() {
    x = y = z = 0.f;
  }

  Vector3f(final float xval, final float yval, final float zval) {
    x = xval;
    y = yval;
    z = zval;
  }

  /**
   * Subtract v from this vector.
   *
   * @param rhs The vector to subtract.
   * @param out The result vector.
   * @return The vector given in out.
   */
  Vector3f minus(final Vector3f rhs, Vector3f out) {
    out.x = x - rhs.x;
    out.y = y - rhs.y;
    out.z = z - rhs.z;
    return out;
  }

  /**
   * Normalises this vector.
   *
   * @return A reference to this for convenient chaining.
   */
  Vector3f normalize() {
    final double magnitudeSqrd = x * x + y * y + z * z;
    if (magnitudeSqrd == 0.) return this;

    final double magnitude = Math.sqrt(magnitudeSqrd);
    x /= magnitude;
    y /= magnitude;
    z /= magnitude;

    return this;
  }

  /**
   * Calculates the cross product for the given vectors as v1 X v2 and writes
   * the result to out.
   *
   * @return The vector given in out.
   */
  static Vector3f crossProduct(final Vector3f v1, final Vector3f v2,
                               Vector3f out) {
    out.x = v1.y * v2.z - v1.z * v2.y;
    out.y = v1.z * v2.x - v1.x * v2.z;
    out.z = v1.x * v2.y - v1.y * v2.x;
    return out;
  }
}
