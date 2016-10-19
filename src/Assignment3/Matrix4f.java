/**
 * Taken from the phong illumination example
 */
package Assignment3;

/**
 * A 4x4 single-precision floating point matrix in column-major layout.
 */
public class Matrix4f {
  public final float[] m;

  /**
   * Initialises the matrix with the identity matrix.
   */
  public Matrix4f() {
    m = new float[16];
    m[0] = 1.f;
    m[5] = 1.f;
    m[10] = 1.f;
    m[15] = 1.f;
  }

  /**
   * Initialises the matrix to the given array. Must be of length 16!
   */
  public Matrix4f(float[] mat) {
    assert (mat != null && mat.length == 16);

    m = mat;
  }

  public static Matrix4f createTranslateInstance(final float dx, final float dy,
                                                 final float dz) {
    final Matrix4f mat = new Matrix4f();
    mat.m[12] = dx;
    mat.m[13] = dy;
    mat.m[14] = dz;
    return mat;
  }

  public static Matrix4f createScaleInstance(final float sx, final float sy,
                                             final float sz) {
    final Matrix4f mat = new Matrix4f();
    mat.m[0] = sx;
    mat.m[5] = sy;
    mat.m[10] = sz;
    return mat;
  }

  public static Matrix4f createRotateXInstance(final float theta) {
    final Matrix4f mat = new Matrix4f();
    final float cos = (float) Math.cos((double) theta);
    final float sin = (float) Math.sin((double) theta);
    mat.m[5] = cos;
    mat.m[9] = -sin;
    mat.m[6] = sin;
    mat.m[10] = cos;
    return mat;
  }

  public static Matrix4f createRotateYInstance(final float theta) {
    final Matrix4f mat = new Matrix4f();
    final float cos = (float) Math.cos((double) theta);
    final float sin = (float) Math.sin((double) theta);
    mat.m[0] = cos;
    mat.m[8] = sin;
    mat.m[2] = -sin;
    mat.m[10] = cos;
    return mat;
  }

  public static Matrix4f createRotateZInstance(final float theta) {
    final Matrix4f mat = new Matrix4f();
    final float cos = (float) Math.cos((double) theta);
    final float sin = (float) Math.sin((double) theta);
    mat.m[0] = cos;
    mat.m[4] = -sin;
    mat.m[1] = sin;
    mat.m[5] = cos;
    return mat;
  }


  /**
   * Multiplies this by rhs and returns the result in a new matrix instance.
   *
   * @param rhs The other matrix.
   * @return A new matrix that is the matrix product of this by rhs.
   */
  public Matrix4f multiply(final Matrix4f rhs) {
    return multiply(rhs, new Matrix4f());
  }

  /**
   * Multiplies this by rhs and returns the result in out.
   *
   * @param rhs The other matrix.
   * @param out The output matrix.
   * @return The matrix given in out.
   */
  public Matrix4f multiply(final Matrix4f rhs, final Matrix4f out) {
    int outIdx;
    for (int i = 0; i < 4; ++i) {
      for (int j = 0; j < 4; ++j) {
        outIdx = i + j * 4;
        out.m[outIdx] = 0.f;
        for (int k = 0; k < 4; ++k) {
          out.m[outIdx] += m[i + k * 4] * rhs.m[k + j * 4];
        }
      }
    }

    return out;
  }

  /**
   * Multiplies this this by rhs and returns the result in a new
   * {@link Vector4f}.
   *
   * @param rhs The column vector to multiply by.
   * @return The result in a new vector.
   */
  public Vector4f multiply(final Vector4f rhs) {
    return multiply(rhs, new Vector4f());
  }

  /**
   * Multiplies this this by rhs and returns the result in out.
   *
   * @param rhs The column vector to multiply by.
   * @param out The result vector.
   * @return The vector given in out.
   */
  public Vector4f multiply(final Vector4f rhs, final Vector4f out) {
    out.x = m[0] * rhs.x + m[4] * rhs.y + m[8] * rhs.z + m[12] * rhs.w;
    out.y = m[1] * rhs.x + m[5] * rhs.y + m[9] * rhs.z + m[13] * rhs.w;
    out.z = m[2] * rhs.x + m[6] * rhs.y + m[10] * rhs.z + m[14] * rhs.w;
    out.w = m[3] * rhs.x + m[7] * rhs.y + m[11] * rhs.z + m[15] * rhs.w;

    return out;
  }

  @Override
  public String toString() {
    String br = System.lineSeparator();
    String str = m[0] + " " + m[4] + " " + m[8] + " " + m[12] + br;
    str += m[1] + " " + m[5] + " " + m[9] + " " + m[13] + br;
    str += m[2] + " " + m[6] + " " + m[10] + " " + m[14] + br;
    str += m[3] + " " + m[7] + " " + m[11] + " " + m[15];
    return str;
  }
}
