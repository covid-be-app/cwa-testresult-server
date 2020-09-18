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

package app.coronawarn.testresult.authorizationcode;

import app.coronawarn.testresult.entity.AuthorizationCodeEntity;
import java.util.List;

/**
 * Contains a list of authorization codes that will be sent for processing.
 */
public class AuthorizationCodeRequest {

  private List<AuthorizationCodeEntity> authorizationCodeEntities;

  public List<AuthorizationCodeEntity> getAuthorizationCodeEntities() {
    return authorizationCodeEntities;
  }

  public void setAuthorizationCodeEntities(List<AuthorizationCodeEntity> authorizationCodeEntities) {
    this.authorizationCodeEntities = authorizationCodeEntities;
  }

  /**
   * Creates an AuthorizationCodeRequest object given the provided authorization codes.
   *
   * @param authorizationCodes  The list of Authorization codes to include in the request.
   * @return An AuthorizationCodeRequest
   */
  public static AuthorizationCodeRequest withAuthorizationCodes(List<AuthorizationCodeEntity> authorizationCodes) {
    AuthorizationCodeRequest authorizationCodeRequest = new AuthorizationCodeRequest();
    authorizationCodeRequest.setAuthorizationCodeEntities(authorizationCodes);
    return authorizationCodeRequest;
  }
}
