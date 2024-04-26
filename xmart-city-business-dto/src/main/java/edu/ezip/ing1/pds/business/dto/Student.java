package edu.ezip.ing1.pds.business.dto;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Student {
    private Integer medicament_id;
    private String nom_medicament;
    private String description;
    private String effet_secondaire;


    public Student() {
    }
    public final Student build(final ResultSet resultSet)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        setFieldsFromResulset(resultSet, "medicament_id","nom_medicament", "description", "effet_secondaire");
        return this;
    }
    public final PreparedStatement build(PreparedStatement preparedStatement)
            throws SQLException, NoSuchFieldException, IllegalAccessException {
        return buildPreparedStatement(preparedStatement, nom_medicament, description, effet_secondaire);
    }
    public Student(Integer medicament_id,String nom_medicament, String description, String effet_secondaire) {
        this.medicament_id = medicament_id;
        this.nom_medicament = nom_medicament;
        this.description = description;
        this.effet_secondaire = effet_secondaire;
    }

    public Integer getMedicament_id() {
        return medicament_id;
    }

    public String getNom_medicament() {
        return nom_medicament;
    }

    public String getDescription() {
        return description;
    }

    public String getEffet_secondaire() {
        return effet_secondaire;
    }
   
    
    
    
    public void setMedicament_id(Integer medicament_id) {
        this.medicament_id = medicament_id;
    }

    public void setNom_medicament(String nom_medicament) {
        this.nom_medicament = nom_medicament;
    }

    public void setDescription(String description) {
        this.description = description;
    }
   
    public void setEffet_secondaire(String effet_secondaire) {
        this.effet_secondaire = effet_secondaire;
    }
    

    private void setFieldsFromResulset(final ResultSet resultSet, final String ... fieldNames )
            throws NoSuchFieldException, SQLException, IllegalAccessException {
        for(final String fieldName : fieldNames ) {
            if(fieldName.equals("medicament_id") ){
                final Field field = this.getClass().getDeclaredField(fieldName);
                    field.set(this, resultSet.getObject(fieldName, Integer.class));}
                    else {
                final Field field = this.getClass().getDeclaredField(fieldName);
                    field.set(this, resultSet.getObject(fieldName, String.class));
               }
            
        }
    }
    private final PreparedStatement buildPreparedStatement(PreparedStatement preparedStatement, final String ... fieldNames )
            throws NoSuchFieldException, SQLException, IllegalAccessException {
        int ix = 0;
       
        for(final String fieldName : fieldNames ) {
            preparedStatement.setString(++ix, fieldName);
        }

        return preparedStatement;
    }

    @Override
    public String toString() {
        return "Student{" +
                "medicament_id='" + medicament_id  + '\'' +
                ", nom_medicament='" + nom_medicament + '\'' +
                ", description='" + description + '\'' +
                ", effet_secondaire='" + effet_secondaire + '\'' +
                
                '}';
    }

  
}
