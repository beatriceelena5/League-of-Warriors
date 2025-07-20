package src;

import src.java.org.example.entities.Account;
import src.java.org.example.entities.exceptions.InvalidCommandException;
import src.java.org.example.entities.exceptions.InvalidEmailException;
import src.java.org.example.game.Game;

import java.io.IOException;
import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        ArrayList<Account> accounts = JsonInput.deserializeAccounts();
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("No accounts loaded from the JSON file.");
            return;
        }

        Game game = Game.getInstance();

        try {
            System.out.println("Starting the game...");
            game.run();
        } catch (IOException e) {
            System.out.println("An error occurred during the game: " + e.getMessage());
        } catch (InvalidCommandException e) {
            System.out.println("Invalid command encountered: " + e.getMessage());
        } catch (InvalidEmailException e) {
            System.out.println(e.getMessage());
        }

    }
}
