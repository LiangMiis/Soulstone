package org.LiangMi.soulstone.other;

public class helper {
    public static String KeepOneDecimal(float d) {
        if (d > 0) {
            //d = (float) (Math.ceil(d * 10) / 10); // Rounding is a vanilla approach, and if you pursue perfection, it may be foolish to do so
            String s = String.format("%.1f", d);
            return s.replace(".0", "");
        } else {
            return "0";
        }
    }
}
