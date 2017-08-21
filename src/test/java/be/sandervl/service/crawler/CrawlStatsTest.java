package be.sandervl.service.crawler;

import be.sandervl.domain.Site;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Sander Van Loock
 */
public class CrawlStatsTest
{
	private ObjectMapper objectMapper;

	@Before
	public void setUp() throws Exception {
		objectMapper = new ObjectMapper();
	}

	@Test
	public void testMarshaling() throws Exception {
		String json = objectMapper.writeValueAsString( new CrawlStats( 500, new Site() ) );
		assertEquals( new Integer( 500 ), JsonPath.parse( json ).read( "$.total", Integer.class ) );
	}
}