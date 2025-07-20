package src.java.org.example.interfaces;

import src.java.org.example.entities.spells.Spell;

import javax.swing.*;

public interface Battle {
    void receiveDamage(int damage);
    void receiveDamage(int damage, JTextArea messageArea);
    public void receiveDamage(Spell ability, JTextArea messageArea);
    int getDamage();
}
