package edu.iu.uits.lms.iuonly.services;

/*-
 * #%L
 * lms-canvas-iu-custom-services
 * %%
 * Copyright (C) 2015 - 2022 Indiana University
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Indiana University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

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
