/*
 * Corona-Warn-App / cwa-testresult-server
 *
 * (C) 2020, T-Systems International GmbH
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
import static app.coronawarn.testresult.entity.TestResultEntity.dummyPendingResult;
import app.coronawarn.testresult.model.MobileTestResultRequest;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.Optional;
import javax.transaction.Transactional;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@Validated
@Transactional
@RestController
public class TestResultController {

  private final TestResultRepository testResultRepository;

  /**
   * Get the test result response for a given mobileTestId and datePatientInfectious.
   *
   * @param request the MobileTestResultRequest containing themobileTestId and datePatientInfectious.
   *
   * @return the test result response.
   */
  @Operation(description = "Get test result response from request.")
  @PostMapping(value = "/v1/app/mobiletestresult", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<TestResultEntity> mobileTestResult(@RequestBody @Valid MobileTestResultRequest request) {
    Optional<TestResultEntity> testResultEntity = testResultRepository.findByMobileTestIdAndDatePatientInfectious(
      request.getMobileTestId(), request.getDatePatientInfectious());

    // TODO: we will make this more robust by introducing an ack endpoint. (so app can notify it got the result)
    // Backend will only set the dateTestCommunicated when the ack is received from app
    testResultEntity.ifPresent(tr -> {
      tr.setDateTestCommunicated(LocalDate.now());
    });

    return testResultEntity
      .map(ResponseEntity::ok)
      // If we don't find a test result (or we did a "dummy" poll)
      // we simply return a dummy test result (plausible deniability)
      .orElse(ResponseEntity.ok(dummyPendingResult(request.getMobileTestId())));
  }

}
