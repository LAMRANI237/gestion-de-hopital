package edu.ezip.ing1.pds.front;


import static org.junit.jupiter.api.Assertions.assertNotNull;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author driss
 */
public class InformationTest {

    private Information information;

    @BeforeEach
    public void setUp() {
        information = new Information();
    }

    @Test
    public void testInformationInitialization() {
        assertNotNull(information);
        assertNotNull(information.getContentPane().getComponent(0)); // Check if searchPanel is added
        assertNotNull(information.getContentPane().getComponent(1)); // Check if scrollPane is added
        assertNotNull(information.getContentPane().getComponent(2)); // Check if returnButton is added
    }

    /**
     *
     */
    @Test
    public void testSearchMedicament() {
        information.searchField.setText("test");
        information.searchButton.doClick();
        // Verify the search results
    }

    @Test
    public void testFetchAndDisplayStudentData() {
        // Mock necessary objects and methods
        // Implement actual test
    }
}
