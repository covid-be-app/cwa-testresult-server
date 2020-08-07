package app.coronawarn.testresult.authorizationcode;

import static app.coronawarn.testresult.HexUtils.toHexString;
import java.io.PrintWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import javax.xml.bind.DatatypeConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.stereotype.Component;


@Component
public class SignatureGenerator {

  // TODO: temporary implementation using keys inside the classpath.
  // TODO: replace with keys coming from SSM.
  private static final String PRIVATE_KEY_RESOURCE = "private.pem";
  private static final String PUBLIC_KEY_RESOURCE = "public.pem";

  private final KeyFactory kf = KeyFactory.getInstance("EC");

  public SignatureGenerator() throws NoSuchAlgorithmException {
  }



  //  public static void main(String[] args) throws Exception {
  //    new SignatureGenerator().test();
  //    //new SignatureGenerator().generateAndPrintKeyPair();
  //  }
  //
  //  public void test() throws Exception {
  //
  //    PrivateKey privateKey = PemUtils.readPrivateKeyFromFile(PRIVATE_KEY_RESOURCE, "RSA");
  //    PublicKey publicKey = PemUtils.readPublicKeyFromFile(PUBLIC_KEY_RESOURCE, "RSA");
  //
  //    String data = "some data";
  //    String signature = sign(privateKey, data);
  //    System.out.println("signature = " + signature);
  //    System.out.println("signature verification = " + verify(publicKey, data, signature));
  //
  //  }

    public void generateAndPrintKeyPair() throws Exception {
      KeyPair keyPair = generateKeyPair();
      PrivateKey priv = keyPair.getPrivate();
      PublicKey pub = keyPair.getPublic();

      PemWriter pemWriter = new PemWriter(new PrintWriter(System.out));
      try {
        pemWriter.writeObject(new PemObject("privKey", priv.getEncoded()));
        pemWriter.writeObject(new PemObject("pubKey", pub.getEncoded()));
      } finally {
        pemWriter.close();
      }

    }

    public KeyPair generateKeyPair() throws Exception {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
      return keyGen.generateKeyPair();
    }

  /**
   * Verifies the signature using the given public key, the data and the signature.
   *
   * @param publicKey the public key used to verify the signature
   * @param data the data where the signature was applied on
   * @param signatureAsHex the signature in hex format
   * @return boolena indicating if signature was valid or not.
   * @throws Exception in case something goes wrong.
   */
  public boolean verify(PublicKey publicKey, String data, String signatureAsHex) throws Exception {
    byte[] signatureBytes = DatatypeConverter.parseHexBinary(signatureAsHex);
    Signature signature = Signature.getInstance("SHA1withRSA");
    signature.initVerify(publicKey);
    signature.update(data.getBytes());
    return signature.verify(signatureBytes);
  }

  /**
   * Sign the data using the provided private key.
   *
   * @param privateKey the public key used to sign the data
   * @param data the data to be signed
   * @return the signature in hex string format.
   * @throws Exception in case something goes wrong.
   */
  public String sign(PrivateKey privateKey, String data) throws Exception {
    Signature dsa = Signature.getInstance("SHA1withRSA");
    dsa.initSign(privateKey);

    byte[] strByte = data.getBytes("UTF-8");
    dsa.update(strByte);
    byte[] realSig = dsa.sign();
    return toHexString(realSig);
  }


}
