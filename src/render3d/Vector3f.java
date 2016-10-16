package render3d;

/**
 * A 3-component vector for single-precision floating point data.
 */
public class Vector3f {
  public float x;
  public float y;
  public float z;

  /**
   * Initialises the vector to (0,0,0).
   */
  public Vector3f() {
    x = y = z = 0.f;
  }

  public Vector3f(final float xval, final float yval, final float zval) {
    x = xval;
    y = yval;
    z = zval;
  }

  public Vector3f(final Vector3f v) {
    x = v.x;
    y = v.y;
    z = v.z;
  }

  /**
   * Subtract rhs from this vector.
   *
   * @param rhs The vector to subtract.
   * @return A new vector with the result.
   */
  public Vector3f minus(final Vector3f rhs) {
    return minus(rhs, new Vector3f());
  }

  /**
   * Subtract rhs from this vector.
   *
   * @param rhs The vector to subtract.
   * @param out The result vector.
   * @return The vector given in out.
   */
  public Vector3f minus(final Vector3f rhs, Vector3f out) {
    out.x = x - rhs.x;
    out.y = y - rhs.y;
    out.z = z - rhs.z;
    return out;
  }

  /**
   * Multiplies each component of this vector by coefficient.
   *
   * @param coefficient The coefficient.
   * @return A new vector with the result.
   */
  public Vector3f multiply(final float coefficient) {
    return multiply(coefficient, new Vector3f());
  }

  /**
   * Multiplies each component of this vector by coefficient.
   *
   * @param coefficient The coefficient.
   * @param out         The result vector.
   * @return The vector given in out.
   */
  public Vector3f multiply(final float coefficient, Vector3f out) {
    out.x = x * coefficient;
    out.y = y * coefficient;
    out.z = z * coefficient;
    return out;
  }

  /**
   * Normalises this vector.
   *
   * @return A reference to this for convenient chaining.
   */
  public Vector3f normalize() {
    final double magnitudeSqrd = x * x + y * y + z * z;
    if (magnitudeSqrd == 0.) return this;

    final double magnitude = Math.sqrt(magnitudeSqrd);
    x /= magnitude;
    y /= magnitude;
    z /= magnitude;

    return this;
  }

  /**
   * Calculates the dot product for the given vectors.
   *
   * @return The dot product.
   */
  public static float dotProduct(final Vector3f v1, final Vector3f v2) {
    return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
  }

  /**
   * Calculates the cross product for the given vectors as v1 X v2 and writes
   * the result to a new vector.
   */
  public static Vector3f crossProduct(final Vector3f v1, final Vector3f v2) {
    return crossProduct(v1, v2, new Vector3f());
  }

  /**
   * Calculates the cross product for the given vectors as v1 X v2 and writes
   * the result to out.
   *
   * @return The vector given in out.
   */
  public static Vector3f crossProduct(final Vector3f v1, final Vector3f v2,
                                      Vector3f out) {
    out.x = v1.y * v2.z - v1.z * v2.y;
    out.y = v1.z * v2.x - v1.x * v2.z;
    out.z = v1.x * v2.y - v1.y * v2.x;
    return out;
  }
}
