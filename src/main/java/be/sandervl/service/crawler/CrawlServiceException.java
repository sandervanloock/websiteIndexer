package be.sandervl.service.crawler;

/**
 * @author Sander Van Loock
 */
public class CrawlServiceException extends Exception
{
	public CrawlServiceException( String message ) {
		super( message );
	}

	public CrawlServiceException( String message, Throwable cause ) {
		super( message, cause );
	}

}
