/*
 * Copyright 2014 kohii.
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
import com.smoothcsv.core.csvsheet.edits.Transaction;
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
    try (Transaction tran = gridSheetPane.transaction()) {
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

    // parse source data into DataSeed
    DataSeed[] dataSeeds = new DataSeed[source.length];
    for (int i = 0; i < source.length; i++) {
      String s = source[i];
      if (StringUtils.isDecimal(s)) {
        dataSeeds[i] = new NumericDataSeed(s);
      } else {
        Matcher matcher = numericPtn.matcher(s);
        if (!matcher.find()) {
          dataSeeds[i] = new StringDataSeed(s);
        } else {
          int start = -1, end = -1;
          do {
            int groupCount = matcher.groupCount();
            start = matcher.start(groupCount);
            end = matcher.end(groupCount);
          } while (matcher.find());
          dataSeeds[i] =
              new CombinedDataSeed(Long.parseLong(s.substring(start, end)), s.substring(0, start),
                  s.substring(end));
        }
      }
    }

    // calculate the increments
    int prevIdx = -1;
    for (int i = 0; i < dataSeeds.length; i++) {
      DataSeed s = dataSeeds[i];
      if (s instanceof NumericDataSeed) {
        NumericDataSeed s1 = (NumericDataSeed) s;
        if (prevIdx == -1) {
          s1.increment = reverse ? BigDecimal.ONE.negate() : BigDecimal.ONE;
        } else {
          s1.increment = s1.value.subtract(((NumericDataSeed) dataSeeds[prevIdx]).value);
          // share the same seed as the cells are sequential
          dataSeeds[prevIdx] = s1;
        }
        prevIdx = i;
      }
    }
    prevIdx = -1;
    for (int i = 0; i < dataSeeds.length; i++) {
      DataSeed s = dataSeeds[i];
      if (s instanceof CombinedDataSeed) {
        CombinedDataSeed s1 = (CombinedDataSeed) s;
        if (prevIdx == -1) {
          s1.increment = reverse ? -1 : 1;
        } else {
          CombinedDataSeed s0 = (CombinedDataSeed) dataSeeds[prevIdx];
          if (s0.prefix.equals(s1.prefix) && s0.postfix.equals(s1.postfix)) {
            s1.increment = s1.value - s0.value;
            // share the same seed as the cells are sequential
            dataSeeds[prevIdx] = s1;
          } else {
            s1.increment = reverse ? -1 : 1;
          }
        }
        prevIdx = i;
      }
    }

    String[] ret = new String[num];
    for (int i = 0; i < num; i++) {
      ret[i] = dataSeeds[i % dataSeeds.length].next();
    }
    return ret;
  }

  private static interface DataSeed {
    String next();
  }

  private static class StringDataSeed implements DataSeed {
    String value;

    StringDataSeed(String value) {
      this.value = value;
    }

    @Override
    public String next() {
      return value;
    }
  }

  private static class NumericDataSeed implements DataSeed {

    BigDecimal value;
    BigDecimal increment;

    NumericDataSeed(String value) {
      this.value = new BigDecimal(value);
    }

    @Override
    public String next() {
      value = value.add(increment);
      return value.toString();
    }
  }

  private static class CombinedDataSeed implements DataSeed {
    long value;
    long increment;
    String prefix;
    String postfix;

    CombinedDataSeed(long value, String prefix, String postfix) {
      this.value = value;
      this.prefix = prefix;
      this.postfix = postfix;
    }

    @Override
    public String next() {
      value += increment;
      return prefix + value + postfix;
    }
  }
}
