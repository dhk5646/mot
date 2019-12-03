package com.aks.mot;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aks.mot.com.config.ApplicationConfig;

import lombok.Setter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {ApplicationConfig.class})

public class DataSourceTests {
	
	@Setter(onMethod_ = { @Autowired})
	private DataSource dataSource;
	@Test
	public void testConnection() {
		
//		try(Connection con = dataSource.getConnection()){
//			
//			System.out.println(con);
//			
//		} catch (Exception e) {
//			System.out.println(e);
//		}
	}
}
