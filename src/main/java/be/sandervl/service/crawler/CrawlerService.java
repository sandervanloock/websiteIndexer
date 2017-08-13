package be.sandervl.service.crawler;

import be.sandervl.domain.Site;
import edu.uci.ics.crawler4j.crawler.CrawlController;

import java.util.Optional;

/**
 * Service for interacting with the crawlers and controllers for a site.
 *
 * @author sander
 */
public interface CrawlerService
{

	/**
	 * See if there is already a controller running for this site.
	 * If not already present, initialize a new controller and put it in the map.
	 * <p/>
	 * If the controller is finished, restart it.
	 *
	 * @param site for initializing the crawler.
	 * @return the started controller for the site.
	 * @throws CrawlServiceException when given site is null or has no seed
	 */
	CrawlController startCrawler( Site site ) throws CrawlServiceException;

	/**
	 * Look for the controller that is running for the given site and invoke the {@link CrawlController#shutdown} method.
	 * The {@code CrawlStats} are updated with the {@link CrawlStatus#SHUTTING_DOWN} status.
	 *
	 * @param site that identifies the crawlers to stop
	 * @return true is a controller was removed that was identified by this site
	 */
	boolean stopCrawler( Site site );

	/**
	 * Get a {@code CrawlStats} object for the given site or {@code Optional#empty} is there is no controller running
	 * for the given site.
	 *
	 * @param site identifying the controller to get stats
	 * @return an optional {@code CrawlStats} for the site
	 */
	Optional<CrawlStats> getStats( Site site );
}
