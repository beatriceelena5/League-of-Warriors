package src.java.org.example.entities.characters;

import src.java.org.example.entities.spells.Earth;
import src.java.org.example.entities.spells.Ice;
import src.java.org.example.entities.spells.Spell;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class Mage extends Characters{
    public Mage(String name, int experience, int level) {
        super(name, experience, level, 5, 20, 10, 80, 60,
                new ArrayList<>());
        this.immuneToIce = true;
    }

    public int getPrimaryAttribute() {
        return charisma;
    }

    public int getDamage() {
        int baseDamage =  charisma + (level % 5) * 3;

        if (charisma > 25) {
            Random random = new Random();
            if(random.nextBoolean()) {
                baseDamage *= 2;
                System.out.println(name + " has gained a double damage bonus!");
            }
        }
        return baseDamage;
    }

    public void receiveDamage(int damage) {
        if ((strength + dexterity) > 20) {
            Random random = new Random();
            if (random.nextBoolean()) {
                damage /= 2;
            }
        }

        currentHealth = Math.max(currentHealth - damage, 0);
        System.out.println(name + " (Mage) received " + damage + " damage. Current health: " + currentHealth);
    }

    public void receiveDamage(int damage, JTextArea messageArea) {
        if ((strength + dexterity) > 20) {
            Random random = new Random();
            if (random.nextBoolean()) {
                damage /= 2;
            }
        }
        currentHealth = Math.max(currentHealth - damage, 0);
        messageArea.append(name + " received " + damage + " normal damage.\n");
    }
}