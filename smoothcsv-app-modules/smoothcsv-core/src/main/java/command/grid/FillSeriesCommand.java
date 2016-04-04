/*
 * Copyright 2016 kohii
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package command.grid;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;

import com.smoothcsv.commons.constants.Direction;
import com.smoothcsv.commons.utils.StringUtils;
import com.smoothcsv.core.command.GridCommand;
import com.smoothcsv.core.csvsheet.CsvGridSheetModel;
import com.smoothcsv.core.csvsheet.CsvGridSheetPane;
import com.smoothcsv.core.csvsheet.edits.EditTransaction;
import com.smoothcsv.swing.gridsheet.model.GridSheetCellRange;

/**
 * @author kohii
 *
 */
public class FillSeriesCommand extends GridCommand {

  private static Pattern numericPtn = Pattern.compile("(\\d+)");

  @Override
  public void run(CsvGridSheetPane gridSheetPane) {

  }

  public static void autofill(CsvGridSheetPane gridSheetPane, GridSheetCellRange base,
      Direction direction, int num) {
    if (num == 0) {
      return;
    }
    try (EditTransaction tran = gridSheetPane.transaction()) {
      CsvGridSheetModel model = gridSheetPane.getModel();
      switch (direction) {
        case DOWN:
        case UP:
          // vertically
          for (int c = base.getFirstColumn(); c <= base.getLastColumn(); c++) {

            // collect base data
            String[] baseData = new String[base.getNumRows()];
            for (int r = base.getFirstRow(), i = 0; r <= base.getLastRow(); r++, i++) {
              baseData[i] = (String) model.getValueAt(r, c);
            }
            if (direction == Direction.UP) {
              ArrayUtils.reverse(baseData);
            }

            // create extended data
            String[] extendedData = getSeriesData(baseData, num, direction == Direction.UP);

            // apply to the model
            int rIdx, d;
            if (direction == Direction.UP) {
              d = -1;
              rIdx = base.getFirstRow() + d;
            } else {
              d = 1;
              rIdx = base.getLastRow() + d;
            }
            for (int i = 0; i < num; i++, rIdx += d) {
              model.setValueAt(extendedData[i], rIdx, c);
            }
          }
          break;
        case LEFT:
        case RIGHT:
          // horizontally
          for (int r = base.getFirstRow(); r <= base.getLastRow(); r++) {

            // collect base data
            String[] baseData = new String[base.getNumColumns()];
            for (int c = base.getFirstColumn(), i = 0; c <= base.getLastColumn(); c++, i++) {
              baseData[i] = (String) model.getValueAt(r, c);
            }
            if (direction == Direction.LEFT) {
              ArrayUtils.reverse(baseData);
            }

            // create extended data
            String[] extendedData = getSeriesData(baseData, num, direction == Direction.LEFT);

            // apply to the model
            int cIdx, d;
            if (direction == Direction.LEFT) {
              d = -1;
              cIdx = base.getFirstColumn() + d;
            } else {
              d = 1;
              cIdx = base.getLastColumn() + d;
            }
            for (int i = 0; i < num; i++, cIdx += d) {
              model.setValueAt(extendedData[i], r, cIdx);
            }
          }
          break;
      }
      GridSheetCellRange autofillRange = base.extend(direction, num);
      gridSheetPane.getSelectionModel().setSelectionIntervalNoChangeAnchor(
          autofillRange.getFirstRow(), autofillRange.getFirstColumn(), autofillRange.getLastRow(),
          autofillRange.getLastColumn());
    }
  }

  private static String[] getSeriesData(String[] source, int num, boolean reverse) {
    if (source.length == 0) {
      throw new IllegalArgumentException();
    }

    DataSeed dataSeed = generateDataSeed(source[0]);

    if (dataSeed != null && source.length > 1) {
      DataSeed secondDataSeed = generateDataSeed(source[1]);
      if (secondDataSeed != null) {
        dataSeed.setStepFrom(secondDataSeed);
      } else {
        dataSeed = null;
      }
    }

    if (dataSeed != null) {
      // test if every source string matches the generated value from data seed

      for (String string : source) {
        if (!dataSeed.next().equals(string)) {
          dataSeed = null;
          break;
        }
      }
    }

    String[] ret = new String[num];
    if (dataSeed != null) {
      for (int i = 0; i < num; i++) {
        ret[i] = dataSeed.next();
      }
    } else {
      for (int i = 0; i < num; i++) {
        ret[i] = source[i % source.length];
      }
    }
    return ret;
  }

  private static DataSeed generateDataSeed(String s) {
    if (StringUtils.isDecimal(s)) {
      return new NumericDataSeed(new BigDecimal(s));
    } else {
      Matcher matcher = numericPtn.matcher(s);
      if (matcher.find()) {
        int start = -1, end = -1;
        do {
          int groupCount = matcher.groupCount();
          start = matcher.start(groupCount);
          end = matcher.end(groupCount);
        } while (matcher.find());
        return new CombinedDataSeed(new BigDecimal(s.substring(start, end)), s.substring(0, start),
            s.substring(end));
      } else {
        return null;
      }
    }
  }

  private static interface DataSeed {
    String next();

    void setStepFrom(DataSeed next);

    void reset();
  }

  private static class NumericDataSeed implements DataSeed {

    final BigDecimal originalValue;
    BigDecimal value;
    BigDecimal step = BigDecimal.ONE;

    NumericDataSeed(BigDecimal value) {
      this.originalValue = this.value = value;
    }

    @Override
    public String next() {
      String ret = value.toString();
      value = value.add(step);
      return ret;
    }

    @Override
    public void setStepFrom(DataSeed next) {
      this.step = ((NumericDataSeed) next).value.subtract(this.value);
    }

    @Override
    public void reset() {
      this.value = originalValue;
    }
  }

  private static class CombinedDataSeed extends NumericDataSeed {
    final String prefix;
    final String postfix;

    CombinedDataSeed(BigDecimal value, String prefix, String postfix) {
      super(value);
      this.prefix = prefix;
      this.postfix = postfix;
    }

    @Override
    public String next() {
      return prefix + super.next() + postfix;
    }

    @Override
    public String toString() {
      return prefix + "(" + value + "~)" + postfix;
    }
  }
}
