package Assignment3;

/**
 * Taken from the Phong illumination example
 */

/**
 * A 3-component vector for single-precision floating point data.
 */
class Vector3f {
  float x;
  float y;
  float z;

  /**
   * Initialises the vector to (0,0,0).
   */
  Vector3f() {
    x = y = z = 0.f;
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
   */
  void normalize() {
    final double magnitudeSquared = x * x + y * y + z * z;
    if (magnitudeSquared == 0.) return;

    final double magnitude = Math.sqrt(magnitudeSquared);
    x /= magnitude;
    y /= magnitude;
    z /= magnitude;

  }

  /**
   * Calculates the cross product for the given vectors as v1 X v2 and writes
   * the result to out.
   */
  static void crossProduct(final Vector3f v1, final Vector3f v2,
                           Vector3f out) {
    out.x = v1.y * v2.z - v1.z * v2.y;
    out.y = v1.z * v2.x - v1.x * v2.z;
    out.z = v1.x * v2.y - v1.y * v2.x;
  }
}
