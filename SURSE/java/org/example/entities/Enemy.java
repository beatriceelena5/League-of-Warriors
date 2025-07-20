package src.java.org.example.entities;

import src.java.org.example.entities.spells.Earth;
import src.java.org.example.entities.spells.Fire;
import src.java.org.example.entities.spells.Ice;
import src.java.org.example.entities.spells.Spell;

import javax.swing.*;
import java.util.Random;

public class Enemy extends Entity{
    public Enemy(int playerLevel) {
        super(generateAbilities(), generateHealth(playerLevel), generateMana(playerLevel), generateImmunity(),
                generateImmunity(), generateImmunity());
    }

    public int getDamage () {
        Random random = new Random();
        int baseDamage = random.nextInt(16) + 5;

        if (random.nextBoolean()) {
            baseDamage *= 2;
            System.out.println("The enemy has gained a double damage bonus: " + baseDamage);
        }

        return baseDamage;
    }

    public void receiveDamage (int damage) {
        Random random = new Random();
        if(random.nextBoolean()) {
            System.out.println("The enemy dodged the attack.");
            return;
        }
        currentHealth = Math.max(currentHealth - damage, 0);
        System.out.println("The enemy received " + damage + " damage. Current health: " + currentHealth );
    }

    public void receiveDamage(int damage, JTextArea messageArea) {
        Random random = new Random();
        if(random.nextBoolean()) {
            messageArea.append("The enemy dodged the attack.\n");
            return;
        }
        currentHealth = Math.max(currentHealth - damage, 0);
        messageArea.append("The enemy received " + damage + " normal damage.\n");
    }

    public void receiveDamage(Spell ability, JTextArea messageArea) {
        if (ability instanceof Fire && immuneToFire) {
            messageArea.append("The attack was fire-based, but the enemy is immune to fire!\n");
            return;
        } else if (ability instanceof Ice && immuneToIce) {
            messageArea.append("The attack was ice-based, but the enemy is immune to ice!\n");
            return;
        } else if (ability instanceof Earth && immuneToEarth) {
            messageArea.append("The attack was earth-based, but the enemy is immune to earth!\n");
            return;
        }

        currentHealth = Math.max(currentHealth - ability.getDamage(), 0);
        messageArea.append("The enemy received " + ability.getDamage() + " damage.\n");
    }


    private static int generateHealth(int playerLevel) {
        Random random = new Random();
        return random.nextInt(51) + 50 + playerLevel * 10;
    }

    private static int generateMana(int playerLevel) {
        Random random = new Random();
        return random.nextInt(21) + 30 + playerLevel * 5;
    }

    public static boolean generateImmunity() {
        Random random = new Random();
        return random.nextBoolean();
    }

    public String toString() {
        return "Enemy:\n" + "Current Health = " + currentHealth + '\n' +
                "Max Health =" + maxHealth + '\n' +
                "Current Mana = " + currentMana + '\n' +
                "Max Mana = " + maxMana + '\n' +
                "Immune To Fire = " + immuneToFire + '\n' +
                "Immune To Ice = " + immuneToIce + '\n' +
                "Immune To Earth = " + immuneToEarth + '\n' +
                "Abilities = " + abilities;
    }
}
