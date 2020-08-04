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
import static app.coronawarn.testresult.entity.TestResultEntity.Result.POSITIVE;
import static app.coronawarn.testresult.entity.TestResultEntity.ResultChannel.LAB;
import app.coronawarn.testresult.model.MobileTestResultList;
import app.coronawarn.testresult.model.MobileTestResultRequest;
import app.coronawarn.testresult.model.MobileTestResultUpdateRequest;
import app.coronawarn.testresult.sciensano.TestResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.Assert;
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
public class AckControllerTest {

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
  public void executingAnAckResultsInDateTestCommunicated() throws Exception {

    MobileTestResultList valid1 = new MobileTestResultList()
      .setMobileTestResultUpdateRequest (Collections.singletonList(
        new MobileTestResultUpdateRequest()
          .setResult(POSITIVE)
          .setResultChannel(LAB)
          .setMobileTestId(MOBILE_TEST_ID)
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

    MobileTestResultRequest request = new MobileTestResultRequest()
      .setMobileTestId(MOBILE_TEST_ID)
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
      .andExpect(jsonPath("$.dateTestCommunicated").doesNotExist());

    mockMvc.perform(MockMvcRequestBuilders
      .post("/v1/app/testresult/ack")
      .accept(MediaType.APPLICATION_JSON_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(request)))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isNoContent());


    TestResultEntity testResultEntity = testResultRepository.findByMobileTestIdAndDatePatientInfectious(MOBILE_TEST_ID, LocalDate.now()).get();
    Assert.assertEquals(LocalDate.now(), testResultEntity.getDateTestCommunicated());

  }

}
