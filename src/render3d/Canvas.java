package render3d;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by dylancross on 17/10/16.
 */
class Canvas extends JPanel {

  Model model;
  Stroke wireframeStroke = new BasicStroke(1);

  boolean renderWireframe = true;
  boolean cullBackFaces = true;
  boolean enableLighting = true;

  // A directional light source.
  Vector3f dirToLight = new Vector3f(1.f, 1.f, 1.f).normalize();
  // The colour and intensity of the light source, for all reflection terms.
  Vector3f lightIntensity = new Vector3f(1.f, 1.f, 1.f);

  Color modelColour = Color.BLUE;
  // The material reflection terms should add up to 1.
  float ambientReflection = 0.3f;
  float diffuseReflection = 0.7f;

  Canvas() {
    setBackground(Color.WHITE);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (model == null) return;

    final ArrayList<Triangle> triangles = model.getTriangles();
    if (triangles == null || triangles.size() == 0) return;

    final Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setStroke(wireframeStroke);

    // Move the origin to the center of the canvas and flip the y-axis.
    g2.translate(getWidth() / 2., getHeight() / 2.);
    g2.scale(1., -1.);

    final Polygon poly = new Polygon(new int[3], new int[3], 3);
    final Vector3f intensity = new Vector3f(1.f, 1.f, 1.f);
    for (final Triangle triangle : triangles) {
      if (cullBackFaces && triangle.normal.z <= 0.f) continue;

      poly.xpoints[0] = (int) triangle.v[0].x;
      poly.xpoints[1] = (int) triangle.v[1].x;
      poly.xpoints[2] = (int) triangle.v[2].x;
      poly.ypoints[0] = (int) triangle.v[0].y;
      poly.ypoints[1] = (int) triangle.v[1].y;
      poly.ypoints[2] = (int) triangle.v[2].y;

      if (enableLighting) {
        calculateLightIntensity(triangle.normal, dirToLight, intensity);
        final Color colour = new Color(
            (int) (modelColour.getRed() * intensity.x),
            (int) (modelColour.getGreen() * intensity.y),
            (int) (modelColour.getBlue() * intensity.z));
        g2.setPaint(colour);
      } else { // No lighting.
        g2.setPaint(modelColour);
      }

      g2.fill(poly);

      if (renderWireframe) {
        g2.setPaint(Color.BLACK);
        g2.draw(poly);
      }
    }

    g2.dispose();
  }

  private void calculateLightIntensity(final Vector3f normal,
                                       final Vector3f toLight,
                                       final Vector3f out) {
    final float diffuseIntensity =
        Math.max(Vector3f.dotProduct(toLight, normal), 0.f)
            * diffuseReflection;

    final float intensity = ambientReflection + diffuseIntensity;

    out.x = lightIntensity.x * intensity;
    out.y = lightIntensity.y * intensity;
    out.z = lightIntensity.z * intensity;
  }
}
