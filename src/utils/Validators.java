package utils;

public class Validators {
    public static boolean isNationalId(String s) {
        return s != null && s.matches("\\d{12}");
    }
    public static boolean isName(String s) {
        return s != null && s.matches("(?i)[A-Z][A-Za-z\\s]{1,24}");
    }
    public static boolean isGender(String s) {
        return s != null && s.matches("(?i)male|female");
    }
    public static boolean isPhoneVN(String s) {
        return s != null && s.matches("0\\d{9}");
    }
    public static boolean isRoomId(String s) {
        return s != null && s.matches("(?i)[A-Z]\\d{1,4}");
    }
    public static boolean isRoomType(String s) {
        return s != null && s.matches("(?i)Deluxe|Standard|Suite|Superior|VIP");
    }
}