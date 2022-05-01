package cloud.dsk8s.lite.security.jwt.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import cloud.dsk8s.lite.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@UnitTest
class ApplicationSecurityPropertiesTest {

  private ApplicationSecurityProperties properties;

  @BeforeEach
  void setup() {
    properties = new ApplicationSecurityProperties();
  }

  @Test
  void shouldGetSecurityAuthenticationJwtSecret() {
    ApplicationSecurityProperties.Authentication.Jwt obj = properties.getAuthentication().getJwt();
    String val = ApplicationSecurityDefaults.SECRET;
    assertThat(obj.getSecret()).isEqualTo(val);
    val = "1" + val;
    obj.setSecret(val);
    assertThat(obj.getSecret()).isEqualTo(val);
  }

  @Test
  void shouldGetSecurityAuthenticationJwtBase64Secret() {
    ApplicationSecurityProperties.Authentication.Jwt obj = properties.getAuthentication().getJwt();
    String val = ApplicationSecurityDefaults.BASE_64_SECRET;
    assertThat(obj.getSecret()).isEqualTo(val);
    val = "1" + val;
    obj.setBase64Secret(val);
    assertThat(obj.getBase64Secret()).isEqualTo(val);
  }

  @Test
  void shouldGetSecurityAuthenticationJwtTokenValidityInSeconds() {
    ApplicationSecurityProperties.Authentication.Jwt obj = properties.getAuthentication().getJwt();
    long val = ApplicationSecurityDefaults.TOKEN_VALIDITY_IN_SECONDS;
    assertThat(obj.getTokenValidityInSeconds()).isEqualTo(val);
    val++;
    obj.setTokenValidityInSeconds(val);
    assertThat(obj.getTokenValidityInSeconds()).isEqualTo(val);
  }

  @Test
  void shouldGetSecurityAuthenticationJwtTokenValidityInSecondsForRememberMe() {
    ApplicationSecurityProperties.Authentication.Jwt obj = properties.getAuthentication().getJwt();
    long val = ApplicationSecurityDefaults.TOKEN_VALIDITY_IN_SECONDS_FOR_REMEMBER_ME;
    assertThat(obj.getTokenValidityInSecondsForRememberMe()).isEqualTo(val);
    val++;
    obj.setTokenValidityInSecondsForRememberMe(val);
    assertThat(obj.getTokenValidityInSecondsForRememberMe()).isEqualTo(val);
  }

  @Test
  void shouldGetSecurityContentSecurityPolicy() {
    ApplicationSecurityProperties obj = properties;
    String val = ApplicationSecurityDefaults.CONTENT_SECURITY_POLICY;
    assertThat(obj.getContentSecurityPolicy()).isEqualTo(val);
    obj.setContentSecurityPolicy("foobar");
    assertThat(obj.getContentSecurityPolicy()).isEqualTo("foobar");
  }
}
