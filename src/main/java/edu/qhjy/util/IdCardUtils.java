package edu.qhjy.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IdCardUtils {

    // 加权因子
    private static final int[] FACTORS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3,
            7, 9, 10, 5, 8, 4, 2};
    // 校验码
    private static final char[] CHECK_CODES = {'1', '0', 'X', '9', '8',
            '7', '6', '5', '4', '3', '2'};

    private static final DateTimeFormatter BIRTHDAY_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 仅校验身份证号是否合法（返回 true/false）
     */
    public static boolean isValid(String id) {
        if (id == null || id.length() != 18) {
            return false;
        }

        // 前17位必须是数字
        String first17 = id.substring(0, 17);
        if (!first17.matches("\\d{17}")) {
            return false;
        }

        // 计算校验位
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            int digit = Character.getNumericValue(first17.charAt(i));
            sum += digit * FACTORS[i];
        }
        char expectedCheckCode = CHECK_CODES[sum % 11];

        // 实际校验位
        char actualCheckCode = Character.toUpperCase(id.charAt(17));
        return expectedCheckCode == actualCheckCode;
    }

    /**
     * 验证身份证号是否合法，不合法时抛异常
     */
    public static void validate(String id) {
        if (!isValid(id)) {
            throw new IllegalArgumentException("身份证号不合法: " + id);
        }
    }

    /**
     * 解析出生日期
     */
    public static LocalDate getBirthday(String id) {
        validate(id); // 先校验
        String birthStr = id.substring(6, 14); // yyyyMMdd
        return LocalDate.parse(birthStr, BIRTHDAY_FORMATTER);
    }

    /**
     * 获取性别：男/女
     */
    public static String getGender(String id) {
        validate(id); // 先校验
        char genderCode = id.charAt(16);
        int genderNum = genderCode - '0';
        return (genderNum % 2 == 0) ? "女" : "男";
    }
}