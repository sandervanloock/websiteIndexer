package be.sandervl;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author: sander
 * @date: 3/12/2016
 */
public class TestObjectCreation {

    public static Document documentFromFileName(String fileName) throws IOException {
        ClassLoader classLoader = TestObjectCreation.class.getClassLoader();
        File input = new File(classLoader.getResource(fileName).getFile());
        return Jsoup.parse(input, "UTF-8", "http://example.com/");
    }

    public static Page pageFromUrl(String fullUrl) {
        Page mock = mock(Page.class);
        WebURL webURL = new WebURL();
        webURL.setURL(fullUrl);
        when(mock.getWebURL()).thenReturn(webURL);
        return mock;
    }
}
