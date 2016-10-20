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

  // Controller variables, changed by the GUI Buttons
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

  // Sets the scale to a value that will show the image inside the screen
  private void setScale() {
    m_model.setScale(getHeight(), getWidth());
    m_model.newTransforms();
  }

  // Update the draw wireframe boolean, passed through the value of the isSelected method, so it's always up to date
  void updateWireframe(boolean update) {
    wireFrame = update;
  }

  // Update the draw solid boolean, passed through the value of the isSelected method, so it's always up to date
  void updateSolid(boolean update) {
    solid = update;
  }

  // Update the cull backfaces boolean, passed through the value of the isSelected method, so it's always up to date
  void updateBackFace(boolean update) {
    backFace = update;
  }

  // Sets the first boolean to true, only done when a model is first loaded in
  void setFirstToTrue() {
    first = true;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // If there is no model to draw, return.
    if (m_model == null) return;

    // Check if this is the first time loading that model, if it is, set a new scale for that model
    if (first) {
      setScale();
      first = false;
    }

    // Get the latest transformed model
    final ArrayList<Triangle> triangles = m_model.getTriangles();

    // If there is no triangles, return. Should of been caught by the m_model == null statement
    if (triangles == null || triangles.size() == 0) return;

    Graphics2D g2 = (Graphics2D) g.create();

    // Set AA on
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Move the origin to the center of the canvas and flip the y-axis.
    g2.translate(getWidth() / 2., getHeight() / 2.);
    // Set pixel scale to 1
    g2.scale(1, -1);

    // Create a polygon that has three x/y points and 3 sides
    final Polygon poly = new Polygon(new int[3], new int[3], 3);

    // Iterate through triangle array, drawing them onto the Panel
    for (final Triangle triangle : triangles) {
      // If Backface culling is enabled, skip the triangles that have a 0.f z normal
      if (backFace && triangle.normal.z <= 0.f) continue;

      // Assign each x/y point on the triangle to the poygon
      poly.xpoints[0] = (int) (triangle.v[0].x);
      poly.xpoints[1] = (int) (triangle.v[1].x);
      poly.xpoints[2] = (int) (triangle.v[2].x);
      poly.ypoints[0] = (int) (triangle.v[0].y);
      poly.ypoints[1] = (int) (triangle.v[1].y);
      poly.ypoints[2] = (int) (triangle.v[2].y);

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
    // Displose of Graphics2D instance
    g2.dispose();
  }
}
