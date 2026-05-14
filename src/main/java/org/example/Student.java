package org.example;

import lombok.ToString;

@ToString(callSuper = true)
public class Student extends User {

    public Student(String id, String name) {
        super(id, name);
    }

    /**
     * Creates new student object with new identifier
     * @param name the display name iof the student
     * @return the new student
     */
    public static Student createWithGeneratedId(String name) {
        return new Student(User.allocateNextId(), name);
    }
}
