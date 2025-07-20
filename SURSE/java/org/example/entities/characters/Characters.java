package src.java.org.example.entities.characters;

import src.java.org.example.entities.Entity;
import src.java.org.example.entities.exceptions.InvalidCommandException;
import src.java.org.example.entities.spells.Earth;
import src.java.org.example.entities.spells.Fire;
import src.java.org.example.entities.spells.Ice;
import src.java.org.example.entities.spells.Spell;

import javax.swing.*;
import java.util.List;
import java.util.Scanner;

public abstract class Characters extends Entity {
    public String name;
    public int experience;
    public int level;

    public int strength;
    public int charisma;
    public int dexterity;

    public Characters (String name, int experience, int level, int strength, int charisma, int dexterity,
                       int maxHealth, int maxMana, List<Spell> abilities) {
        super(abilities, maxHealth, maxMana, false, false, false);
        this.name = name;
        this.experience = experience;
        this.level = level;

        this.strength = strength;
        this.charisma = charisma;
        this.dexterity = dexterity;
    }

    public void gainExperience (int addExperience) {
        experience += addExperience;

        if(experience >= getNextLevelThreshold()) {
            levelUp();
        }
    }

    public int getNextLevelThreshold() {
        return 100 * level;
    }

    public void levelUp() {
        experience += level*2;
        level++;

        strength += 10 + level;
        charisma +=5 + level;
        dexterity += 7 + level;

        maxHealth += 10 + level * 2;
        maxMana += 10 + level * 2;

        currentHealth = maxHealth;
        currentMana = maxMana;

        System.out.println(name + " has leveled up to level " + level);
        System.out.println();
    }

    public abstract int getPrimaryAttribute();

    public Spell selectAbility() throws InvalidCommandException {
        if (abilities.isEmpty()) {
            System.out.println("No abilities available.");
            return null;
        }

        System.out.println("Select an ability to use (Current mana: " + this.getCurrentMana() + ")");
        for (int i = 0; i < abilities.size(); i++) {
            Spell spell = abilities.get(i);
            System.out.println((i + 1) + ". " + spell);
        }

        Scanner scanner = new Scanner(System.in);
        try {
            int choice = scanner.nextInt();
            if (choice > 0 && choice <= abilities.size()) {
                Spell selectedSpell = abilities.get(choice - 1);
                if (currentMana >= selectedSpell.getManaCost()) {
                    return selectedSpell;
                } else {
                    System.out.println("Not enough mana to use this ability.");
                    return null;
                }
            } else {
                throw new InvalidCommandException("Invalid selection. Please choose a valid ability.");
            }
        } catch (Exception e) {
            throw new InvalidCommandException("Invalid input. Please enter a valid number.");
        }
    }

    public void regenarateHealth(int amount) {
        currentHealth = Math.min(currentHealth + amount, maxHealth);
        System.out.println(name + " regenerated " + amount + " health. Current health: " + currentHealth);
    }

    public void regenarateMana(int amount) {
        currentMana = Math.min(currentMana + amount, maxMana);
        System.out.println(name + " regenerated " + amount + " mana. Current mana: " + currentMana);
    }

    @Override
    public void receiveDamage(Spell ability, JTextArea messageArea) {
        if (ability instanceof Fire && isImmuneToFire()) {
            messageArea.append("The attack was fire-based, but " + name + " is immune to fire!\n");
            return;
        }
        if (ability instanceof Earth && isImmuneToEarth()) {
            messageArea.append("The attack was earth-based, but " + name + " is immune to earth!\n");
            return;
        }
        if (ability instanceof Ice && isImmuneToIce()) {
            messageArea.append("The attack was ice-based, but " + name + " is immune to ice!\n");
            return;
        }

        int damage = ability.getDamage();
        currentHealth = Math.max(0, currentHealth - damage);
        messageArea.append(name + " received " + damage + " damage.\n");
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getCharisma() {
        return charisma;
    }

    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}
