package com.aks.mot.sample;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aks.mot.com.config.ApplicationConfig;

import lombok.Setter;
import lombok.extern.java.Log;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {ApplicationConfig.class})
@Log
public class SampleTests {
	
	@Setter(onMethod_ = { @Autowired })
	private Restaurant restaurant;
	
	@Test
	public void testExist() {
		
		assertNotNull(restaurant);
		
		//log.info(restaurant);
		log.info("--------------------------");
		//log.info(restaurant.getChef());
	}
}