package cloud.dsk8s.lite.security.jwt.infrastructure.config;

public class ApplicationSecurityDefaults {

  private ApplicationSecurityDefaults() {}

  public static final String CONTENT_SECURITY_POLICY =
    "default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:";

  public static final String SECRET = null;
  public static final String BASE_64_SECRET = null;
  public static final long TOKEN_VALIDITY_IN_SECONDS = 1800; // 30 minutes
  public static final long TOKEN_VALIDITY_IN_SECONDS_FOR_REMEMBER_ME = 2592000; // 30 days
}
