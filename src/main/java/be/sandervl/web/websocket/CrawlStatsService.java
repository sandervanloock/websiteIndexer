package be.sandervl.web.websocket;

import be.sandervl.service.crawler.CrawlStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.Observable;
import java.util.Observer;

@Controller
public class CrawlStatsService implements Observer
{
	private static final Logger log = LoggerFactory.getLogger( CrawlStatsService.class );

	private final SimpMessageSendingOperations messagingTemplate;

	public CrawlStatsService( SimpMessageSendingOperations messagingTemplate ) {
		this.messagingTemplate = messagingTemplate;
	}

	@SubscribeMapping("/topic/getstat")
	@SendTo("/topic/crawlstat")
	public CrawlStats sendCrawlStatus( CrawlStats stats ) {
		log.debug( "Sending crawl status {}", stats );
		messagingTemplate.convertAndSend( "/topic/crawlstat/" + stats.getSite().getId(), stats );
		return stats;
	}

	@Override
	public void update( Observable o, Object arg ) {
		if ( o instanceof CrawlStats ) {
			sendCrawlStatus( (CrawlStats) o );
		}
	}
}
