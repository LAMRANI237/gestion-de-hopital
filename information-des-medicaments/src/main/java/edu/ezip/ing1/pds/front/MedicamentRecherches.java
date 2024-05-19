/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

 package edu.ezip.ing1.pds.front;

 import java.util.ArrayList;
 import java.util.List;
 import java.util.ArrayList;
 import java.util.List;
 
 public class MedicamentRecherches {
     private List<String> medicaments;
 
     public MedicamentRecherches() {
         this.medicaments = new ArrayList<>();
     }
 
     public void ajouterMedicament(String nomMedicament) {
         medicaments.add(nomMedicament);
     }
 
     public List<String> getMedicaments() {
         return medicaments;
     }
 }
 