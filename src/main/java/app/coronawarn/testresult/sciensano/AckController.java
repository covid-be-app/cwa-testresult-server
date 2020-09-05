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
import app.coronawarn.testresult.model.MobileTestResultRequest;
import io.swagger.v3.oas.annotations.Operation;
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
public class AckController {

  private final TestResultRepository testResultRepository;

  /**
   * Provides a way for the mobile app to acknowledge the reception of a result.
   * only when properly ack-ed by the mobile app will the dateTestCommunicated date be set
   *
   * @param request the MobileTestResultRequest containing themobileTestId and datePatientInfectious.
   *
   * @return the test result response.
   */
  @Operation(description = "Get test result response from request.")
  @PostMapping(value = "/v1/app/testresult/ack", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<?> mobileTestResult(@RequestBody @Valid MobileTestResultRequest request) {
    Optional<TestResultEntity> testResultEntity = testResultRepository.findByMobileTestIdAndDatePatientInfectious(
      request.getMobileTestId(), request.getDatePatientInfectious());

    testResultEntity.ifPresent(tr -> {
      testResultRepository.delete(tr);
    });

    //TODO: here we will need to trigger the AC calculation (CBA-92)
    return ResponseEntity.noContent().build();
  }

}
