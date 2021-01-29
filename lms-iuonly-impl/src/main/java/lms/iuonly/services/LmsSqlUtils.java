package lms.iuonly.services;

import lombok.NonNull;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chmaurer on 1/29/16.
 */
public class LmsSqlUtils {

    /**
     * Limit that (oracle) databases might put on the number of items in an IN clause.
     */
    private static final int IN_CLAUSE_ITEM_LIMIT = 999;

    /**
     * Get the
     * @return
     */
    private static int getINClauseItemLimit() {
        return IN_CLAUSE_ITEM_LIMIT;
    }

    /**
     * Build a comma delimited list of values.  If the values should be represented as strings, wrap with a single quote (')
     * @param values List of values to turn into a comma delimited list
     * @param resultsAsStrings Flag indicating if the results should be treated as strings (wrapped in quotes) or numbers
     * @return
     */
    public static String buildCommaDelimitedList(@NonNull List<String> values, boolean resultsAsStrings) {
        String quotedChar = resultsAsStrings ? "'" : "";
        String delimiter = quotedChar + "," + quotedChar;

        String result = String.join(delimiter, values);
        result = quotedChar + result + quotedChar;
        return result;
    }

    /**
     *
     * @param fieldName
     * @param itemList
     * @param includeWhere
     * @return
     */
    public static String buildWhereInClause(String fieldName, List<String> itemList, boolean includeWhere) {
        return buildWhereInClause(fieldName, itemList, includeWhere, true);
    }

    /**
     *
     * @param fieldName
     * @param itemList
     * @param includeWhere
     * @param resultsAsStrings Flag indicating if the results should be treated as strings (wrapped in quotes) or numbers
     * @return
     */
    public static String buildWhereInClause(String fieldName, List<String> itemList, boolean includeWhere, boolean resultsAsStrings) {
        StringBuilder sql = new StringBuilder();
        if (itemList.size() > LmsSqlUtils.getINClauseItemLimit()) {
            List<List<String>> subLists = ListUtils.partition(itemList, LmsSqlUtils.getINClauseItemLimit());
            if (includeWhere) {
                sql.append("where ");
            }
            sql.append("(");
            List<String> predicates = new ArrayList<>();
            for (List<String> sublist : subLists) {
                predicates.add(buildPredicate(fieldName, sublist, resultsAsStrings));
            }
            sql.append(String.join(" or ", predicates));

            sql.append(")");
        } else {
            if (includeWhere) {
                sql.append("where ");
            }
            sql.append(buildPredicate(fieldName, itemList, resultsAsStrings));
        }
        return sql.toString();
    }

    /**
     *
     * @param fieldName
     * @param theList
     * @param resultsAsStrings Flag indicating if the results should be treated as strings (wrapped in quotes) or numbers
     * @return
     */
    private static String buildPredicate(String fieldName, List<String> theList, boolean resultsAsStrings) {
        return fieldName + " in (" + LmsSqlUtils.buildCommaDelimitedList(theList, resultsAsStrings) + ")";
    }
}
