package edu.ezip.ing1.pds.front;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MainTest {

    private Main main;

    @BeforeEach
    public void setUp() throws Exception {
        SwingUtilities.invokeAndWait(() -> main = new Main());
    }

    @Test
    public void testMainFrameInitialization() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            assertNotNull(main, "Main frame should not be null.");
            JFrame contentPane = main.getContentPane();

            assertNotNull(contentPane.getComponent(0), "imageLabel should be added to the content pane.");
            assertNotNull(contentPane.getComponent(1), "label should be added to the content pane.");
            assertNotNull(contentPane.getComponent(2), "button1 should be added to the content pane.");
            assertNotNull(contentPane.getComponent(3), "button2 should be added to the content pane.");
        });
    }

    @Test
    public void testButton1Action() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JButton button1 = (JButton) main.getContentPane().getComponent(2);
            button1.doClick();
            
            Information informationMock = mock(Information.class);
            assertNotNull(informationMock, "Information instance should be created when button1 is clicked.");
        });
    }

    @Test
    public void testButton2Action() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            JButton button2 = (JButton) main.getContentPane().getComponent(3);
            button2.doClick();
            
            MainSelectClient mainSelectClientMock = mock(MainSelectClient.class);
            assertNotNull(mainSelectClientMock, "MainSelectClient instance should be created when button2 is clicked.");
        });
    }
}