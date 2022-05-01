package cloud.dsk8s.lite.technical.infrastructure.secondary.aop.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cloud.dsk8s.lite.UnitTest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;

@UnitTest
@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

  @Mock
  private Logger logger;

  @Mock
  private ProceedingJoinPoint joinPoint;

  @Mock
  private Signature signature;

  private LoggingAspect loggingAspect;

  private static String signatureTypeName = "signature type name";
  private static String signatureName = "signature name";

  private static String exceptionMessage = "exception message";
  private static String exceptionCause = "exception cause";
  private static String exceptionFormatComplete = "Exception in {}() with cause = '{}' and exception = '{}'";
  private static String exceptionFormat = "Exception in {}() with cause = {}";

  private static String enterMessage = "Enter: {}() with argument[s] = {}";
  private static String exitMessage = "Exit: {}() with result = {}";
  private static String illegalMessage = "Illegal argument: {} in {}()";

  private static String joinArgs = "join args";

  @BeforeEach
  void setup() {
    loggingAspect = new LoggingAspect();
  }

  private void initMocks() {
    when(joinPoint.getSignature()).thenReturn(signature);
    when(signature.getDeclaringTypeName()).thenReturn(signatureTypeName);
  }

  @Test
  void shouldGetLogger() {
    initMocks();
    try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
      loggerFactory.when(() -> LoggerFactory.getLogger(signatureTypeName)).thenReturn(logger);

      loggingAspect.logger(joinPoint);

      verify(joinPoint).getSignature();
      verify(signature).getDeclaringTypeName();
      loggerFactory.verify(() -> LoggerFactory.getLogger(signatureTypeName));
    }
  }

  @Test
  void shouldLogAfterWithLoggingTrueAndExceptionCause() {
    initMocks();
    when(signature.getName()).thenReturn(signatureName);
    ReflectionTestUtils.setField(loggingAspect, "logging", true);

    try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
      loggerFactory.when(() -> LoggerFactory.getLogger(signatureTypeName)).thenReturn(logger);
      Exception exception = new Exception(exceptionMessage, new Exception(exceptionCause));

      loggingAspect.logAfterThrowing(joinPoint, exception);

      verify(logger).error(exceptionFormatComplete, signatureName, exception.getCause(), exceptionMessage, exception);
    }
  }

  @Test
  void shouldLogAfterWithLoggingTrueAndNoExceptionCause() {
    initMocks();
    when(signature.getName()).thenReturn(signatureName);
    ReflectionTestUtils.setField(loggingAspect, "logging", true);

    try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
      loggerFactory.when(() -> LoggerFactory.getLogger(signatureTypeName)).thenReturn(logger);
      Exception exception = new Exception(exceptionMessage);

      loggingAspect.logAfterThrowing(joinPoint, exception);

      verify(logger).error(exceptionFormatComplete, signatureName, "NULL", exceptionMessage, exception);
    }
  }

  @Test
  void shouldLogAfterWithLoggingFalseAndExceptionCause() {
    initMocks();
    when(signature.getName()).thenReturn(signatureName);
    ReflectionTestUtils.setField(loggingAspect, "logging", false);

    try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
      loggerFactory.when(() -> LoggerFactory.getLogger(signatureTypeName)).thenReturn(logger);
      Exception exception = new Exception(exceptionMessage, new Exception(exceptionCause));

      loggingAspect.logAfterThrowing(joinPoint, exception);

      verify(logger).error(exceptionFormat, signatureName, exception.getCause());
    }
  }

  @Test
  void shouldLogAfterWithLoggingFalseAndNoExceptionCause() {
    initMocks();
    when(signature.getName()).thenReturn(signatureName);
    ReflectionTestUtils.setField(loggingAspect, "logging", false);

    try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
      loggerFactory.when(() -> LoggerFactory.getLogger(signatureTypeName)).thenReturn(logger);
      Exception exception = new Exception(exceptionMessage);

      loggingAspect.logAfterThrowing(joinPoint, exception);

      verify(logger).error(exceptionFormat, signatureName, "NULL");
    }
  }

  @Test
  void shouldLogAroundWithDebug() throws Throwable {
    initMocks();
    when(logger.isDebugEnabled()).thenReturn(true);
    when(signature.getName()).thenReturn(signatureName);
    String proceedResult = "proceed result";
    when(joinPoint.proceed()).thenReturn(proceedResult);
    when(joinPoint.getArgs()).thenReturn(new String[] { joinArgs });

    try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
      loggerFactory.when(() -> LoggerFactory.getLogger(signatureTypeName)).thenReturn(logger);

      Object result = loggingAspect.logAround(joinPoint);
      assertThat(result).isEqualTo(proceedResult);

      verify(logger).debug(enterMessage, signatureName, "[" + joinArgs + "]");
      verify(logger).debug(exitMessage, signatureName, proceedResult);
    }
  }

  @Test
  void shouldLogAroundWithoutDebug() throws Throwable {
    initMocks();
    when(logger.isDebugEnabled()).thenReturn(false);
    String proceedResult = "proceed result";
    when(joinPoint.proceed()).thenReturn(proceedResult);

    try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
      loggerFactory.when(() -> LoggerFactory.getLogger(signatureTypeName)).thenReturn(logger);

      Object result = loggingAspect.logAround(joinPoint);
      assertThat(result).isEqualTo(proceedResult);

      verify(logger, never()).debug(anyString(), any(Object.class), any(Object.class));
    }
  }

  @Test
  void shouldLogAroundWitProceedException() throws Throwable {
    initMocks();
    when(logger.isDebugEnabled()).thenReturn(false);
    when(signature.getName()).thenReturn(signatureName);
    when(joinPoint.proceed()).thenThrow(IllegalArgumentException.class);
    when(joinPoint.getArgs()).thenReturn(new String[] { joinArgs });

    try (MockedStatic<LoggerFactory> loggerFactory = mockStatic(LoggerFactory.class)) {
      loggerFactory.when(() -> LoggerFactory.getLogger(signatureTypeName)).thenReturn(logger);

      assertThatThrownBy(() -> loggingAspect.logAround(joinPoint)).isExactlyInstanceOf(IllegalArgumentException.class);

      verify(logger, never()).debug(anyString(), any(Object.class), any(Object.class));
      verify(logger).error(illegalMessage, "[" + joinArgs + "]", signatureName);
    }
  }

  @Test
  void shouldSpringBeanPoincut() {
    assertThatCode(() -> loggingAspect.springBeanPointcut()).doesNotThrowAnyException();
  }

  @Test
  void shouldApplicationPoincut() {
    assertThatCode(() -> loggingAspect.applicationPackagePointcut()).doesNotThrowAnyException();
  }
}
