package org.example;

import lombok.ToString;

@ToString(callSuper = true)
public class Admin extends User{

    public Admin(String id, String name) {
        super(id, name);
    }

    /**
     * Creates new admin object with new identifier
     * @param name the display name of the admin
     * @return the new admin
     */
    public static Admin createWithGeneratedId(String name) {
        return new Admin(User.allocateNextId(), name);
    }
}
