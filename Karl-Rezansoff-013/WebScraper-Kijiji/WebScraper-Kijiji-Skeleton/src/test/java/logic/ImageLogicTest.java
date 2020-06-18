package logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import entity.Image;
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
class ImageLogicTest {

    private ImageLogic logic;
    private Image expectedImage;

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
        //we manually make the Image to not rely on any logic functionality , just for testing
        Image Image = new Image();
        Image.setUrl("www.itemtesturl.ca");
        Image.setPath("/test/");
        Image.setName("test image");

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMFactory().createEntityManager();
        //start a Transaction 
        em.getTransaction().begin();
        //add an Image to hibernate, Image is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedImage = em.merge(Image);
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();

        logic = new ImageLogic();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if (expectedImage != null) {
            logic.delete(expectedImage);
        }
    }

    @Test
    final void testGetAll() {
        //get all the Images from the DB
        List<Image> list = logic.getAll();
        //store the size of list, this way we know how many Images exists in DB
        int originalSize = list.size();

        //make sure Image was created successfully
        assertNotNull(expectedImage);
        //delete the new Image
        logic.delete(expectedImage);

        //get all Images again
        list = logic.getAll();
        //the new size of Image list must be one less
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * helper method for testing all Image fields
     *
     * @param expected
     * @param actual
     */
    private void assertImageEquals(Image expected, Image actual) {
        //assert all fields to guarantee they are the same
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getUrl(), actual.getUrl());
        assertEquals(expected.getPath(), actual.getPath());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    final void testGetWithId() {
        //using the id of test Image to get another Image from logic
        Image returnedImage = logic.getWithId(expectedImage.getId());

        //the two Images (testAcounts and returnedImages) must be the same
        assertImageEquals(expectedImage, returnedImage);
    }

    @Test
    final void testGetWithUrl() {
        int foundFull = 0;
        List<Image> returnedImages = logic.getWithUrl(expectedImage.getUrl());
        for (Image image : returnedImages) {
            //all Images must have the same url
            assertEquals(expectedImage.getUrl(), image.getUrl());
            //exactly one Image must be the same
            if (image.getUrl().equals(expectedImage.getUrl())) {
                assertImageEquals(expectedImage, image);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }

    @Test
    final void testGetWithPath() {
        Image returnedImage = logic.getWithPath(expectedImage.getPath());

        //the two Images (testAcounts and returnedImages) must be the same
        assertImageEquals(expectedImage, returnedImage);
    }

    @Test
    final void testGetWithName() {
        int foundFull = 0;
        List<Image> returnedImages = logic.getWithName(expectedImage.getName());
        for (Image image : returnedImages) {
            //all Images must have the same name
            assertEquals(expectedImage.getName(), image.getName());
            //exactly one Image must be the same
            if (image.getId().equals(expectedImage.getId())) {
                assertImageEquals(expectedImage, image);
                foundFull++;
            }
        }
        assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(ImageLogic.ID, new String[]{Integer.toString(expectedImage.getId())});
        sampleMap.put(ImageLogic.PATH, new String[]{expectedImage.getPath()});
        sampleMap.put(ImageLogic.NAME, new String[]{expectedImage.getName()});
        sampleMap.put(ImageLogic.URL, new String[]{expectedImage.getUrl()});

        Image returnedImage = logic.createEntity(sampleMap);

        assertImageEquals(expectedImage, returnedImage);
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("ID", "URL", "Path", "Name"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(ImageLogic.ID, ImageLogic.URL, ImageLogic.PATH, ImageLogic.NAME), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedImage);
        assertEquals(expectedImage.getId(), list.get(0));
        assertEquals(expectedImage.getUrl(), list.get(1));
        assertEquals(expectedImage.getPath(), list.get(2));
        assertEquals(expectedImage.getName(), list.get(3));
    }
}
