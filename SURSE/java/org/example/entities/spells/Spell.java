package src.java.org.example.entities.spells;

import src.java.org.example.entities.Entity;
import src.java.org.example.interfaces.Visitor;

public abstract class Spell implements Visitor<Entity> {
    private final int damage;
    private final int manaCost;

    public Spell (int damage, int manaCost) {
        this.damage = damage;
        this.manaCost = manaCost;
    }

    public int getDamage() {
        return damage;
    }

    public int getManaCost() {
        return manaCost;
    }

    public String toString() {
        return "Spell:" + this.getClass().getSimpleName() + " (damage = " + damage +
                ", manaCost = " + manaCost + ")";
    }

    @Override
    public void visit(Entity entity) {
        boolean isImmune = false;

        if (this instanceof Fire && entity.isImmuneToFire())
            isImmune = true;
        else if (this instanceof Earth && entity.isImmuneToEarth())
            isImmune = true;
        else if (this instanceof Ice && entity.isImmuneToIce())
            isImmune = true;

        if (isImmune) {
            System.out.println("The entitity is immune to " + this.getClass().getSimpleName());
            return;
        }

        int damage = this.getDamage();
        entity.receiveDamage(damage);
        //System.out.println(this.getClass().getSimpleName() + " spell inflicted " + damage + " damage.");
    }
}
