package src.java.org.example.entities.characters;

import src.java.org.example.entities.spells.Earth;
import src.java.org.example.entities.spells.Fire;
import src.java.org.example.entities.spells.Ice;
import src.java.org.example.entities.spells.Spell;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class Warrior extends Characters{
    public Warrior(String name, int experience, int level) {
        super(name, experience, level, 15, 5, 10, 100, 50,
                new ArrayList<>());
        this.immuneToFire = true;
    }

    public int getPrimaryAttribute() {
        return strength;
    }

    public int getDamage() {
       int baseDamage =  strength + (level % 5) * 2;

       if (strength > 20) {
           Random random = new Random();
           if(random.nextBoolean()) {
               baseDamage *= 2;
               System.out.println(name + " has gained a double damage bonus!");
           }
       }
       return baseDamage;
    }

    public void receiveDamage(int damage) {
       if ((charisma + dexterity) > 15) {
           Random random = new Random();
           if (random.nextBoolean()) {
               damage /= 2;
           }
       }

       currentHealth = Math.max(currentHealth - damage, 0);
       System.out.println(name + " (Warrior) received " + damage + " damage. Current health: " + currentHealth);
    }

    public void receiveDamage(int damage, JTextArea messageArea) {
        if ((charisma + dexterity) > 15) {
            Random random = new Random();
            if (random.nextBoolean()) {
                damage /= 2;
                messageArea.append(name + " evaded part of the damage! Damage reduced by half.\n");
            }
        }
        currentHealth = Math.max(currentHealth - damage, 0);
        messageArea.append(name + " received " + damage + " normal damage.\n");
    }

}
