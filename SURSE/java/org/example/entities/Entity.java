package src.java.org.example.entities;

import src.java.org.example.entities.spells.Earth;
import src.java.org.example.entities.spells.Fire;
import src.java.org.example.entities.spells.Ice;
import src.java.org.example.entities.spells.Spell;
import src.java.org.example.interfaces.Battle;
import src.java.org.example.interfaces.Element;
import src.java.org.example.interfaces.Visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Entity implements Battle, Element<Entity> {
    public List<Spell> abilities;

    public int currentHealth;
    public int maxHealth;
    public int currentMana;
    public int maxMana;

    public boolean immuneToFire;
    public boolean immuneToIce;
    public boolean immuneToEarth;

    public Entity (List<Spell> abilities, int maxHealth, int maxMana, boolean immuneToFire, boolean immuneToIce, boolean immuneToEarth) {
        this.abilities = abilities;

        this.currentHealth = maxHealth;
        this.maxHealth = maxHealth;

        this.currentMana = maxMana;
        this.maxMana = maxMana;

        this.immuneToFire = immuneToFire;
        this.immuneToIce = immuneToIce;
        this.immuneToEarth = immuneToEarth;
    }

    public void regenarateHealth (int addHealth) {
        currentHealth = Math.min(currentHealth + addHealth, maxHealth);
    }

    public void regenarateMana (int addMana) {
        currentMana = Math.min(currentMana + addMana, maxMana);
    }

    public void useAbility (Spell ability, Entity enemy) {
        if (currentMana < ability.getManaCost()) {
            System.out.println("Not enough mana to use the ability.");
            System.out.println("Current mana: " + currentMana);
            return;
        }

        currentMana -= ability.getManaCost();
        ability.visit(enemy);
        abilities.remove(ability);
    }

    public static List<Spell> generateAbilities() {
        Random random = new Random();
        List<Spell> abilities = new ArrayList<>();

        int damage;
        int manaCost;

        // Fire
        damage = random.nextInt(16) + 5;
        manaCost = random.nextInt(11) + 10;
        abilities.add (new Fire(damage, manaCost));

        // Ice
        damage = random.nextInt(16) + 5;
        manaCost = random.nextInt(11) + 10;
        abilities.add (new Ice(damage, manaCost));

        // Earth
        damage = random.nextInt(16) + 5;
        manaCost = random.nextInt(11) + 10 ;
        abilities.add (new Earth(damage, manaCost));

        // Random
        int nrAbilities = random.nextInt(4);
        for (int i = 0; i < nrAbilities; i++) {
            int type = random.nextInt(3);
            damage = random.nextInt(16) + 5;
            manaCost = random.nextInt(11) + 10;

            switch (type) {
                case 0:
                    abilities.add (new Fire(damage, manaCost));
                    break;
                case 1:
                    abilities.add (new Ice(damage, manaCost));
                    break;
                case 2:
                    abilities.add (new Earth(damage, manaCost));
                    break;
            }

        }
        return abilities;

    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth (int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(int currentMana) {
        this.currentMana = currentMana;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public List<Spell> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<Spell> abilities) {
        this.abilities = abilities;
    }

    public void addAbility (Spell ability) {
        abilities.add(ability);
    }

    public void setImmuneToEarth(boolean immuneToEarth) {
        this.immuneToEarth = immuneToEarth;
    }

    public boolean isImmuneToEarth() {
        return immuneToEarth;
    }

    public void setImmuneToFire(boolean immuneToFire) {
        this.immuneToFire = immuneToFire;
    }

    public boolean isImmuneToFire() {
        return immuneToFire;
    }

    public void setImmuneToIce(boolean immuneToIce) {
        this.immuneToIce = immuneToIce;
    }

    public boolean isImmuneToIce() {
        return immuneToIce;
    }

    public void accept(Visitor<Entity> visitor) {
        visitor.visit(this);
    }
}
