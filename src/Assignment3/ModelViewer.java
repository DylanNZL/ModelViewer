package Assignment3;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

@SuppressWarnings("WeakerAccess")
public class ModelViewer {

  private static final int INIT_WIDTH = 1024;
  private static final int INIT_HEIGHT = 768;

  private JFrame m_frame;

  private Canvas m_canvas;

  private JSlider m_sliderRotateX;
  private JSlider m_sliderRotateY;
  private JSlider m_sliderRotateZ;

  private JButton m_btnScaleUp;
  private JButton m_btnScaleDown;
  private JButton m_btnIncreaseX;
  private JButton m_btnDecreaseX;
  private JButton m_btnIncreaseY;
  private JButton m_btnDecreaseY;
  private JButton m_btnIncreaseZ;
  private JButton m_btnDecreaseZ;

  private JCheckBox m_chkRenderWireframe;
  private JCheckBox m_chkRenderSolid;
  private JCheckBox m_chkCullBackFaces;

  private JMenuItem m_menuOpenModelFile;

  private Model m_currentModel;

  //////////////////////////////////////////////////////////////////////////////

  /**
   * GUI Slider buttons to rotate the model in the X/Y/Z axis
   * Sent to functions in the model class that take the direct value of the slider and convert it to radians.
   */
  private final ChangeListener m_sliderChangeListener = new ChangeListener() {

    @Override
    public void stateChanged(ChangeEvent e) {
      final JSlider source = (JSlider) e.getSource();
      if (m_currentModel != null) {
        if (source == m_sliderRotateX) {
          m_currentModel.setRotateX(m_sliderRotateX.getValue());
          m_canvas.repaint();
        } else if (source == m_sliderRotateY) {
          m_currentModel.setRotateY(m_sliderRotateY.getValue());
          m_canvas.repaint();
        } else if (source == m_sliderRotateZ) {
          m_currentModel.setRotateZ(m_sliderRotateZ.getValue());
          m_canvas.repaint();
        }
      }
    }
  };

  /**
   * GUI buttons to:
   * Scale the model up/Down: Sends to functions that increase the scale by a factor of 1.1 or 0.9
   * Increase/Decrease X/Y/Z values: Send to functions that translate the model 0.5f in that particular axis
   * NB: Z increase & decrease is hard to see unless model is rotated
   */
  private final ActionListener m_btnActionListener = new ActionListener() {

    @Override
    public void actionPerformed(ActionEvent e) {
      final Object source = e.getSource();
      if (m_currentModel != null) {
        // scale changes
        if (source == m_btnScaleUp) {
          m_currentModel.incrementScale();
          m_canvas.repaint();
        } else if (source == m_btnScaleDown) {
          m_currentModel.decrementScale();
          m_canvas.repaint();
        }
        // translation changes
        else if (source == m_btnIncreaseX) {
          m_currentModel.increaseX();
          m_canvas.repaint();
        } else if (source == m_btnDecreaseX) {
          m_currentModel.decreaseX();
          m_canvas.repaint();
        } else if (source == m_btnIncreaseY) {
          m_currentModel.increaseY();
          m_canvas.repaint();
        } else if (source == m_btnDecreaseY) {
          m_currentModel.decreaseY();
          m_canvas.repaint();
        } else if (source == m_btnIncreaseZ) {
          m_currentModel.increaseZ();
          m_canvas.repaint();
        } else if (source == m_btnDecreaseZ) {
          m_currentModel.decreaseZ();
          m_canvas.repaint();
        }
      }
    }
  };

  /**
   * GUI Check Boxes to decide how to draw the image
   * They send through the isSelected property of each check box so that the booleans are always up to date.
   */
  private final ActionListener m_chkActionListener = new ActionListener() {

    @Override
    public void actionPerformed(ActionEvent e) {
      final Object source = e.getSource();
      if (source == m_chkRenderWireframe) {
        m_canvas.updateWireframe(m_chkRenderWireframe.isSelected());
        m_canvas.repaint();
      } else if (source == m_chkRenderSolid) {
        m_canvas.updateSolid(m_chkRenderSolid.isSelected());
        m_canvas.repaint();
      } else if (source == m_chkCullBackFaces) {
        m_canvas.updateBackFace(m_chkCullBackFaces.isSelected());
        m_canvas.repaint();
      }
    }
  };

