package com.example.library.utils;

public class SettingUtils {
    private static volatile SettingUtils instance;
    private boolean highlightLate = false;  // Giá trị mặc định
    private boolean highlightReturn = false;

    private SettingUtils() {
        // private constructor để ngăn chặn tạo instance từ bên ngoài
    }

    public static SettingUtils getInstance() {
        if (instance == null) {
            synchronized (SettingUtils.class) {
                if (instance == null) {
                    instance = new SettingUtils();
                }
            }
        }
        return instance;
    }

    public boolean isHighlightLate() {
        return highlightLate;
    }

    public void setHighlightLate(boolean highlightLate) {
        this.highlightLate = highlightLate;
    }

    public boolean isHighlightReturn() {
        return highlightReturn;
    }

    public void setHighlightReturn(boolean highlightReturn) {
        this.highlightReturn = highlightReturn;
    }


}
