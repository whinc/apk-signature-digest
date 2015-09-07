package com.whinc.apksignaturedigest;

/**
 * Created by wuhui on 9/4/15.
 */

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * 常用系统服务工具类
 */
public class SystemServiceUtils {

    /**
     * copy text to system clipboard
     * @param context {@link Context}
     * @param text text will be copied.
     */
    public static void copyToClipboard(Context context, CharSequence text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData.Item item = new ClipData.Item(text);
        ClipData clipData = new ClipData("", new String[]{"text/plain"}, item);
        clipboardManager.setPrimaryClip(clipData);
    }
}
