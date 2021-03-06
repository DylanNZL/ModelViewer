package Assignment3;

/**
 * Taken from the Phong illumination example
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
   * Constructor for Vector4f
   * @param mX is the x value of the vector
   * @param mY is the y value of the vector
   * @param mZ is the z value of the vector
   * NB: w is assumed to be 1.f (not really used in this program).
   */
  Vector4f(float mX, float mY, float mZ) {
    x = mX;
    y = mY;
    z = mZ;
    w = 1.f;
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

}
