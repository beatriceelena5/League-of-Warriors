package src.java.org.example.entities;

import src.java.org.example.entities.characters.Characters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;

public class Account {
    public int gamesCompleted;
    public Information information;
    public ArrayList<Characters> characters;

    public Account(ArrayList<Characters> characters, int gamesCompleted, Information information) {
        if (characters != null)
            this.characters = characters;
        else
            this.characters = new ArrayList<>();
        this.gamesCompleted = gamesCompleted;
        this.information = information;
    }

    public void incrementGamesCompleted() {
        this.gamesCompleted++;
    }

    public int getGamesCompleted() {
        return gamesCompleted;
    }

    public void setGamesCompleted(int gamesCompleted) {
        this.gamesCompleted = gamesCompleted;
    }

    public Information getInformation() {
        return information;
    }

    public void setInformation (Information information) {
        this.information = information;
    }

    public ArrayList<Characters> getCharacters() {
        return characters;
    }

    public void setCharacters(ArrayList<Characters> characters) {
        this.characters = characters;
    }


    public static class Information {
        private Credentials credentials;
        private SortedSet<String> favoriteGames;
        private String name;
        private String country;

        private Information (Builder builder) {
            this.credentials = builder.credentials;
            this.favoriteGames = builder.favoriteGames;
            this.name = builder.name;
            this.country = builder.country;
        }

        public Credentials getCredentials() {
            return credentials;
        }

        public SortedSet<String> getFavoriteGames() {
            return favoriteGames;
        }

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }

        public String toString() {
            return "Name = " + name + "\n" +
                    "Country = " + country + "\n" +
                    "Credentials = " + credentials + "\n" +
                    "Favorite Games = " + favoriteGames;
        }

        public static class Builder {
            private Credentials credentials;
            private SortedSet<String> favoriteGames;
            private String name;
            private String country;

            public Builder setCredentials (Credentials credentials) {
                this.credentials = credentials;
                return this;
            }

            public Builder setFavoriteGames (SortedSet<String> favoriteGames) {
                this.favoriteGames = favoriteGames;
                return this;
            }

            public Builder setName (String name) {
                this.name = name;
                return this;
            }

            public Builder setCountry (String country) {
                this.country = country;
                return this;
            }

            public Information build() {
                return new Information(this);
            }
        }
    }

    public String toString() {
        return "Information = " + information + "\n" +
                "Games Compeleted = " + gamesCompleted + "\n" +
                "Characters = " + characters;
    }
}
