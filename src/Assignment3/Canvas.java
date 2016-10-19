package Assignment3;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * The draw area.
 */
class Canvas extends JPanel {

  private static final long serialVersionUID = 1L;

  private Model m_model;

  // Controller variables, changed by the GUI
  private float scale = 50.f;
  private boolean wireFrame = true;
  private boolean solid = true;
  private boolean backFace = true;

  // Boolean to check if this is the first time loading an image
  private boolean first = true;

  Canvas() {
    setOpaque(true);
  }

  void setModel(final Model model) {
    m_model = model;
  }

  void incrementScale() {
    scale = scale * 1.1f;
  }

  void decrementScale() {
    scale = scale * 0.9f;
  }

  // Sets the scale to a value that will show the image inside the screen
  private void setScale() {
    if (getHeight() < getWidth()) {
      scale = (getHeight() / 2) / m_model.getMaxSize();
    } else {
      scale = (getWidth() / 2) / m_model.getMaxSize();
    }
  }

  void updateWireframe(boolean update) {
    wireFrame = update;
  }

  void updateSolid(boolean update) {
    solid = update;
  }

  void updateBackFace(boolean update) {
    backFace = update;
  }

  void setFirstToTrue() {
    first = true;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (m_model == null) return;

    if (first) {
      setScale();
      first = false;
    }

    final ArrayList<Triangle> triangles = m_model.getTriangles();
    if (triangles == null || triangles.size() == 0) return;

    Graphics2D g2 = (Graphics2D) g.create();
    // Set AA on
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Move the origin to the center of the canvas and flip the y-axis.
    g2.translate(getWidth() / 2., getHeight() / 2.);
    g2.scale(1, -1);

    final Polygon poly = new Polygon(new int[3], new int[3], 3);
    for (final Triangle triangle : triangles) {
      if (backFace && triangle.normal.z <= 0.f) continue;

      poly.xpoints[0] = (int) (triangle.v[0].x * scale);
      poly.xpoints[1] = (int) (triangle.v[1].x * scale);
      poly.xpoints[2] = (int) (triangle.v[2].x * scale);
      poly.ypoints[0] = (int) (triangle.v[0].y * scale);
      poly.ypoints[1] = (int) (triangle.v[1].y * scale);
      poly.ypoints[2] = (int) (triangle.v[2].y * scale);

      // if render solids is true, fill the polygon
      if (solid) {
        g2.setPaint(Color.BLUE);
        g2.fill(poly);
      }

      // if render wireframe is true, draw a black
      if (wireFrame) {
        g2.setPaint(Color.BLACK);
        g2.draw(poly);
      }
    }
    g2.dispose();
  }
}
