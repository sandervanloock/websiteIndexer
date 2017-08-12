package be.sandervl.service.jsoup;

import be.sandervl.domain.Selector;
import org.jsoup.nodes.Document;

import java.util.Optional;
import java.util.Set;

/**
 * @author: sander
 * @date: 21/11/2016
 */
public interface JsoupService {

    Optional<Document> getDocumentFromUrl(String url);

    /**
     * Select from a Jsoup document the values from a given CSS-selector and attribute.  If the given
     * attribute value is blank, it will be discarded and the value of the CSS-selector will be returned.
     * <p>
     * If innerHtml is set to true, only the inner html of the CSS-selector will be returned.
     */
    Set<String> getElementsFromType(Document jsoupDocument, Selector selector, be.sandervl.domain.Document document);

}
