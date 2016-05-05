package com.sms.deliamao.stockmarketsearch;

/**
 * Created by wenjieli on 5/4/16.
 */
public class FormatHelper {
    // Return + or - as prefix for number.
    public static String getNumberPrefix(double value) {
        if (value == 0) {
            return "";
        } else if (value > 0) {
            return "+";
        }
        return "-";
    }
}
