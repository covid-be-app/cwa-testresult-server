package app.coronawarn.testresult.sciensano;

import app.coronawarn.testresult.config.TestResultConfig;
import java.time.LocalDate;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TestResultCleanup {

  private final TestResultConfig testResultConfig;
  private final TestResultRepository testResultRepository;

  /**
   * All test results that are older than configured days should get deleted from the database.
   */
  @Scheduled(
    fixedDelayString = "${testresult.cleanup.delete.rate}"
  )
  @Transactional
  public void delete() {
    Integer deleted = testResultRepository.deleteByResultDateBefore(
      LocalDate.now().minusDays(testResultConfig.getCleanup().getDelete().getDays()));
    log.info("Cleanup deleted {} test results.", deleted);
  }
}
