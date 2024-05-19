package edu.ezip.ing1.pds.front;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MedicamentsRecherchesFrame extends JFrame {
    public MedicamentsRecherchesFrame(List<String> medicaments) {
        setTitle("Médicaments recherchés");
        setSize(400, 300);
        setLocationRelativeTo(null);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String medicament : medicaments) {
            listModel.addElement(medicament);
        }

        JList<String> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(list);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }
}
