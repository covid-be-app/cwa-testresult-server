package app.coronawarn.testresult.authorizationcode;

import app.coronawarn.testresult.client.SubmissionServerClient;
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
  private final SubmissionServerClient submissionServerClient;

  /**
   * Fetch all ACs and transfer them to the submission server.
   */
  @Scheduled(fixedDelayString = "${testresult.authorizationcode.transfer.rate}")
  @Transactional
  public void copyACs() {
    List<AuthorizationCodeEntity> all = authorizationCodeRepository.findAll();
    log.info("Fetched {} ACs.", all.size());
    ResponseEntity<Void> voidResponseEntity = submissionServerClient.processAuthorizationCodes(
      AuthorizationCodeRequest.withAuthorizationCodes(all));
    log.info("Found return code {}",voidResponseEntity.getStatusCode());

    log.info("Deleting {} ACs.", all.size());
    authorizationCodeRepository.deleteAll(all);
  }

}
