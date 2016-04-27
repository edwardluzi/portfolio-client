package org.goldenroute.portfolioclient.utils;


import android.graphics.Color;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class PriceFormatter {
    public static void setDecimalText(TextView textView, BigDecimal value) {

        if (value != null) {
            DecimalFormat formatter = new DecimalFormat();
            formatter.setGroupingUsed(true);
            formatter.setGroupingSize(3);
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
            textView.setText(formatter.format(value));
        }
    }

    public static void setChangeText(TextView textView, BigDecimal value) {

        if (value != null) {
            DecimalFormat formatter = new DecimalFormat();
            formatter.setGroupingUsed(true);
            formatter.setGroupingSize(3);
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
            textView.setText(formatter.format(value));
            if (value.compareTo(BigDecimal.ZERO) >= 0) {
                textView.setTextColor(Color.GREEN);
            } else {
                textView.setTextColor(Color.RED);
            }
        }
    }

    public static void setChangePercentageText(TextView textView, BigDecimal value) {
        if (value != null) {
            DecimalFormat formatter = new DecimalFormat();
            formatter.setGroupingUsed(true);
            formatter.setGroupingSize(3);
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
            formatter.setPositivePrefix("+");
            formatter.setNegativePrefix("-");
            String text = formatter.format(value.multiply(BigDecimal.valueOf(100))) + "%";
            textView.setText(text);
            if (value.compareTo(BigDecimal.ZERO) >= 0) {
                textView.setTextColor(Color.GREEN);
            } else {
                textView.setTextColor(Color.RED);
            }
        }
    }
}
