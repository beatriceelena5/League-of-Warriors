package src.java.org.example.game;

import src.java.org.example.entities.Account;
import src.JsonInput;
import src.java.org.example.entities.Enemy;
import src.java.org.example.entities.characters.Characters;
import src.java.org.example.entities.exceptions.ImpossibleMoveException;
import src.java.org.example.entities.exceptions.InvalidCommandException;
import src.java.org.example.entities.exceptions.InvalidEmailException;
import src.java.org.example.entities.spells.Spell;
import src.java.org.example.enums.CellEntityType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import static src.java.org.example.entities.Entity.generateAbilities;

public class Game {
    private static Game instance = null;
    private ArrayList<Account> accounts;
    private Grid grid;
    private static Characters currentCharacter;
    private Account currentAccount;
    private JFrame gameFrame = null;
    private JFrame battleFrame = null;
    private JFrame characterFrame = null;
    private int enemiesDefeated;
    private int experienceGained;

    private Game() {
        accounts = new ArrayList<>();
    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void run() throws IOException, InvalidCommandException, InvalidEmailException {
        populateAccounts();
        authenticateUser();
    }

    private void populateAccounts() {
        ArrayList<Account> importedAccounts = JsonInput.deserializeAccounts();

        if (importedAccounts == null || importedAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No accounts found. Exiting game.");
            System.exit(0);
        }

        accounts = new ArrayList<>(importedAccounts);
    }

    private void authenticateUser() {
        // The main login window
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 350);
        loginFrame.setLayout(new BorderLayout());

        // Title label at the top of the window
        JLabel loginTitle = new JLabel("Login", SwingConstants.CENTER);
        loginTitle.setFont(new Font("Arial", Font.BOLD, 24));
        loginTitle.setOpaque(true);
        loginTitle.setBackground(GameColors.DARK_BLUE);
        loginTitle.setForeground(Color.WHITE);
        loginTitle.setPreferredSize(new Dimension(400, 50));
        loginFrame.add(loginTitle, BorderLayout.NORTH);

        // Panel for the login form
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(GameColors.VERY_LIGHT_CYAN);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label and text field for email and password
        JLabel emailLabel = new JLabel("Type your email:");
        emailLabel.setForeground(GameColors.DARK_BLUE);
        JTextField emailField = new JTextField(15);
        emailField.setBorder(BorderFactory.createLineBorder(GameColors.MEDIUM_BLUE, 2));

        JLabel orEmailLabel = new JLabel("Or select your email:");
        orEmailLabel.setForeground(GameColors.DARK_BLUE);
        JComboBox<String> emailComboBox = new JComboBox<>();
        emailComboBox.addItem("Select an email");
        for (Account account : accounts) {
            emailComboBox.addItem(account.getInformation().getCredentials().getEmail());
        }
        emailComboBox.setBackground(Color.WHITE);
        emailComboBox.setBorder(BorderFactory.createLineBorder(GameColors.MEDIUM_BLUE, 2));

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(GameColors.DARK_BLUE);
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setBorder(BorderFactory.createLineBorder(GameColors.MEDIUM_BLUE, 2));


        JButton loginButton = new JButton("Login");
        loginButton.setBackground(GameColors.LIGHT_BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createLineBorder(GameColors.DARK_BLUE, 2));

        // Add components to the GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        loginPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(orEmailLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(emailComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);

        loginFrame.add(loginPanel, BorderLayout.CENTER);

        // Listener for the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    String selectedEmail;
                    if (emailField.getText().isEmpty()) {
                        selectedEmail = (String) emailComboBox.getSelectedItem();
                        if (selectedEmail == null || selectedEmail.equals("Select an email")) {
                            JOptionPane.showMessageDialog(loginFrame, "Please select a valid email or type " +
                                    "one.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else {
                        selectedEmail = emailField.getText();
                    }

                    if (!selectedEmail.contains("@") || !selectedEmail.contains(".")) {
                        throw new InvalidEmailException("Invalid email format. Please enter a valid email address.");
                    }

                    String password = new String(passwordField.getPassword());

                    for (Account account : accounts) {
                        if (account.getInformation().getCredentials().getEmail().equals(selectedEmail) &&
                                account.getInformation().getCredentials().getPassword().equals(password)) {
                            currentAccount = account;

                            JOptionPane.showMessageDialog(loginFrame, "Successfully logged in!\nWelcome, " +
                                    account.getInformation().getName());
                            loginFrame.dispose();
                            selectCharacter();
                            return;
                        }
                    }

                    JOptionPane.showMessageDialog(loginFrame, "Invalid Credentials!", "Error",
                            JOptionPane.ERROR_MESSAGE);

                } catch (InvalidEmailException e) {
                    JOptionPane.showMessageDialog(loginFrame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        loginFrame.setVisible(true);
    }


    private void selectCharacter() {
        // Close any previously opened game or battle windows
        if (gameFrame != null && gameFrame.isDisplayable()) {
            gameFrame.dispose();
        }
        if (battleFrame != null && battleFrame.isDisplayable()) {
            battleFrame.dispose();
        }

        ArrayList<Characters> characters = currentAccount.getCharacters();
        ArrayList<Characters> availableCharacters = new ArrayList<>();
        for (Characters character : characters) {
            if (character.getCurrentHealth() > 0) {
                availableCharacters.add(character);
            }
        }

        if (availableCharacters.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No characters with health available for " +
                    "this account.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Window for character selection
        characterFrame = new JFrame("Select Your Character");
        characterFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        characterFrame.setSize(600, 450);
        characterFrame.setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Select Your Character", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(GameColors.DARK_BLUE);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setPreferredSize(new Dimension(600, 50));
        characterFrame.add(titleLabel, BorderLayout.NORTH);

        // Panel for account details
        JPanel accountDetailsPanel = new JPanel();
        accountDetailsPanel.setLayout(new BoxLayout(accountDetailsPanel, BoxLayout.Y_AXIS));
        accountDetailsPanel.setBackground(GameColors.LIGHT_CYAN);
        accountDetailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea accountDetailsArea = new JTextArea();
        accountDetailsArea.setEditable(false);
        accountDetailsArea.setBackground(GameColors.LIGHT_CYAN);
        accountDetailsArea.setForeground(GameColors.DARK_BLUE);
        accountDetailsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        accountDetailsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        accountDetailsArea.setLineWrap(true);
        accountDetailsArea.setWrapStyleWord(true);

        // Set the account details text
        StringBuilder accountDetailsBuilder = new StringBuilder();
        accountDetailsBuilder.append("Name: ").append(currentAccount.getInformation().getName()).append("\n\n");
        accountDetailsBuilder.append("Country: ").append(currentAccount.getInformation().getCountry()).append("\n\n");
        accountDetailsBuilder.append("Email: ").append(currentAccount.getInformation().getCredentials()
                .getEmail()).append("\n\n");
        accountDetailsBuilder.append("Favorite Games: ").append(String.join(", ",
                currentAccount.getInformation().getFavoriteGames()));

        accountDetailsArea.setText(accountDetailsBuilder.toString());
        accountDetailsPanel.add(accountDetailsArea);

        // Panel for character selection
        JPanel characterSelectionPanel = new JPanel(new BorderLayout());
        characterSelectionPanel.setBackground(GameColors.VERY_LIGHT_CYAN);
        characterSelectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        DefaultListModel<String> characterNames = new DefaultListModel<>();
        for (Characters character : availableCharacters) {
            characterNames.addElement(character.getName());
        }

        // Configure the list component
        JList<String> characterList = new JList<>(characterNames);
        characterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        characterList.setFixedCellHeight(30);
        characterList.setFixedCellWidth(200);
        characterList.setBorder(BorderFactory.createLineBorder(GameColors.MEDIUM_BLUE, 2));
        characterList.setBackground(GameColors.LIGHT_CYAN);
        characterList.setForeground(GameColors.DARK_BLUE);

        JScrollPane scrollPane = new JScrollPane(characterList);
        characterSelectionPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel for the selection button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(GameColors.VERY_LIGHT_CYAN);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        // Configure the 'Start Game' button
        JButton selectButton = new JButton("Start Game");
        selectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectButton.setBackground(GameColors.LIGHT_BLUE);
        selectButton.setForeground(Color.WHITE);
        selectButton.setFont(new Font("Arial", Font.BOLD, 14));
        selectButton.setFocusPainted(false);
        selectButton.setBorder(BorderFactory.createLineBorder(GameColors.DARK_BLUE, 2));

        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(selectButton);

        characterSelectionPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Split pane to show account details and character selection side by side
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, accountDetailsPanel,
                characterSelectionPanel);
        splitPane.setDividerLocation(200);
        splitPane.setResizeWeight(0.3);
        splitPane.setBackground(GameColors.VERY_LIGHT_CYAN);

        characterFrame.add(splitPane, BorderLayout.CENTER);

        // Action listener for character selection
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int selectedIndex = characterList.getSelectedIndex();
                if (selectedIndex != -1) {
                    // Set the selected character as the current character
                    currentCharacter = availableCharacters.get(selectedIndex);
                    JOptionPane.showMessageDialog(characterFrame, "Character selected: " +
                            currentCharacter.getName());
                    characterFrame.dispose();

                    initializeGame(); // Start the game
                } else {
                    JOptionPane.showMessageDialog(characterFrame, "Please select a character.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        characterFrame.setVisible(true);
    }


    private void initializeGame() {
        if (currentCharacter.level == 1) {
            enemiesDefeated = 0;
            experienceGained = 0;
            System.out.println("Running predefined scenario...");
            grid = Grid.generatePredefinedGrid(currentCharacter);
        } else {
            System.out.println("Starting normal gameplay...");
            generateGrid();
        }

        startGame();
    }


    private void generateGrid() {
        if (gameFrame != null && gameFrame.isDisplayable()) {
            gameFrame.dispose();
        }
        Random random = new Random();
        int lenght = 3 + random.nextInt(8);
        int width = 3 + random.nextInt(8);
        grid = Grid.generateGrid(lenght, width, currentCharacter);
    }


    private void startGame() {
        gameFrame = new JFrame("League of Warriors - Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(800, 600);
        gameFrame.setLayout(new BorderLayout());

        // Player statistics panel
        JPanel playerInfoPanel = new JPanel(new GridLayout(5, 1));
        JLabel levelLabel = new JLabel("Level: " + currentCharacter.getLevel());
        JLabel experienceLabel = new JLabel("Experience: " + currentCharacter.getExperience());
        JLabel healthLabel = new JLabel("Health: " + currentCharacter.getCurrentHealth());
        JLabel manaLabel = new JLabel("Mana: " + currentCharacter.getCurrentMana());

        playerInfoPanel.add(levelLabel);
        playerInfoPanel.add(experienceLabel);
        playerInfoPanel.add(healthLabel);
        playerInfoPanel.add(manaLabel);

        // Directional button panel
        JPanel controlsPanel = new JPanel(new GridLayout(4, 1));
        JButton northButton = new JButton("NORTH");
        JButton southButton = new JButton("SOUTH");
        JButton eastButton = new JButton("EAST");
        JButton westButton = new JButton("WEST");

        controlsPanel.add(northButton);
        controlsPanel.add(southButton);
        controlsPanel.add(eastButton);
        controlsPanel.add(westButton);

        gameFrame.add(playerInfoPanel, BorderLayout.WEST);
        gameFrame.add(controlsPanel, BorderLayout.EAST);

        // Grid panel for game map
        JPanel gridPanel = new JPanel(new GridLayout(grid.length, grid.width));
        JButton[][] gridButtons = new JButton[grid.length][grid.width];

        int i, j;
        for (i = 0; i < grid.length; i++) {
            for (j = 0; j < grid.width; j++) {
                gridButtons[i][j] = new JButton();
                // Disable buttons
                gridButtons[i][j].setEnabled(false);
                gridPanel.add(gridButtons[i][j]);
            }
        }

        gameFrame.add(gridPanel, BorderLayout.CENTER);

        // Action listener for directional movement
        ActionListener moveActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String command = ae.getActionCommand();
                try {
                    // Move the player based on the button clicked
                    switch (command) {
                        case "NORTH":
                            grid.goNorth();
                            break;
                        case "SOUTH":
                            grid.goSouth();
                            break;
                        case "EAST":
                            grid.goEast();
                            break;
                        case "WEST":
                            grid.goWest();
                            break;
                    }
                    refresh(gridButtons, levelLabel, experienceLabel, healthLabel, manaLabel, northButton, southButton,
                            eastButton, westButton);
                } catch (ImpossibleMoveException e) {
                    JOptionPane.showMessageDialog(gameFrame, "Please select a valid move.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        northButton.setActionCommand("NORTH");
        southButton.setActionCommand("SOUTH");
        eastButton.setActionCommand("EAST");
        westButton.setActionCommand("WEST");
        northButton.addActionListener(moveActionListener);
        southButton.addActionListener(moveActionListener);
        eastButton.addActionListener(moveActionListener);
        westButton.addActionListener(moveActionListener);

        refresh(gridButtons, levelLabel, experienceLabel, healthLabel, manaLabel, northButton, southButton, eastButton,
                westButton);

        gameFrame.setVisible(true);
    }

    private void refresh(JButton[][] gridButtons, JLabel levelLabel, JLabel experienceLabel, JLabel healthLabel,
                         JLabel manaLabel, JButton northButton, JButton southButton, JButton eastButton,
                         JButton westButton) {
        // Updade player's status labels
        levelLabel.setText("Level: " + currentCharacter.getLevel());
        experienceLabel.setText("Experience: " + currentCharacter.getExperience());
        healthLabel.setText("Health: " + currentCharacter.getCurrentHealth());
        manaLabel.setText("Mana: " + currentCharacter.getCurrentMana());

        // Refresh the grid panel
        JPanel gridPanel = (JPanel) gameFrame.getContentPane().getComponent(2);
        gridPanel.removeAll();

        gridButtons = new JButton[grid.length][grid.width];
        gridPanel.setLayout(new GridLayout(grid.length, grid.width, 5, 5));

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.width; j++) {
                JButton button = new JButton();
                button.setEnabled(false);

                Cell cell = grid.getCell(i, j);

                // Customize button appearance based on the cell type
                if (cell.equals(grid.currentCell)) {
                    button.setBackground(GameColors.DARK_BLUE);
                    button.setText("Player");
                    button.setForeground(Color.WHITE);
                    button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
                } else if (currentCharacter.getLevel() == 1) {
                    // Detailed view for level 1
                    switch (cell.getType()) {
                        case SANCTUARY:
                            button.setBackground(GameColors.MEDIUM_BLUE);
                            button.setText("Sanctuary");
                            button.setForeground(Color.WHITE);
                            button.setBorder(BorderFactory.createLineBorder(GameColors.DARK_BLUE, 2));
                            break;
                        case ENEMY:
                            button.setBackground(Color.RED);
                            button.setText("Enemy");
                            button.setForeground(Color.BLACK);
                            button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
                            break;
                        case PORTAL:
                            button.setBackground(GameColors.LIGHT_BLUE);
                            button.setText("Portal");
                            button.setForeground(Color.WHITE);
                            button.setBorder(BorderFactory.createDashedBorder(Color.WHITE, 2, 5));
                            break;
                        case VOID:
                            button.setBackground(GameColors.VERY_LIGHT_CYAN);
                            button.setBorder(BorderFactory.createLineBorder(GameColors.MEDIUM_BLUE, 1));
                            break;
                        default:
                            button.setBackground(GameColors.LIGHT_CYAN);
                            button.setBorder(BorderFactory.createLineBorder(GameColors.MEDIUM_BLUE, 1));
                            break;
                    }
                } else {
                    // Normal behavior for higher levels
                    if (cell.getVisited()) {
                        button.setBackground(GameColors.LIGHT_CYAN);
                        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                    } else {
                        button.setBackground(GameColors.VERY_LIGHT_CYAN);
                        button.setBorder(BorderFactory.createDashedBorder(Color.LIGHT_GRAY, 1, 4));
                    }
                }

                gridButtons[i][j] = button;
                gridPanel.add(button);
            }
        }

        // Refresh and redraw the grid panel
        gameFrame.revalidate();
        gameFrame.repaint();

        Cell currentCell = grid.currentCell;

        // Update the navigation buttons' enabled state
        northButton.setEnabled(currentCell.getX() > 0);
        southButton.setEnabled(currentCell.getX() < grid.length - 1);
        westButton.setEnabled(currentCell.getY() > 0);
        eastButton.setEnabled(currentCell.getY() < grid.width - 1);

        // Handle special cell types
        switch (currentCell.getType()) {
            case SANCTUARY:
                Random random = new Random();
                int healthRestored = random.nextInt(20, 50);
                int manaRestored = random.nextInt(10, 30);

                currentCharacter.regenarateHealth(healthRestored);
                currentCharacter.regenarateMana(manaRestored);

                JOptionPane.showMessageDialog(null, "You have entered a Sanctuary! " +
                        "\nHealth restored: " + healthRestored +
                        "\nMana restored: " + manaRestored, "Sanctuary", JOptionPane.INFORMATION_MESSAGE);
                break;

            case PORTAL:
                experienceGained += currentCharacter.getLevel() * 5;

                currentCharacter.gainExperience(experienceGained);
                currentCharacter.regenarateHealth(currentCharacter.getMaxHealth());
                currentCharacter.regenarateMana(currentCharacter.getMaxMana());

                JOptionPane.showMessageDialog(null, "You have found a Portal! " +
                        "\nExperience gained: " + experienceGained +
                        "\nYou are fully restored and moving to the next level!", "Portal",
                        JOptionPane.INFORMATION_MESSAGE);

                currentCharacter.levelUp();
                currentAccount.incrementGamesCompleted();

                generateGrid();

                displayLevelSummary(currentCharacter, enemiesDefeated, experienceGained);
                return;
            case ENEMY:
                JOptionPane.showMessageDialog(null, "You have encountered an enemy! Prepare " +
                        "for battle.", "Enemy Encounter", JOptionPane.WARNING_MESSAGE);
                Enemy enemy = new Enemy(currentCharacter.level);
                startBattle(currentCharacter, enemy);

                currentCell.setType(CellEntityType.VISITED);
                break;

            default:
                break;
        }

        currentCell.setType(CellEntityType.VISITED);

        gameFrame.revalidate();
        gameFrame.repaint();
    }

    private void displayLevelSummary(Characters player, int enemiesDefeated, int experienceGained) {
        JFrame summaryFrame = new JFrame("Level Summary");
        summaryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        summaryFrame.setSize(600, 400);
        summaryFrame.setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Level Completed!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(GameColors.DARK_BLUE);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setPreferredSize(new Dimension(600, 50));
        summaryFrame.add(titleLabel, BorderLayout.NORTH);

        // Main panel
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(GameColors.VERY_LIGHT_CYAN);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Display the characrer's image
        JLabel characterImage = new JLabel();
        try {
            String playerImageFile = player.getClass().getSimpleName() + ".png";
            characterImage.setIcon(loadAndScaleImage(playerImageFile, 150, 150));
        } catch (IllegalArgumentException e) {
            characterImage.setText("No Image Available");
            characterImage.setHorizontalAlignment(SwingConstants.CENTER);
            characterImage.setForeground(GameColors.DARK_BLUE);
        }
        characterImage.setBorder(BorderFactory.createLineBorder(GameColors.MEDIUM_BLUE, 3));
        summaryPanel.add(characterImage, BorderLayout.WEST);

        // Panel for progress details
        JPanel detailsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        detailsPanel.setBackground(GameColors.LIGHT_CYAN);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel characterLabel = new JLabel("Character: " + player.getName());
        characterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        characterLabel.setForeground(GameColors.DARK_BLUE);

        JLabel levelLabel = new JLabel("Level: " + player.getLevel());
        levelLabel.setFont(new Font("Arial", Font.BOLD, 14));
        levelLabel.setForeground(GameColors.MEDIUM_BLUE);

        JLabel experienceLabel = new JLabel("Experience Gained: " + experienceGained);
        experienceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        experienceLabel.setForeground(GameColors.LIGHT_BLUE);

        JLabel enemiesDefeatedLabel = new JLabel("Enemies Defeated: " + enemiesDefeated);
        enemiesDefeatedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        enemiesDefeatedLabel.setForeground(Color.RED);

        detailsPanel.add(characterLabel);
        detailsPanel.add(levelLabel);
        detailsPanel.add(experienceLabel);
        detailsPanel.add(enemiesDefeatedLabel);
        summaryPanel.add(detailsPanel, BorderLayout.CENTER);

        // Panel for the 'Next level' button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(GameColors.VERY_LIGHT_CYAN);

        JButton nextLevelButton = new JButton("Start Next Level");
        nextLevelButton.setBackground(GameColors.LIGHT_BLUE);
        nextLevelButton.setForeground(Color.WHITE);
        nextLevelButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextLevelButton.setBorder(BorderFactory.createLineBorder(GameColors.DARK_BLUE, 2));

        nextLevelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                summaryFrame.dispose();
                generateGrid();
                startGame();
            }
        });

        buttonPanel.add(nextLevelButton);
        summaryPanel.add(buttonPanel, BorderLayout.SOUTH);

        summaryFrame.add(summaryPanel, BorderLayout.CENTER);
        summaryFrame.setVisible(true);
    }


    private ImageIcon loadAndScaleImage(String fileName, int width, int height) {
        String filePath = "src/resources/images/" + fileName;
        java.io.File file = new java.io.File(filePath);

        if (!file.exists()) {
            System.err.println("Failed to load image: " + filePath);
            throw new IllegalArgumentException("File not found: " + filePath);
        }

        ImageIcon icon = new ImageIcon(filePath);
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return new ImageIcon(scaledImage);
    }

    private void startBattle(Characters player, Enemy enemy) {
        // Generate abilities for player and enemy
        player.setAbilities(generateAbilities());
        enemy.setAbilities(generateAbilities());

        battleFrame = new JFrame("Fight");
        battleFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        battleFrame.setSize(800, 600);
        battleFrame.setLayout(new BorderLayout());
        battleFrame.getContentPane().setBackground(GameColors.VERY_LIGHT_CYAN);

        int imageWidth = 350;
        int imageHeight = 350;

        // Player stats and image
        JPanel playerPanel = new JPanel(new BorderLayout());
        playerPanel.setBackground(GameColors.LIGHT_CYAN);
        playerPanel.setBorder(BorderFactory.createLineBorder(GameColors.MEDIUM_BLUE, 3));

        String playerImageFile = player.getClass().getSimpleName() + ".png";
        JLabel playerImage = new JLabel(loadAndScaleImage(playerImageFile, imageWidth, imageHeight));

        playerPanel.add(playerImage, BorderLayout.CENTER);

        JPanel playerStats = new JPanel(new GridLayout(2, 1));
        playerStats.setBackground(GameColors.LIGHT_CYAN);
        JLabel playerHealth = new JLabel("Health: " + player.getCurrentHealth());
        playerHealth.setForeground(GameColors.DARK_BLUE);
        JLabel playerMana = new JLabel("Mana: " + player.getCurrentMana());
        playerMana.setForeground(GameColors.DARK_BLUE);

        playerStats.add(playerHealth);
        playerStats.add(playerMana);
        playerPanel.add(playerStats, BorderLayout.SOUTH);

        // Enemy stats and image
        JPanel enemyPanel = new JPanel(new BorderLayout());
        enemyPanel.setBackground(GameColors.LIGHT_CYAN);
        enemyPanel.setBorder(BorderFactory.createLineBorder(GameColors.MEDIUM_BLUE, 3));
        JLabel enemyImage = new JLabel(loadAndScaleImage("Enemy.png", imageWidth, imageHeight));

        enemyPanel.add(enemyImage, BorderLayout.CENTER);

        JPanel enemyStats = new JPanel(new GridLayout(2, 1));
        enemyStats.setBackground(GameColors.LIGHT_CYAN);
        JLabel enemyHealth = new JLabel("Health: " + enemy.getCurrentHealth());
        enemyHealth.setForeground(GameColors.DARK_BLUE);
        JLabel enemyMana = new JLabel("Mana: " + enemy.getCurrentMana());
        enemyMana.setForeground(GameColors.DARK_BLUE);

        enemyStats.add(enemyHealth);
        enemyStats.add(enemyMana);
        enemyPanel.add(enemyStats, BorderLayout.SOUTH);

        //  Action panel for attack and ability buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        actionPanel.setBackground(GameColors.VERY_LIGHT_CYAN);

        JButton attackButton = new JButton("ATTACK");
        attackButton.setBackground(GameColors.LIGHT_BLUE);
        attackButton.setForeground(Color.WHITE);
        attackButton.setFont(new Font("Arial", Font.BOLD, 14));
        attackButton.setBorder(BorderFactory.createLineBorder(GameColors.DARK_BLUE, 2));

        JButton abilityButton = new JButton("ABILITY");
        abilityButton.setBackground(GameColors.MEDIUM_BLUE);
        abilityButton.setForeground(Color.WHITE);
        abilityButton.setFont(new Font("Arial", Font.BOLD, 14));
        abilityButton.setBorder(BorderFactory.createLineBorder(GameColors.DARK_BLUE, 2));

        actionPanel.add(attackButton);
        actionPanel.add(abilityButton);

        // Message area for battle log
        JTextArea messageArea = new JTextArea(5, 30);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBackground(GameColors.VERY_LIGHT_CYAN);
        messageArea.setForeground(GameColors.DARK_BLUE);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane messageScroll = new JScrollPane(messageArea);
        battleFrame.add(messageScroll, BorderLayout.SOUTH);

        // Action listener for the attack button
        attackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int damageDealt = player.getDamage();
                enemy.receiveDamage(damageDealt, messageArea);
                enemyHealth.setText("Health: " + enemy.getCurrentHealth());

                if (enemy.getCurrentHealth() <= 0) {
                    messageArea.append("You defeated the enemy!\n");
                    enemiesDefeated++;
                    JOptionPane.showMessageDialog(battleFrame, "You defeated the enemy!", "Victory",
                            JOptionPane.INFORMATION_MESSAGE);
                    battleFrame.dispose();
                    return;
                }

                Random random = new Random();
                boolean useAbility = random.nextBoolean();

                if (useAbility && !enemy.getAbilities().isEmpty()) {
                    Spell enemyAbility = enemy.getAbilities().get(random.nextInt(enemy.getAbilities().size()));
                    enemy.setCurrentMana(enemy.getCurrentMana() - enemyAbility.getManaCost());
                    player.receiveDamage(enemyAbility, messageArea);
                    playerHealth.setText("Health: " + player.getCurrentHealth());
                    messageArea.append("Enemy used " + enemyAbility.getClass().getSimpleName() + " for " +
                            enemyAbility.getDamage() + " damage.\n");
                } else {
                    int enemyDamage = enemy.getDamage();
                    player.receiveDamage(enemyDamage, messageArea);
                    playerHealth.setText("Health: " + player.getCurrentHealth());
                    messageArea.append("Enemy attacked for " + enemyDamage + " damage.\n");
                }

                if (player.getCurrentHealth() <= 0) {
                    messageArea.append("You have been defeated!\n");
                    JOptionPane.showMessageDialog(battleFrame, "You have been defeated!", "Defeat",
                            JOptionPane.ERROR_MESSAGE);
                    battleFrame.dispose();
                    selectCharacter();
                }
            }
        });

        // Action listener for the ability button
        abilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayAbilitySelection(player, enemy, playerMana, enemyHealth, playerHealth, messageArea);

                playerHealth.setText("Health: " + player.getCurrentHealth());
                enemyHealth.setText("Health: " + enemy.getCurrentHealth());
            }
        });

        battleFrame.add(playerPanel, BorderLayout.WEST);
        battleFrame.add(enemyPanel, BorderLayout.EAST);
        battleFrame.add(actionPanel, BorderLayout.NORTH);

        battleFrame.setVisible(true);
    }


    private void displayAbilitySelection(Characters player, Enemy enemy, JLabel playerManaLabel,
                                         JLabel enemyHealthLabel, JLabel playerHealthLabel, JTextArea messageArea) {
        JFrame abilityFrame = new JFrame("Select an Ability");
        abilityFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        abilityFrame.setSize(600, 400);
        abilityFrame.setLayout(new BorderLayout());

        // Panel for displaying the list of abilities
        JPanel abilityListPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        abilityListPanel.setBackground(GameColors.VERY_LIGHT_CYAN);

        if (player.getAbilities().isEmpty()) {
            JLabel noAbilitiesLabel = new JLabel("No abilities available!");
            noAbilitiesLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noAbilitiesLabel.setForeground(GameColors.DARK_BLUE);
            noAbilitiesLabel.setFont(new Font("Arial", Font.BOLD, 16));
            abilityFrame.add(noAbilitiesLabel, BorderLayout.CENTER);
            abilityFrame.setVisible(true);
            return;
        }

        // Iterate through each ability and create a panel to display it
        for (Spell ability : player.getAbilities()) {
            JPanel abilityPanel = new JPanel(new BorderLayout());
            abilityPanel.setBorder(BorderFactory.createLineBorder(GameColors.DARK_BLUE, 2));
            abilityPanel.setBackground(GameColors.LIGHT_CYAN);

            // Display the ability's image
            JLabel abilityImage = new JLabel();
            try {
                abilityImage.setIcon(loadAndScaleImage(ability.getClass().getSimpleName() + ".png", 80,
                        80));
            } catch (IllegalArgumentException e) {
                abilityImage.setText("No Image");
                abilityImage.setHorizontalAlignment(SwingConstants.CENTER);
                abilityImage.setForeground(GameColors.DARK_BLUE);
            }
            abilityPanel.add(abilityImage, BorderLayout.WEST);

            // Panel to display details of the ability
            JPanel detailsPanel = new JPanel(new GridLayout(3, 1));
            detailsPanel.setBackground(GameColors.VERY_LIGHT_CYAN);
            JLabel nameLabel = new JLabel("Name: " + ability.getClass().getSimpleName());
            nameLabel.setForeground(GameColors.DARK_BLUE);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
            JLabel manaCostLabel = new JLabel("Mana Cost: " + ability.getManaCost());
            manaCostLabel.setForeground(GameColors.MEDIUM_BLUE);
            JLabel damageLabel = new JLabel("Damage: " + ability.getDamage());
            damageLabel.setForeground(GameColors.LIGHT_BLUE);

            detailsPanel.add(nameLabel);
            detailsPanel.add(manaCostLabel);
            detailsPanel.add(damageLabel);
            abilityPanel.add(detailsPanel, BorderLayout.CENTER);

            // Button to use the ability
            JButton useButton = new JButton("Use");
            useButton.setBackground(GameColors.LIGHT_BLUE);
            useButton.setForeground(Color.WHITE); // Text alb
            useButton.setFont(new Font("Arial", Font.BOLD, 14));
            useButton.setFocusPainted(false);
            useButton.setBorder(BorderFactory.createLineBorder(GameColors.DARK_BLUE, 2));

            // Action Listener for the button
            useButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (player.getCurrentMana() >= ability.getManaCost()) {

                        player.setCurrentMana(player.getCurrentMana() - ability.getManaCost());
                        enemy.receiveDamage(ability, messageArea);
                        player.getAbilities().remove(ability);

                        playerManaLabel.setText("Mana: " + player.getCurrentMana());
                        enemyHealthLabel.setText("Health: " + enemy.getCurrentHealth());
                        messageArea.append("You used " + ability.getClass().getSimpleName() + " for " +
                                ability.getDamage() + " damage.\n");

                        // Check if the enemy is defeated
                        if (enemy.getCurrentHealth() <= 0) {
                            //messageArea.append("Enemy defeated!\n");
                            enemiesDefeated++;
                            JOptionPane.showMessageDialog(abilityFrame, "You defeated the enemy!");
                            abilityFrame.dispose();
                            battleFrame.dispose();
                        } else {
                            // Enemy counterattacks
                            Random random = new Random();
                            boolean useAbility = random.nextBoolean();

                            if (useAbility && !enemy.getAbilities().isEmpty()) {
                                Spell enemyAbility = enemy.getAbilities().get(random.nextInt(enemy.getAbilities()
                                        .size()));
                                enemy.setCurrentMana(enemy.getCurrentMana() - enemyAbility.getManaCost());
                                player.receiveDamage(enemyAbility, messageArea);
                                playerHealthLabel.setText("Health: " + player.getCurrentHealth());
                                messageArea.append("Enemy used " + enemyAbility.getClass().getSimpleName() + " for " +
                                        enemyAbility.getDamage() + " damage.\n");
                            } else {
                                int enemyDamage = enemy.getDamage();
                                player.receiveDamage(enemyDamage, messageArea);
                                playerHealthLabel.setText("Health: " + player.getCurrentHealth());
                                messageArea.append("Enemy attacked for " + enemyDamage + " damage.\n");
                            }


                            // Check if the player is defeated
                            if (player.getCurrentHealth() <= 0) {
                                messageArea.append("You were defeated!\n");
                                JOptionPane.showMessageDialog(battleFrame, "You were defeated!");
                                battleFrame.dispose();
                                abilityFrame.dispose();
                                selectCharacter();
                            }
                        }

                        // Close the ability selection frame after use
                        abilityFrame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(abilityFrame, "Not enough mana!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            abilityPanel.add(useButton, BorderLayout.EAST);
            abilityListPanel.add(abilityPanel);
        }

        JScrollPane scrollPane = new JScrollPane(abilityListPanel);
        scrollPane.setBackground(GameColors.VERY_LIGHT_CYAN);
        abilityFrame.add(scrollPane, BorderLayout.CENTER);

        // Title for the frame
        JLabel titleLabel = new JLabel("Select an Ability", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(GameColors.DARK_BLUE);
        abilityFrame.add(titleLabel, BorderLayout.NORTH);

        abilityFrame.setVisible(true);
    }

    private String getUserCommand() {
        StringBuilder commands = new StringBuilder();
        Cell currentCell = grid.currentCell;
        commands.append("Available commands: ");

        if (currentCell.getX() > 0) {
            commands.append("W (North)  ");
        }
        if (currentCell.getX() < grid.length - 1) {
            commands.append("S (South)  ");
        }
        if (currentCell.getY() > 0) {
            commands.append("A (West)  ");
        }
        if (currentCell.getY() < grid.width - 1) {
            commands.append("D (East)  ");
        }
        commands.append("Q (Quit)");

        System.out.println();
        System.out.println(commands);
        System.out.print("Enter your command: ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().toUpperCase();
    }


    private void displayCharacterInfo() {
        System.out.println("Character: " + currentCharacter.getName());
        System.out.println("Level: " + currentCharacter.getLevel());
        System.out.println("Experience: " + currentCharacter.getExperience());
        System.out.println("Health: " + currentCharacter.getCurrentHealth() + "/" + currentCharacter.getMaxHealth());
        System.out.println("Mana: " + currentCharacter.getCurrentMana() + "/" + currentCharacter.getMaxMana());
    }


    private void handleCommand(String command) throws InvalidCommandException {
        try {
            switch (command) {
                case "W":
                    grid.goNorth();
                    break;
                case "S":
                    grid.goSouth();
                    break;
                case "D":
                    grid.goEast();
                    break;
                case "A":
                    grid.goWest();
                    break;
                case "Q":
                    //isRunning = false;
                    System.out.println("Game exited");
                    break;
                default:
                    System.out.println("Invalid command");
            }

            Cell currentCell = grid.currentCell;
            switch (currentCell.getType()) {
                case ENEMY:
                    Enemy enemy = new Enemy(currentCharacter.level);

                    startBattle(currentCharacter, enemy);

                    currentCell.setType(CellEntityType.VISITED);
                    break;

                case SANCTUARY:
                    System.out.println("You have entered a sanctuary. Health and mana are resorted!");
                    Random random = new Random();
                    int healthRestored = random.nextInt(20, 50);
                    int manaRestored = random.nextInt(10, 30);

                    currentCharacter.regenarateHealth(healthRestored);
                    currentCharacter.regenarateMana(manaRestored);

                    currentCell.setType(CellEntityType.VISITED);
                    break;

                case PORTAL:
                    int experienceGained = currentCharacter.getLevel() * 5;

                    currentCharacter.gainExperience(experienceGained);
                    currentCharacter.regenarateHealth(currentCharacter.getMaxHealth());
                    currentCharacter.regenarateMana(currentCharacter.getMaxMana());

                    System.out.println("You have found a portal! Experience gained: " + experienceGained);
                    currentCharacter.levelUp();
                    currentAccount.incrementGamesCompleted();

                    generateGrid();
                    break;

                case VISITED:
                default:
                    break;
            }
        } catch (ImpossibleMoveException e) {
            System.out.println(e.getMessage());
        }
    }


}


