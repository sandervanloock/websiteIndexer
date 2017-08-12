package be.sandervl.service.jsoup;

import org.jsoup.Jsoup;

/**
 * @author: sander
 * @date: 21/11/2016
 */
public class StringUtils {
    public static String removeHtmlTags(String attributeValueWithHtml) {
        return Jsoup.parse(attributeValueWithHtml).text();
    }

}
