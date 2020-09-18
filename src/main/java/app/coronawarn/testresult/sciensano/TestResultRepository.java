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

import app.coronawarn.testresult.entity.TestResultEntity;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TestResultRepository extends JpaRepository<TestResultEntity, Long> {

  Optional<TestResultEntity> findByMobileTestIdAndDatePatientInfectious(
    String mobileTestId, LocalDate datePatientInfectious);

  /**
   * Ensure that test results beyond the patient infectious date + 10 can get deleted.
   *
   * @param before all test results older than the date provided that are to be deleted.
   * @return
   */
  @Modifying
  @Query("delete from TestResultEntity t where t.datePatientInfectious <= ?1")
  Integer deleteObsoleteTestResult(LocalDate before);
}
