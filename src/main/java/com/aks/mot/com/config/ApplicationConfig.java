package com.aks.mot.com.config;

import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.aks.mot.com.annotation.Comment;
import com.aks.mot.com.mybatis.RefreshableSqlSessionFactoryBean;
import com.aks.mot.com.util.BeanUtil;
import com.zaxxer.hikari.HikariDataSource;

@Comment("application-context.xml Role")
@Configuration
@ComponentScan(basePackages= {"com.aks.mot"}, excludeFilters = @ComponentScan.Filter(Controller.class))
@MapperScan(basePackages= {"com.aks.mot"})
@PropertySource({"classpath:application.properties"})
@EnableTransactionManagement
public class ApplicationConfig{
	
	@Resource
	ApplicationContext applicationContext;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public BeanUtil beanUtil() {
		return new BeanUtil();
	}
	
	
	@Primary
	@Comment("mariaDb Datasource config")
	@Bean(name = "mariaDbDs")
	public DataSource mariaDbDs() {
		
		HikariDataSource dataSource = new HikariDataSource(); 
		dataSource.setMaximumPoolSize(20); 
		dataSource.setDriverClassName("net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
		dataSource.setJdbcUrl("jdbc:log4jdbc:mariadb://localhost:3306/mot");
		dataSource.addDataSourceProperty("user", "dbsvc"); 
		dataSource.addDataSourceProperty("password", "dbsvc1234!");
		dataSource.setAutoCommit(false); 
		
		return dataSource;
	}
	
	@Comment("mariaDbSqlSessionFactory and MyBatis config")
	@Bean(name = "mariaDbSqlSessionFactory")
	public SqlSessionFactory mariaDbSqlSessionFactory() throws Exception {
		// Q. 리프레쉬 소스 분석
		RefreshableSqlSessionFactoryBean sqlSessionFactoryBean = new RefreshableSqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(mariaDbDs());
		sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:/mybatis-config.xml"));
		sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath*:com/aks/**/*.xml"));
		sqlSessionFactoryBean.setTypeAliases(new Class<?>[] { Map.class});
		sqlSessionFactoryBean.setInterval(3000);
		sqlSessionFactoryBean.afterPropertiesSet();
		// Q. 인터셉터는 뭐지?
		//Interceptor[] interceptor = { statementLoggingInterceptor() };
		//sessionFactoryBean.setPlugins(interceptor);
		
		return (SqlSessionFactory) sqlSessionFactoryBean.getObject();
	}
	
	@Primary
	@Comment("SqlSession config")
	@Bean(name = "mariaDbSqlSession")
    public SqlSessionTemplate mariaDbSqlSession() throws Exception {
        return new SqlSessionTemplate(mariaDbSqlSessionFactory());
    }
	
	@Primary
	@Bean(name = "mariaDbTemplate")
	public JdbcTemplate mariaDbTemplate() {
		JdbcTemplate template = new JdbcTemplate();
		template.setDataSource(mariaDbDs());
		return template;
	}
	
	@Comment("Transaction config")
	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(mariaDbDs());
		transactionManager.setDataSource(mariaDbDs());
		return transactionManager;
    }
	
	/*
	 * public PlatformTransactionManager transactionManager1() {
	 * JtaTransactionManager manager = new JtaTransactionManager();
	 * 
	 * //manager.setUserTransaction(userTransaction);
	 * 
	 * return manager; }
	 * 
	 * public PlatformTransactionManager
	 * transactionManager(PlatformTransactionManager txManager1,
	 * PlatformTransactionManager txManager2) {
	 * 
	 * return new ChainedTransactionManager(txManager1, txManager2); }
	 */
}