package sample;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputChecker {
    private static Pattern pattern;
    private static Matcher matcher;

    public static boolean check(String pat, String mat){
        pattern = Pattern.compile(pat);
        matcher = pattern.matcher(mat);
        return matcher.matches();
    }
}
