package edu.qhjy.util;

import java.util.regex.Pattern;

public final class MobileValidator {

    // 11 位手机号，第一位 1，第二位 3-9
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    private MobileValidator() {
    }

    /**
     * 校验手机号是否合法，返回布尔值
     */
    public static boolean isValid(String mobile) {
        if (mobile == null) return false;
        return MOBILE_PATTERN.matcher(mobile.trim()).matches();
    }

    /**
     * 校验手机号是否合法，不合法抛出 IllegalArgumentException
     */
    public static void validateOrThrow(String mobile) {
        if (!isValid(mobile)) {
            throw new IllegalArgumentException("手机号不合法: " + mobile);
        }
    }
}