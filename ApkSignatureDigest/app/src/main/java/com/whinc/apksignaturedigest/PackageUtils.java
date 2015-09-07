package com.whinc.apksignaturedigest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wuhui on 8/31/15.
 */
public class PackageUtils {
    private final Context mContext;
    private PackageInfo mPkgInfo;
    private static final char[] HEX_CHAR = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /** 内部调用 {@code PackageUtils.newInstance(context, context.getPackageName())} */
    public static PackageUtils newInstance(Context context) {
        return newInstance(context, context.getPackageName());
    }

    /** 通过指定包名创建{@link PackageUtils}实例,之后的方法调用获取的都是该包的相关信息 */
    public static PackageUtils newInstance(Context context, String packageName) {
        try {
            return new PackageUtils(context, packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private PackageUtils(Context context, String packageName) throws PackageManager.NameNotFoundException {
        mContext = context;
        mPkgInfo = mContext.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
    }

    public String versionName() {
        return mPkgInfo.versionName;
    }

    public int versionCode() {
        return mPkgInfo.versionCode;
    }

    /** 获取签名的MD5摘要 */
    public String[] signatureDigest() {
        int length = mPkgInfo.signatures.length;
        String[] digests = new String[length];

        for (int i = 0; i < length; ++i) {
            Signature sign = mPkgInfo.signatures[i];
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                byte[] digest = md5.digest(sign.toByteArray()); // get digest with md5 algorithm
                digests[i] = toHexString(digest);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                digests[i] = null;
            }
        }
        return digests;
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

    public List<String> installedPkgNames() {
        List<PackageInfo> pkgs = mContext.getPackageManager().getInstalledPackages(PackageManager.GET_SIGNATURES);
        List<String> pkgNames = new LinkedList<>();
        for (PackageInfo v : pkgs) {
            pkgNames.add(v.packageName);
        }
        return pkgNames;
    }
}
