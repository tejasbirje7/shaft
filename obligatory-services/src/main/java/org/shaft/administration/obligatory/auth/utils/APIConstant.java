package org.shaft.administration.obligatory.auth.utils;

public class APIConstant {
    public static String secretKey = "l18CLKCN7OnCat6MNsO1iQmrV/FjFuCmQYAx+5bx4cM=";

    public static String salt = "yd89w7e=2390u4hbjw;jfhasjdf";

    public static String encryptMode = "Encrypt";

    public static String decryptMode = "Decrypt";

    public static String flowfileKey = "HashedKey";
    public static String decryptedKey = "decryptedKey";
    public static String processStarted = "Process for hashing started";
    public static String processFinsished = "Process for hashing finished";
    public static String detectedMode = "Detected Mode : ";

    public static String error = "Error in hashing key :";
    public static String attribute = "Flowfile Attributes Fetched";

    public static String attributeMissing = "Attributes Missing";

    public static byte[] iv = new byte[16];

    public static String algorithm = "PBKDF2WithHmacSHA256";
}
