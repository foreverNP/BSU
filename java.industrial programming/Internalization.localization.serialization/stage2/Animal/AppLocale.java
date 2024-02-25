package Animal;

import java.util.*;

public class

AppLocale {
    private static final String strMsg = "msg";
    private static Locale loc = Locale.getDefault();
    private static ResourceBundle res = ResourceBundle.getBundle(AppLocale.strMsg, AppLocale.loc);

    public static Locale get() {
        return AppLocale.loc;
    }

    public static void set(Locale loc) {
        AppLocale.loc = loc;
        res = ResourceBundle.getBundle(AppLocale.strMsg, AppLocale.loc);
    }

    public static ResourceBundle getBundle() {
        return AppLocale.res;
    }

    public static String getString(String key) {
        return AppLocale.res.getString(key);
    }

    // Resource keys:

    public static final String dog = "dog";
    public static final String cat = "cat";
    public static final String parrot = "parrot";
    public static final String dog_voice = "dog_voice";
    public static final String cat_voice = "cat_voice";
    public static final String parrot_voice = "parrot_voice";
    public static final String name = "name";
    public static final String age = "age";
    public static final String species = "species";
    public static final String color = "color";
    public static final String breed = "breed";
    public static final String GRAY = "GRAY";
    public static final String BLACK = "BLACK";
    public static final String WHITE = "WHITE";
    public static final String SHEPHERD = "SHEPHERD";
    public static final String LABRADOR = "LABRADOR";
    public static final String POODLE = "POODLE";
    public static final String MACAW = "MACAW";
    public static final String COCKATIEL = "COCKATIEL";
    public static final String PARAKEET = "PARAKEET";
    public static final String OTHER = "OTHER";
    public static final String dog_default_name = "dog_default_name";
    public static final String cat_default_name = "cat_default_name";
    public static final String parrot_default_name = "parrot_default_name";
    public static final String creation = "creation";
}