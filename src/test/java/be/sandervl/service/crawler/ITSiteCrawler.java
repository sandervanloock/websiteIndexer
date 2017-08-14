package be.sandervl.service.crawler;

import be.sandervl.domain.Document;
import be.sandervl.domain.Site;
import be.sandervl.repository.DocumentRepository;
import be.sandervl.repository.SelectorRepository;
import be.sandervl.service.AttributeService;
import be.sandervl.service.jsoup.JsoupService;
import be.sandervl.web.rest.CrawlerResource;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.frontier.DocIDServer;
import edu.uci.ics.crawler4j.frontier.Frontier;
import edu.uci.ics.crawler4j.url.WebURL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Sander Van Loock
 */
@RunWith(MockitoJUnitRunner.class)
public class ITSiteCrawler
{
	private SiteCrawler siteCrawler;
	@Mock
	private JsoupService jsoupService; // = spy( new JsoupServiceImpl() );
	@Mock
	private DocumentRepository documentRepository;
	@Mock
	private AttributeService attributeService;
	@Mock
	private SelectorRepository selectorRepository;
	@Mock
	private CrawlerResource controller;
	@Mock
	private CrawlController crawlController;
	@Mock
	private Frontier frontier;
	@Mock
	private PageFetcher pageFetcher;
	@Mock
	private DocIDServer docIdServer;
	@Mock
	private PageFetchResult pageFetchResult;

	@Before
	public void setUp() throws Exception {
		siteCrawler = new SiteCrawler( attributeService, selectorRepository, documentRepository, controller,
		                               jsoupService );
		when( jsoupService.getDocumentFromUrl( "http://www.resto.be" ) ).thenReturn(
				Optional.of( new org.jsoup.nodes.Document( "http://www.resto.be" ) ) );
		when( documentRepository.findByUrl( anyString() ) ).thenReturn( Optional.empty() );
		when( attributeService.findByDocument( any( Document.class ) ) ).thenReturn( Collections.emptySet() );
		when( selectorRepository.findBySiteAndParentIsNull( any( Site.class ) ) ).thenReturn( Collections.emptySet() );

		WebURL url = new WebURL();
		url.setURL( "http://www.resto.be" );
		List<WebURL> urls = Arrays.asList( url, url, url );
		//frontier first gets this list of urls
		doAnswer( answer -> ( (List) answer.getArguments()[1] )
				.addAll( urls ) )
				.doAnswer( answer -> answer )
				.when( frontier ).getNextURLs( anyInt(), anyList() );
		when( pageFetchResult.getStatusCode() ).thenReturn( HttpStatus.OK.value() );
		when( pageFetchResult.getFetchedUrl() ).thenReturn( url.getURL() );
		when( pageFetchResult.fetchContent( any( Page.class ), anyInt() ) )
				.thenAnswer( answer -> {
					Page page = (Page) answer.getArguments()[0];
					page.setContentData( "<html></html>".getBytes() );
					return true;
				} );
		when( pageFetcher.fetchPage( url ) ).thenReturn( pageFetchResult );
		//always mark url as unseen
		when( docIdServer.getDocId( anyString() ) ).thenReturn( -1 );
		when( docIdServer.getNewDocID( anyString() ) ).thenReturn( 1 );

		when( crawlController.getFrontier() ).thenReturn( frontier );
		when( crawlController.isShuttingDown() ).thenReturn( false );
		when( crawlController.getPageFetcher() ).thenReturn( pageFetcher );
		when( crawlController.getDocIdServer() ).thenReturn( docIdServer );
		when( crawlController.getConfig() ).thenReturn( new CrawlConfig() );
	}

	@Test
	public void verifyCrawl() throws Exception {
		Site site = new Site();
		site.setRegex( "http://www.resto.be" );
		siteCrawler.setUp( site, new CrawlStats() );
		Thread thread = new Thread( siteCrawler, "Crawler 1" );
		siteCrawler.init( 1, crawlController );
		thread.start();
		thread.join( 5000 );
		verify( documentRepository, times( 3 ) ).save( any( Document.class ) );
	}
}
