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
package com.smoothcsv.core.sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author kohii
 *
 */
public class CsvSorter {

  public static SortResult sort(List<SortCriteria> criterias, List<List> data,
      boolean exceptLastEmptyRow) {
    int rowSize = data.size();
    boolean exceptLastRow = exceptLastEmptyRow && data.get(rowSize - 1).size() == 0;
    if (exceptLastRow) {
      rowSize--;
    }
    RowWrapper[] rowWrappers = new RowWrapper[rowSize];
    for (int i = 0; i < rowSize; i++) {
      rowWrappers[i] = new RowWrapper(i, data.get(i));
    }

    SortInfo[] sortInfos = criterias.stream().map(c -> new SortInfo(c)).toArray(SortInfo[]::new);
    Arrays.sort(rowWrappers, new CsvSortComparator(sortInfos));

    int[] order = new int[rowSize + (exceptLastRow ? 1 : 0)];
    List<List> sorted = new ArrayList<>(rowSize + (exceptLastRow ? 1 : 0));
    for (int i = 0; i < rowSize; i++) {
      order[i] = rowWrappers[i].num;
      sorted.add(rowWrappers[i].rowData);
    }
    if (exceptLastRow) {
      order[rowSize] = rowSize;
      sorted.add(data.get(rowSize));
    }
    return new SortResult(order, sorted);
  }

  private static int getBlanksOptionAsInt(SortCriteria criteria, int sortOrder) {
    switch (criteria.getBlanksOption()) {
      case BLANKS_FIRST:
        return 1;
      case BLANKS_LAST:
        return -1;
      case DEFAULT:
        return criteria.getOrder() == Order.ASCENDING ? -1 : 1;
      default:
        throw new IllegalArgumentException(criteria.getBlanksOption() + "");
    }
  }

  private static boolean isDecimalNumber(final String s) {
    if (s == null || s.length() == 0) {
      return false;
    }

    boolean hasDot = false;

    int size = s.length();
    for (int i = 0; i < size; i++) {
      char chr = s.charAt(i);
      if ((chr < '0' || '9' < chr) && chr != '.') {
        if (!(i == 0 && chr == '-')) {
          return false;
        }
      }
      if (chr == '.') {
        if (hasDot) {
          return false;
        }
        hasDot = true;
      }
    }

    return true;
  }

  private static class SortInfo {
    int index;
    int sortOrder;
    int nullsOption;
    boolean asNumber;

    public SortInfo(SortCriteria criteria) {
      this.index = criteria.getColumn();
      this.sortOrder = criteria.getOrder() == Order.ASCENDING ? 1 : -1;
      this.nullsOption = getBlanksOptionAsInt(criteria, this.sortOrder);
      this.asNumber = criteria.getType() == ValueType.NUMBER;
    }
  }

  private static class RowWrapper {
    int num;
    List rowData;

    public RowWrapper(int num, List rowData) {
      this.num = num;
      this.rowData = rowData;
    }
  }

  private static class CsvSortComparator implements Comparator<RowWrapper> {

    private SortInfo[] sortInfos;

    public CsvSortComparator(SortInfo[] sortInfos) {
      this.sortInfos = sortInfos;
    }

    private int compareAsNumber(String arg0, String arg1, int nullsOption, int sortOrder) {

      if (arg0.length() == 0) {
        if (arg1.length() == 0) {
          return 0;
        } else {
          return -1 * nullsOption;
        }
      } else {
        if (arg1.length() == 0) {
          return nullsOption;
        } else {
          if (!isDecimalNumber(arg0)) {
            if (!isDecimalNumber(arg1)) {
              return compareAsString(arg0, arg1, nullsOption, sortOrder);
            } else {
              return 1;
            }
          } else {
            if (!isDecimalNumber(arg1)) {
              return -1;
            } else {
              return new BigDecimal(arg0).compareTo(new BigDecimal(arg1)) * sortOrder;
            }
          }
        }
      }
    }

    private int compareAsString(String arg0, String arg1, int nullsOption, int sortOrder) {

      if (arg0.length() == 0) {
        if (arg1.length() == 0) {
          return 0;
        } else {
          return -1 * nullsOption;
        }
      } else {
        if (arg1.length() == 0) {
          return nullsOption;
        } else {
          return arg0.compareTo(arg1) * sortOrder;
        }
      }
    }

    @Override
    public int compare(RowWrapper row1, RowWrapper row2) {
      List list1 = row1.rowData;
      List list2 = row2.rowData;
      for (SortInfo sortInfo : sortInfos) {
        int ret = 0;
        int index = sortInfo.index;
        if (list1.size() <= index) {
          if (list2.size() <= index) {
            ret = 0;
          } else {
            ret = 1;
          }
        } else {
          if (list2.size() <= index) {
            ret = -1;
          } else {
            Object val0 = list1.get(index);
            Object val1 = list2.get(index);
            if (!sortInfo.asNumber) {
              ret =
                  compareAsString(val0 == null ? "" : val0.toString(),
                      val1 == null ? "" : val1.toString(), sortInfo.nullsOption, sortInfo.sortOrder);
            } else {
              ret =
                  compareAsNumber(val0 == null ? "" : val0.toString(),
                      val1 == null ? "" : val1.toString(), sortInfo.nullsOption, sortInfo.sortOrder);
            }
          }

        }
        if (ret != 0) {
          return ret;
        }
      }
      return 0;
    }
  }

  @Getter
  @AllArgsConstructor
  public static class SortResult {
    private int[] order;
    private List<List> sortedData;
  }
}
