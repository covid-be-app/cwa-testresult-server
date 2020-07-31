package app.coronawarn.testresult.sciensano;

import app.coronawarn.testresult.config.TestResultConfig;
import java.time.LocalDate;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * A cleanup job will delete all test results where the datePatientInfectious has exceed x number of days.
 * This is to ensure that test results that haven't been downloaded by the app, or haven't been acknowledged
 * for download by the app will get deleted from the database.
 * This is an additional measure to get sure we don't keep test results in the database for too long.
 * (Keep in mind that results are deleted after having been acknowledged for download by the application.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TestResultCleanup {

  private final TestResultConfig testResultConfig;
  private final TestResultRepository testResultRepository;

  /**
   * All test results that are older than configured days should get deleted from the database.
   */
  @Scheduled(fixedDelayString = "${testresult.cleanup.delete.rate}")
  @Transactional
  public void delete() {
    Integer deleted = testResultRepository.deleteObsoleteTestResult(
      LocalDate.now().minusDays(testResultConfig.getCleanup().getDelete().getDays()));
    log.info("Cleanup deleted {} test results.", deleted);
  }
}
