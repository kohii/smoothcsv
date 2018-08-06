package com.smoothcsv.core.sql.component;

import java.awt.Color;

import com.smoothcsv.swing.icon.AwesomeIcon;
import com.smoothcsv.swing.icon.AwesomeIconConstants;

/**
 * @author kohii
 */
public class SqlComponentConstants {

  public static final AwesomeIcon TABLE_ICON = AwesomeIcon.create(AwesomeIconConstants.FA_TABLE, Color.decode("#888888"));

  public static final AwesomeIcon SQL_INSERT_TEXT_ICON = AwesomeIcon.create(AwesomeIconConstants.FA_ANGLE_DOUBLE_RIGHT, Color.decode("#cccccc"));
  public static final AwesomeIcon SQL_INSERT_TEXT_ICON_HOVER = AwesomeIcon.create(AwesomeIconConstants.FA_ANGLE_DOUBLE_RIGHT, Color.decode("#3c7565"));
  public static final AwesomeIcon SQL_INSERT_TEXT_ICON_SELECTED_HOVER = AwesomeIcon.create(AwesomeIconConstants.FA_ANGLE_DOUBLE_RIGHT, Color.decode("#adeddb"));

  public static final Color HOVERED_CELL_BACKGROUND = Color.decode("#f4f4f4");
  public static final Color SELECTED_CELL_BACKGROUND = Color.decode("#116CD6");
}
