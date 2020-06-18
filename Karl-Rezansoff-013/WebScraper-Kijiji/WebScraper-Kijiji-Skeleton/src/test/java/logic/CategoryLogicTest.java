package logic;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import entity.Category;
import common.TomcatStartUp;
import dal.EMFactory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Karl
 */
class CategoryLogicTest {

    private CategoryLogic logic;
    private Category expectedCategory;

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
        //we manually make the Category to not rely on any logic functionality , just for testing
        Category Category = new Category();
        Category.setTitle("www.categorytesturl.ca");
        Category.setUrl("/test/category");

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMFactory().createEntityManager();
        //start a Transaction 
        em.getTransaction().begin();
        //add a Category to hibernate, Category is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedCategory = em.merge(Category);
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();

        logic = new CategoryLogic();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if (expectedCategory != null) {
            logic.delete(expectedCategory);
        }
    }

    @Test
    final void testGetAll() {
        //get all the Categories from the DB
        List<Category> list = logic.getAll();
        //store the size of list, this way we know how many Categories exits in DB
        int originalSize = list.size();

        //make sure Category was created successfully
        assertNotNull(expectedCategory);
        //delete the new Category
        logic.delete(expectedCategory);

        //get all Categoryies again
        list = logic.getAll();
        //the new size of Category list must be one less
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * helper method for testing all Category fields
     *
     * @param expected
     * @param actual
     */
    private void assertCategoryEquals(Category expected, Category actual) {
        //assert all field to guarantee they are the same
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getUrl(), actual.getUrl());
    }

    @Test
    final void testGetWithId() {
        //using the id of test Category get another Category from logic
        Category returnedCategory = logic.getWithId(expectedCategory.getId());

        //the two Categorys (testAcounts and returnedCategorys) must be the same
        assertCategoryEquals(expectedCategory, returnedCategory);
    }

    @Test
    final void testGetWithUrl() {
        Category returnedCategory = logic.getWithUrl(expectedCategory.getUrl());

        //the two Category entities must be the same
        assertCategoryEquals(expectedCategory, returnedCategory);
    }

    @Test
    final void testGetWithTitle() {
        Category returnedCategory = logic.getWithTitle(expectedCategory.getTitle());

        //the two Category entities must be the same
        assertCategoryEquals(expectedCategory, returnedCategory);
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(CategoryLogic.ID, new String[]{Integer.toString(expectedCategory.getId())});
        sampleMap.put(CategoryLogic.TITLE, new String[]{expectedCategory.getTitle()});
        sampleMap.put(CategoryLogic.URL, new String[]{expectedCategory.getUrl()});

        Category returnedCategory = logic.createEntity(sampleMap);

        assertCategoryEquals(expectedCategory, returnedCategory);
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("ID", "URL", "Title"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(CategoryLogic.ID, CategoryLogic.URL, CategoryLogic.TITLE), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedCategory);
        assertEquals(expectedCategory.getId(), list.get(0));
        assertEquals(expectedCategory.getUrl(), list.get(1));
        assertEquals(expectedCategory.getTitle(), list.get(2));
    }
}
