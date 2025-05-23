package com.dtflys.forest.logging;

import cn.hutool.core.text.StrFormatter;
import com.dtflys.forest.exceptions.ForestRuntimeException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ForestAndroidLogger implements ForestLogger {

    private final Class<?> clazz;

    private final Class<?> androidLogClass;

    private final Method infoMethod;

    private final Method errorMethod;

    public ForestAndroidLogger(Class<?> clazz) {
        this.clazz = clazz;

        try {
            androidLogClass = Class.forName("android.util.Log");
            infoMethod = androidLogClass.getMethod("i", String.class, String.class);
            errorMethod = androidLogClass.getMethod("e", String.class, String.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public void info(String content, Object... args) {
        try {
            final String str = StrFormatter.format(content, args);
            infoMethod.invoke(androidLogClass, clazz.getSimpleName(), str);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ForestRuntimeException(e);
        }
    }

    @Override
    public void error(String content, Object... args) {
        try {
            final String str = StrFormatter.format(content, args);
            errorMethod.invoke(androidLogClass, clazz.getSimpleName(), str);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ForestRuntimeException(e);
        }
    }
}
