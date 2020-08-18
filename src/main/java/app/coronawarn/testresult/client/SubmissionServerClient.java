/*
 * Corona-Warn-App / cwa-verification
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

package app.coronawarn.testresult.client;

import app.coronawarn.testresult.authorizationcode.AuthorizationCodeRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * This class represents submission server feign client.
 */
@FeignClient(
  name = "testResultServerClient",
  url = "${cwa-submission-server.url}")
public interface SubmissionServerClient {

  /**
   * This method calls the submission server and delivers a set of ACs.
   *
   * @param authorizationCodeRequest the requrst containinh the authorization codes that need to be processed.
   * @return Empty response
   */
  @PostMapping(value = "/version/v1/authorizationcodes/process",
    consumes = MediaType.APPLICATION_JSON_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
  )
  ResponseEntity<Void> processAuthorizationCodes(AuthorizationCodeRequest authorizationCodeRequest);

}
