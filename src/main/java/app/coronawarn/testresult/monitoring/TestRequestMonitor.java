/*-
 * ---license-start
 * Corona-Warn-App
 * ---
 * Copyright (C) 2020 SAP SE and all other contributors
 * All modifications are copyright (c) 2020 Devside SRL.
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ---license-end
 */

package app.coronawarn.testresult.monitoring;

import app.coronawarn.testresult.config.TestResultConfig;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Provides functionality for monitoring the diagnosis key submission handling logic.
 */
@Component
@ConfigurationProperties(prefix = "services.submission.monitoring")
public class TestRequestMonitor {

  private final MeterRegistry meterRegistry;
  private final long batchSize;

  private BatchCounter positiveResponses;
  private BatchCounter negativeResponses;

  /**
   * Constructor for {@link TestRequestMonitor}. Initializes all counters to 0 upon being called.
   *
   * @param meterRegistry the meterRegistry
   */
  protected TestRequestMonitor(
    MeterRegistry meterRegistry, TestResultConfig testResultConfig) {
    this.meterRegistry = meterRegistry;
    this.batchSize = testResultConfig.getMonitoring().getBatchSize();
    initializeCounters();
  }

  /**
   * We count the following values.
   *  <ul>
   *    <li> the number of nonexisting test requests.
   *    <li> the number of dummy test requests.
   *    <li> the number of negative test responses
   *    <li> the number of positive test responses
   *  </ul>
   */
  private void initializeCounters() {
    positiveResponses = new BatchCounter(meterRegistry, batchSize, "positive");
    negativeResponses = new BatchCounter(meterRegistry, batchSize, "negative");
  }

  public void incrementPositiveTestResponse() {
    positiveResponses.increment();
  }

  public void incrementNegativeTestResponse() {
    negativeResponses.increment();
  }


}
