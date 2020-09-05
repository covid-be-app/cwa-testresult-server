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

package app.coronawarn.testresult.model;

import app.coronawarn.testresult.entity.TestResultEntity.Result;
import app.coronawarn.testresult.entity.TestResultEntity.ResultChannel;
import static app.coronawarn.testresult.entity.TestResultEntity.ResultChannel.UNKNOWN;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Request model of the test result update request.
 * This model can be used to insert test results (only used for non-prod and testing purposes).
 */
@Schema(
  description = "The test result request model."
)
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class MobileTestResultUpdateRequest {

  @NotNull
  @Pattern(regexp = "^[0-9]{15}")
  private String mobileTestId;

  @NotNull
  private LocalDate datePatientInfectious;

  private LocalDate dateSampleCollected;

  private LocalDate dateTestPerformed;

  private LocalDate dateTestCommunicated;

  @NotNull
  private Result result;

  @NotNull
  private ResultChannel resultChannel = UNKNOWN;
}
