package src.java.org.example.entities.characters;

import src.java.org.example.entities.spells.Earth;
import src.java.org.example.entities.spells.Fire;
import src.java.org.example.entities.spells.Spell;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class Rogue extends Characters{
    public Rogue(String name, int experience, int level) {
        super(name, experience, level, 5, 5, 10, 90, 40,
                new ArrayList<>());
        this.immuneToEarth = true;
    }

    public int getPrimaryAttribute() {
        return dexterity;
    }

    public int getDamage() {
        int baseDamage =  dexterity + (level % 5) * 2;

        if (dexterity > 15) {
            Random random = new Random();
            if(random.nextBoolean()) {
                baseDamage *= 2;
                System.out.println(name + " has gained a double damage bonus!");
            }
        }
        return baseDamage;
    }

    public void receiveDamage(int damage) {
        if ((strength + charisma) > 15) {
            Random random = new Random();
            if (random.nextBoolean()) {
                damage /= 2;
            }
        }

        currentHealth = Math.max(currentHealth - damage, 0);
        System.out.println(name + " (Rogue) received " + damage + " damage. Current health: " + currentHealth);
    }

    public void receiveDamage(int damage, JTextArea messageArea) {
        if ((strength + charisma) > 15) {
            Random random = new Random();
            if (random.nextBoolean()) {
                damage /= 2;
            }
        }
        currentHealth = Math.max(currentHealth - damage, 0);
        messageArea.append(name + " received " + damage + " normal damage.\n");
    }

}