package be.sandervl.web.rest;

import be.sandervl.WebsiteIndexerApp;
import be.sandervl.domain.Selector;
import be.sandervl.domain.Site;
import be.sandervl.repository.SiteRepository;
import be.sandervl.service.crawler.CrawlStatus;
import be.sandervl.service.crawler.CrawlerService;
import be.sandervl.web.rest.errors.ExceptionTranslator;
import be.sandervl.web.websocket.CrawlStatsService;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.persistence.EntityManager;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Sander Van Loock
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebsiteIndexerApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CrawlerResourceIntTest
{
	private static final String WEBSOCKET_TOPIC = "/topic/crawlstat/";
	private String websocketUriPattern = "ws://localhost:%d/websocket/tracker";
	@LocalServerPort
	private int port;

	@Autowired
	private EntityManager em;

	@Autowired
	private CrawlerService crawlerService;

	@Autowired
	private CrawlStatsService crawlStatService;

	@Autowired
	private SiteRepository siteRepository;

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

	@Autowired
	private ExceptionTranslator exceptionTranslator;

	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc restCrawlerMockMvc;
	private WebSocketStompClient stompClient;
	private BlockingQueue<String> blockingQueue;
	private Site site;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks( this );
		Selector selector = SelectorResourceIntTest.createEntity( em );
		site = selector.getSite();
		CrawlerResource selectorResource = new CrawlerResource( crawlerService, siteRepository );
		this.restCrawlerMockMvc = MockMvcBuilders.standaloneSetup( selectorResource )
		                                         .setCustomArgumentResolvers( pageableArgumentResolver )
		                                         .setControllerAdvice( exceptionTranslator )
		                                         .setMessageConverters( jacksonMessageConverter ).build();
		blockingQueue = new LinkedBlockingDeque<>();
		stompClient = new WebSocketStompClient( new SockJsClient(
				Collections.singletonList( new WebSocketTransport( new StandardWebSocketClient() ) ) ) );
		StompSession session = stompClient
				.connect( String.format( websocketUriPattern, port ), new StompSessionHandlerAdapter()
				{
				} )
				.get( 1, SECONDS );
		session.subscribe( WEBSOCKET_TOPIC + site.getId(), new DefaultStompFrameHandler() );
	}

	@Test
	public void fullCrawlerTest() throws Exception {
		//start a crawler from API
		restCrawlerMockMvc.perform( post( "/api/crawler/" + site.getId() + "/start" ) )
		                  .andExpect( content().string( containsString( "/api/crawler/" + site.getId() + "/stats" ) ) );

		//get stats from crawler
		restCrawlerMockMvc.perform( get( "/api/crawler/" + site.getId() + "/stats" ) )
		                  .andExpect( status().isOk() )
		                  .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
		                  .andExpect( jsonPath( "$.numberProcessed", is( 0 ) ) );

		//get first message from web socket
		long start = System.currentTimeMillis();
		boolean found = false;
		//wait until 1 page is processed
		while ( System.currentTimeMillis() - start < 10000 ) {
			if ( JsonPath.parse( blockingQueue.poll( 1, MINUTES ) ).read( "$.numberProcessed", Integer.class ) >= 1 ) {
				found = true;
				break;

			}
		}
		assertTrue( found );

		//stop the crawler
		restCrawlerMockMvc.perform( post( "/api/crawler/" + site.getId() + "/stop" ) )
		                  .andExpect( status().isNoContent() );

		//get stats, and verify correct status
		restCrawlerMockMvc.perform( get( "/api/crawler/" + site.getId() + "/stats" ) )
		                  .andExpect( status().isOk() )
		                  .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
		                  .andExpect( jsonPath( "$.status", is( CrawlStatus.SHUTTING_DOWN.name() ) ) );

		//resume the crawler
		restCrawlerMockMvc.perform( post( "/api/crawler/" + site.getId() + "/resume" ) )
		                  .andExpect( content().string( containsString( "/api/crawler/" + site.getId() + "/stats" ) ) );

		//get stats, and verify correct status
		restCrawlerMockMvc.perform( get( "/api/crawler/" + site.getId() + "/stats" ) )
		                  .andExpect( status().isOk() )
		                  .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
		                  .andExpect( jsonPath( "$.status", is( CrawlStatus.RUNNING.name() ) ) );

		//stop the crawler
		restCrawlerMockMvc.perform( post( "/api/crawler/" + site.getId() + "/stop" ) )
		                  .andExpect( status().isNoContent() );

	}

	private class DefaultStompFrameHandler implements StompFrameHandler
	{
		@Override
		public Type getPayloadType( StompHeaders stompHeaders ) {
			return byte[].class;
		}

		@Override
		public void handleFrame( StompHeaders stompHeaders, Object o ) {
			String message = new String( (byte[]) o );
			System.out.println( message );
			blockingQueue.offer( message );
		}
	}
}