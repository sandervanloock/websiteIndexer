package be.sandervl.service.crawler;

import be.sandervl.WebsiteIndexerApp;
import be.sandervl.domain.Site;
import be.sandervl.repository.DocumentRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author Sander Van Loock
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebsiteIndexerApp.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
		"crawler.maxPagesToFetch=1"
})
public class CrawlerServiceIntTest
{
	@Autowired
	private CrawlerService crawlerService;
	@Autowired
	private DocumentRepository documentRepository;

	@Before
	public void setUp() throws Exception {

	}

	@Test
	@Ignore
	public void homePageShouldBeCrawled() throws Exception {
		Site site = new Site();
		site.setSeed( "http://www.resto.be" );
		crawlerService.startCrawler( site, false );
		assertEquals( 1, documentRepository.findAll().size() );
	}
}
