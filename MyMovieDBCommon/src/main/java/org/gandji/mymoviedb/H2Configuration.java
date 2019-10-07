package org.gandji.mymoviedb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by gandji on 14/01/2018.
 */
@Profile("h2")
@PropertySource("classpath:application-h2.properties")
@Configuration
@Slf4j
public class H2Configuration extends MyMovieDBCommonConfiguration {

    /* noooo? or yes? see
http://stackoverflow.com/questions/26548505/org-hibernate-hibernateexception-access-to-dialectresolutioninfo-cannot-be-null
I think this is to get the hibernate specific "sessionfactory". Specific to hibernate.*/
    /*
    seems I can get a sessionfactory bean like so, but unable to ope a session*/
    /*@Bean
    public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory entityManagerFactory) {
        HibernateJpaSessionFactoryBean factory = new HibernateJpaSessionFactoryBean();
        factory.setEntityManagerFactory(entityManagerFactory);
        return factory;
    }*/

    // pure hibernate session factory: NOT WORKING
    /*@Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean lsfb = new LocalSessionFactoryBean();
        lsfb.setDataSource(dataSource);
        lsfb.setAnnotatedClasses(Movie.class,VideoFile.class,Actor.class,
                Genre.class,KeywordExcludeRegexp.class);
        return lsfb;
    }

    @Bean
    public AbstractPlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
        // OK: return new DataSourceTransactionManager(dataSource);
        // OK: return new JpaTransactionManager();
        return new HibernateTransactionManager(sessionFactory);
    }*/

    /* grrrr
    @Bean
    public EntityManagerFactory entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        HibernateJpaVendorAdapter hjva = new HibernateJpaVendorAdapter();
        hjva.setShowSql(true);
        hjva.setGenerateDdl(true);
        emf.setJpaVendorAdapter(hjva);
        Map<String,Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto","update");
        properties.put("hibernate.format_sql",true);
        properties.put("hibernate.use_sql_comments", false);
        properties.put("hibernate.show_sql",true);
        properties.put("hibernate.search.defaultIndexBase", "C:/Users/");
        emf.setJpaPropertyMap(properties);
        return emf.getNativeEntityManagerFactory();
    }*/

    /*@Bean
    public HibernateMovieDao hibernateMovieDao() {
        return new HibernateMovieDaoH2();
    }*/
}
