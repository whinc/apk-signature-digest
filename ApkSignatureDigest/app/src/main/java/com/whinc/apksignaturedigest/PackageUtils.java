package com.whinc.apksignaturedigest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by wuhui on 8/31/15.
 */
public class PackageUtils {
    private static final PackageUtils sSingleton = new PackageUtils();
    private static final char[] HEX_CHAR = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private PackageUtils() {}

    public static PackageUtils getInstance() {
        return sSingleton;
    }

    /**
     * <p>Get the PackageInfo of specified package.</p>
     * @param context
     * @param pkgName the package name
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public PackageInfo getPackageInfo(@NonNull Context context, @NonNull String pkgName) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getPackageInfo(pkgName, 0);
    }

    /** 获取签名的MD5摘要 */
    public String getSignatureDigest(@NonNull PackageInfo pkgInfo) {
        int length = pkgInfo.signatures.length;
        if (length <= 0) {
            return "";
        }

        Signature signature = pkgInfo.signatures[0];
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // Should not occur
        }
        byte[] digest = md5.digest(signature.toByteArray()); // get digest with md5 algorithm
        return toHexString(digest);
    }

    /** 将字节数组转化为对应的十六进制字符串 */
    private String toHexString(byte[] rawByteArray) {
        char[] chars = new char[rawByteArray.length * 2];
        for (int i = 0; i < rawByteArray.length; ++i) {
            byte b = rawByteArray[i];
            chars[i*2] = HEX_CHAR[(b >>> 4 & 0x0F)];
            chars[i*2+1] = HEX_CHAR[(b & 0x0F)];
        }
        return new String(chars);
    }

    public List<PackageInfo> getInstalledPackages(@NonNull Context context) {
        return context.getPackageManager().getInstalledPackages(PackageManager.GET_SIGNATURES);
    }
}
