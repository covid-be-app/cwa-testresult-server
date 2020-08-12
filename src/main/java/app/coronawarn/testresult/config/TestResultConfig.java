package app.coronawarn.testresult.config;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "testresult")
public class TestResultConfig {

  private Cleanup cleanup;
  private Signature signature;

  @Getter
  @Setter
  public static class Cleanup {

    private Scheduled delete;

  }

  @Getter
  @Setter
  public static class Scheduled {

    private Integer days;

  }

  @Getter
  @Setter
  public static class Signature {

    @NotNull
    private String privateKeyContent;

  }

}
