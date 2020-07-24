package com.haitai.template.shiro.util;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * @author bin.wang
 * @version 1.0 2020/7/22
 */
public class PasswordHelper {
    private static String algorithmName = "md5";
    private static int hashIterations = 1024;

    public static String encryptPassword(String userName, String password) {
        String newPassword = new SimpleHash(algorithmName, password, ByteSource.Util.bytes(userName + password), hashIterations).toHex();
        return newPassword;
    }
}
