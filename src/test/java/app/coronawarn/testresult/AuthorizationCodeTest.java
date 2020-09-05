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

import app.coronawarn.testresult.authorizationcode.AuthorizationCodeRepository;
import app.coronawarn.testresult.entity.TestResultEntity;
import static app.coronawarn.testresult.entity.TestResultEntity.Result.NEGATIVE;
import static app.coronawarn.testresult.entity.TestResultEntity.Result.POSITIVE;
import static app.coronawarn.testresult.entity.TestResultEntity.ResultChannel.LAB;
import app.coronawarn.testresult.model.MobileTestResultRequest;
import app.coronawarn.testresult.sciensano.TestResultRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("allow-test-result-insert")
@AutoConfigureMockMvc
@ContextConfiguration(classes = TestResultApplication.class)
public class AuthorizationCodeTest {

  public static final String MOBILE_TEST_ID = "123456789012345";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private TestResultRepository testResultRepository;

  @Autowired
  private AuthorizationCodeRepository authorizationCodeRepository;

  @Before
  public void before() {
    testResultRepository.deleteAll();
    authorizationCodeRepository.deleteAll();
  }

  @Test
  public void pollingNegativeResultShouldNotResultInACGeneration() throws Exception {
    LocalDate now = LocalDate.now();

    TestResultEntity testNegative = new TestResultEntity()
      .setResult(NEGATIVE)
      .setResultChannel(LAB)
      .setMobileTestId(MOBILE_TEST_ID)
      .setDatePatientInfectious(now)
      .setDateSampleCollected(now);

    testResultRepository.save(testNegative);

    MobileTestResultRequest request = new MobileTestResultRequest()
      .setMobileTestId(MOBILE_TEST_ID)
      .setDatePatientInfectious(now);

    mockMvc.perform(MockMvcRequestBuilders
      .post("/v1/app/testresult/poll")
      .accept(MediaType.APPLICATION_JSON_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(request)))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isOk());

    Assert.assertTrue(authorizationCodeRepository.findAll().isEmpty());
  }

  @Test
  public void pollingPositiveResultShouldResultInACGeneration() throws Exception {
    LocalDate now = LocalDate.now();

    TestResultEntity testPositive = new TestResultEntity()
      .setResult(POSITIVE)
      .setResultChannel(LAB)
      .setMobileTestId(MOBILE_TEST_ID)
      .setDatePatientInfectious(now)
      .setDateSampleCollected(now);

    testResultRepository.save(testPositive);

    MobileTestResultRequest request = new MobileTestResultRequest()
      .setMobileTestId(MOBILE_TEST_ID)
      .setDatePatientInfectious(now);

    mockMvc.perform(MockMvcRequestBuilders
      .post("/v1/app/testresult/poll")
      .accept(MediaType.APPLICATION_JSON_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(request)))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isOk());

    Assert.assertEquals(1,authorizationCodeRepository.findAll().size());

    mockMvc.perform(MockMvcRequestBuilders
      .post("/v1/app/testresult/poll")
      .accept(MediaType.APPLICATION_JSON_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(request)))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isOk());

    Assert.assertEquals(1,authorizationCodeRepository.findAll().size());
  }

  @Test
  public void pollingNonExistingTestResultShouldNotGenerateAC() throws Exception {
    MobileTestResultRequest request = new MobileTestResultRequest()
      .setMobileTestId("987654321012345")
      .setDatePatientInfectious(LocalDate.now());

    mockMvc.perform(MockMvcRequestBuilders
      .post("/v1/app/testresult/poll")
      .accept(MediaType.APPLICATION_JSON_VALUE)
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(request)))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isOk());

    Assert.assertTrue(authorizationCodeRepository.findAll().isEmpty());

  }

}
