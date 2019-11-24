package com.aks.mot.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/* root-context.xml
 * 
 * */


@Configuration
@ComponentScan(basePackages= {"com.aks.mot"}, excludeFilters = @ComponentScan.Filter(Controller.class))
@MapperScan(basePackages= {"com.aks.mot"})
@PropertySource("classpath:mot.properties")
@EnableTransactionManagement
public class RootConfig{
	
	@Autowired
	ApplicationContext applicationContext;
	
	/* Database 선언
	 * */
	@Bean
	public DataSource dataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		/* log4jdbc 설정 전 */ 
		hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
		hikariConfig.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/mot");
		
		/* log4jdbc 설정 후*/
		//hikariConfig.setDriverClassName("net.sf.log4jdbc.sql.jdbcapi.DriverSpy");
		//hikariConfig.setJdbcUrl("jdbc:log4jdbc:mysql://127.0.0.1:3306/mot");
		hikariConfig.setUsername("dbsvc");
		hikariConfig.setPassword("dbsvc1234!");
		
		HikariDataSource dataSource = new HikariDataSource(hikariConfig);
		
		return dataSource;
		
	}
	
	/* MyBatis 설정 */
	@Bean
	public SqlSessionFactory sqlSessionFactoryBean() throws Exception {
		
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(dataSource());
		sqlSessionFactory.setConfigLocation(applicationContext.getResource("classpath:/mybatis/mybatis-config.xml"));
		sqlSessionFactory.setMapperLocations(applicationContext.getResources("classpath:/mybatis/sql/*.xml"));
		return (SqlSessionFactory) sqlSessionFactory.getObject();
		
	}
	
	/* SqlSession 설정 */
	
	@Bean(destroyMethod = "clearCache")
    public SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
	
	/* Transaction 설정 */
	@Bean
	public DataSourceTransactionManager transactionManager() {

        return new DataSourceTransactionManager(dataSource());
    }	
	
	
	
	
	
}