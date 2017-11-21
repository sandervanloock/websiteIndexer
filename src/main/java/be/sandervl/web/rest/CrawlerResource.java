package be.sandervl.web.rest;

import be.sandervl.domain.Site;
import be.sandervl.repository.SiteRepository;
import be.sandervl.service.crawler.CrawlServiceException;
import be.sandervl.service.crawler.CrawlStats;
import be.sandervl.service.crawler.CrawlerService;
import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;
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
	private final SiteRepository siteRepository;

	public CrawlerResource( CrawlerService crawlerService, SiteRepository siteRepository ) {
		this.crawlerService = crawlerService;
		this.siteRepository = siteRepository;
	}

	/**
	 * GET  /crawler : get all the crawler stats
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of crawler stats in body
	 */
	@GetMapping
	@Timed
	public ResponseEntity<Collection<CrawlStats>> getAllAttributes() {
		log.debug( "REST request to get all CrawlerStats" );
		Collection<CrawlStats> page = crawlerService.getAllCrawlStats();
		return new ResponseEntity<>( page, new LinkedMultiValueMap<>(), HttpStatus.OK );
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

		log.debug( "Stopping crawl for site {}", site );
		crawlerService.stopCrawler( site );
	}

	@PostMapping(value = "/{id:\\d+}/resume")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Object resumeCrawler( @PathVariable(name = "id") Long id ) {
		if ( validSiteId( id ) ) {
			return ResponseEntity.badRequest().build();
		}
		Site site = siteRepository.findOne( id );

		log.debug( "Resuming crawl for site {}", site );
		try {
			crawlerService.resumeCrawler( site );
		}
		catch ( CrawlServiceException e ) {
			log.error( "Exception occurred while resuming crawl for site {}", site, e );
		}
		return "redirect:/api/crawler/" + site.getId() + "/stats";
	}

	@PostMapping(value = "/{id:\\d+}/start")
	public Object startCrawler( @PathVariable(name = "id") Long id ) throws IOException {
		if ( validSiteId( id ) ) {
			return ResponseEntity.badRequest().build();
		}
		Site site = siteRepository.findOne( id );

		log.debug( "Starting crawl for site {}", site );
		try {
			crawlerService.startCrawler( site );
		}
		catch ( CrawlServiceException e ) {
			log.error( "Exception occurred while starting new crawl for site {}", site, e );
		}
		return "redirect:/api/crawler/" + site.getId() + "/stats";
	}

	private boolean validSiteId( @PathVariable(name = "id") Long id ) {
		return id == null;
	}

}
