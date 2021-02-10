package code.debug;

public class ErrorManager {

    public static boolean isNumber(String path){
        try{
            Integer.parseInt(path);
            return true;

        }catch (NumberFormatException numberFormatException){
            return false;
        }
    }

}
