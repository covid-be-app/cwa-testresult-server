package app.coronawarn.testresult;

import app.coronawarn.testresult.authorizationcode.AuthorizationCodeCopier;
import app.coronawarn.testresult.authorizationcode.AuthorizationCodeRepository;
import app.coronawarn.testresult.authorizationcode.AuthorizationCodeRequest;
import app.coronawarn.testresult.authorizationcode.SubmissionServerGateway;
import app.coronawarn.testresult.entity.TestResultEntity;
import app.coronawarn.testresult.entity.TestResultEntity.Result;
import static app.coronawarn.testresult.entity.TestResultEntity.Result.NEGATIVE;
import static app.coronawarn.testresult.entity.TestResultEntity.Result.PENDING;
import static app.coronawarn.testresult.entity.TestResultEntity.Result.POSITIVE;
import app.coronawarn.testresult.entity.TestResultEntity.ResultChannel;
import static app.coronawarn.testresult.entity.TestResultEntity.ResultChannel.LAB;
import app.coronawarn.testresult.model.MobileTestResultRequest;
import app.coronawarn.testresult.sciensano.TestResultController;
import app.coronawarn.testresult.sciensano.TestResultRepository;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import rx.Single;

@RunWith(SpringRunner.class)
@SpringBootTest(
  properties = {
    "testresult.cleanup.delete.days=10",
    "testresult.cleanup.delete.rate=1000",
    "testresult.authorizationcode.tranfer.rate=1000"
  }
)
@ContextConfiguration(classes = TestResultApplication.class)
public class AuthorizationCodeTransferTest {

  @Autowired
  private TestResultRepository testResultRepository;

  @Autowired
  private TestResultController testResultController;

  @Autowired
  private AuthorizationCodeRepository authorizationCodeRepository;

  @MockBean
  private SubmissionServerGateway submissionServerGateway;

  @Autowired
  private AuthorizationCodeCopier authorizationCodeCopier;

  @Before
  public void before() {
    testResultRepository.deleteAll();
    authorizationCodeRepository.deleteAll();
    when(submissionServerGateway.processAuthorizationCodes(any(AuthorizationCodeRequest.class))).thenReturn(ResponseEntity.ok().build());
  }


  @Test
  public void createSingleAcForPositiveTestAndDeleteAfterwards() {

    Result result = PENDING;
    ResultChannel channel = LAB;
    String mobileTestId = "123456789012345";
    LocalDate datePatientInfectious = LocalDate.now().minusDays(10);

    // create 3 testresults, 2 pos , 1 neg.
    createTestResult(POSITIVE,"000000000000001",LocalDate.now());
    createTestResult(NEGATIVE,"000000000000002",LocalDate.now());
    createTestResult(POSITIVE,"000000000000003",LocalDate.now());

    // poll one single testresult
    testResultController.pollMobileTestResult(new MobileTestResultRequest("000000000000003",LocalDate.now()));

    // expect to have a AC in the system
    Assert.assertEquals(1,authorizationCodeRepository.findAll().size());

    // wait for the AC copying
    Single.fromCallable(() -> true).delay(2, TimeUnit.SECONDS).toBlocking().value();

    // verify that all ACs are deleted
    Assert.assertEquals(0,authorizationCodeRepository.findAll().size());
  }

  private TestResultEntity createTestResult(Result result,String mobileTestId, LocalDate datePatientInfectious) {

    return testResultRepository.save(new TestResultEntity()
      .setResult(result)
      .setResultChannel(LAB)
      .setDatePatientInfectious(datePatientInfectious)
      .setDateTestPerformed(datePatientInfectious)
      .setDateSampleCollected(datePatientInfectious.plusDays(2))
      .setMobileTestId(mobileTestId)
    );
  }

//  public static class TestResultServerClientMock implements SubmissionServerClient {
//
//    @Override
//    public ResponseEntity<Void> processAuthorizationCodes(AuthorizationCodeRequest authorizationCodeRequest) {
//      return ResponseEntity.noContent().build();
//    }
//  }
}
