package edu.ezip.ing1.pds.front;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import edu.ezip.ing1.pds.front.MainSelectClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
public class MainSelectClientTest {

    private MainSelectClient client;

    @BeforeEach
    public void setUp() {
        client = new MainSelectClient();
    }

    @Test
    public void testConstructor() {
        assertNotNull(client);
    }

    // Ajoutez d'autres tests pour les méthodes de la classe MainSelectClient
}
