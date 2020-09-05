package app.coronawarn.testresult.authorizationcode;

import app.coronawarn.testresult.entity.AuthorizationCodeEntity;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * This component will copy the ACs on an hourly basis to the submission server.
 * That way the submission server can validate incoming TEKs using the AC signature.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AuthorizationCodeCopier {

  private final AuthorizationCodeRepository authorizationCodeRepository;
  private final SubmissionServerGateway submissionServerGateway;


  /**
   * Fetch all ACs and transfer them to the submission server.
   */
  @Scheduled(initialDelay = 2000, fixedDelayString = "${testresult.authorizationcode.transfer.rate}")
  @Transactional
  public void copyACs() {
    List<AuthorizationCodeEntity> all = authorizationCodeRepository.findAll();

    ResponseEntity<Void> voidResponseEntity = submissionServerGateway.processAuthorizationCodes(
      AuthorizationCodeRequest.withAuthorizationCodes(all));

    if (voidResponseEntity.getStatusCode().isError()) {
      log.error("Error while processing authorization codes by submission server");
    }
    authorizationCodeRepository.deleteAll(all);
  }

}
