package src.java.org.example.entities.characters;

public class CharacterFactory {
    public static Characters createCharacter (String name, String profession, int experience, int level) {
        switch (profession) {
            case "Warrior":
                return new Warrior(name, experience, level);
            case "Rogue":
                return new Rogue(name, experience, level);
            case "Mage":
                return new Mage(name, experience, level);
            default:
                throw new IllegalArgumentException("Invalid character profession: " + profession);
        }
    }
}
