package edu.ezip.ing1.pds.front;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;
import javax.swing.*;
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

public class Information extends JFrame {
    
    private final static String LoggingLabel = "Inserter-Client";
    private final static Logger logger = LoggerFactory.getLogger(LoggingLabel);
    private final static String networkConfigFile = "network.yaml";
    private static final String threadName = "inserter-client";
    private static final String requestOrder = "SELECT_ALL_MEDICAMENTS";
    private static final Deque<ClientRequest> clientRequests = new ArrayDeque<>();
    private DefaultTableModel tableModel;
    private JTable table;
    public JTextField searchField;
    public JButton searchButton;
    private final JButton returnButton;
    private JScrollPane scrollPane;

    public Information() {
        setTitle("Recherche des medicament");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Initialize components
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID", "Nom Médicament", "Description", "Effet Secondaire", "id_Similaire"});
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        scrollPane.setVisible(false);

        // Create search components
        JPanel searchPanel = new JPanel(new FlowLayout());
        JLabel searchLabel = new JLabel("Rechercher Médicament:");
        searchField = new JTextField(20);
        searchButton = new JButton("Rechercher");

        // Add listeners
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchMedicament();
                }
            }
        });

        searchButton.addActionListener(e -> searchMedicament());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        getContentPane().add(searchPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        try {
            fetchAndDisplayStudentData();
        } catch (IOException | InterruptedException | SQLException e) {
            logger.error("Failed to fetch and display student data", e);
        }

        // Return button
        returnButton = new JButton("Retourner à la page principale");
        returnButton.addActionListener(e -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            setVisible(false);
        });
        add(returnButton, BorderLayout.SOUTH);
    }

    private void fetchAndDisplayStudentData() throws IOException, InterruptedException, SQLException {
        NetworkConfig networkConfig = ConfigLoader.loadConfig(NetworkConfig.class, networkConfigFile);
        logger.debug("Load Network config file: {}", networkConfig.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        Request request = new Request();
        request.setRequestId(UUID.randomUUID().toString());
        request.setRequestOrder(requestOrder);

        byte[] requestBytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(request);
        LoggingUtils.logDataMultiLine(logger, Level.TRACE, requestBytes);
        SelectAllStudentsClientRequest clientRequest = new SelectAllStudentsClientRequest(
                networkConfig, 0, request, null, requestBytes);
        clientRequests.push(clientRequest);

        while (!clientRequests.isEmpty()) {
            ClientRequest joinedClientRequest = clientRequests.pop();
            joinedClientRequest.join();
            logger.debug("Thread {} complete.", joinedClientRequest.getThreadName());
            Students students = (Students) joinedClientRequest.getResult();
            displayStudents(students);
        }
    }

    private void displayStudents(Students students) {
        for (Student student : students.getStudents()) {
            tableModel.addRow(new Object[]{
                student.getMedicament_id(),
                student.getNom_medicament(),
                student.getDescription(),
                student.getEffet_secondaire(),
                student.getId_Similaire()
            });
        }
        scrollPane.setVisible(true); // Show the table after adding data
    }

    private void searchMedicament() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un nom de médicament.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean found = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nomMedicament = ((String) tableModel.getValueAt(i, 1)).toLowerCase();
            if (nomMedicament.equals(searchTerm)) {
                String description = (String) tableModel.getValueAt(i, 2);
                String effetsSecondaires = (String) tableModel.getValueAt(i, 3);
                int idSimilaire = (int) tableModel.getValueAt(i, 4);

                JPanel dialogPanel = new JPanel(new BorderLayout());
                JLabel descriptionPanel = new JLabel("<html>Description :<br>" + description + "</html>");
                descriptionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                JLabel effetsPanel = new JLabel("<html>Effets Secondaires :<br>" + effetsSecondaires + "</html>");
                effetsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                dialogPanel.add(descriptionPanel, BorderLayout.NORTH);
                dialogPanel.add(effetsPanel, BorderLayout.CENTER);

                StringBuilder medSimilaires = new StringBuilder();
                for (int j = 0; j < tableModel.getRowCount(); j++) {
                    if ((int) tableModel.getValueAt(j, 4) == idSimilaire) {
                        medSimilaires.append((String) tableModel.getValueAt(j, 1)).append("\n");
                    }
                }

                if (medSimilaires.length() > 0) {
                    JLabel similairesPanel = new JLabel("<html>Médicaments similaires :<br>" + medSimilaires.toString() + "</html>");
                    similairesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    dialogPanel.add(similairesPanel, BorderLayout.SOUTH);
                }

                JOptionPane.showMessageDialog(this, dialogPanel, "Détails du médicament", JOptionPane.INFORMATION_MESSAGE);
                found = true;
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Aucun médicament trouvé avec ce nom.", "Résultat de la recherche", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public JButton getSearchButton() {
        return searchButton;
    }
}
