package edu.ezip.ing1.pds.front;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
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
    private JTextField searchField;
    private JButton searchButton;
    private JButton returnButton; // Ajout du bouton de retour
    private JScrollPane scrollPane;

    public Information() {
        setTitle("Recherche des medicament ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Create table model with column names
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"ID", "Nom Médicament", "Description", "Effet Secondaire"});

        // Create table with the model
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        scrollPane.setVisible(false); // Hide the scroll pane initially

        // Create search components
        JPanel searchPanel = new JPanel(new FlowLayout());
        JLabel searchLabel = new JLabel("Rechercher Médicament:");
        searchField = new JTextField(20);
        searchButton = new JButton("Rechercher");

        // Add a key listener to the search field to search on Enter key press
        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchMedicament();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        // Add an action listener to the search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchMedicament();
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Add components to the frame
        getContentPane().add(searchPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Fetch and display student data
        try {
            fetchAndDisplayStudentData();
        } catch (IOException | InterruptedException | SQLException e) {
            e.printStackTrace();
        }

        // Ajout du bouton de retour
        returnButton = new JButton("Retourner à la page principale");
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Instancier la classe MainFrame
                MainFrame mainFrame = new MainFrame();
                // Rendre la fenêtre principale visible
                mainFrame.setVisible(true);
                // Cacher la fenêtre actuelle (Information)
                setVisible(false);
            }
        });
        // Ajoutez le bouton à votre frame
        add(returnButton, BorderLayout.SOUTH); // Ajout du bouton à la partie inférieure de la frame
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
                    student.getEffet_secondaire()
            });
        }
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

                // Création du panel principal
                JPanel dialogPanel = new JPanel(new BorderLayout());

                // Création du panel pour afficher la description
                JLabel descriptionPanel = new JLabel("<html>Description :<br>" + description + "</html>");
              
// Création du panel pour afficher les effets secondaires
                JLabel effetsPanel = new JLabel("<html>Effets Secondaires :<br>" + effetsSecondaires + "</html>");
                // Ajout des panels au panel principal
                dialogPanel.add(descriptionPanel, BorderLayout.NORTH); // Aligner à gauche
                dialogPanel.add(effetsPanel, BorderLayout.SOUTH); // Aligner à droite
               
              //  dialogPanel.add(descriptionPanel);
               
              //  dialogPanel.add(effetsPanel);

                
                // Affichage de la boîte de dialogue
                JOptionPane.showMessageDialog(this, dialogPanel, "Résultat de la recherche", JOptionPane.INFORMATION_MESSAGE);
                found = true;
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Aucun médicament trouvé avec ce nom.", "Résultat de la recherche", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createPanelWithText(String labelText, String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(200, 200, 200, 200)); // Ajuster les marges
    
        JLabel label = new JLabel("<html><b>" + labelText + "</b></html>");
        label.setFont(new Font("Arial", Font.BOLD, 14)); // Augmenter la taille de la police
    
        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true); // Assurer un retour à la ligne sur les mots entiers
        textArea.setBackground(Color.WHITE); // Fond blanc pour une meilleure lisibilité
        textArea.setFont(new Font("Arial", Font.PLAIN, 14)); // Augmenter la taille de la police
    
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100))); // Bordure plus légère
    
        panel.add(label, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
    
        return panel;
    }
    
  
}
