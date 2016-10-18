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
  private JButton m_btnIncrX;
  private JButton m_btnDecrX;
  private JButton m_btnIncrY;
  private JButton m_btnDecrY;
  private JButton m_btnIncrZ;
  private JButton m_btnDecrZ;

  private JCheckBox m_chkRenderWireframe;
  private JCheckBox m_chkRenderSolid;
  private JCheckBox m_chkCullBackFaces;

  private JMenuItem m_menuOpenModelFile;

  private Model m_currentModel;

  //////////////////////////////////////////////////////////////////////////////
  private ChangeListener m_sliderChangeListener = new ChangeListener() {

    @Override
    public void stateChanged(ChangeEvent e) {
      final JSlider source = (JSlider) e.getSource();
      if (m_currentModel != null) {
        if (source == m_sliderRotateX) {
          // TODO rotate x
          m_currentModel.setRotateX(m_sliderRotateX.getValue());
          m_canvas.repaint();
          //System.out.println("RotateX " + m_sliderRotateX.getValue());
        } else if (source == m_sliderRotateY) {
          // TODO rotate x
          m_currentModel.setRotateY(m_sliderRotateY.getValue());
          m_canvas.repaint();
          //System.out.println("RotateY " + m_sliderRotateY.getValue());
        } else if (source == m_sliderRotateZ) {
          // TODO rotate z
          m_currentModel.setRotateZ(m_sliderRotateY.getValue());
          m_canvas.repaint();
          //System.out.println("RotateZ " + m_sliderRotateZ.getValue());
        }
      }
    }
  };

  private ActionListener m_btnActionListener = new ActionListener() {

    @Override
    public void actionPerformed(ActionEvent e) {
      final Object source = e.getSource();
      if (m_currentModel != null) {
        // scale changes
        if (source == m_btnScaleUp) {
          m_canvas.incrementScale();
          m_canvas.repaint();
        } else if (source == m_btnScaleDown) {
          m_canvas.decrementScale();
          m_canvas.repaint();
        }
        // translation changes
        else if (source == m_btnIncrX) {
          m_currentModel.increaseX();
          m_canvas.repaint();
        } else if (source == m_btnDecrX) {
          m_currentModel.decreaseX();
          m_canvas.repaint();
        } else if (source == m_btnIncrY) {
          m_currentModel.increaseY();
          m_canvas.repaint();
        } else if (source == m_btnDecrY) {
          m_currentModel.decreaseY();
          m_canvas.repaint();
        } else if (source == m_btnIncrZ) {
          // TODO increase z
          m_currentModel.increaseZ();
          m_canvas.repaint();
          System.out.println("IncrZ");
        } else if (source == m_btnDecrZ) {
          // TODO decrease z
          m_currentModel.decreaseZ();
          m_canvas.repaint();
          System.out.println("DecrZ");
        }
      }
    }
  };

  private ActionListener m_chkActionListener = new ActionListener() {

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

  private ActionListener m_menuActionListener = new ActionListener() {

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
  public static ModelViewer create() {
    final ModelViewer viewer = new ModelViewer();
    SwingUtilities.invokeLater(viewer::createGui);
    return viewer;
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

    m_btnIncrX = new JButton("+ x");
    m_btnDecrX = new JButton("- x");
    m_btnIncrY = new JButton("+ y");
    m_btnDecrY = new JButton("- y");
    m_btnIncrZ = new JButton("+ z");
    m_btnDecrZ = new JButton("- z");
    m_btnIncrX.addActionListener(m_btnActionListener);
    m_btnDecrX.addActionListener(m_btnActionListener);
    m_btnIncrY.addActionListener(m_btnActionListener);
    m_btnDecrY.addActionListener(m_btnActionListener);
    m_btnIncrZ.addActionListener(m_btnActionListener);
    m_btnDecrZ.addActionListener(m_btnActionListener);

    gbc = (GridBagConstraints) gbcTwoCol.clone();
    toolbar.add(m_btnDecrX, gbc);
    gbc.gridx = 1;
    gbc.insets.left = gbc.insets.right;
    gbc.insets.right = gbcDefault.insets.right;
    toolbar.add(m_btnIncrX, gbc);

    gbc = (GridBagConstraints) gbcTwoCol.clone();
    toolbar.add(m_btnDecrY, gbc);
    gbc.gridx = 1;
    gbc.insets.left = gbc.insets.right;
    gbc.insets.right = gbcDefault.insets.right;
    toolbar.add(m_btnIncrY, gbc);

    gbc = (GridBagConstraints) gbcTwoCol.clone();
    toolbar.add(m_btnDecrZ, gbc);
    gbc.gridx = 1;
    gbc.insets.left = gbc.insets.right;
    gbc.insets.right = gbcDefault.insets.right;
    toolbar.add(m_btnIncrZ, gbc);

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
        // initialise the scale value so that the model fits into the
        // render window
        // TODO scale

      }
      return model;
    }
    return null;
  }

  public static void main(String[] args) {
    System.out.println("*****************************************");
    System.out.println("* 159.235 Assignment 3, Semester 2 2016 *");
    System.out.println("* Submitted by:  Cross, Dylan, 15219491 *");
    System.out.println("*****************************************");

    ModelViewer.create();
  }
}