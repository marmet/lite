package cloud.dsk8s.lite.security.jwt.application;

import static org.assertj.core.api.Assertions.assertThat;

import cloud.dsk8s.lite.UnitTest;
import cloud.dsk8s.lite.security.jwt.domain.AuthoritiesConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

@UnitTest
class SecurityUtilsTest {

  @BeforeEach
  @AfterEach
  void cleanup() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldNotGetAuthorities() {
    assertThat(SecurityUtils.getAuthorities()).isEmpty();
  }

  @Test
  void shouldNotGetCurrentUserLoginForNull() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(null);
    SecurityContextHolder.setContext(securityContext);
    Optional<String> login = SecurityUtils.getCurrentUserLogin();
    assertThat(login).isEmpty();

    assertThat(SecurityUtils.getAuthorities()).isEmpty();
  }

  @Test
  void shouldNotGetCurrentUserLoginForAnotherInstance() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(new TestingAuthenticationToken(null, null));
    SecurityContextHolder.setContext(securityContext);

    Optional<String> login = SecurityUtils.getCurrentUserLogin();
    assertThat(login).isEmpty();

    assertThat(SecurityUtils.getAuthorities()).isEmpty();
  }

  @Test
  void shouldGetCurrentUserLogin() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
    SecurityContextHolder.setContext(securityContext);
    Optional<String> login = SecurityUtils.getCurrentUserLogin();
    assertThat(login).contains("admin");

    assertThat(SecurityUtils.getAuthorities()).isEmpty();
  }

  @Test
  void shouldGetCurrentUserLoginWithUserDetails() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
    User user = new User("admin", "admin", authorities);
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(user, "admin", authorities));
    SecurityContextHolder.setContext(securityContext);
    Optional<String> login = SecurityUtils.getCurrentUserLogin();
    assertThat(login).contains("admin");

    assertThat(SecurityUtils.getAuthorities()).contains(AuthoritiesConstants.ADMIN);
  }

  @Test
  @DisplayName("should get current user jwt")
  void shouldGetCurrentUserJWT() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "token"));
    SecurityContextHolder.setContext(securityContext);
    Optional<String> jwt = SecurityUtils.getCurrentUserJWT();
    assertThat(jwt).contains("token");
  }

  @Test
  void shouldBeAuthenticated() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
    SecurityContextHolder.setContext(securityContext);
    boolean isAuthenticated = SecurityUtils.isAuthenticated();
    assertThat(isAuthenticated).isTrue();
  }

  @Test
  void shouldNotBeAuthenticatedWithNull() {
    SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
    boolean isAuthenticated = SecurityUtils.isAuthenticated();
    assertThat(isAuthenticated).isFalse();
  }

  @Test
  void shouldAnonymousIsNotAuthenticated() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities));
    SecurityContextHolder.setContext(securityContext);
    boolean isAuthenticated = SecurityUtils.isAuthenticated();
    assertThat(isAuthenticated).isFalse();
  }

  @Test
  void shouldHasCurrentUserThisAuthority() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
    SecurityContextHolder.setContext(securityContext);

    assertThat(SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.USER)).isTrue();
    assertThat(SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.ADMIN)).isFalse();
  }

  @Test
  void shouldHasCurrentUserAnyOfAuthorities() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
    SecurityContextHolder.setContext(securityContext);

    assertThat(SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)).isTrue();
    assertThat(SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ANONYMOUS, AuthoritiesConstants.ADMIN)).isFalse();
  }

  @Test
  void shouldNotHaveCurrentUserAnyOfAuthoritiesForNull() {
    SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());

    assertThat(SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)).isFalse();
  }

  @Test
  void shouldHasCurrentUserNoneOfAuthorities() {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
    SecurityContextHolder.setContext(securityContext);

    assertThat(SecurityUtils.hasCurrentUserNoneOfAuthorities(AuthoritiesConstants.USER, AuthoritiesConstants.ADMIN)).isFalse();
    assertThat(SecurityUtils.hasCurrentUserNoneOfAuthorities(AuthoritiesConstants.ANONYMOUS, AuthoritiesConstants.ADMIN)).isTrue();
  }
}
