package com.dentalclinic.dental.UI;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public final class Validators {
    private Validators() {}

    public static boolean notEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static boolean isPositiveDouble(String s) {
        if (s == null) return false;
        try {
            double d = Double.parseDouble(s.trim());
            return d >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // simple Philippine phone-ish validation (loosish)
    private static final Pattern PHONE = Pattern.compile("^[+0-9()\\-\\s]{6,20}$");
    public static boolean isPhone(String s) {
        if (s == null) return false;
        String t = s.trim();
        return PHONE.matcher(t).matches();
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
    public static boolean isDateTime(String s) {
        if (s == null) return false;
        try {
            LocalDateTime.parse(s.trim(), FORMATTER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static LocalDateTime parseDateTime(String s) {
        return LocalDateTime.parse(s.trim(), FORMATTER);
    }

    public static DateTimeFormatter getFormatter() {
        return FORMATTER;
    }
}
