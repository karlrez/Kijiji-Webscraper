package logic;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import entity.Item;
import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Category;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import static logic.ItemLogic.DATE;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Shariar
 */
class ItemLogicTest {

    private ItemLogic logic;
    private Item expectedItem;

    @BeforeAll
    final static void setUpBeforeClass() throws Exception {
        TomcatStartUp.createTomcat();
    }

    @AfterAll
    final static void tearDownAfterClass() throws Exception {
        TomcatStartUp.stopAndDestroyTomcat();
    }

    @BeforeEach
    final void setUp() throws Exception {
        //we manually make the Item to not rely on any logic functionality , just for testing
        Item Item = new Item();
        Item.setId(99);
        Item.setDescription("test item");
        //just using whats already in the db for Item and Category
        Item.setCategory(new CategoryLogic().getAll().get(0)); 
        Item.setImage(new ImageLogic().getAll().get(0));
        Item.setLocation("test location");
        Item.setPrice(new BigDecimal(20));
        Item.setTitle("test item title");
        Item.setUrl("test item url");
        
        try {
            Item.setDate(new SimpleDateFormat("dd/mm/yyyy").parse("02/02/2020"));
        }
        catch (ParseException ex) {
            Item.setDate(new Date());
        }
        
        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMFactory().createEntityManager();
        //start a Transaction 
        em.getTransaction().begin();
        //add an Item to hibernate, Item is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedItem = em.merge(Item);
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();

        logic = new ItemLogic();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if (expectedItem != null) {
            logic.delete(expectedItem);
        }
    }

    @Test
    final void testGetAll() {
        //get all the Items from the DB
        List<Item> list = logic.getAll();
        //store the size of list, this way we know how many Items exits in DB
        int originalSize = list.size();

        //make sure Item was created successfully
        assertNotNull(expectedItem);
        //delete the new Item
        logic.delete(expectedItem);

        //get all Items again
        list = logic.getAll();
        //the new size of Items must be one less
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * helper method for testing all Item fields
     *
     * @param expected
     * @param actual
     */
    private void assertItemEquals(Item expected, Item actual) {
        //assert all field to guarantee they are the same
        assertEquals(expected.getDescription(), actual.getDescription());
        //assertEquals(expected, actual.getCategory());
        //assertEquals(expected.getImage(), actual.getImage());
        assertEquals(expected.getLocation(), actual.getLocation());
        assertTrue(expected.getPrice().compareTo(actual.getPrice()) == 0); // returns 0 for true
        assertEquals(expected.getTitle(), actual.getTitle());
        //assertEquals(expected.getDate(), actual.getDate());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getId(), actual.getId());
    }

    @Test
    final void testGetWithId() {
        //using the id of test Item get another Item from logic
        Item returnedItem = logic.getWithId(expectedItem.getId());

        //the two Items (testAcounts and returnedItems) must be the same
        assertItemEquals(expectedItem, returnedItem);
    }

    @Test
    final void testGetWithPrice() {
        int foundFull = 0;
        List<Item> returnedItems = logic.getWithPrice(expectedItem.getPrice());
        for (Item item : returnedItems) {
            //all items must have the same price
            assertTrue(expectedItem.getPrice().compareTo(item.getPrice()) == 0);
            //exactly one item must be the same
            if (item.getId().equals(expectedItem.getId())) {
                assertItemEquals(expectedItem, item);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }

    @Test
    final void testGetWithTitle() {
        int foundFull = 0;
        List<Item> returnedItems = logic.getWithTitle(expectedItem.getTitle());
        for (Item item : returnedItems) {
            //all items must have the same title
            assertEquals(expectedItem.getTitle(), item.getTitle());
            //exactly one item must be the same
            if (item.getId().equals(expectedItem.getId())) {
                assertItemEquals(expectedItem, item);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }
    
    @Test
    final void testGetWithLocation() {
        int foundFull = 0;
        List<Item> returnedItems = logic.getWithLocation(expectedItem.getLocation());
        for (Item item : returnedItems) {
            //all items must have the same location
            assertEquals(expectedItem.getLocation(), item.getLocation());
            //exactly one item must be the same
            if (item.getId().equals(expectedItem.getId())) {
                assertItemEquals(expectedItem, item);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }
    
    @Test
    final void testGetWithDescription() {
        int foundFull = 0;
        List<Item> returnedItems = logic.getWithDescription(expectedItem.getDescription());
        for (Item item : returnedItems) {
            //all items must have the same description
            assertEquals(expectedItem.getDescription(), item.getDescription());
            //exactly one item must be the same
            if (item.getId().equals(expectedItem.getId())) {
                assertItemEquals(expectedItem, item);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }
    
    /*
    @Test
    final void testGetWithUrl() {
        //using the Url of test Item get another Item from logic
        Item returnedItem = logic.getWithUrl(expectedItem.getUrl());

        //the two Items must be the same
        assertItemEquals(expectedItem, returnedItem);
    }
    */
    
    /*
    @Test
    final void testGetWithCategory() {
        int foundFull = 0;
        List<Item> returnedItems = logic.getWithCategory(expectedItem.getCategory().getId());
        for (Item item : returnedItems) {
            //all items must have the same category
            assertEquals(expectedItem.getCategory(), item.getCategory());
            //exactly one item must be the same
            if (item.getId().equals(expectedItem.getId())) {
                assertItemEquals(expectedItem, item);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }
    */

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(ItemLogic.DESCRIPTION, new String[]{expectedItem.getDescription()});
        sampleMap.put(ItemLogic.CATEGORY_ID, new String[]{expectedItem.getCategory().getId().toString()});
        sampleMap.put(ItemLogic.IMAGE_ID, new String[]{expectedItem.getImage().getId().toString()});
        sampleMap.put(ItemLogic.LOCATION, new String[]{expectedItem.getLocation()});
        sampleMap.put(ItemLogic.PRICE, new String[]{expectedItem.getPrice().toString()});
        sampleMap.put(ItemLogic.TITLE, new String[]{expectedItem.getTitle()});
        sampleMap.put(ItemLogic.DATE, new String[]{expectedItem.getDate().toString()});
        sampleMap.put(ItemLogic.URL, new String[]{expectedItem.getUrl()});
        sampleMap.put(ItemLogic.ID, new String[]{expectedItem.getId().toString()});

        Item returnedItem = logic.createEntity(sampleMap);
        returnedItem.setCategory(expectedItem.getCategory());
        returnedItem.setImage(expectedItem.getImage());

        assertItemEquals(expectedItem, returnedItem);
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("ID", "Image_ID", "Category_ID", "Price",
                "Title", "Date", "Location", "Description", "URL"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(ItemLogic.ID, ItemLogic.IMAGE_ID, ItemLogic.CATEGORY_ID, ItemLogic.PRICE,
                ItemLogic.TITLE, ItemLogic.DATE, ItemLogic.LOCATION, ItemLogic.DESCRIPTION, ItemLogic.URL), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedItem);
        assertEquals(expectedItem.getId(), list.get(0));
        assertEquals(expectedItem.getImage().getId(), list.get(1));
        assertEquals(expectedItem.getCategory().getId(), list.get(2));
        assertEquals(expectedItem.getPrice(), list.get(3));
        assertEquals(expectedItem.getTitle(), list.get(4));
        assertEquals(expectedItem.getDate(), list.get(5));
        assertEquals(expectedItem.getLocation(), list.get(6));
        assertEquals(expectedItem.getDescription(), list.get(7));
        assertEquals(expectedItem.getUrl(), list.get(8));
    }
}
