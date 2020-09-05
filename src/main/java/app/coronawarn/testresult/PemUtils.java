package app.coronawarn.testresult;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.PrivateKey;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class PemUtils {

  /**
   * Retries the private key from a string (used when SecureString resources are fetched via SSM).
   *
   * @param pemAsString The content of the pem as a string
   * @return a proviate key
   * @throws IOException in case of an exception
   */
  public static PrivateKey getPrivateKeyFromString(String pemAsString) throws IOException {
    ByteArrayInputStream pemInputStream = new ByteArrayInputStream(pemAsString.getBytes());
    Reader reader = new BufferedReader(new InputStreamReader(pemInputStream));

    Object parsed = new PEMParser(reader).readObject();
    return new JcaPEMKeyConverter().getPrivateKey((PrivateKeyInfo) parsed);
  }

}
