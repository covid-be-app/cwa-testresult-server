package app.coronawarn.testresult.authorizationcode;

import app.coronawarn.testresult.client.SubmissionServerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SubmissionServerGateway {

  private final SubmissionServerClient submissionServerClient;

  public ResponseEntity<Void> processAuthorizationCodes(AuthorizationCodeRequest authorizationCodes) {
    return submissionServerClient.processAuthorizationCodes(authorizationCodes);
  }
}
