package com.dtflys.forest.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-10 22:48
 */
public class NameUtils {

    private final static String GETTER_PREFIX = "get";

    private final static String SETTER_PREFIX = "set";

    /**
     * 是否为 Getter
     *
     * @param name 名称
     * @return {@code true}: 是 Getter, {@code false}: 不是 Getter
     */
    public static boolean isGetter(final String name) {
        return name != null && name.length() > GETTER_PREFIX.length() && name.startsWith(GETTER_PREFIX);
    }

    /**
     * 是否为 Setter
     *
     * @param name 名称
     * @return {@code true}: 是 Setter, {@code false}: 不是 Setter
     */
    public static boolean isSetter(final String name) {
        return name != null && name.length() > SETTER_PREFIX.length() && name.startsWith(SETTER_PREFIX);
    }

    /**
     * 将首字母小写
     *
     * @param name 名称
     * @return 首字母小写化的名称
     */
    public static String lowerFirst(final String name) {
        if (StringUtils.isEmpty(name)) {
            return name;
        }
        final char first = name.charAt(0);
        if (Character.isUpperCase(first)) {
            return Character.toLowerCase(first) + name.substring(1);
        }
        return name;
    }

    /**
     * 按驼峰命名法的规则将字符串分割
     *
     * @param name 源字符串
     * @return 分割后的字符串数组
     */
    public static String[] splitCamelName(final String name) {
        final int len = name.length();
        final List<String> names = new LinkedList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            final char ch = name.charAt(i);
            if (Character.isUpperCase(ch)) {
                String item = builder.toString();
                if (StringUtils.isNotBlank(item)) {
                    names.add(item);
                }
                builder = new StringBuilder();
                builder.append(Character.toLowerCase(ch));
            } else {
                builder.append(ch);
            }
        }
        final String last = builder.toString();
        if (StringUtils.isNotBlank(last)) {
            names.add(last);
        }
        final String[] result = new String[names.size()];
        return names.toArray(result);
    }

    public static String propNameFromGetter(final String name) {
        return propNameFrom(name, GETTER_PREFIX);
    }

    public static String propNameFromSetter(final String name) {
        return propNameFrom(name, SETTER_PREFIX);
    }

    private static String propNameFrom(final String name, final String prefix) {
        final String[] strs = splitCamelName(name);
        final String namePrefix = strs[0];
        if (!prefix.equals(namePrefix) || strs.length < 2) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < strs.length; i++) {
            String str = strs[i];
            if (i > 1) {
                final char[] chars = str.toCharArray();
                builder.append(String.valueOf(chars[0]).toUpperCase());
                if (chars.length > 1) {
                    for (int j = 1; j < chars.length; j++) {
                        builder.append(chars[j]);
                    }
                }
            } else {
                builder.append(str);
            }
        }
        return builder.toString();
    }

    public static String setterName(String name) {
        String[] strs = splitCamelName(name);
        String prefix = strs[0];
        if (SETTER_PREFIX.equals(prefix)) {
            return name;
        } else {
            StringBuilder builder = new StringBuilder(SETTER_PREFIX);
            for (int i = 0; i < strs.length; i++) {
                String str = strs[i];
                builder.append(Character.toUpperCase(str.charAt(0)))
                        .append(str.substring(1));
            }
            return builder.toString();
        }
    }

}
