package com.smoothcsv.framework.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

/**
 * @author kohii
 */
public class DigestUtils {

  public static String md5Hex(String input) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] result = md.digest(input.getBytes());
    return DatatypeConverter.printHexBinary(result);
  }
}
