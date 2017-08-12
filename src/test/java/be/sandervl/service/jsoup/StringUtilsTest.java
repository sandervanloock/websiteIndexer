package be.sandervl.service.jsoup;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author: sander
 * @date: 21/11/2016
 */
public class StringUtilsTest {
    @Test
    public void removeHtmlTags() throws Exception {
        String result = StringUtils.removeHtmlTags("  <span>Emile Braunplein</span>\n" +
            "                                        <span></span>\n" +
            "                                        <span class=\"m-detail-info-adres__comma\">1</span>\n" +
            "                                        <span>\n" +
            "                                            <a class=\"h-bold cwb-city-inline cwb-city-link\" href=\"http://nl.resto.be/restaurant/gent/9000-gent\">9000 GENT</a>\n" +
            "                                        </span>");
        assertEquals("Emile Braunplein 1 9000 GENT", result);
    }

}
