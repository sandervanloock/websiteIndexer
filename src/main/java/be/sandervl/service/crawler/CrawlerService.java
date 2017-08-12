package be.sandervl.service.crawler;

import be.sandervl.domain.Site;

import java.util.Optional;

/**
 * @author: sander
 * @date: 15/11/2016
 */
public interface CrawlerService {

    void startCrawler(Site site) throws Exception;

    void stopCrawler(Site site);

    Optional<CrawlStats> getStats(Site site);
}
