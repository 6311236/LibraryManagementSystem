package org.example;

import java.util.Comparator;

public final class UserSortStrategies {

    private UserSortStrategies() {

    }

    /**
     * Orders by name first and then by id
     * @return the comparatoer instance
     */
    public static Comparator<User> byNameThenId() {
        return Comparator.comparing((User u) -> u.getName().toLowerCase())
                .thenComparing(User::getId);
    }
                                                                                // two diff types of ordering, name and id and only id
    /**
     * Orders by id
     * @return the comparator instance
     */
    public static Comparator<User> byId() {
        return Comparator.comparing(User::getId);
    }
}
