package cloud.dsk8s.lite.technical.infrastructure.secondary.postgresql;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories({ "cloud.dsk8s.lite" })
@EnableTransactionManagement
public class DatabaseConfiguration {}
