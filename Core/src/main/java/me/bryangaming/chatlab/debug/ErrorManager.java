package me.bryangaming.chatlab.debug;

public class ErrorManager {

    public static boolean isNumber(String path) {
        try {
            Integer.parseInt(path);
            return true;

        } catch (NumberFormatException numberFormatException) {
            return false;
        }
    }

    public static boolean isNumberOr(String... paths) {
        for (String path : paths) {
            try {
                Integer.parseInt(path);
                return true;

            } catch (NumberFormatException numberFormatException) {
                return false;
            }
        }

        return false;
    }
}
