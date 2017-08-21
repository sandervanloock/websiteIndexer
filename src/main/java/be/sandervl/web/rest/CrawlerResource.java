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
	private static Logger log = LoggerFactory.getLogger( CrawlerResource.class );

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

	@DeleteMapping(value = "/{id:\\d+}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void stopCrawler( @PathVariable(name = "id") Long id ) {
		Site site = siteRepository.findOne( id );
		crawlerService.stopCrawler( site );
	}

	@PostMapping(value = "/{id:\\d+}")
	public Object startCrawler( @PathVariable(name = "id") Long id ) throws IOException {
		if ( id == null ) {
			return ResponseEntity.badRequest().build();
		}
		Site site = siteRepository.findOne( id );

		log.debug( "Starting crawl for site {}", site );
		try {
			crawlerService.startCrawler( site );
			crawlerService.addObserver( site, crawlStatsService );
		}
		catch ( CrawlServiceException e ) {
			log.error( "Exception occurred while starting new crawl for site {}", site, e );
		}
		return "redirect:/api/crawler/" + site.getId() + "/stats";
	}

}
