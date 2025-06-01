import java.util.*;

public class Messages {
    private static ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    public static void setLocale(Locale locale) {
        bundle = ResourceBundle.getBundle("messages", locale);
    }

    public static String get(String key) {
        return bundle.getString(key);
    }
}