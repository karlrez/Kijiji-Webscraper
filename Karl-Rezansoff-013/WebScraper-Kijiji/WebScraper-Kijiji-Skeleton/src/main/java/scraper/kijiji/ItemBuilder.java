/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper.kijiji;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 *
 * @author karlr
 */
public class ItemBuilder {
    
    private static final String URL_BASE = "https://www.kijiji.ca";
    private static final String ATTRIBUTE_ID = "data-listing-id";
    private static final String ATTRIBUTE_IMAGE = "image";
    private static final String ATTRIBUTE_IMAGE_SRC = "data-src";
    private static final String ATTRIBUTE_IMAGE_NAME = "alt";
    private static final String ATTRIBUTE_PRICE = "price";
    private static final String ATTRIBUTE_TITLE = "title";
    private static final String ATTRIBUTE_LOCATION = "location";
    private static final String ATTRIBUTE_DATE = "date-posted";
    private static final String ATTRIBUTE_DESCRIPTION = "description";
    
    private Element element;
    private KijijiItem item;
    
    ItemBuilder() {
        item = new KijijiItem();
    }
    
    public ItemBuilder setElement(Element element) { 
        this.element = element;
        return this;
    }
    /*
    build method calls all the getters from BadKijijiItem and sets the values
    */
    public KijijiItem build() {
        
        item.setUrl(URL_BASE+element.getElementsByClass(ATTRIBUTE_TITLE).get(0).child(0).attr("href").trim());
        item.setId(element.attr(ATTRIBUTE_ID).trim());
        
        //setImageUrl
        Elements elements = element.getElementsByClass(ATTRIBUTE_IMAGE);
        if (elements.isEmpty()) {
            item.setImageUrl("");
        }
        String image = elements.get(0).child(0).attr(ATTRIBUTE_IMAGE_SRC).trim();
        if (image.isEmpty()) {
            image = elements.get(0).child(0).attr("src").trim();
            if (image.isEmpty()) {
                image = elements.get(0).child(0).child(1).attr(ATTRIBUTE_IMAGE_SRC).trim();
            }
        }
        item.setImageUrl(image);
        
        //setImageName
        elements = element.getElementsByClass(ATTRIBUTE_IMAGE);

        if(elements.isEmpty())
        elements = element.getElementsByClass(ATTRIBUTE_IMAGE);
        if (elements.isEmpty()) {
            item.setImageName("");
        }
        String imageName = elements.get(0).child(0).attr(ATTRIBUTE_IMAGE_NAME).trim();
        if (imageName.isEmpty()) {
            image = elements.get(0).child(0).child(1).attr(ATTRIBUTE_IMAGE_NAME).trim();

        }
        item.setImageName(imageName);
        
        //setPrice
        elements = element.getElementsByClass(ATTRIBUTE_PRICE);
        if(elements.isEmpty())
            item.setPrice("");
        else item.setPrice(elements.get(0).text().trim());
        
        //setTitle
        elements = element.getElementsByClass(ATTRIBUTE_TITLE);
        if(elements.isEmpty())
            item.setTitle("");
        else item.setTitle(elements.get(0).child(0).text().trim());
        
        //setDate
        elements = element.getElementsByClass(ATTRIBUTE_DATE);
        if(elements.isEmpty())
            item.setDate("");
        else item.setDate(elements.get(0).text().trim());
        
        //setLocation
        elements = element.getElementsByClass(ATTRIBUTE_LOCATION);
        if(elements.isEmpty())
            item.setLocation("");
        else item.setLocation(elements.get(0).childNode(0).outerHtml().trim());
        
        //setDescription
        elements = element.getElementsByClass(ATTRIBUTE_DESCRIPTION);
        if(elements.isEmpty())
            item.setDescription("");
        else item.setDescription(elements.get(0).text().trim());
        
        return item;
    }
    
}
