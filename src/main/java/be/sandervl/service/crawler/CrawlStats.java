package be.sandervl.service.crawler;


/**
 * @author: sander
 * @date: 15/11/2016
 */
public class CrawlStats {

    private int numberProcessed = 0;
    private int total = 0;
    private int numberVisited = 0;
    private int crawlersRunning = 0;
    private int newDocuments = 0;
    private int newAttributes = 0;
    private CrawlStatus status = CrawlStatus.NOT_RUNNING;

    public void incNumberProcessed() {
        this.numberProcessed++;
    }

    public void incNumberVisited() {
        this.numberVisited++;
    }

    public void incCrawlersRunning() {
        this.crawlersRunning++;
        if (this.crawlersRunning > 0) {
            this.status = CrawlStatus.RUNNING;
        }
    }

    public void incNewDocuments() {
        this.newDocuments++;
    }

    public void incNewAttributes() {
        this.newAttributes++;
    }

    public void decCrawlersRunning() {
        this.crawlersRunning--;
        this.status = CrawlStatus.SHUTTING_DOWN;
        if (this.crawlersRunning <= 0) {
            this.status = CrawlStatus.NOT_RUNNING;
        }
    }

    public int getNumberProcessed() {
        return numberProcessed;
    }

    public void setNumberProcessed(int numberProcessed) {
        this.numberProcessed = numberProcessed;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNumberVisited() {
        return numberVisited;
    }

    public void setNumberVisited(int numberVisited) {
        this.numberVisited = numberVisited;
    }

    public int getCrawlersRunning() {
        return crawlersRunning;
    }

    public void setCrawlersRunning(int crawlersRunning) {
        this.crawlersRunning = crawlersRunning;
    }

    public int getNewDocuments() {
        return newDocuments;
    }

    public void setNewDocuments(int newDocuments) {
        this.newDocuments = newDocuments;
    }

    public int getNewAttributes() {
        return newAttributes;
    }

    public void setNewAttributes(int newAttributes) {
        this.newAttributes = newAttributes;
    }

    public CrawlStatus getStatus() {
        return status;
    }

    public void setStatus(CrawlStatus status) {
        this.status = status;
    }
}
