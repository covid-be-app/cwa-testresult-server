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
import static app.coronawarn.testresult.entity.TestResultEntity.pendingResult;
import app.coronawarn.testresult.model.MobileTestResultList;
import app.coronawarn.testresult.model.MobileTestResultRequest;
import app.coronawarn.testresult.model.MobileTestResultUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
@RestController
public class TestResultController {

  private final TestResultRepository testResultRepository;

  /**
   * Get the test result response from a request containing the id.
   *
   * @param request the test result request with id
   * @return the test result response
   */
  @Operation(description = "Get test result response from request.")
  @PostMapping(value = "/v1/app/mobiletestresult", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<TestResultEntity> mobileTestResult(@RequestBody @Valid MobileTestResultRequest request) {
    log.info("Received test result request from app.");
    Optional<TestResultEntity> testResultEntity = testResultRepository.findByMobileTestIdAndDatePatientInfectious(
      request.getMobileTestId(), request.getDatePatientInfectious());

    testResultEntity.ifPresent(tr -> {
      tr.setDateTestCommunicated(LocalDate.now());
      testResultRepository.saveAndFlush(tr);
    });


    return testResultEntity
      .map(
        tr -> {
          tr.setDateTestCommunicated(LocalDate.now());
          testResultRepository.saveAndFlush(tr);
          return ResponseEntity.ok(tr);
        }
      )
      //TODO: verify if it is ok if we return a non-persisted pending result to the app in case it is not found.
      .orElse(ResponseEntity.ok(dummyPendingResult(request.getMobileTestId())));
  }

  /**
   * Insert or update the test results.
   *
   * @param list the test result list request
   * @return the response
   */
  @Operation(description = "Create test results from collection.")
  @PostMapping(value = "/v1/lab/results", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<?> results(@RequestBody @NotNull @Valid MobileTestResultList list) {
    // TODO: find out how this worked in DE (how this was secured)
    // https://github.com/corona-warn-app/cwa-testresult-server/issues/65
    // Managed on firewall level
    log.info("Received {} test results to insert or update from lab.", list.getMobileTestResultUpdateRequest().size());
    list.getMobileTestResultUpdateRequest().forEach(this::createOrUpdate);
    return ResponseEntity.noContent().build();
  }

  private void createOrUpdate(@Valid MobileTestResultUpdateRequest mobileTestResultUpdateRequest) {
    TestResultEntity testResultEntity = testResultRepository.findByMobileTestIdAndDatePatientInfectious(
      mobileTestResultUpdateRequest.getMobileTestId(),
      mobileTestResultUpdateRequest.getDatePatientInfectious()
    ).orElse(pendingResult(
      mobileTestResultUpdateRequest.getMobileTestId(),
      mobileTestResultUpdateRequest.getDatePatientInfectious())
    );

    testResultEntity.setDateTestPerformed(mobileTestResultUpdateRequest.getDateTestPerformed());
    testResultEntity.setDateSampleCollected(mobileTestResultUpdateRequest.getDateSampleCollected());
    testResultEntity.setResultChannel(mobileTestResultUpdateRequest.getResultChannel());
    testResultEntity.setResult(mobileTestResultUpdateRequest.getResult());
    testResultRepository.saveAndFlush(testResultEntity);

  }

}
