package com.myproject.auth_service.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public LocalSessionFactoryBean sessionFactory() {

        LocalSessionFactoryBean sessionFactory =
                new LocalSessionFactoryBean();

        sessionFactory.setDataSource(dataSource);

        sessionFactory.setPackagesToScan(
                "com.myproject.auth_service.entity"
        );

        Properties props = new Properties();

        props.put("hibernate.dialect",
                "org.hibernate.dialect.PostgreSQLDialect");

        props.put("hibernate.show_sql", "true");

        props.put("hibernate.format_sql", "true");

        props.put("hibernate.hbm2ddl.auto", "update");

        props.put("hibernate.current_session_context_class",
                "org.springframework.orm.hibernate5.SpringSessionContext");

        sessionFactory.setHibernateProperties(props);

        return sessionFactory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(
            SessionFactory sessionFactory
    ) {

        HibernateTransactionManager txManager =
                new HibernateTransactionManager();

        txManager.setSessionFactory(sessionFactory);

        return txManager;
    }
}