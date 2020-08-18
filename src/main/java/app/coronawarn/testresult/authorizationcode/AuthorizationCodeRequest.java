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
