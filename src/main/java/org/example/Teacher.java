package org.example;

import lombok.ToString;

@ToString(callSuper = true)
public class Teacher extends User {

    public Teacher(String id, String name) {
        super(id, name);
    }

    /**
     * Creates new student object with new identifier
     * @param name the display name of the teacher
     * @return the new teacher
     */
    public static Teacher createWithGeneratedId(String name) {
        return new Teacher(User.allocateNextId(), name);
    }
}
