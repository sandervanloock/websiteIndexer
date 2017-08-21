package be.sandervl.config;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "crawler")
public class CrawlerProperties {

	/**
	 * @see edu.uci.ics.crawler4j.crawler.CrawlConfig#crawlStorageFolder
	 */
	private String crawlStorageFolder = "./target/crawler";

	/**
	 * The number of crawler instances (threads) used in a controller.
	 *
	 * @see CrawlController#startNonBlocking(edu.uci.ics.crawler4j.crawler.CrawlController.WebCrawlerFactory, int)
	 */
	private int numberOfCrawlers = 1;

	/**
	 * @see edu.uci.ics.crawler4j.crawler.CrawlConfig#maxPagesToFetch
	 */
	private int maxPagesToFetch = 10000;

	/**
	 * @see edu.uci.ics.crawler4j.crawler.CrawlConfig#politenessDelay
	 */
	private int politenessDelay = 10;

    public String getCrawlStorageFolder() {
        return crawlStorageFolder;
    }

    public void setCrawlStorageFolder(String crawlStorageFolder) {
        this.crawlStorageFolder = crawlStorageFolder;
    }

    public int getNumberOfCrawlers() {
        return numberOfCrawlers;
    }

    public void setNumberOfCrawlers(int numberOfCrawlers) {
        this.numberOfCrawlers = numberOfCrawlers;
    }

    public int getMaxPagesToFetch() {
        return maxPagesToFetch;
    }

    public void setMaxPagesToFetch(int maxPagesToFetch) {
        this.maxPagesToFetch = maxPagesToFetch;
    }

    public int getPolitenessDelay() {
        return politenessDelay;
    }

    public void setPolitenessDelay(int politenessDelay) {
        this.politenessDelay = politenessDelay;
    }
}
