package cloud.dsk8s.lite.security.jwt.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import cloud.dsk8s.lite.UnitTest;
import cloud.dsk8s.lite.security.jwt.domain.AuthoritiesConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

@UnitTest
class TokenProviderTest {

  private static final long ONE_MINUTE = 60000;

  private Key key;
  private TokenProvider tokenProvider;

  @BeforeEach
  public void setup() {
    ApplicationSecurityProperties properties = new ApplicationSecurityProperties();
    String base64Secret = "fd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8";
    properties.getAuthentication().getJwt().setBase64Secret(base64Secret);
    tokenProvider = new TokenProvider(properties);
    key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));

    ReflectionTestUtils.setField(tokenProvider, "key", key);
    ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", ONE_MINUTE);
  }

  @Test
  void testReturnFalseWhenJWThasInvalidSignature() {
    boolean isTokenValid = tokenProvider.validateToken(createTokenWithDifferentSignature());

    assertThat(isTokenValid).isFalse();
  }

  @Test
  void testReturnFalseWhenJWTisMalformed() {
    Authentication authentication = createAuthentication();
    String token = tokenProvider.createToken(authentication, false);
    String invalidToken = token.substring(1);
    boolean isTokenValid = tokenProvider.validateToken(invalidToken);

    assertThat(isTokenValid).isFalse();
  }

  @Test
  void testReturnFalseWhenJWTisExpired() {
    ReflectionTestUtils.setField(tokenProvider, "tokenValidityInMilliseconds", -ONE_MINUTE);

    Authentication authentication = createAuthentication();
    String token = tokenProvider.createToken(authentication, false);

    boolean isTokenValid = tokenProvider.validateToken(token);

    assertThat(isTokenValid).isFalse();
  }

  @Test
  void testReturnFalseWhenJWTisUnsupported() {
    String unsupportedToken = createUnsupportedToken();

    boolean isTokenValid = tokenProvider.validateToken(unsupportedToken);

    assertThat(isTokenValid).isFalse();
  }

  @Test
  void testReturnFalseWhenJWTisInvalid() {
    boolean isTokenValid = tokenProvider.validateToken("");

    assertThat(isTokenValid).isFalse();
  }

  @Test
  void testKeyIsSetFromSecretWhenSecretIsNotEmpty() {
    final String secret = "NwskoUmKHZtzGRKJKVjsJF7BtQMMxNWi";
    ApplicationSecurityProperties properties = new ApplicationSecurityProperties();
    properties.getAuthentication().getJwt().setSecret(secret);

    TokenProvider tokenProvider = new TokenProvider(properties);

    Key key = (Key) ReflectionTestUtils.getField(tokenProvider, "key");
    assertThat(key).isNotNull().isEqualTo(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)));
  }

  @Test
  void testKeyIsSetFromBase64SecretWhenSecretIsEmpty() {
    final String base64Secret = "fd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8";
    ApplicationSecurityProperties properties = new ApplicationSecurityProperties();
    properties.getAuthentication().getJwt().setBase64Secret(base64Secret);

    TokenProvider tokenProvider = new TokenProvider(properties);

    Key key = (Key) ReflectionTestUtils.getField(tokenProvider, "key");
    assertThat(key).isNotNull().isEqualTo(Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret)));
  }

  @Test
  void shouldGetAuthenticationWithoutAuthorities() {
    Authentication authentication = tokenProvider.getAuthentication(createTokenWithoutAuthorities());

    assertThat(authentication).isNotNull();
    assertThat(authentication.getAuthorities()).isEmpty();
  }

  private Authentication createAuthentication() {
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
    return new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities);
  }

  private String createUnsupportedToken() {
    return Jwts.builder().setPayload("payload").signWith(key, SignatureAlgorithm.HS512).compact();
  }

  private String createTokenWithDifferentSignature() {
    Key otherKey = Keys.hmacShaKeyFor(
      Decoders.BASE64.decode("Xfd54a45s65fds737b9aafcb3412e07ed99b267f33413274720ddbb7f6c5e64e9f14075f2d7ed041592f0b7657baf8")
    );

    return Jwts
      .builder()
      .setSubject("anonymous")
      .signWith(otherKey, SignatureAlgorithm.HS512)
      .setExpiration(new Date(new Date().getTime() + ONE_MINUTE))
      .compact();
  }

  private String createTokenWithoutAuthorities() {
    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("test-user", "test-password", List.of());
    return tokenProvider.createToken(authentication, true);
  }
}
