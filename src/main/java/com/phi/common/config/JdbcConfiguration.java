package com.phi.common.config;

import com.phi.auth.dao.UserRepository;
import com.phi.common.LongToInstantConverter;
import com.phi.material.dao.MaterialRepository;
import java.util.List;
import org.komamitsu.spring.data.sqlite.EnableSqliteRepositories;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.lang.NonNull;

@Configuration
@EnableSqliteRepositories(basePackageClasses = {UserRepository.class, MaterialRepository.class})
public class JdbcConfiguration extends AbstractJdbcConfiguration {

    @Override
    protected @NonNull List<?> userConverters() {
        return List.of(new LongToInstantConverter());
    }
}
