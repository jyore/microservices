package com.jyore.microservices.geolocator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/geodata")
public class RestEndpoint {
	
	private static final String ZIPCODE_LOCATOR = "select distinct z.name,z.stabb as state,z.zip,ST_DISTANCE(c.geom,c2.geom) * 100 as distance from geodata.centroids c LEFT JOIN geodata.centroids c2 ON ST_DWithin(c.geom,c2.geom, ?) INNER JOIN geodata.zipcodes z ON (c2.lookup = z.zip) WHERE c.lookup = ? ORDER BY distance";
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@RequestMapping(value="/zipcodes/locate", method=RequestMethod.GET, produces="application/json")
	public Object locateZipcodesWithinRadius(@RequestParam(value="from",required=true) String from, @RequestParam(value="within",required=true) Double within) {
		return jdbcTemplate.queryForList(ZIPCODE_LOCATOR, within/100.0, from);
	}
}
