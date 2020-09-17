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

package app.coronawarn.testresult;

import app.coronawarn.testresult.entity.TestResultEntity;
import app.coronawarn.testresult.entity.TestResultEntity.Result;
import static app.coronawarn.testresult.entity.TestResultEntity.Result.PENDING;
import app.coronawarn.testresult.entity.TestResultEntity.ResultChannel;
import static app.coronawarn.testresult.entity.TestResultEntity.ResultChannel.LAB;
import app.coronawarn.testresult.sciensano.TestResultRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = TestResultApplication.class)
public class TestResultRepositoryTest {

  @Autowired
  private TestResultRepository testResultRepository;

  @Before
  public void before() {
    testResultRepository.deleteAll();
  }

  @Test
  public void createAndFindByResultId() {
    Result result = PENDING;
    ResultChannel channel = LAB;
    String mobileTestId = "123456789012345";
    LocalDate datePatientInfectious = LocalDate.now();
    // create
    TestResultEntity create = testResultRepository.save(new TestResultEntity()
      .setResult(result)
      .setResultChannel(channel)
      .setDatePatientInfectious(datePatientInfectious)
      .setDateTestPerformed(datePatientInfectious.minusDays(5))
      .setDateSampleCollected(datePatientInfectious.minusDays(4))
      .setDateTestCommunicated(null)
      .setMobileTestId(mobileTestId)
    );
    assertNotNull(create);
    assertEquals(mobileTestId, create.getMobileTestId());
    // find
    Optional<TestResultEntity> find = testResultRepository.findByMobileTestIdAndDatePatientInfectious(mobileTestId,datePatientInfectious);
    Assert.assertTrue(find.isPresent());
    assertEquals(result, find.get().getResult());
    assertEquals(mobileTestId, find.get().getMobileTestId());
    assertEquals(datePatientInfectious, find.get().getDatePatientInfectious());
    assertEquals(datePatientInfectious.minusDays(5), find.get().getDateTestPerformed());
    assertEquals(datePatientInfectious.minusDays(4), find.get().getDateSampleCollected());
    assertNull(find.get().getDateTestCommunicated());
    assertNotNull(find.get().getCreatedAt());
    assertNotNull(find.get().getUpdatedAt());
    assertNotNull(find.get().getVersion());
  }

}
