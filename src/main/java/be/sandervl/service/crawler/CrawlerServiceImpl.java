package be.sandervl.service.crawler;

import be.sandervl.config.CrawlerProperties;
import be.sandervl.domain.Site;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author: sander
 * @date: 15/11/2016
 */
@Service
public class CrawlerServiceImpl implements CrawlerService {

    private static Logger log = LoggerFactory.getLogger(CrawlerService.class);

    @Autowired
    private CrawlerProperties crawlerProperties;

    @Autowired
    private ApplicationContext context;

    private Map<Site, CrawlController> controllers = new HashMap<>();
    private Map<Site, CrawlStats> statsMap = new HashMap<>();

    @Override
    public void startCrawler(Site site) throws Exception {
        final CrawlController controller = controllers.computeIfAbsent(site, this::createController);
        if (controller.isFinished()) {
            startController(site, controller);
        }
    }

    private void startController(Site site, CrawlController controller) {
        // shard stats object for multiple crawlers, crawler is initialized multiple times
        CrawlStats stats = new CrawlStats();
        stats.setTotal(controller.getConfig().getMaxPagesToFetch());
        statsMap.put(site, stats);
        controller.startNonBlocking(() -> context.getBean(Crawler.class).setUp(site, stats),
            crawlerProperties.getNumberOfCrawlers());
    }

    @Override
    public void stopCrawler(Site site) {
        controllers.computeIfPresent(site, (sdlkk, controller) -> {
            controller.shutdown();
            return controller;
        });
        statsMap.computeIfPresent(site, (sqdfkj, stats) -> {
            stats.setStatus(CrawlStatus.SHUTTING_DOWN);
            return stats;
        });
        controllers.remove(site);
    }

    @Override
    public Optional<CrawlStats> getStats(Site site) {
        return this.statsMap.containsKey(site) ? Optional.of(this.statsMap.get(site)) : Optional.empty();
    }

    private CrawlController createController(Site site) {
        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlerProperties.getCrawlStorageFolder());
        config.setMaxPagesToFetch(crawlerProperties.getMaxPagesToFetch());
        config.setPolitenessDelay(crawlerProperties.getPolitenessDelay());
        config.setResumableCrawling(true);

            /*
             * Instantiate the controller for this crawl.
             */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        try {
            CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
            /*
             * For each crawl, you need to add some seed urls. These are the first
             * URLs that are fetched and then the crawler starts following links
             * which are found in these pages
             */
            controller.addSeed(site.getSeed());
            startController(site, controller);
            return controller;
        } catch (Exception e) {
            log.error("Error creating site controller, given config {} is invalid", config);
            return null;
        }
    }

}
