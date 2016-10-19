package Assignment3;

import render3d.Vector4f;

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
   * Returns the view matrix for the given camera in the scene.
   *
   * @param camPos    The camera position in world coordinates.
   * @param targetPos The point of interest toward which the camera faces.
   * @param camUp     The up direction vector.
   * @return The view transformation matrix.
   */
  public static Matrix4f lookAt(final render3d.Vector3f camPos, final render3d.Vector3f targetPos,
                                final render3d.Vector3f camUp) {
    // Calculate the direction vector from the cam to the point of interest.
    final render3d.Vector3f forward = targetPos.minus(camPos).normalize();
    // Calculate the orthogonal right vector.
    final render3d.Vector3f right = render3d.Vector3f.crossProduct(forward, camUp);
    // Now calculate an up vector that is orthogonal to both forward and right.
    final render3d.Vector3f up = render3d.Vector3f.crossProduct(right, forward);

    // Construct the view matrix.
    final Matrix4f rotation = new Matrix4f(new float[]{
        right.x, up.x, -forward.x, 0,
        right.y, up.y, -forward.y, 0,
        right.z, up.z, -forward.z, 0,
        0, 0, 0, 1
    });
    final Matrix4f translation = new Matrix4f(new float[]{
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        -camPos.x, -camPos.y, -camPos.z, 1
    });

    return rotation.multiply(translation);
  }

  /**
   * Returns the perspective projection matrix.
   *
   * @param dist The distance from the centre of projection (at the origin)
   *             to the projection plane. Must be > 0.
   */
  public static Matrix4f createPerspective(final float dist) {
    if (dist <= 0.f) return new Matrix4f();

    final Matrix4f p = new Matrix4f();
    p.m[11] = 1.f / -dist;
    p.m[15] = 0.f;
    return p;
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
   * {@link render3d.Vector4f}.
   *
   * @param rhs The column vector to multiply by.
   * @return The result in a new vector.
   */
  public render3d.Vector4f multiply(final render3d.Vector4f rhs) {
    return multiply(rhs, new render3d.Vector4f());
  }

  /**
   * Multiplies this this by rhs and returns the result in out.
   *
   * @param rhs The column vector to multiply by.
   * @param out The result vector.
   * @return The vector given in out.
   */
  public render3d.Vector4f multiply(final render3d.Vector4f rhs, final Vector4f out) {
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
