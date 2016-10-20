package Assignment3;

/**
 * Taken from the Phong illumination example
 */

/**
 * A polygon with three vertices, a.k.a. a triangle.
 */
class Triangle implements Comparable<Triangle> {
  final Vector4f[] v; // Vertices
  final Vector3f normal;

  // NOTE: These members are used for efficiency reasons, but this is not
  // thread-safe. Need to use thread-local temporaries if updateNormal() is to
  // be called concurrently.
  private static final Vector3f tmpV1 = new Vector3f();
  private static final Vector3f tmpV2 = new Vector3f();
  private static final Vector3f tmpV3 = new Vector3f();
  private static final Vector3f vec1 = new Vector3f();
  private static final Vector3f vec2 = new Vector3f();

  Triangle(Vector4f vertex1, Vector4f vertex2, Vector4f vertex3) {
    v = new Vector4f[3];
    v[0] = vertex1;
    v[1] = vertex2;
    v[2] = vertex3;
    normal = new Vector3f();
  }

  /**
   * Calculates and updates the surface {@link #normal} for this triangle from
   * its vertices.
   * <p>
   * <b>NOTE that this implementation is not thread-safe.</b>
   */
  void calculateNormal() {
    tmpV1.x = v[0].x;
    tmpV1.y = v[0].y;
    tmpV1.z = v[0].z;
    tmpV2.x = v[1].x;
    tmpV2.y = v[1].y;
    tmpV2.z = v[1].z;
    tmpV3.x = v[2].x;
    tmpV3.y = v[2].y;
    tmpV3.z = v[2].z;
    Vector3f.crossProduct(
        tmpV2.minus(tmpV1, vec1),
        tmpV3.minus(tmpV1, vec2),
        normal);
    normal.normalize();
  }

  /**
   * Returns the average z-value for all vertices of this polygon.
   */
  private float getAverageDepth() {
    return ((v[0].z + v[1].z + v[2].z) / 3.f);
  }

  @Override
  public int compareTo(Triangle o) {
    final float depth = getAverageDepth();
    final float otherDepth = o.getAverageDepth();

    if (depth < otherDepth) return -1;
    else if (depth == otherDepth) return 0;
    else return 1;
  }
}
