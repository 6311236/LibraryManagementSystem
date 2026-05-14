package org.example;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DVD extends Item {
    private String director;
    private int durationMinutes;

    public DVD(String id, String title, String director, int durationMinutes, ItemStatus status) {
        super(id, title, status);
        this.director = director;
        this.durationMinutes = durationMinutes;
    }
}
