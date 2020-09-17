/*
 * Coronalert / cwa-testresult-server
 *
 * (c) 2020 Devside SRL
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "authorization_code")
public class AuthorizationCodeEntity {

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

  @Column(name = "signature")
  @NonNull
  private String signature;

  @Column(name = "mobile_test_id")
  private String mobileTestId;

  @Column(name = "date_patient_infectious")
  private LocalDate datePatientInfectious;

  @Column(name = "date_test_communicated")
  private LocalDate dateTestCommunicated;

}
