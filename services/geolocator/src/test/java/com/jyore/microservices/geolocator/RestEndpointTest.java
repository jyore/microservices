package com.jyore.microservices.geolocator;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=TestApplication.class)
public class RestEndpointTest {

	private MockMvc mockMvc;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Mock
	private JdbcTemplate jdbcTemplate;
	
	@InjectMocks
	private RestEndpoint endpoint = new RestEndpoint();
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(endpoint).build();
	}
	
	@Test
	public void test() throws Exception {
		
		List<Map<String,Object>> list = generateZipcodeList();
		when(jdbcTemplate.queryForList(any(String.class),any(String.class),any(Double.class))).thenReturn(list);
		
		//Successful request
		mockMvc.perform(get("/geodata/zipcodes/locate?from=12345&within=5").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$", hasSize(list.size())));
		;
		
		//Invalid data type
		mockMvc.perform(get("/geodata/zipcodes/locate?from=12345&within=BAD").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is4xxClientError())
		;
		
		//Missing params
		mockMvc.perform(get("/geodata/zipcodes/locate").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is4xxClientError())
		;
		
		//Invalid accept header
		mockMvc.perform(get("/geodata/zipcodes/locate?from=12345&within=5").accept(MediaType.APPLICATION_FORM_URLENCODED))
			.andExpect(status().is4xxClientError())
		;
		
		//Invalid method
		mockMvc.perform(post("/geodata/zipcodes/locate?from=12345&within=5").accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is4xxClientError())
		;
		
	}
	
	private List<Map<String,Object>> generateZipcodeList() {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("name", "test");
		list.add(map);
		
		return list;
	}
	
}
