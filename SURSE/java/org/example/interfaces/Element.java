package src.java.org.example.interfaces;

import src.java.org.example.entities.Entity;

public interface Element <T extends Entity> {
    void accept(Visitor<T> visitor);
}
