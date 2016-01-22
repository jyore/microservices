package com.jyore.microservices.geolocator;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/geodata")
public class RestEndpoint {

	private static final String ZIPCODE_LOCATOR = "select distinct z2.name,z2.stabb as state,z2.zip,ST_Distance(ST_CENTROID(z.geom),ST_CENTROID(z2.geom)) * 100 as distance from geodata.zipcodes z left join geodata.zipcodes z2 on ST_DWithin(z.geom,z2.geom,?) where z.zip = ? order by distance";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@RequestMapping(value="/zipcodes/locate", method=RequestMethod.GET, produces="application/json")
	public Object findZipsInProximity(@RequestParam(value="from",required=true) String from, @RequestParam(value="within",required=true) Double within) {
		
		List<Map<String,Object>> result = jdbcTemplate.queryForList(ZIPCODE_LOCATOR, within/100, from);
		
		return result;
	}
}
