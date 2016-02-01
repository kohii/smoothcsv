/*
 * Copyright 2015 kohii
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
package com.smoothcsv.core.filter;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.smoothcsv.core.find.Regex;
import com.smoothcsv.framework.util.SCBundle;

import lombok.Getter;

/**
 * @author kohii
 */
public class FilterConditionItem extends FilterConditionGroup {

  @Getter
  private final IValue left;
  @Getter
  private final Criteria criteria;
  @Getter
  private final IValue[] right;
  @Getter
  private final boolean caseSensitive;

  /**
   * @param left
   * @param criteria
   * @param right
   * @param caseSensitive
   */
  public FilterConditionItem(IValue left, Criteria criteria, IValue[] right,
      boolean caseSensitive) {
    this.left = left;
    this.criteria = criteria;
    this.right = right;
    this.caseSensitive = caseSensitive;
  }

  public FilterConditionItem(IValue left, Criteria criteria, IValue right, boolean caseSensitive) {
    this(left, criteria, new IValue[] {right}, caseSensitive);
  }

  public FilterConditionItem(IValue left, Criteria criteria) {
    this(left, criteria, (IValue[]) null, false);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(left);
    sb.append(' ');
    sb.append(criteria);
    if (right != null) {
      sb.append(' ');
      for (int i = 0; i < right.length; i++) {
        if (i != 0) {
          sb.append(", ");
        }
        sb.append(right[i]);
      }
      sb.append(' ');
      if (!caseSensitive) {
        sb.append("(" + SCBundle.get("key.caseSensitive") + ")");
      }
    }
    return sb.toString();
  }

  @Override
  protected String toString(int depth) {
    return toString();
  }

