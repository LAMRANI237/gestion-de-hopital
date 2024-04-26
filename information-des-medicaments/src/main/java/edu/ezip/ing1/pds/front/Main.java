package edu.ezip.ing1.pds.front;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Main {

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();
        frame.setVisible(true);
    }

}

class MainFrame extends JFrame {
    MainFrame() {
        // Create a JFrame
        super("les medicaments");
        setBackground(Color.cyan);
        setSize(700, 500); // Set size
        setLayout(null); // Set layout to null for manual component placement
        getContentPane().setBackground(Color.darkGray); // Set background color
        ImageIcon icon = new ImageIcon("C:/Users/driss/Downloads/Doc1_page-0001.jpg");
        JLabel imageLabel = new JLabel(icon);
        add(imageLabel, BorderLayout.BEFORE_LINE_BEGINS);

        // Create a JLabel
        JLabel label = new JLabel("EPITAL");
        label.setFont(new Font("Times New Roman", Font.ITALIC, 50));
        label.setForeground(Color.white);
        label.setBounds(300, 30, 250, 70); // Set bounds (x, y, width, height)
        add(label, BorderLayout.NORTH); // Add label to fram

        // Create the first button
        JButton button1 = new JButton("Recherche des medicaments");
        button1.setBounds(40, 300, 250 , 30); // Set bounds (x, y, width, height)
        add(button1); // Add button to frame
        button1.setFont(new Font("Arial", Font.BOLD, 14));
        button1.setBackground(new Color(100, 189, 188)); // Couleur de fond
        button1.setForeground(Color.WHITE); // Couleur du texte
        button1.setFocusPainted(false);

        // Create the second button
        JButton button2 = new JButton("Historique des medicaments");
        button2.setBounds(400, 300, 250, 30); // Set bounds (x, y, width, height)
        add(button2); // Add button to frame
        button2.setFont(new Font("Arial", Font.BOLD, 14));
        button2.setBackground(new Color(100, 189, 188)); // Couleur de fond
        button2.setForeground(Color.WHITE); // Couleur du texte
        button2.setFocusPainted(false); // Suppression du rectangle de focus


        JButton button3 = new JButton("liste des medicament");
        button3.setBounds(400, 350, 250, 30); // Set bounds (x, y, width, height)
        add(button3); // Add button to frame
        button3.setFont(new Font("Arial", Font.BOLD, 14));
        button3.setBackground(new Color(100, 189, 188)); // Couleur de fond
        button3.setForeground(Color.WHITE); // Couleur du texte
        button3.setFocusPainted(false); // Suppression du rectangle de focus

        // À l'est pas possible car il n'y a que le haut

        // Set default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Make the frame visible
        setVisible(true);

        // Action listener for button1
        button1.addActionListener((ActionEvent e) -> {
            // Add action for button1 here
            Information info = new Information();
            info.setVisible(true);
        });

        // Action listener for button2
        button2.addActionListener((ActionEvent e) -> {
            
        });

        button3.addActionListener((ActionEvent e) -> {
            MainSelectClient mainSelectClient = new MainSelectClient();
            mainSelectClient.setVisible(true);
            dispose();

        });
    }

}
