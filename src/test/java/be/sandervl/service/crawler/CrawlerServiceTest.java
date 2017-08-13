package be.sandervl.service.crawler;

import be.sandervl.config.CrawlerProperties;
import be.sandervl.domain.Site;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Sander Van Loock
 */
@RunWith(MockitoJUnitRunner.class)
public class CrawlerServiceTest
{
	private CrawlerService crawlerService;
	@Mock
	private ApplicationContext context;
	@Mock
	private SiteCrawler siteCrawler;

	@Before
	public void setUp() throws Exception {
		CrawlerProperties properties = new CrawlerProperties();
		properties.setNumberOfCrawlers( 1 );
		when( context.getBean( SiteCrawler.class ) ).thenReturn( siteCrawler );
		crawlerService = new CrawlerServiceImpl( properties, context );
	}

	@Test(expected = CrawlServiceException.class)
	public void siteWithoutSeedThrowsException() throws Exception {
		Site site = new Site();
		crawlerService.startCrawler( site );
	}

	@Test
	public void startAlreadyStartedCrawler() throws Exception {
		Site site = new Site();
		site.setSeed( "www.google.be" );
		CrawlController actual = crawlerService.startCrawler( site );
		CrawlController actual2 = crawlerService.startCrawler( site );
		assertSame( actual, actual2 );
		verify( context, times( 1 ) ).getBean( SiteCrawler.class );
	}

	@Test(expected = CrawlServiceException.class)
	public void nullSite() throws Exception {
		crawlerService.startCrawler( null );
	}

	@Test(expected = CrawlServiceException.class)
	public void unableToCreateController() throws Exception {
		CrawlerProperties properties = new CrawlerProperties();
		properties.setCrawlStorageFolder( null );
		CrawlerService invalidService = new CrawlerServiceImpl( properties, context );
		Site site = new Site();
		site.setSeed( "www.google.be" );
		invalidService.startCrawler( site );
	}

	@Test
	public void stopCrawlerSetsStatus() throws Exception {
		Site site = new Site();
		site.setSeed( "www.google.be" );
		crawlerService.startCrawler( site );
		boolean actual = crawlerService.stopCrawler( site );
		Optional<CrawlStats> stats = crawlerService.getStats( site );

		assertTrue( stats.isPresent() );
		assertEquals( CrawlStatus.SHUTTING_DOWN, stats.get().getStatus() );
		assertTrue( actual );
	}

	@Test
	public void stopUnregisteredCrawler() throws Exception {
		boolean actual = crawlerService.stopCrawler( new Site() );
		assertFalse( actual );
	}

	@Test
	public void stopCrawlerWithNull() throws Exception {
		boolean actual = crawlerService.stopCrawler( null );
		assertFalse( actual );
	}

	@Test
	public void getStatsWithNullReturnsEmpty() throws Exception {
		Optional<CrawlStats> stats = crawlerService.getStats( null );
		assertFalse( stats.isPresent() );
	}

	@Test
	public void getStatsWithUnregistersReturnsEmpty() throws Exception {
		Optional<CrawlStats> stats = crawlerService.getStats( new Site() );
		assertFalse( stats.isPresent() );
	}

	@Test
	public void getStatsWithInitialData() throws Exception {
		Site site = new Site();
		site.setSeed( "www.google.be" );
		crawlerService.startCrawler( site );
		Optional<CrawlStats> stats = crawlerService.getStats( site );
		assertTrue( stats.isPresent() );
		assertEquals( CrawlStatus.NOT_RUNNING, stats.get().getStatus() );
		assertEquals( 0, stats.get().getCrawlersRunning() );
		assertEquals( 0, stats.get().getNumberProcessed() );
		assertEquals( 0, stats.get().getNumberVisited() );
		assertEquals( 0, stats.get().getNewAttributes() );
		assertEquals( 0, stats.get().getNewDocuments() );
		assertEquals( 100, stats.get().getTotal() );
	}
}