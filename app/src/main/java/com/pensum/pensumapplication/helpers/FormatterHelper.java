package com.pensum.pensumapplication.helpers;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Created by violetaria on 8/25/16.
 */
public class FormatterHelper {
    public static String formatName(String fullName){
        String[] nameParts = fullName.split(" ");
        String displayName = "" + nameParts[0];
        if(nameParts.length > 1)
        {
            displayName += nameParts[nameParts.length-1].charAt(0) + ".";
        }
        return displayName;
    }

    public static String formatMoney(String s){
        String cleanString = s.replaceAll("[$,.]","");
        BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        String formatted = NumberFormat.getCurrencyInstance().format(parsed);
        return formatted;
    }

    public static String formatDoubleToMoney(Double d) {
        String doubleStr = d.toString();
        return doubleStr.length() - doubleStr.indexOf('.') -1 == 1? doubleStr+'0' : doubleStr;
    }
}
