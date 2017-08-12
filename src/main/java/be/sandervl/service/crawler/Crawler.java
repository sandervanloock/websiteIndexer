package be.sandervl.service.crawler;


import be.sandervl.domain.Site;

/**
 * @author: sander
 * @date: 18/11/2016
 */
public interface Crawler {
    SiteCrawler setUp(Site site, CrawlStats stats);
}
