package org.smart4j.chapter2.util;
/*
* 转型工具类
* */
public class CastUtil {
    /*
    * 转为String
    * */
    public static String castString (Object obj) {
        return castString(obj, "");
    }
    /*
    * 转为String(提供默认值)
    * */
    public static String castString (Object obj, String defaultValue) {
        return obj != null ? String.valueOf(obj) : defaultValue;
    }
    /*
    * 转为double
    * */
    public static double castDouble (Object obj) {
        return castDouble(obj, 0.0D);
    }
    /*
    * 转为double(提供默认值)
    * */
    public static double castDouble (Object obj, double defaultValue) {
        double doubleValue = defaultValue;
        if (obj != null) {
            String strValue = castString(obj);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    defaultValue = Double.parseDouble(strValue);
                } catch (NumberFormatException e) {
                    doubleValue = defaultValue;

                }
            }
        }
        return doubleValue;
    }
    /*
    * 转型为long
    * */
    public static long castLong (Object obj) {
        return castLong(obj, 0L);
    }
    /*
    * 转型为long（提供默认值）
    * */
    public static long castLong (Object obj, long defaultValue) {
        long longValue = defaultValue;
        if (obj != null) {
            String strValue = castString(obj);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    longValue = Long.parseLong(strValue);
                } catch (NumberFormatException e) {
                    longValue = defaultValue;
                }
            }
        }
        return longValue;
    }
    /*
    * 转为int型
    * */
    public static int castInt (Object obj) {
        return castInt(obj, 0);
    }
    /*
    * 转为int型（提供默认值）
    * */
    public static int castInt (Object obj, int defaultValue) {
        int intValue = defaultValue;
        if (obj != null) {
            String strValue = castString(obj);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    intValue = Integer.parseInt(strValue);
                } catch (NumberFormatException e) {
                    intValue = defaultValue;
                }
            }
        }
        return intValue;
    }
    /*
    * 转为boolean型
    * */
    public static boolean castBoolean (Object obj) {
        return castBoolean(obj, false);
    }
    /*
    * 转为Boolean型（提供默认值）
    * */
    public static boolean castBoolean (Object obj, boolean defaultValue) {
        boolean booleanValue = defaultValue;
        if (obj != null) {
            booleanValue = Boolean.parseBoolean(castString(obj));
        }
        return booleanValue;
    }
}
