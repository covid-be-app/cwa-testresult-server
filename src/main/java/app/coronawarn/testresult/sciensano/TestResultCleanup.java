/*
 * Corona-Warn-App / cwa-testresult-server
 *
 * (C) 2020, T-Systems International GmbH
 * All modifications are copyright (c) 2020 Devside SRL.
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
