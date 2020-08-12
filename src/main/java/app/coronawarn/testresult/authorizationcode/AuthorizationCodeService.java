package app.coronawarn.testresult.authorizationcode;

import static app.coronawarn.testresult.HexUtils.toHexString;
import app.coronawarn.testresult.PemUtils;
import app.coronawarn.testresult.config.TestResultConfig;
import app.coronawarn.testresult.entity.AuthorizationCodeEntity;
import app.coronawarn.testresult.entity.TestResultEntity;
import java.security.PrivateKey;
import java.security.Signature;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Capable of generating and saving an authorization code (AC) for a test result.
 * As soon as a test result has been downloaded and given a dateTestCommunicated (t3), an AC is generated
 * and stored in the database.
 * </p>
 * <p>
 * A batch job will periodically transfer all generated ACs on the polling server to the submission service.
 * </p>
 * <p>
 * It is important to only generate 1 such signature for a test result to avoid having duplicates in the database.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class AuthorizationCodeService {

  private final AuthorizationCodeRepository authorizationCodeRepository;

  private final TestResultConfig testResultConfig;

  /**
   * Key algortihm used during key generation / key reads.
   * https://docs.oracle.com/javase/10/docs/specs/security/standard-names.html#keypairgenerator-algorithms
   */
  private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";     // WORKS WITH Key Algorithm EC


  /**
   * Generates the authorization code for the test result.
   * @param testResultEntity the test result
   * @throws IllegalArgumentException in case something goes wrong
   */
  public void generateAndSaveAuthorizationCode(TestResultEntity testResultEntity) throws IllegalArgumentException {

    try {

      AuthorizationCodeEntity authorizationCodeEntity = new AuthorizationCodeEntity();

      PrivateKey privateKey = PemUtils.getPrivateKeyFromString(testResultConfig.getSignature().getPrivateKeyContent());
      String signature = sign(privateKey, testResultEntity.getSignatureData());

      authorizationCodeEntity.setMobileTestId(testResultEntity.getMobileTestId());
      authorizationCodeEntity.setDatePatientInfectious(testResultEntity.getDatePatientInfectious());
      authorizationCodeEntity.setDateTestCommunicated(testResultEntity.getDateTestCommunicated());
      authorizationCodeEntity.setSignature(signature);

      authorizationCodeRepository.save(authorizationCodeEntity);

    } catch (Exception ex) {
      throw new IllegalArgumentException("Unable to generate and save authorization code",ex);
    }

  }

  /**
   * Sign the data using the provided private key.
   *
   * @param privateKey the public key used to sign the data
   * @param data       the data to be signed
   * @return the signature in hex string format.
   * @throws Exception in case something goes wrong.
   */
  private String sign(PrivateKey privateKey, String data) throws Exception {
    Signature dsa = Signature.getInstance(SIGNATURE_ALGORITHM);
    dsa.initSign(privateKey);

    byte[] strByte = data.getBytes("UTF-8");
    dsa.update(strByte);
    byte[] realSig = dsa.sign();
    return toHexString(realSig);
  }



}
