package be.sandervl.web.rest;

import be.sandervl.domain.Site;
import be.sandervl.repository.SiteRepository;
import be.sandervl.service.crawler.CrawlerService;
import be.sandervl.web.websocket.CrawlStatsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * @author Sander Van Loock
 */
@RunWith(MockitoJUnitRunner.class)
public class CrawlerResourceTest
{
	private CrawlerResource resource;
	@Mock
	private CrawlerService crawlerService;
	@Mock
	private CrawlStatsService crawlStatsService;
	@Mock
	private SiteRepository siteRepository;

	@Before
	public void setUp() throws Exception {
		resource = new CrawlerResource( crawlerService, crawlStatsService, siteRepository );
	}

	@Test
	public void startCrawler() throws Exception {
		Site site = new Site();
		when( siteRepository.findOne( 1L ) ).thenReturn( site );
		resource.startCrawler( 1L );

		verify( crawlerService, times( 1 ) ).startCrawler( site );
		verify( crawlerService, times( 1 ) ).addObserver( site, crawlStatsService );
	}
}