  @Override
  public boolean matches(List<String> list) {
    String l, r;
    BigDecimal ln, rn;
    switch (criteria) {
      case EQUALS:
        l = left.getValue(list);
        r = right[0].getValue(list);
        if (l == null || r == null) {
          return false;
        }
        return caseSensitive ? l.equalsIgnoreCase(r) : l.equals(r);
      case DOES_NOT_EQUAL:
        l = left.getValue(list);
        r = right[0].getValue(list);
        if (l == null || r == null) {
          return false;
        }
        return caseSensitive ? !l.equalsIgnoreCase(r) : !l.equals(r);
      case MATCHES_THE_REGEX_OF:
        l = left.getValue(list);
        Regex regex = right[0].getRegexValue(list, caseSensitive);
        if (l == null || regex == null) {
          return false;
        }
        return regex.getPattern().matcher(l).matches();
      case STARTS_WITH:
        l = left.getValue(list);
        r = right[0].getValue(list);
        if (l == null || r == null) {
          return false;
        }
        return caseSensitive ? StringUtils.startsWithIgnoreCase(l, r)
            : StringUtils.startsWith(l, r);
      case DOES_NOT_START_WITH:
        l = left.getValue(list);
        r = right[0].getValue(list);
        if (l == null || r == null) {
          return false;
        }
        return caseSensitive ? !StringUtils.startsWithIgnoreCase(l, r)
            : !StringUtils.startsWith(l, r);
      case ENDS_WITH:
        l = left.getValue(list);
        r = right[0].getValue(list);
        if (l == null || r == null) {
          return false;
        }
        return caseSensitive ? StringUtils.endsWithIgnoreCase(l, r) : StringUtils.endsWith(l, r);
      case DOES_NOT_END_WITH:
        l = left.getValue(list);
        r = right[0].getValue(list);
        if (l == null || r == null) {
          return false;
        }
        return caseSensitive ? !StringUtils.endsWithIgnoreCase(l, r) : !StringUtils.endsWith(l, r);
      case CONTAINS:
        l = left.getValue(list);
        r = right[0].getValue(list);
        if (l == null || r == null) {
          return false;
        }
        return caseSensitive ? StringUtils.containsIgnoreCase(l, r) : StringUtils.contains(l, r);
      case DOES_NOT_CONTAIN:
        l = left.getValue(list);
        r = right[0].getValue(list);
        if (l == null || r == null) {
          return false;
        }
        return caseSensitive ? !StringUtils.containsIgnoreCase(l, r) : !StringUtils.contains(l, r);
      case IS_A_NUMBER_GREATER_THAN:
        ln = left.getNumericValue(list);
        rn = right[0].getNumericValue(list);
        if (ln == null || rn == null) {
          return false;
        }
        try {
          return ln.compareTo(rn) > 0;
        } catch (Exception e) {
          return false;
        }
      case IS_A_NUMBER_LESS_THAN:
        ln = left.getNumericValue(list);
        rn = right[0].getNumericValue(list);
        if (ln == null || rn == null) {
          return false;
        }
        try {
          return ln.compareTo(rn) < 0;
        } catch (Exception e) {
          return false;
        }
      case IS_A_NUMBER_EQUAL_TO_OR_GREATER_THAN:
        ln = left.getNumericValue(list);
        rn = right[0].getNumericValue(list);
        if (ln == null || rn == null) {
          return false;
        }
        try {
          return ln.compareTo(rn) >= 0;
        } catch (Exception e) {
          return false;
        }
      case IS_A_NUMBER_EQUAL_TO_OR_LESS_THAN:
        ln = left.getNumericValue(list);
        rn = right[0].getNumericValue(list);
        if (ln == null || rn == null) {
          return false;
        }
        try {
          return ln.compareTo(rn) <= 0;
        } catch (Exception e) {
          return false;
        }
      case IS_IN:
        l = left.getValue(list);
        if (l == null) {
          return false;
        }
        for (IValue rv : right) {
          r = rv.getValue(list);
          if (r == null) {
            return false;
          }
          boolean b = caseSensitive ? l.equalsIgnoreCase(r) : l.equals(r);
          if (b) {
            return true;
          }
        }
        return false;

      case IS_NOT_IN:
        l = left.getValue(list);
        if (l == null) {
          return false;
        }
        for (IValue rv : right) {
          r = rv.getValue(list);
          if (r == null) {
            return false;
          }
          boolean b = caseSensitive ? l.equalsIgnoreCase(r) : l.equals(r);
          if (b) {
            return false;
          }
        }
        return true;
      case IS_EMPTY:
        l = left.getValue(list);
        if (l == null) {
          return false;
        }
        return StringUtils.isEmpty(l);
      case IS_NOT_EMPTY:
        l = left.getValue(list);
        if (l == null) {
          return false;
        }
        return StringUtils.isNotEmpty(l);
      case IS_A_NUMERIC:
        l = left.getValue(list);
        if (l == null) {
          return false;
        }
        return StringUtils.isNumeric(l);
      case IS_NOT_A_NUMERIC:
        l = left.getValue(list);
        if (l == null) {
          return false;
        }
        return !StringUtils.isNumeric(l);
      case IS_A_STRING_GREATER_THAN:
        l = left.getValue(list);
        r = right[0].getValue(list);
        if (l == null || r == null) {
          return false;
        }
        if (caseSensitive) {
          return l.toLowerCase().compareTo(r.toLowerCase()) > 0;
        } else {
          return l.compareTo(r) > 0;
        }
      case IS_A_STRING_LESS_THAN:
        l = left.getValue(list);
        r = right[0].getValue(list);
        if (l == null || r == null) {
          return false;
        }
        if (caseSensitive) {
          return l.toLowerCase().compareTo(r.toLowerCase()) < 0;
        } else {
          return l.compareTo(r) < 0;
        }
      case EXISTS:
        l = left.getValue(list);
        if (l == null) {
          return false;
        }
        return l != null;
      case DOES_NOT_EXISTS:
        l = left.getValue(list);
        if (l == null) {
          return false;
        }
        return l == null;
      default:
        throw new IllegalArgumentException();
    }
  }
}
