package be.sandervl.service.crawler;

import be.sandervl.domain.Attribute;
import be.sandervl.domain.Document;
import be.sandervl.domain.Selector;
import be.sandervl.domain.Site;
import be.sandervl.repository.DocumentRepository;
import be.sandervl.repository.SelectorRepository;
import be.sandervl.service.AttributeService;
import be.sandervl.service.jsoup.JsoupService;
import be.sandervl.service.jsoup.JsoupServiceImpl;
import be.sandervl.web.rest.CrawlerResource;
import edu.uci.ics.crawler4j.crawler.Page;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static be.sandervl.TestObjectCreation.documentFromFileName;
import static be.sandervl.TestObjectCreation.pageFromUrl;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Sander Van Loock
 */
@RunWith(MockitoJUnitRunner.class)
public class SiteCrawlerTest {

    @Spy
    @InjectMocks
    private JsoupService jsoupService = spy(new JsoupServiceImpl());

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private AttributeService attributeService;

    @Mock
    private SelectorRepository selectorRepository;

    @Mock
    private CrawlerResource controller;

    @InjectMocks
    private SiteCrawler siteCrawler;

    //@Mock
    //private ProcessorChain processorChain;

    @Before
    public void setUp() throws Exception {
        siteCrawler = new SiteCrawler(attributeService, selectorRepository, documentRepository, controller, jsoupService);
        //initMocks(this);

        doReturn(Optional.of(mock(org.jsoup.nodes.Document.class))).when(jsoupService).getDocumentFromUrl(anyString());

        when(documentRepository.findByUrl(anyString())).thenReturn(Optional.empty());

        when(attributeService.findByDocument(any(Document.class))).thenReturn(Collections.emptySet());

        when(selectorRepository.findBySiteAndParentIsNull(any(Site.class))).thenReturn(Collections.emptySet());

        //when(processorChain.process(anyString(), any(Selector.class), any(Document.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
    }

    @Test
    public void urlWithParamsAndAnchors() throws Exception {
        String fullUrl = "http://www.google.be/my/path?abc=123#subscription";
        Page mock = pageFromUrl(fullUrl);

        siteCrawler.visit(mock);

        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
        verify(documentRepository, times(1)).save(documentCaptor.capture());

        assertEquals("/my/path?abc=123#subscription", documentCaptor.getValue().getUrl());
    }

    @Test
    public void rootUrl() throws Exception {
        String fullUrl = "http://www.google.be/";
        Page mock = pageFromUrl(fullUrl);

        siteCrawler.visit(mock);

        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
        verify(documentRepository, times(1)).save(documentCaptor.capture());

        assertEquals("/", documentCaptor.getValue().getUrl());
    }

    @Test
    public void urlWithMultiplePaths() throws Exception {
        String fullUrl = "http://www.google.be/my/path/is/so/cool";
        Page mock = pageFromUrl(fullUrl);

        siteCrawler.visit(mock);

        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
        verify(documentRepository, times(1)).save(documentCaptor.capture());

        assertEquals("/my/path/is/so/cool", documentCaptor.getValue().getUrl());
    }

    @Test
    public void fullInputSelectorTreeTest() throws Exception {
        org.jsoup.nodes.Document document = documentFromFileName("testinput2.html");

        doReturn(Optional.of(document)).when(jsoupService).getDocumentFromUrl(anyString());

        String fullUrl = "http://www.google.be/my/path/is/so/cool";
        Page mock = pageFromUrl(fullUrl);

        Selector parent = new Selector();
        parent.setName("post");
        parent.setValue(".postcontainer");
        Selector child1 = new Selector();
        child1.setName("date");
        child1.setValue(".postdate");
        child1.setParent(parent);
        Selector child2 = new Selector();
        child2.setName("username");
        child2.setValue(".postdetails .username");
        child2.setParent(parent);
        parent.setChildren(new HashSet<>(Arrays.asList(child1, child2)));

        when(selectorRepository.findBySiteAndParentIsNull(any(Site.class))).thenReturn(Arrays.asList(
            parent
        ));

        siteCrawler.visit(mock);

        ArgumentCaptor<Document> documentCaptor = ArgumentCaptor.forClass(Document.class);
        verify(documentRepository, times(1)).save(documentCaptor.capture());
        ArgumentCaptor<Attribute> attributeCaptor = ArgumentCaptor.forClass(Attribute.class);

        verify(attributeService, times(6)).save(attributeCaptor.capture());
        assertEquals(6, attributeCaptor.getAllValues().size());
    }


}
