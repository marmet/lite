package cloud.dsk8s.lite.technical.infrastructure.secondary.aop.logging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {

  @Bean
  @ConditionalOnProperty("application.aop.logging")
  public LoggingAspect loggingAspect() {
    return new LoggingAspect();
  }
}
