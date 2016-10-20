package Assignment3;

/**
 * Taken from the phong illumination example
 */

/**
 * A 4-component vector for single-precision floating point data.
 */
class Vector4f {
  float x;
  float y;
  float z;
  float w;

  /**
   * Initialises the vector to (0,0,0,0).
   */
  Vector4f() {
    x = y = z = w = 0.f;
  }

  /**
   * Copy Constructor
   * @param v is another Vector4f
   * Just steals it's variables
   */
  Vector4f(final Vector4f v) {
    x = v.x;
    y = v.y;
    z = v.z;
    w = v.w;
  }

  /**
   * Constructor for converting a Vector3f to a Vector4f
   * @param v is a Vector3f, so is missing a w value
   *          This is aassumed to be 1.f and assigned to the new Vector4f.
   */
  Vector4f(final Vector3f v) {
    x = v.x;
    y = v.y;
    z = v.z;
    w = 1.f;
  }
}
