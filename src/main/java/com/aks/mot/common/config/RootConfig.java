package com.aks.mot.common.config;

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
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;

import com.aks.mot.common.mybatis.RefreshableSqlSessionFactoryBean;
import com.aks.mot.common.util.BeanUtil;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySource({"classpath:application.properties"})
//@EnableTransactionManagement
@MapperScan(basePackages= {"com.aks.mot"})
@ComponentScan(basePackages= {"com.aks.mot"}, excludeFilters = @ComponentScan.Filter(Controller.class))
public class RootConfig{

	@Resource
	ApplicationContext applicationContext;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
	}

	//@Primary
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
	
	@Bean(name = "mariaDbSqlSessionFactory")
	public SqlSessionFactory mariaDbSqlSessionFactory() throws Exception {
		RefreshableSqlSessionFactoryBean sqlSessionFactoryBean = new RefreshableSqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(mariaDbDs());
		sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:/mybatis-config.xml"));
		sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath*:com/aks/**/*.xml"));
		sqlSessionFactoryBean.setTypeAliases(new Class<?>[] { Map.class});
		sqlSessionFactoryBean.setInterval(3000);
		sqlSessionFactoryBean.afterPropertiesSet();
		return (SqlSessionFactory) sqlSessionFactoryBean.getObject();
	}
	
	//@Primary
	@Bean(name = "mariaDbSqlSession")
    public SqlSessionTemplate mariaDbSqlSession() throws Exception {
        return new SqlSessionTemplate(mariaDbSqlSessionFactory());
    }
	
	//@Primary
	@Bean(name = "mariaDbTemplate")
	public JdbcTemplate mariaDbTemplate() {
		JdbcTemplate template = new JdbcTemplate();
		template.setDataSource(mariaDbDs());
		return template;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(mariaDbDs());
		return transactionManager;
    }
	
	@Bean
	public BeanUtil beanUtil() {
		return new BeanUtil();
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