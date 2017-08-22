package be.sandervl.web.rest;

import be.sandervl.domain.Site;
import be.sandervl.repository.SiteRepository;
import be.sandervl.service.crawler.CrawlServiceException;
import be.sandervl.service.crawler.CrawlStats;
import be.sandervl.service.crawler.CrawlerService;
import be.sandervl.web.websocket.CrawlStatsService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

/**
 * @author: sander
 * @date: 17/11/2016
 */
@RestController
@RequestMapping("/api/crawler")
public class CrawlerResource
{
	private static Logger LOG = LoggerFactory.getLogger( CrawlerResource.class );

	private final CrawlerService crawlerService;
	private final CrawlStatsService crawlStatsService;
	private final SiteRepository siteRepository;

	public CrawlerResource( CrawlerService crawlerService,
	                        CrawlStatsService crawlStatsService, SiteRepository siteRepository ) {
		this.crawlerService = crawlerService;
		this.crawlStatsService = crawlStatsService;
		this.siteRepository = siteRepository;
	}

	@GetMapping(value = "/{id:\\d+}/stats")
	public ResponseEntity getCrawler( @PathVariable(name = "id") Long id ) {
		Site site = siteRepository.findOne( id );
		Optional<CrawlStats> stats = crawlerService.getStats( site );
		return ResponseUtil.wrapOrNotFound( stats );
	}

	@PostMapping(value = "/{id:\\d+}/stop")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void stopCrawler( @PathVariable(name = "id") Long id ) {
		Site site = siteRepository.findOne( id );

		LOG.debug( "Stopping crawl for site {}", site );
		crawlerService.stopCrawler( site );
	}

	@PostMapping(value = "/{id:\\d+}/resume")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Object resumeCrawler( @PathVariable(name = "id") Long id ) {
		if ( validSiteId( id ) ) {
			return ResponseEntity.badRequest().build();
		}
		Site site = siteRepository.findOne( id );

		LOG.debug( "Resuming crawl for site {}", site );
		try {
			crawlerService.resumeCrawler( site );
		}
		catch ( CrawlServiceException e ) {
			LOG.error( "Exception occurred while resuming crawl for site {}", site, e );
		}
		return "redirect:/api/crawler/" + site.getId() + "/stats";
	}

	@PostMapping(value = "/{id:\\d+}/start")
	public Object startCrawler( @PathVariable(name = "id") Long id ) throws IOException {
		if ( validSiteId( id ) ) {
			return ResponseEntity.badRequest().build();
		}
		Site site = siteRepository.findOne( id );

		LOG.debug( "Starting crawl for site {}", site );
		try {
			crawlerService.startCrawler( site );
		}
		catch ( CrawlServiceException e ) {
			LOG.error( "Exception occurred while starting new crawl for site {}", site, e );
		}
		return "redirect:/api/crawler/" + site.getId() + "/stats";
	}

	private boolean validSiteId( @PathVariable(name = "id") Long id ) {
		if ( id == null ) {
			return true;
		}
		return false;
	}

}
