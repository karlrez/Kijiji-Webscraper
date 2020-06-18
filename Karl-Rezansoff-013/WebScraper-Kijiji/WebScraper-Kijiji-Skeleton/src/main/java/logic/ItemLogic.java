/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;


import dal.ItemDAL;
import entity.Item;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author karlr
 */
public class ItemLogic extends GenericLogic<Item, ItemDAL> {
    
    public static final String DESCRIPTION = "description";
    public static final String CATEGORY_ID = "categoryId";
    public static final String IMAGE_ID = "imageId";
    public static final String LOCATION = "location";
    public static final String PRICE = "price";
    public static final String TITLE = "title";
    public static final String DATE = "date";
    public static final String URL = "url";
    public static final String ID = "id";
    
    public ItemLogic() {
        super (new ItemDAL());
    }
    
    @Override
    public List<Item> getAll(){
        return get(()->dao().findAll());
    }
    
    @Override
    public Item getWithId(int id) {
        return get(()->dao().findByID(id));
    }
    
    public List<Item> getWithPrice(BigDecimal price) {
        return get(()->dao().findByPrice(price));
    }
    
    public List<Item> getWithTitle(String title) {
        return get(()->dao().findByTitle(title));
    }
    
    public List<Item> getWithDate(String date) {
        return get(()->dao().findByDate(date));
    }
    
    public List<Item> getWithLocation(String location) {
        return get(()->dao().findByLocation(location));
    }
    
    public List<Item> getWithDescription(String description) {
        return get(()->dao().findByDescription(description));
    }
    
    public Item getWithUrl(String url) {
        return get(()->dao().findByUrl(url));
    }
    
    public List<Item> getWithCategory(int categoryId) {
        return get(()->dao().findByCategory(categoryId));
    }
    
    @Override
    public List<Item> search(String search) {
        return get(()->dao().findContaining(search));
    }
    
    @Override
    public Item createEntity(Map<String, String[]> parameterMap) {
        Item item = new Item();
        
        if (parameterMap.containsKey(ID)){
            item.setId(Integer.parseInt(parameterMap.get(ID)[0]));
        }
        
        item.setDescription(parameterMap.get(DESCRIPTION)[0]);
        item.setLocation(parameterMap.get(LOCATION)[0]);
        
        //need to reformat price
        //reference: https://stackoverflow.com/questions/6016501/parsing-a-currency-string-in-java
        String price = parameterMap.get(PRICE)[0].replaceAll("[^\\d.]+", "");
        try {
            item.setPrice(new BigDecimal(price));
        } //if theres a string like "please contact" price is 0
        catch (Exception e) {
            item.setPrice(new BigDecimal("0"));
        }

        item.setTitle(parameterMap.get(TITLE)[0]);
        item.setUrl(parameterMap.get(URL)[0]);
        
        // Will set Date to current date if exception is thrown
        try {
            item.setDate(new SimpleDateFormat("dd/mm/yyyy").parse(parameterMap.get(DATE)[0]));
        }
        catch (ParseException ex) {
            item.setDate(new Date());
        }
        return item;
    } 
    
    @Override
    public List<String> getColumnNames(){
        return Arrays.asList("ID", "Image_ID", "Category_ID", "Price",
                "Title", "Date", "Location", "Description", "URL");
    }
    
    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList(ID, IMAGE_ID, CATEGORY_ID, PRICE,
                TITLE, DATE, LOCATION, DESCRIPTION, URL);
    }
    
    @Override
    public List<?> extractDataAsList(Item e) {
        return Arrays.asList(e.getId(), e.getImage().getId(), e.getCategory().getId(), e.getPrice(),
                e.getTitle(), e.getDate(), e.getLocation(), e.getDescription(), e.getUrl());
    }
    }
