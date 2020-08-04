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

package app.coronawarn.testresult;

import app.coronawarn.testresult.entity.TestResultEntity;
import static app.coronawarn.testresult.entity.TestResultEntity.Result.NEGATIVE;
import static app.coronawarn.testresult.entity.TestResultEntity.Result.PENDING;
import static app.coronawarn.testresult.entity.TestResultEntity.Result.POSITIVE;
import static app.coronawarn.testresult.entity.TestResultEntity.ResultChannel.LAB;
import static app.coronawarn.testresult.entity.TestResultEntity.ResultChannel.UNKNOWN;
import app.coronawarn.testresult.model.MobileTestResultList;
import app.coronawarn.testresult.model.MobileTestResultRequest;
import app.coronawarn.testresult.model.MobileTestResultUpdateRequest;
import app.coronawarn.testresult.sciensano.TestResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("allow-test-result-insert")
@AutoConfigureMockMvc
@ContextConfiguration(classes = TestResultApplication.class)
public class TestResultControllerTest {

  public static final String MOBILE_TEST_ID = "123456789012345";
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private TestResultRepository testResultRepository;

  @Before
  public void before() {
    testResultRepository.deleteAll();
  }

  @Test
  public void insertInvalidIdShouldReturnBadRequest() throws Exception {
    MobileTestResultList invalid = new MobileTestResultList()
      .setMobileTestResultUpdateRequest (Collections.singletonList(
        new MobileTestResultUpdateRequest()
          .setResult(TestResultEntity.Result.PENDING)
          .setMobileTestId(null)
          .setDatePatientInfectious(LocalDate.now())
        )
      );
    mockMvc.perform(MockMvcRequestBuilders
      .post("/v1/lab/results")
      .accept(MediaType.APPLICATION_JSON_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(invalid)))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  public void insertInvalidResultShouldReturnBadRequest() throws Exception {

    MobileTestResultList invalid = new MobileTestResultList()
      .setMobileTestResultUpdateRequest (Collections.singletonList(
        new MobileTestResultUpdateRequest()
          .setResult(TestResultEntity.Result.PENDING)

          .setMobileTestId("123456789012345")
          .setDatePatientInfectious(null)
        )
      );

    mockMvc.perform(MockMvcRequestBuilders
      .post("/v1/lab/results")
      .accept(MediaType.APPLICATION_JSON_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(invalid)))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isBadRequest());

  }

  @Test
  public void insertValidShouldReturnNoContent() throws Exception {

    MobileTestResultList valid = new MobileTestResultList()
      .setMobileTestResultUpdateRequest (Collections.singletonList(
        new MobileTestResultUpdateRequest()
          .setResult(POSITIVE)
          .setResultChannel(LAB)
          .setMobileTestId("123456789012345")
          .setDatePatientInfectious(LocalDate.now())
        )
      );

    mockMvc.perform(MockMvcRequestBuilders
      .post("/v1/lab/results")
      .accept(MediaType.APPLICATION_JSON_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(valid)))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  public void insertMultipleMobileTestIdsWithDifferentDatesShouldReturnNoContent() throws Exception {

    MobileTestResultList valid1 = new MobileTestResultList()
      .setMobileTestResultUpdateRequest (Collections.singletonList(
        new MobileTestResultUpdateRequest()
          .setResult(POSITIVE)
          .setResultChannel(LAB)
          .setMobileTestId("123456789012345")
          .setDatePatientInfectious(LocalDate.now())
        )
      );

    mockMvc.perform(MockMvcRequestBuilders
      .post("/v1/lab/results")
      .accept(MediaType.APPLICATION_JSON_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(valid1)))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isNoContent());

    MobileTestResultList valid2 = new MobileTestResultList()
      .setMobileTestResultUpdateRequest (Collections.singletonList(
        new MobileTestResultUpdateRequest()
          .setResult(NEGATIVE)
          .setResultChannel(LAB)
          .setMobileTestId("123456789012345")
          .setDatePatientInfectious(LocalDate.now().minusDays(1))
        )
      );

    mockMvc.perform(MockMvcRequestBuilders
      .post("/v1/lab/results")
      .accept(MediaType.APPLICATION_JSON_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(valid2)))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isNoContent());

    MobileTestResultRequest request1 = new MobileTestResultRequest()
      .setMobileTestId("123456789012345")
      .setDatePatientInfectious(LocalDate.now());

