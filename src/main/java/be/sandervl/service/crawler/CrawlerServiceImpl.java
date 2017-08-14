package be.sandervl.service.crawler;

import be.sandervl.config.CrawlerProperties;
import be.sandervl.domain.Site;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author sander
 */
@Service
public class CrawlerServiceImpl implements CrawlerService
{
	public static final UrlValidator URL_VALIDATOR = new UrlValidator();
	private static Logger log = LoggerFactory.getLogger( CrawlerService.class );

	private final CrawlerProperties crawlerProperties;
	private final ApplicationContext context;

	/**
	 * Map of sites that have running controllers
	 */
	private Map<Site, CrawlController> controllers = new HashMap<>();

	/**
	 * Map of sites with the crawl stats
	 */
	private Map<Site, CrawlStats> statsMap = new HashMap<>();

	public CrawlerServiceImpl( CrawlerProperties crawlerProperties, ApplicationContext context ) {
		this.crawlerProperties = crawlerProperties;
		this.context = context;
	}

	@Override
	public CrawlController startCrawler( Site site, boolean nonBlocking ) throws CrawlServiceException {
		if ( site == null ) {
			throw new CrawlServiceException( "Given Site should not be null" );
		}
		if ( org.apache.commons.lang3.StringUtils.isBlank( site.getSeed() ) ||
				!URL_VALIDATOR.isValid( site.getSeed() ) ) {
			throw new CrawlServiceException( String.format( "Given Site %s should have a valid seed", site ) );
		}
		CrawlController controller = controllers.get( site );
		if ( controller == null ) {
			controller = createController( site, nonBlocking );
			controllers.put( site, controller );
		}
		if ( controller.isFinished() ) {
			startController( site, controller, nonBlocking );
		}
		return controller;
	}

	@Override
	public boolean stopCrawler( Site site ) {
		controllers.computeIfPresent( site, ( old, controller ) -> {
			controller.shutdown();
			return controller;
		} );
		statsMap.computeIfPresent( site, ( old, stats ) -> {
			stats.setStatus( CrawlStatus.SHUTTING_DOWN );
			return stats;
		} );
		return controllers.remove( site ) != null;
	}

	@Override
	public Optional<CrawlStats> getStats( Site site ) {
		return this.statsMap.containsKey( site ) ?
				Optional.of( this.statsMap.get( site ) ) :
				Optional.empty();
	}

	private CrawlController createController( Site site, boolean nonBlocking ) throws CrawlServiceException {
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder( crawlerProperties.getCrawlStorageFolder() );
		config.setMaxPagesToFetch( crawlerProperties.getMaxPagesToFetch() );
		config.setPolitenessDelay( crawlerProperties.getPolitenessDelay() );
		config.setResumableCrawling( true );

		PageFetcher pageFetcher = new PageFetcher( config );
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer( robotstxtConfig, pageFetcher );
		try {
			CrawlController controller = new CrawlController( config, pageFetcher, robotstxtServer );
			controller.addSeed( site.getSeed() );
			startController( site, controller, nonBlocking );
			return controller;
		}
		catch ( Exception e ) {
			throw new CrawlServiceException(
					String.format( "Unable to create a new CrawlController with site %s", site ), e );
		}
	}

	private void startController( Site site, CrawlController controller, boolean nonBlocking ) {
		// share stats object for multiple crawlers, crawler is initialized multiple times
		CrawlStats stats = new CrawlStats();
		stats.setTotal( crawlerProperties.getMaxPagesToFetch() );
		statsMap.put( site, stats );
		CrawlController.WebCrawlerFactory<SiteCrawler> factory =
				() -> {
					log.info( "Creating new crawler for site {}", site );
					SiteCrawler crawler = context.getBean( SiteCrawler.class );
					crawler.setUp( site, stats );
					return crawler;
				};
		if ( nonBlocking ) {
			controller.startNonBlocking( factory, crawlerProperties.getNumberOfCrawlers() );
		}
		else {
			controller.start( factory, crawlerProperties.getNumberOfCrawlers() );
		}
	}

}
