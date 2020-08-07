package app.coronawarn.testresult.authorizationcode;

import app.coronawarn.testresult.PemUtils;
import app.coronawarn.testresult.entity.AuthorizationCodeEntity;
import app.coronawarn.testresult.entity.TestResultEntity;
import java.security.PrivateKey;
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

  private static final String PRIVATE_KEY_RESOURCE = "private.pem";

  private final SignatureGenerator signatureGenerator;
  private final AuthorizationCodeRepository authorizationCodeRepository;

  /**
   * Generates the authorization code for the test result.
   * @param testResultEntity the test result
   * @throws IllegalArgumentException in case something goes wrong
   */
  public void generateAndSaveAuthorizationCode(TestResultEntity testResultEntity) throws IllegalArgumentException {

    try {

      AuthorizationCodeEntity authorizationCodeEntity = new AuthorizationCodeEntity();

      PrivateKey privateKey = PemUtils.readPrivateKeyFromFile(PRIVATE_KEY_RESOURCE, "RSA");
      String signature = signatureGenerator.sign(privateKey, testResultEntity.getSignatureData());

      authorizationCodeEntity.setMobileTestId(testResultEntity.getMobileTestId());
      authorizationCodeEntity.setDatePatientInfectious(testResultEntity.getDatePatientInfectious());
      authorizationCodeEntity.setDateTestCommunicated(testResultEntity.getDateTestCommunicated());
      authorizationCodeEntity.setSignature(signature);

      authorizationCodeRepository.save(authorizationCodeEntity);

    } catch (Exception ex) {
      throw new IllegalArgumentException("Unable to generate and save authorization code",ex);
    }

  }

}
