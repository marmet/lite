package cloud.dsk8s.lite.security.jwt.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application.security", ignoreUnknownFields = false)
public class ApplicationSecurityProperties {

  private String contentSecurityPolicy = ApplicationSecurityDefaults.CONTENT_SECURITY_POLICY;

  private final Authentication authentication = new Authentication();

  public Authentication getAuthentication() {
    return authentication;
  }

  public String getContentSecurityPolicy() {
    return contentSecurityPolicy;
  }

  public void setContentSecurityPolicy(String contentSecurityPolicy) {
    this.contentSecurityPolicy = contentSecurityPolicy;
  }

  public static class Authentication {

    private final Jwt jwt = new Jwt();

    public Jwt getJwt() {
      return jwt;
    }

    public static class Jwt {

      private String secret = ApplicationSecurityDefaults.SECRET;

      private String base64Secret = ApplicationSecurityDefaults.BASE_64_SECRET;

      private long tokenValidityInSeconds = ApplicationSecurityDefaults.TOKEN_VALIDITY_IN_SECONDS;

      private long tokenValidityInSecondsForRememberMe = ApplicationSecurityDefaults.TOKEN_VALIDITY_IN_SECONDS_FOR_REMEMBER_ME;

      public String getSecret() {
        return secret;
      }

      public void setSecret(String secret) {
        this.secret = secret;
      }

      public String getBase64Secret() {
        return base64Secret;
      }

      public void setBase64Secret(String base64Secret) {
        this.base64Secret = base64Secret;
      }

      public long getTokenValidityInSeconds() {
        return tokenValidityInSeconds;
      }

      public void setTokenValidityInSeconds(long tokenValidityInSeconds) {
        this.tokenValidityInSeconds = tokenValidityInSeconds;
      }

      public long getTokenValidityInSecondsForRememberMe() {
        return tokenValidityInSecondsForRememberMe;
      }

      public void setTokenValidityInSecondsForRememberMe(long tokenValidityInSecondsForRememberMe) {
        this.tokenValidityInSecondsForRememberMe = tokenValidityInSecondsForRememberMe;
      }
    }
  }
}
