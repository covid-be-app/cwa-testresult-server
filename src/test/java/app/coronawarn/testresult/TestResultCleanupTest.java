package app.coronawarn.testresult;

import app.coronawarn.testresult.entity.TestResultEntity;
import app.coronawarn.testresult.entity.TestResultEntity.Result;
import static app.coronawarn.testresult.entity.TestResultEntity.Result.PENDING;
import app.coronawarn.testresult.entity.TestResultEntity.ResultChannel;
import static app.coronawarn.testresult.entity.TestResultEntity.ResultChannel.LAB;
import app.coronawarn.testresult.sciensano.TestResultRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import rx.Single;

@RunWith(SpringRunner.class)
@SpringBootTest(
  properties = {
    "testresult.cleanup.delete.days=10",
    "testresult.cleanup.delete.rate=1000"
  }
)
@ContextConfiguration(classes = TestResultApplication.class)
public class TestResultCleanupTest {

  @Autowired
  private TestResultRepository testResultRepository;

  @Before
  public void before() {
    testResultRepository.deleteAll();
  }

  @Test
  public void shouldCleanupDelete() {
    // prepare
    testResultRepository.deleteAll();

    Result result = PENDING;
    ResultChannel channel = LAB;
    String mobileTestId = "123456789012345";
    LocalDate datePatientInfectious = LocalDate.now().minusDays(10);
    // create
    TestResultEntity create = testResultRepository.save(new TestResultEntity()
      .setResult(result)
      .setResultChannel(channel)
      .setDatePatientInfectious(datePatientInfectious)
      .setDateTestPerformed(datePatientInfectious)
      .setDateSampleCollected(datePatientInfectious.plusDays(2))
      .setMobileTestId(mobileTestId)
    );

    Assert.assertNotNull(create);
    Assert.assertEquals(mobileTestId, create.getMobileTestId());
    // find
    Optional<TestResultEntity> find = testResultRepository.findByMobileTestIdAndDatePatientInfectious(
      mobileTestId,datePatientInfectious);
    Assert.assertTrue(find.isPresent());
    Assert.assertEquals(mobileTestId, find.get().getMobileTestId());
    Assert.assertEquals(datePatientInfectious, find.get().getDatePatientInfectious());
    // wait
    Single.fromCallable(() -> true).delay(2, TimeUnit.SECONDS).toBlocking().value();
    // find
    find =testResultRepository.findByMobileTestIdAndDatePatientInfectious(
      mobileTestId,datePatientInfectious);
    Assert.assertFalse(find.isPresent());
  }
}
