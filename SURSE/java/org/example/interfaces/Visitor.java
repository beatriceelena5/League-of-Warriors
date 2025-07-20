package src.java.org.example.interfaces;

import src.java.org.example.entities.Entity;

public interface Visitor <T extends Entity> {
    void visit(T entity);
}
