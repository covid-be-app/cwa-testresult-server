package app.coronawarn.testresult;

public class HexUtils {

  private static final char[] hex = "0123456789abcdef".toCharArray();

  /**
   * Generate a hex string from the given set of bytes.
   *
   * @param bytes The bytes we want to transfor into a hex string
   * @return the hex string
   */
  public static String toHexString(byte[] bytes) {
    if (null == bytes) {
      return null;
    }

    StringBuilder sb = new StringBuilder(bytes.length << 1);

    for (byte b : bytes) {
      sb.append(hex[(b & 0xf0) >> 4])
        .append(hex[(b & 0x0f)])
      ;
    }

    return sb.toString();
  }
}