    mockMvc.perform(MockMvcRequestBuilders
      .post("/v1/app/testresult/poll")
      .accept(MediaType.APPLICATION_JSON_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(request1)))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(jsonPath("$.mobileTestId").value(request1.getMobileTestId()))
      .andExpect(jsonPath("$.datePatientInfectious").value(request1.getDatePatientInfectious().toString()))
      .andExpect(jsonPath("$.result").value(POSITIVE.toString()))
      .andExpect(jsonPath("$.id").doesNotExist())
      .andExpect(jsonPath("$.createdAt").doesNotExist())
      .andExpect(jsonPath("$.updatedAt").doesNotExist())
      .andExpect(jsonPath("$.version").doesNotExist());



    MobileTestResultRequest request2 = new MobileTestResultRequest()
      .setMobileTestId("123456789012345")
      .setDatePatientInfectious(LocalDate.now().minusDays(1));

    mockMvc.perform(MockMvcRequestBuilders
      .post("/v1/app/testresult/poll")
      .accept(MediaType.APPLICATION_JSON_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(request2)))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(jsonPath("$.mobileTestId").value(request2.getMobileTestId()))
      .andExpect(jsonPath("$.datePatientInfectious").value(request2.getDatePatientInfectious().toString()))
      .andExpect(jsonPath("$.result").value(NEGATIVE.toString()))
      .andExpect(jsonPath("$.id").doesNotExist())
      .andExpect(jsonPath("$.createdAt").doesNotExist())
      .andExpect(jsonPath("$.updatedAt").doesNotExist())
      .andExpect(jsonPath("$.version").doesNotExist());




  }

  @Test
  public void insertValidAndGetShouldReturnOk() throws Exception {
    LocalDate now = LocalDate.now();

    MobileTestResultList valid = new MobileTestResultList()
      .setMobileTestResultUpdateRequest (Collections.singletonList(
        new MobileTestResultUpdateRequest()
          .setResult(POSITIVE)
          .setResultChannel(LAB)
          .setMobileTestId(MOBILE_TEST_ID)
          .setDatePatientInfectious(now)
          .setDateSampleCollected(now)
        )
      );

    mockMvc.perform(MockMvcRequestBuilders
      .post("/v1/lab/results")
      .accept(MediaType.APPLICATION_JSON_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(valid)))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isNoContent());

    // get
    MobileTestResultRequest request = new MobileTestResultRequest()
      .setMobileTestId(MOBILE_TEST_ID)
      .setDatePatientInfectious(now);

    mockMvc.perform(MockMvcRequestBuilders
      .post("/v1/app/testresult/poll")
      .accept(MediaType.APPLICATION_JSON_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(request)))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(jsonPath("$.mobileTestId").value(request.getMobileTestId()))
      .andExpect(jsonPath("$.datePatientInfectious").value(request.getDatePatientInfectious().toString()))
      .andExpect(jsonPath("$.dateSampleCollected").value(LocalDate.now().toString()))
      .andExpect(jsonPath("$.dateTestCommunicated").value(LocalDate.now().toString()))
      .andExpect(jsonPath("$.result").value(POSITIVE.toString()))
      .andExpect(jsonPath("$.resultChannel").value(LAB.toString()))
      .andExpect(jsonPath("$.id").doesNotExist())
      .andExpect(jsonPath("$.createdAt").doesNotExist())
      .andExpect(jsonPath("$.updatedAt").doesNotExist())
      .andExpect(jsonPath("$.version").doesNotExist());
  }

  @Test
  public void notExistingTestResultShouldReturnOk() throws Exception {
    MobileTestResultRequest request = new MobileTestResultRequest()
      .setMobileTestId("987654321012345")
      .setDatePatientInfectious(LocalDate.now());
    mockMvc.perform(MockMvcRequestBuilders
      .post("/v1/app/testresult/poll")
      .accept(MediaType.APPLICATION_JSON_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(request)))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(jsonPath("$.mobileTestId").value(request.getMobileTestId()))
      .andExpect(jsonPath("$.datePatientInfectious").value(request.getDatePatientInfectious().toString()))
      .andExpect(jsonPath("$.dateSampleCollected").value(LocalDate.now().toString()))
      .andExpect(jsonPath("$.dateTestCommunicated").value(LocalDate.now().toString()))
      .andExpect(jsonPath("$.result").value(PENDING.toString()))
      .andExpect(jsonPath("$.resultChannel").value(UNKNOWN.toString()))
      .andExpect(jsonPath("$.id").doesNotExist())
      .andExpect(jsonPath("$.createdAt").doesNotExist())
      .andExpect(jsonPath("$.updatedAt").doesNotExist())
      .andExpect(jsonPath("$.version").doesNotExist());
  }

}
