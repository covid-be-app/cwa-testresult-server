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

package app.coronawarn.testresult.entity;

import static app.coronawarn.testresult.entity.TestResultEntity.Result.PENDING;
import static app.coronawarn.testresult.entity.TestResultEntity.Result.POSITIVE;
import static app.coronawarn.testresult.entity.TestResultEntity.ResultChannel.UNKNOWN;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import static javax.persistence.EnumType.ORDINAL;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * This class represents the main test result entity as it will be provided to us by Sciensano
 * Data is inserted by recognized test labs via a secure one-way connection. (We cannot access their systems)
 * The Mobile application polls the results via the verification server, that in turn calls this test result service.
 * The verification service acts as a proxy to this data.
 *
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sciensano_test_result")
public class TestResultEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  @JsonIgnore
  private Long id;

  @CreatedDate
  @Column(name = "created_at")
  @JsonIgnore
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  @JsonIgnore
  private LocalDateTime updatedAt;

  @Version
  @Column(name = "version")
  @JsonIgnore
  private Long version;

  @Enumerated(ORDINAL)
  @Column(name = "result")
  private Result result;

  @Enumerated(ORDINAL)
  @Column(name = "result_channel")
  private ResultChannel resultChannel;

  @Column(name = "mobile_test_id")
  private String mobileTestId;

  @Column(name = "date_patient_infectious")
  private LocalDate datePatientInfectious;

  @Column(name = "date_sample_collected")
  private LocalDate dateSampleCollected;

  @Column(name = "date_test_performed")
  private LocalDate dateTestPerformed;

  @Column(name = "date_test_communicated")
  private LocalDate dateTestCommunicated;


  /**
   * Create a pending result with an unknown channel for a particular mobileTestId and datePatientInfectious.
   *
   *
   * @param mobileTestId The mobile test id.
   * @param datePatientInfectious the date patient was diagnosed infectious.
   * @return
   */
  public static TestResultEntity pendingResult(String mobileTestId,LocalDate datePatientInfectious) {
    return new TestResultEntity()
      .setResult(PENDING)
      .setResultChannel(UNKNOWN)
      .setMobileTestId(mobileTestId)
      .setDatePatientInfectious(datePatientInfectious);
  }

  public boolean hasTestBeenCommunicated() {
    return this.dateTestCommunicated != null;
  }

  public boolean isPositive() {
    return POSITIVE.equals(getResult());
  }

  /**
   * Create a pending result.
   *
   * @param mobileTestId The mobile test id.
   * @return
   */
  public static TestResultEntity dummyPendingResult(String mobileTestId) {
    return new TestResultEntity()
      .setResult(PENDING)
      .setResultChannel(UNKNOWN)
      .setMobileTestId(mobileTestId)
      .setDateTestCommunicated(LocalDate.now())
      .setDateTestPerformed(LocalDate.now())
      .setDatePatientInfectious(LocalDate.now())
      .setDateSampleCollected(LocalDate.now());
  }

  /**
   * Generate the data on the result that is used to calculate a signature.
   * @return the data on the result that is used to calculate a signature.
   */
  @JsonIgnore
  public String getSignatureData() {
    StringBuilder sb = new StringBuilder();

    return sb
      .append(getMobileTestId())
      .append(getDatePatientInfectious())
      .append(getDateTestCommunicated())
      .append(getResultChannel().ordinal())
      .toString();

  }


  public enum Result {
    PENDING, NEGATIVE, POSITIVE, INVALID, REDEEMED
  }

  public enum ResultChannel {
    UNKNOWN,LAB,DOCTOR
  }
}
