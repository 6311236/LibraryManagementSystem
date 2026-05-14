package org.example;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class Magazine extends Item{
    private int issueNumber;
    private String publisher;

    public Magazine(String id, String title, int issueNumber, String publisher, ItemStatus status) {
        super(id, title, status);
        this.issueNumber = issueNumber;
        this.publisher = publisher;
    }
}
