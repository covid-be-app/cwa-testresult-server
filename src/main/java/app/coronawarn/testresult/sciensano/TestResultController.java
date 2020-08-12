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

import app.coronawarn.testresult.authorizationcode.AuthorizationCodeService;
import app.coronawarn.testresult.entity.TestResultEntity;
import static app.coronawarn.testresult.entity.TestResultEntity.dummyPendingResult;
import app.coronawarn.testresult.model.MobileTestResultRequest;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.Optional;
import static java.util.function.Predicate.not;
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

  private final AuthorizationCodeService authorizationCodeService;

  /**
   * <p>
   * Get the test result response for a given mobileTestId and datePatientInfectious.
   * When fetching for a test result there can be multiple outcomes ....
   * </p>
   * a) a valid test result has been found and is returned. At that point we set the communication date and calculate
   *    the AC
   * b) a valid test result has been found but was already communicated. In that case we simply return the result
   *    but do nothing
   * c) no test result is found for the given token, In that case we return a dummy request.
   *
   * @param request the MobileTestResultRequest containing themobileTestId and datePatientInfectious.
   *
   * @return the test result response.
   */
  @Operation(description = "Get test result response from request.")
  @PostMapping(value = "/v1/app/testresult/poll", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<TestResultEntity> mobileTestResult(@RequestBody @Valid MobileTestResultRequest request) {
    Optional<TestResultEntity> testResultEntity = testResultRepository.findByMobileTestIdAndDatePatientInfectious(
      request.getMobileTestId(), request.getDatePatientInfectious());

    testResultEntity.filter(
      not(TestResultEntity::hasTestBeenCommunicated)
    ).ifPresent(tr -> {
      tr.setDateTestCommunicated(LocalDate.now());
      if (tr.isPositive()) {
        authorizationCodeService.generateAndSaveAuthorizationCode(tr);
      }
    });

    return testResultEntity
      .map(ResponseEntity::ok)
      .orElse(ResponseEntity.ok(dummyPendingResult(request.getMobileTestId())));
  }

}
