package be.sandervl.service.crawler;

import be.sandervl.domain.Site;

import java.util.Observable;

/**
 * @author sander
 */
public class CrawlStats extends Observable
{
    /**
     * The {@link Site} these stats belong to
     */
    private final Site site;
    /**
     * Number of pages that should be taken into account.
     */
    private final int total;
    /**
     * Number of pages processed by JSON and potentially converted to Document objects.
     */
    private int numberProcessed = 0;
    /**
     * Number of processed pages, i.e. number of visited pages. Not necessary processed by Jsoup.
     */
    private int numberVisited = 0;

    /**
     * Number of crawlers that are simultaneously processing the site.
     */
    private int crawlersRunning = 0;

    /**
     * Number of new documents created while processing the site.
     */
    private int newDocuments = 0;

    /**
     * Number of new attributes created while processing the site.
     */
    private int newAttributes = 0;

    /**
     * Status of crawling
     *
     * @see CrawlStatus
     */
    private CrawlStatus status = CrawlStatus.NOT_RUNNING;

    public CrawlStats( int total, Site site ) {
        this.total = total;
        this.site = site;
    }

    public void incNumberProcessed() {
        this.numberProcessed++;
        notifyObservers();
    }

    public void incNumberVisited() {
        this.numberVisited++;
        notifyObservers();
    }

    public void incCrawlersRunning() {
        this.crawlersRunning++;
        if (this.crawlersRunning > 0) {
            this.status = CrawlStatus.RUNNING;
        }
        notifyObservers();
    }

    public void incNewDocuments() {
        this.newDocuments++;
        notifyObservers();
    }

    public void incNewAttributes() {
        this.newAttributes++;
        notifyObservers();
    }

    public void decCrawlersRunning() {
        this.crawlersRunning--;
        this.status = CrawlStatus.SHUTTING_DOWN;
        if (this.crawlersRunning <= 0) {
            this.status = CrawlStatus.NOT_RUNNING;
        }
        notifyObservers();
    }

    public void updateStatus( CrawlStatus status ) {
        this.status = status;
        notifyObservers();
    }

    public Site getSite() {
        return site;
    }

    //Getters and setters needed for marshaling

    public int getNumberProcessed() {
        return numberProcessed;
    }

    public int getTotal() {
        return total;
    }

    public int getNumberVisited() {
        return numberVisited;
    }

    public int getCrawlersRunning() {
        return crawlersRunning;
    }

    public int getNewDocuments() {
        return newDocuments;
    }

    public int getNewAttributes() {
        return newAttributes;
    }

    public CrawlStatus getStatus() {
        return status;
    }

    public void setNumberProcessed( int numberProcessed ) {
        this.numberProcessed = numberProcessed;
    }

    public void setNumberVisited( int numberVisited ) {
        this.numberVisited = numberVisited;
    }

    public void setCrawlersRunning( int crawlersRunning ) {
        this.crawlersRunning = crawlersRunning;
    }

    public void setNewDocuments( int newDocuments ) {
        this.newDocuments = newDocuments;
    }

    public void setNewAttributes( int newAttributes ) {
        this.newAttributes = newAttributes;
    }

    public void setStatus( CrawlStatus status ) {
        this.status = status;
    }

    @Override
    public void notifyObservers() {
        super.setChanged();
        super.notifyObservers();
    }

    @Override
    public String toString() {
        return "CrawlStats{" +
                "siteId=" + site.getId() +
                ", numberProcessed=" + numberProcessed +
                ", total=" + total +
                ", crawlersRunning=" + crawlersRunning +
                '}';
    }
}
