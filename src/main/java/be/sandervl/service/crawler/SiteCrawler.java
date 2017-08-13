package be.sandervl.service.crawler;

import be.sandervl.domain.Attribute;
import be.sandervl.domain.Document;
import be.sandervl.domain.Selector;
import be.sandervl.domain.Site;
import be.sandervl.repository.DocumentRepository;
import be.sandervl.repository.SelectorRepository;
import be.sandervl.service.AttributeService;
import be.sandervl.service.jsoup.JsoupService;
import be.sandervl.web.rest.CrawlerResource;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
@Component
@Scope("prototype")
public class SiteCrawler extends WebCrawler
{
	private final AttributeService attributeService;
	private final SelectorRepository selectorRepository;
	private final DocumentRepository documentRepository;
	private final CrawlerResource controller;
	private final JsoupService jsoupService;

	private Pattern pattern = Pattern.compile( ".*" );
	private Site site;
	private CrawlStats stats = new CrawlStats();

	public SiteCrawler( AttributeService attributeService,
	                    SelectorRepository selectorRepository,
	                    DocumentRepository documentRepository,
	                    CrawlerResource controller,
	                    JsoupService jsoupService ) {
		this.attributeService = attributeService;
		this.selectorRepository = selectorRepository;
		this.documentRepository = documentRepository;
		this.controller = controller;
		this.jsoupService = jsoupService;
	}

	public void setUp( Site site, CrawlStats crawlStats ) {
		this.site = site;
		this.stats = crawlStats;
		this.pattern = Pattern.compile( site.getRegex() );
	}

	@Override
	public void onStart() {
		this.stats.incCrawlersRunning();
		controller.sendCrawlStatus( this.stats );
		super.onStart();
	}

	@Override
	public void onBeforeExit() {
		this.stats.decCrawlersRunning();
		controller.sendCrawlStatus( this.stats );
		super.onBeforeExit();
	}

	/**
	 * This method receives two parameters. The first parameter is the page
	 * in which we have discovered this new url and the second parameter is
	 * the new url. You should implement this function to specify whether
	 * the given url should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit( Page referringPage, WebURL url ) {
		this.stats.incNumberVisited();
		this.controller.sendCrawlStatus( this.stats );
		String href = url.getURL().toLowerCase();
		return this.pattern.matcher( href ).find();
	}

	@Override
	protected boolean shouldFollowLinksIn( WebURL url ) {
		String href = url.getURL().toLowerCase();
		return this.pattern.matcher( href ).find();
	}

	/**
	 * This function is called when a page is fetched and ready
	 * to be processed by your program.
	 */
	@Override
	public void visit( Page page ) {
		if ( !shouldVisit( page, page.getWebURL() ) ) {
			return;
		}
		String url = page.getWebURL().getURL();
		logger.debug( "Fetching URL: " + url );
		if ( this.stats != null ) {
			this.stats.incNumberProcessed();
			this.controller.sendCrawlStatus( this.stats );
		}
		jsoupService.getDocumentFromUrl( url ).ifPresent( jsoupDocument -> processDocument( page, jsoupDocument ) );
	}

	private void processDocument( Page page, org.jsoup.nodes.Document jsoupDocument ) {
		//remove protocol and domain
		String url = page.getWebURL().getURL().replaceFirst( ".*" + page.getWebURL().getDomain(), "" );
		Document document = documentRepository
				.findByUrl( url )
				.orElse( createDocument() );
		document.setSite( site );
		document.setCreated( ZonedDateTime.now() );
		document.setUrl( url );
		documentRepository.save( document );
		Set<Attribute> exitingAttributes = attributeService.findByDocument( document );
		Iterable<Selector> selectors = selectorRepository.findBySiteAndParentIsNull( site );
		selectors.forEach( selector -> processJsoupDocument( jsoupDocument, document, exitingAttributes, selector ) );
		controller.sendCrawlStatus( this.stats );
	}

	private Document createDocument() {
		this.stats.incNewDocuments();
		this.controller.sendCrawlStatus( this.stats );
		return new Document();
	}

	private Set<Attribute> processJsoupDocument( org.jsoup.nodes.Document jsoupDocument,
	                                             Document document,
	                                             Set<Attribute> exitingAttributes,
	                                             Selector selector ) {
		Set<Attribute> result = new HashSet<>();
		jsoupService.getElementsFromType( jsoupDocument, selector, document )
		            .forEach( value -> {
			            Attribute attribute = findExistingAttribute( exitingAttributes, selector, value );
			            attribute.setSelector( selector );
			            attribute.setDocument( document );
			            if ( selector.getChildren() != null ) {
				            attribute.setRelatives( selector.getChildren()
				                                            .stream()
				                                            .flatMap( rel -> processJsoupDocument( Jsoup.parse( value ),
				                                                                                   document,
				                                                                                   exitingAttributes,
				                                                                                   rel ).stream() )
				                                            .collect( Collectors.toSet() ) );
				            logger.trace( "Setting {} relatives for selector {}", attribute.getRelatives().size(),
				                          selector );
			            }
			            attribute.setValue( value );
			            if ( StringUtils.isNotBlank( attribute.getValue() ) ) {
				            logger.trace( "Saving attribute for selector {}", selector );
				            attributeService.save( attribute );
			            }
			            result.add( attribute );
		            } );
		return result;
	}

	private Attribute findExistingAttribute( Set<Attribute> exitingAttributes, Selector selector, String value ) {
		return exitingAttributes
				.stream()
				.filter( attributesFromSelectorName( selector ) )
				.filter( attributesFromValue( value ) )
				.findAny()
				.orElse( createNewAttribute() );
	}

	private Attribute createNewAttribute() {
		this.stats.incNewAttributes();
		this.controller.sendCrawlStatus( this.stats );
		return new Attribute();
	}

	private Predicate<Attribute> attributesFromValue( String value ) {
		return attribute -> attribute.getValue().equals( value );
	}

	private Predicate<Attribute> attributesFromSelectorName( Selector selector ) {
		return attr -> attr.getSelector().getName().equals( selector.getName() );
	}

	@Override
	public Object getMyLocalData() {
		return stats;
	}
}
