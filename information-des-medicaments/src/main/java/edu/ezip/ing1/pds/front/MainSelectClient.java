package edu.ezip.ing1.pds.front;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import edu.ezip.commons.LoggingUtils;
import edu.ezip.ing1.pds.business.dto.Student;
import edu.ezip.ing1.pds.business.dto.Students;
import edu.ezip.ing1.pds.client.SelectAllStudentsClientRequest;
import edu.ezip.ing1.pds.client.commons.ClientRequest;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.commons.Request;

public class MainSelectClient extends JFrame {

    private final static String LoggingLabel = "Inserter-Client";
    private final static Logger logger = LoggerFactory.getLogger(LoggingLabel);
    private final static String networkConfigFile = "network.yaml";
    private static final String threadName = "inserter-client";
    private static final String requestOrder = "SELECT_ALL_MEDICAMENTS";
    private static final Deque<ClientRequest> clientRequests = new ArrayDeque<>();
    private DefaultTableModel tableModel;
    private JTable table;

    public MainSelectClient() {
        setTitle("Liste des médicaments");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Create table model with column names
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID", "Nom Médicament", "Description", "Effet Secondaire","id_Similaire"});

        // Create table with the model
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Add components to the frame
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Ajout du bouton de retour
        JButton returnButton = new JButton("Retourner à la page principale");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Instancier la classe MainFrame
                MainFrame mainFrame = new MainFrame();
                // Rendre la fenêtre principale visible
                mainFrame.setVisible(true);
                // Cacher la fenêtre actuelle (MainSelectClient)
                setVisible(false);
            }
        });
        // Ajoutez le bouton à votre frame
        add(returnButton, BorderLayout.SOUTH); // Ajout du bouton à la partie inférieure de la frame

        // Fetch and display student data
        try {
            fetchAndDisplayStudentData();
        } catch (IOException | InterruptedException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchAndDisplayStudentData() throws IOException, InterruptedException, SQLException {
        final NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, networkConfigFile);
        logger.debug("Load Network config file : {}", networkConfig.toString());

        int birthdate = 0;
        final ObjectMapper objectMapper = new ObjectMapper();
        final String requestId = UUID.randomUUID().toString();
        final Request request = new Request();
        request.setRequestId(requestId);
        request.setRequestOrder(requestOrder);
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final byte []  requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);
        LoggingUtils.logDataMultiLine(logger, Level.TRACE, requestBytes);
        final SelectAllStudentsClientRequest clientRequest = new SelectAllStudentsClientRequest(
                networkConfig,
                birthdate++, request, null, requestBytes);
        clientRequests.push(clientRequest);

        while (!clientRequests.isEmpty()) {
            final ClientRequest joinedClientRequest = clientRequests.pop();
            joinedClientRequest.join();
            logger.debug("Thread {} complete.", joinedClientRequest.getThreadName());
            final Students students = (Students) joinedClientRequest.getResult();
            displayStudents(students);
        }
    }

    private void displayStudents(Students students) {
        for (final Student student : students.getStudents()) {
            tableModel.addRow(new Object[]{
                    student.getMedicament_id(),
                    student.getNom_medicament(),
                    student.getDescription(),
                    student.getEffet_secondaire(),
                    student.getId_Similaire()
            });
        }
    }
}
