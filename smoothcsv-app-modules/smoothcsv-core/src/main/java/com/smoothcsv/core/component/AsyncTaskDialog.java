package com.smoothcsv.core.component;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

import com.smoothcsv.framework.util.SCBundle;
import com.smoothcsv.swing.utils.SwingUtils;
import org.apache.commons.lang3.StringUtils;

public class AsyncTaskDialog extends JDialog {

  private static final long serialVersionUID = -1399739025229156187L;

  private final JPanel contentPanel = new JPanel();

  private JPanel buttonPane;

  private int minHeight = 173;
  private int minWidth = 460;
  private int maxHeight = 210;
  private int maxWidth = 550;

  public static final int OK_PROCESSED = 0;
  public static final int CANCEL_PROCESSED = 1;
  public static final int NO_PROCESSED = 2;

  private int processedType = -1;

  private JButton okButton;

  private JButton cancelButton;

  private JButton noButton;

  private JButton defaultButton;

  private final JLabel label = new JLabel("　");
  private final JLabel label_1 = new JLabel("　");
  private final JLabel label_2 = new JLabel("　");

  @SuppressWarnings("serial")
  private AbstractAction cancelAction = new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (!processCancel()) {
        return;
      }
      processedType = CANCEL_PROCESSED;
      AsyncTaskDialog.this.dispose();
    }
  };

  /**
   * @wbp.parser.constructor
   */
  public AsyncTaskDialog() {
    this((Frame) null);
  }

  public AsyncTaskDialog(Frame owner) {
    this(owner, false);
  }

  public AsyncTaskDialog(Frame owner, boolean modal) {
    this(owner, null, modal);
  }

  public AsyncTaskDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
    initialize();
  }

  public AsyncTaskDialog(Frame owner, String title) {
    super(owner, title, ModalityType.DOCUMENT_MODAL);
    initialize();
  }

  public AsyncTaskDialog(Dialog owner) {
    this(owner, false);
  }

  public AsyncTaskDialog(Dialog owner, boolean modal) {
    this(owner, null, modal);
  }

  public AsyncTaskDialog(Dialog owner, String title, boolean modal) {
    super(owner, title, modal);
    initialize();
  }

  /**
   * Create the dialog.
   */
  private void initialize() {
    label.setIcon(SwingUtils.getImageIcon("/img/progress.gif"));

    setBounds(0, 0, minWidth, minHeight);
    setPreferredSize(new Dimension(minWidth, minHeight));
    setResizable(false);

    getContentPane().setLayout(new BorderLayout());
    contentPanel.setBorder(createContentPanelBorder());
    getContentPane().add(contentPanel, BorderLayout.CENTER);

    createButtons();

    buttonPane = createButtonPane();
    getContentPane().add(buttonPane, BorderLayout.SOUTH);

    getContentPane().setFocusable(false);
    contentPanel.setFocusable(false);
    GridBagLayout gbl_contentPanel = new GridBagLayout();
    gbl_contentPanel.columnWidths = new int[]{0, 0};
    gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0};
    gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
    gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0,
        Double.MIN_VALUE};
    contentPanel.setLayout(gbl_contentPanel);
    {
      GridBagConstraints gbc_label = new GridBagConstraints();
      gbc_label.fill = GridBagConstraints.VERTICAL;
      gbc_label.anchor = GridBagConstraints.WEST;
      gbc_label.insets = new Insets(0, 0, 5, 0);
      gbc_label.gridx = 0;
      gbc_label.gridy = 0;
      contentPanel.add(label, gbc_label);
    }
    {
      GridBagConstraints gbc_label_1 = new GridBagConstraints();
      gbc_label_1.fill = GridBagConstraints.VERTICAL;
      gbc_label_1.anchor = GridBagConstraints.WEST;
      gbc_label_1.insets = new Insets(5, 10, 5, 0);
      gbc_label_1.gridx = 0;
      gbc_label_1.gridy = 1;
      contentPanel.add(label_1, gbc_label_1);
    }
    {
      GridBagConstraints gbc_label_2 = new GridBagConstraints();
      gbc_label_2.fill = GridBagConstraints.VERTICAL;
      gbc_label_2.anchor = GridBagConstraints.WEST;
      gbc_label_2.insets = new Insets(5, 10, 0, 0);
      gbc_label_2.gridx = 0;
      gbc_label_2.gridy = 2;
      contentPanel.add(label_2, gbc_label_2);
    }
    buttonPane.setFocusable(false);

    getRootPane()
        .getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel");

    getRootPane().getActionMap().put("Cancel", cancelAction);

    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        cancelAction.actionPerformed(null);
      }
    });

  }

  private Border createContentPanelBorder() {
    return BorderFactory.createEmptyBorder(5, 5, 5, 5);
  }

  private JPanel createButtonPane() {
    JPanel buttonPane = new JPanel();
    buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
    buttonPane.add(cancelButton);
    return buttonPane;
  }

  private void createButtons() {
    cancelButton = new JButton(SCBundle.get("key.cancel"));
    cancelButton.addActionListener(cancelAction);
    SwingUtils.putNemonicKey(cancelButton, 'C');
  }

  public int getProcessedType() {
    return processedType;
  }

  protected boolean processCancel() {
    return true;
  }

  public boolean isCanceled() {
    return processedType == CANCEL_PROCESSED;
  }

  @Override
  public void setVisible(boolean arg0) {
    if (arg0) {
      processedType = -1;
    }
    super.setVisible(arg0);
  }

  public void setText1(String s) {
    label.setText(StringUtils.isEmpty(s) ? " " : s);
  }

  public void setText2(String s) {
    label_1.setText(StringUtils.isEmpty(s) ? " " : s);
  }

  public void setText3(String s) {
    label_2.setText(StringUtils.isEmpty(s) ? " " : s);
  }


  public static class Monitor {

    private AsyncTaskDialog asyncTaskDialog;

    private boolean started = false;

    public boolean isStarted() {
      return started;
    }

    private boolean canceled;

    public boolean isCanceled() {
      return canceled;
    }

    public Monitor(Frame owner, String title, boolean modal) {
      asyncTaskDialog = new AsyncTaskDialog(owner, title, modal);
    }

    public Monitor(Dialog owner, String title, boolean modal) {
      asyncTaskDialog = new AsyncTaskDialog(owner, title, modal);
    }

    public void setDialogSize(int width, int height) {
      packDialog();
      asyncTaskDialog.setBounds(0, 0, width, height);
    }

    public void packDialog() {
      asyncTaskDialog.pack();
    }

    public AsyncTaskDialog getAsyncTaskDialog() {
      return asyncTaskDialog;
    }

    public void begin() {
      started = true;
      asyncTaskDialog.setLocationRelativeTo(asyncTaskDialog.getOwner());
      asyncTaskDialog.setVisible(true);
      if (asyncTaskDialog.isCanceled()) {
        canceled = true;
      }
    }

    public void end() {
      asyncTaskDialog.setVisible(false);
      asyncTaskDialog.dispose();
    }

    public boolean isDialogVisible() {
      return asyncTaskDialog.isVisible();
    }

    public void setText1(final String s) {
      asyncTaskDialog.setText1(s);
    }

    public void setText2(final String s) {
      asyncTaskDialog.setText2(s);
    }

    public void setText3(final String s) {
      asyncTaskDialog.setText3(s);
    }
  }
}
