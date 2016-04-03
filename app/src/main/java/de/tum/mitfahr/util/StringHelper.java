package de.tum.mitfahr.util;

/**
 * Created by abhijith on 19/08/14.
 */
public class StringHelper {

    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        if (str.trim().length() == 0) {
            return true;
        }
        if (str.equals("")) {
            return true;
        }
        if (str.equals("null") || str.equals("NULL")) {
            return true;
        }
        return false;
    }

    //configured according to the server reply
    public static String parseDate(String dateTimeStr) {
        String[] dateTime = dateTimeStr.split(" ");
        String dateStr =  dateTime[0];
        String[] dateArr = dateStr.split("-");
        String date = dateArr[2]+"/"+dateArr[1]+"/"+dateArr[0];
        return date;
    }

    public static String parseTime(String dateTimeStr) {
        String[] dateTime = dateTimeStr.split(" ");
        String time =  dateTime[1];
        return time;
    }
}
