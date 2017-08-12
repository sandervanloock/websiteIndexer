package be.sandervl.service.jsoup;

import be.sandervl.domain.Selector;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;
import java.util.Set;

import static be.sandervl.TestObjectCreation.documentFromFileName;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author: sander
 * @date: 21/11/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class JsoupServiceTest {

    //@Mock
    //private ProcessorChain processorChain;

    @InjectMocks
    private JsoupService jsoupService = spy(new JsoupServiceImpl());

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        doReturn(Optional.of(documentFromFileName("testinput.html"))).when(jsoupService).getDocumentFromUrl(anyString());
        //when(processorChain.process(anyString(), any(Selector.class), any(be.sandervl.neighbournet.domain.Document.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    }

    @Test
    public void getDocumentFromUrl() throws Exception {
        Optional<Document> actualDoc = jsoupService.getDocumentFromUrl("http://nl.resto.be/restaurant/antwerpen/2000-antwerpen-centrum/1345-kommilfoo/");

        assertTrue(actualDoc.isPresent());
    }

    @Test
    public void getAttributeFromType() throws Exception {
        Optional<Document> actualDoc = jsoupService.getDocumentFromUrl("http://nl.resto.be/restaurant/antwerpen/2000-antwerpen-centrum/1345-kommilfoo/");
        Selector selector = new Selector();
        selector.setValue(".h-mobile-hidden img[data-src]");
        selector.setAttribute("data-src");

        Set<String> actualElem = jsoupService.getElementsFromType(actualDoc.get(), selector, null);

        assertNotNull(actualElem);
        assertTrue(actualElem.size() > 0);
        assertTrue(actualElem.contains("https://images.resto.com/view?iid=resto.be:ff45f473-6320-4a71-b596-35c29301e4a3&context=default&width=1400&height=605"));
    }

}
