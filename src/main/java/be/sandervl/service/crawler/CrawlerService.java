package be.sandervl.service.crawler;

import be.sandervl.domain.Site;
import edu.uci.ics.crawler4j.crawler.CrawlController;

import java.util.Collection;
import java.util.Optional;

/**
 * Service for interacting with the crawlers and controllers for a site.
 *
 * @author sander
 */
public interface CrawlerService
{
	/**
	 * starts a non-blocking crawl for given site.
	 *
	 * @see CrawlerService#startCrawler(Site, boolean)
	 */
	default CrawlController startCrawler( Site site ) throws CrawlServiceException {
		return startCrawler( site, true );
	}

	/**
	 * resumes a non-blocking crawl for given site.
	 *
	 * @see CrawlerService#resumeCrawler(Site, boolean)
	 */
	default CrawlController resumeCrawler( Site site ) throws CrawlServiceException {
		return resumeCrawler( site, true );
	}

	Collection<CrawlStats> getAllCrawlStats();

	/**
	 * See if there is already a controller running for this site.
	 * If not already present, initialize a new controller and put it in the map.
	 * <p/>
	 * If the controller is finished, restart it.
	 * <p/>
	 * The indexed of Frontier are deleted when starting the crawl.  The new indexes are marked as resumable
	 * ({@see edu.uci.ics.crawler4j.crawler.CrawlConfig#resumableCrawling}) so calling
	 * {@link CrawlerService#resumeCrawler} can be invoked when pauzed.
	 *
	 * @param site        for initializing the crawler.
	 * @param nonBlocking indicating weather the controller should run in a different thread (if true) or blocks execution in the current thread (if false)
	 * @return the started controller for the site.
	 * @throws CrawlServiceException when given site is null or has no seed.
	 */
	CrawlController startCrawler( Site site, boolean nonBlocking ) throws CrawlServiceException;

	/**
	 * Same bevahiour as {@link CrawlerService#startCrawler(Site, boolean)} except the Frontier crawler storage folder
	 * is not deleted prior to starting the crawler.
	 *
	 * @throws CrawlServiceException when given site is null or has no seed.
	 * @see CrawlerService#startCrawler(Site, boolean)
	 */
	CrawlController resumeCrawler( Site site, boolean nonBlocking ) throws CrawlServiceException;

	/**
	 * Delete the crawler storage folder as configured in the {@code CrawlerProperties}.
	 *
	 * @return true if and only if the deletion of the folder succeeded.
	 * @throws CrawlServiceException When the deletion of the folder fails due to an invalid path or insufficient access rights.
	 * @see be.sandervl.config.CrawlerProperties#crawlStorageFolder
	 */
	void deleteOldCrawlerStoragePath() throws CrawlServiceException;

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
