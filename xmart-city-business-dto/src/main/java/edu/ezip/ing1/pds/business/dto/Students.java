package edu.ezip.ing1.pds.business.dto;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Students implements Iterable<Student> {

    private  Set<Student> students = new LinkedHashSet<Student>();

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public final Students add (final Student student) {
        students.add(student);
        return this;
    }

    @Override
    public String toString() {
        return "Students{" +
                "students=" + students +
                '}';
    }

    @Override
    public Iterator<Student> iterator() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'iterator'");
    }
}