  /**
   * GUI Menu.
   * Responsible for selecting the model to open.
   */
  private final ActionListener m_menuActionListener = new ActionListener() {

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getSource() == m_menuOpenModelFile) {
        final Model model = loadModelFile();
        if (model != null) {
          m_currentModel = model;
          m_canvas.setModel(model);
          m_canvas.repaint();
        }
      }
    }
  };

  //////////////////////////////////////////////////////////////////////////////
  private ModelViewer() {
  }

  /**
   * Factory method for {@link ModelViewer}.
   */
  private static void create() {
    final ModelViewer viewer = new ModelViewer();
    SwingUtilities.invokeLater(viewer::createGui);
    //return viewer;
  }

  /**
   * Creates the GUI. Must be called from the EDT.
   */
  private void createGui() {
    m_frame = new JFrame("Assignment3.Model Viewer");
    m_frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    // setup the content pane
    final JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());
    m_frame.setContentPane(contentPane);

    final Border border = BorderFactory.createBevelBorder(BevelBorder.LOWERED);

    // setup the canvas and control panel
    m_canvas = new Canvas();
    m_canvas.setBorder(border);
    contentPane.add(m_canvas, BorderLayout.CENTER);
    final JComponent controlPanel = createControlPanel();
    controlPanel.setBorder(border);
    contentPane.add(controlPanel, BorderLayout.LINE_START);

    // add the menu
    final JMenuBar menuBar = new JMenuBar();
    m_frame.setJMenuBar(menuBar);
    final JMenu fileMenu = new JMenu("File");
    menuBar.add(fileMenu);
    m_menuOpenModelFile = new JMenuItem("Open");
    m_menuOpenModelFile.addActionListener(m_menuActionListener);
    fileMenu.add(m_menuOpenModelFile);

    // register a key event dispatcher to get a turn in handling all
    // key events, independent of which component currently has the focus
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addKeyEventDispatcher(e -> {
          switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
              System.exit(0);
              return true; // consume the event
            default:
              return false; // do not consume the event
          }
        });

    m_frame.setSize(new Dimension(INIT_WIDTH, INIT_HEIGHT));
    m_frame.setVisible(true);
  }

  /**
   * Creates and returns the control panel. Must be called from the EDT.
   */
  private JComponent createControlPanel() {
    final JPanel toolbar = new JPanel(new GridBagLayout());

    final GridBagConstraints gbcDefault = new GridBagConstraints();
    gbcDefault.gridx = 0;
    gbcDefault.gridy = GridBagConstraints.RELATIVE;
    gbcDefault.gridwidth = 2;
    gbcDefault.fill = GridBagConstraints.HORIZONTAL;
    gbcDefault.insets = new Insets(5, 10, 5, 10);
    gbcDefault.anchor = GridBagConstraints.FIRST_LINE_START;
    gbcDefault.weightx = 0.5;

    final GridBagConstraints gbcLabels =
        (GridBagConstraints) gbcDefault.clone();
    gbcLabels.insets = new Insets(5, 10, 0, 10);

    final GridBagConstraints gbcTwoCol =
        (GridBagConstraints) gbcDefault.clone();
    gbcTwoCol.gridwidth = 1;
    gbcTwoCol.gridx = 0;
    gbcTwoCol.insets.right = 5;

    GridBagConstraints gbc;

    // setup the rotation sliders
    m_sliderRotateX = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
    m_sliderRotateY = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
    m_sliderRotateZ = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
    m_sliderRotateX.setPaintLabels(true);
    m_sliderRotateY.setPaintLabels(true);
    m_sliderRotateZ.setPaintLabels(true);
    m_sliderRotateX.setPaintTicks(true);
    m_sliderRotateY.setPaintTicks(true);
    m_sliderRotateZ.setPaintTicks(true);
    m_sliderRotateX.setMajorTickSpacing(90);
    m_sliderRotateY.setMajorTickSpacing(90);
    m_sliderRotateZ.setMajorTickSpacing(90);
    m_sliderRotateX.addChangeListener(m_sliderChangeListener);
    m_sliderRotateY.addChangeListener(m_sliderChangeListener);
    m_sliderRotateZ.addChangeListener(m_sliderChangeListener);
    gbc = gbcDefault;
    toolbar.add(new JLabel("Rotation X:"), gbcLabels);
    toolbar.add(m_sliderRotateX, gbc);
    toolbar.add(new JLabel("Rotation Y:"), gbcLabels);
    toolbar.add(m_sliderRotateY, gbc);
    toolbar.add(new JLabel("Rotation Z:"), gbcLabels);
    toolbar.add(m_sliderRotateZ, gbc);

    m_btnScaleDown = new JButton("- size");
    m_btnScaleUp = new JButton("+ size");
    m_btnScaleDown.addActionListener(m_btnActionListener);
    m_btnScaleUp.addActionListener(m_btnActionListener);
    gbc = (GridBagConstraints) gbcTwoCol.clone();
    toolbar.add(m_btnScaleDown, gbc);
    gbc.gridx = 1;
    gbc.insets.left = gbc.insets.right;
    gbc.insets.right = gbcDefault.insets.right;
    toolbar.add(m_btnScaleUp, gbc);

    m_btnIncreaseX = new JButton("+ x");
    m_btnDecreaseX = new JButton("- x");
    m_btnIncreaseY = new JButton("+ y");
    m_btnDecreaseY = new JButton("- y");
    m_btnIncreaseZ = new JButton("+ z");
    m_btnDecreaseZ = new JButton("- z");
    m_btnIncreaseX.addActionListener(m_btnActionListener);
    m_btnDecreaseX.addActionListener(m_btnActionListener);
    m_btnIncreaseY.addActionListener(m_btnActionListener);
    m_btnDecreaseY.addActionListener(m_btnActionListener);
    m_btnIncreaseZ.addActionListener(m_btnActionListener);
    m_btnDecreaseZ.addActionListener(m_btnActionListener);

    gbc = (GridBagConstraints) gbcTwoCol.clone();
    toolbar.add(m_btnDecreaseX, gbc);
    gbc.gridx = 1;
    gbc.insets.left = gbc.insets.right;
    gbc.insets.right = gbcDefault.insets.right;
    toolbar.add(m_btnIncreaseX, gbc);

    gbc = (GridBagConstraints) gbcTwoCol.clone();
    toolbar.add(m_btnDecreaseY, gbc);
    gbc.gridx = 1;
    gbc.insets.left = gbc.insets.right;
    gbc.insets.right = gbcDefault.insets.right;
    toolbar.add(m_btnIncreaseY, gbc);

    gbc = (GridBagConstraints) gbcTwoCol.clone();
    toolbar.add(m_btnDecreaseZ, gbc);
    gbc.gridx = 1;
    gbc.insets.left = gbc.insets.right;
    gbc.insets.right = gbcDefault.insets.right;
    toolbar.add(m_btnIncreaseZ, gbc);

    // add check boxes
    gbc = gbcDefault;

    m_chkRenderWireframe = new JCheckBox("Render Wireframe");
    m_chkRenderWireframe.setSelected(true);
    m_chkRenderWireframe.addActionListener(m_chkActionListener);
    toolbar.add(m_chkRenderWireframe, gbc);

    m_chkRenderSolid = new JCheckBox("Render Solid");
    m_chkRenderSolid.setSelected(true);
    m_chkRenderSolid.addActionListener(m_chkActionListener);
    toolbar.add(m_chkRenderSolid, gbc);

    m_chkCullBackFaces = new JCheckBox("Cull Back Faces");
    m_chkCullBackFaces.setSelected(true);
    m_chkCullBackFaces.addActionListener(m_chkActionListener);
    gbc = (GridBagConstraints) gbcDefault.clone();
    gbc.weighty = 1.;
    gbc.gridheight = GridBagConstraints.REMAINDER;
    toolbar.add(m_chkCullBackFaces, gbc);

    return toolbar;
  }

  /**
   * Displays a chooser dialog and loads the selected model.
   *
   * @return The model, or null if the user cancels the action or something
   * goes wrong.
   */
  private Model loadModelFile() {
    // show a file chooser for model files
    JFileChooser chooser = new JFileChooser("./");
    chooser.setFileFilter(new FileNameExtensionFilter(
        ".dat model files", "dat"));
    int retVal = chooser.showOpenDialog(m_frame);
    if (retVal == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();

      // try to load the model from the selected file
      final Model model = Model.loadModel(file);
      if (model != null) {
        // Set the first boolean to true, which will dynamically scale the mode up/down to make it fit comfortably in the window.
        m_canvas.setFirstToTrue();
      }
      return model;
    }
    return null;
  }

  /**
   * Main function, called on start up
   * Prints out my information and then creates the GUI and the rest of the application
   */
  public static void main(String[] args) {
    System.out.println("*****************************************");
    System.out.println("* 159.235 Assignment 3, Semester 2 2016 *");
    System.out.println("* Submitted by:  Cross, Dylan, 15219491 *");
    System.out.println("*****************************************");

    ModelViewer.create();
  }
}