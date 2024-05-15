import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class LifeQuestUI extends JFrame {
 
    JPanel leftPanel, rightPanel;
    private Font customFont;
    private LifeQuest game;
    SpriteAnimation playerAnimation;
    private Player player;
    private JTextArea battlePromptTextArea;
    private JProgressBar healthBar;
    private JProgressBar manaBar;
    private int maxPlayerHealth;
    private int maxPlayerMana;
    private EnemyGenerator enemyGenerator;
    private Scanner scanner;
    private LifeQuestUI ui;
    private EnemyInfoBox enemyInfoBox;
    private Enemy enemy;
    JLayeredPane layeredPane;
    SpriteAnimation skeletonAnimation;
    private Battle battle;
    public JButton attackButton;

    private void run() {
        // Call runGameLoop repeatedly as long as the player is playing
        while (game.isPlayerPlaying()) {
            game.runGameLoop(this, enemyInfoBox); // Removed enemyInfoBox argument
            try {
                Thread.sleep(100); // Add a small delay to prevent excessive looping
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class TextAreaOutputStream extends OutputStream {
        private JTextArea textArea;

        public TextAreaOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            textArea.append(String.valueOf((char) b));
            textArea.setCaretPosition(textArea.getDocument().getLength()); 
        }
    }

    public LifeQuestUI(LifeQuest game, Player player) {
        this.game = game;
        this.ui = this;
        this.enemyGenerator = new EnemyGenerator(); 
        this.scanner = new Scanner(System.in);
        this.maxPlayerHealth = player.getHealth();
        this.maxPlayerMana = player.getMana();
        try {
            InputStream fontStream = getClass().getResourceAsStream("resources/myFont.TTF");
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("resources/bg-night.png"));

        setTitle("LifeQuest"); 
        setSize(1024, 514); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setResizable(false);
 
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH; 
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        leftPanel = new JPanel();
        leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(514, 514));

        rightPanel = new JPanel();
        rightPanel.setBackground(new Color(245, 245, 245));

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(leftPanel.getPreferredSize());
        try {
            BufferedImage spriteSheet = ImageIO.read(getClass().getResource("/resources/playerSheet.png"));

            int frameWidth = 50;
            int frameHeight = 37;
            int startCol = 4; 
            int startRow = 6 - 1; 
            int endCol = 7 - 1;
            int endRow = 6 - 1; 
            double scaleFactor = 3.0;
            int totalFrames = endCol - startCol + 1;
 
            playerAnimation = new SpriteAnimation(spriteSheet, frameWidth, frameHeight, totalFrames, 350, scaleFactor, startCol, startRow, endCol, endRow);

            playerAnimation.setBounds(10, 245, playerAnimation.getIcon().getIconWidth(), playerAnimation.getIcon().getIconHeight());
            playerAnimation.startAnimation();
        } catch (IOException e) {
            e.printStackTrace();
        }
        layeredPane.add(playerAnimation, JLayeredPane.PALETTE_LAYER);
        playerAnimation.startAnimation();
        leftPanel.add(layeredPane, BorderLayout.CENTER);
        rightPanel.setLayout(new BorderLayout());
 
        JLabel battlePromptLabel = new JLabel("BATTLE PROMPT");
        JTextArea battlePromptTextArea = new JTextArea(5, 20);
        battlePromptTextArea.setEditable(false);
 
        battlePromptLabel.setHorizontalAlignment(JLabel.CENTER);
        battlePromptLabel.setFont(customFont.deriveFont(14f));
        battlePromptTextArea.setFont(customFont.deriveFont(14f));
        battlePromptTextArea.setLineWrap(true);
        battlePromptTextArea.setWrapStyleWord(true);
        battlePromptTextArea.setBackground(new Color(179, 179, 179));
        battlePromptTextArea.setForeground(Color.WHITE);
        battlePromptTextArea.setCaret(new BetterCaret());
 
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints centerPanelConstraints = new GridBagConstraints();
        centerPanelConstraints.fill = GridBagConstraints.BOTH;
        centerPanelConstraints.insets = new Insets(10, 10, 0, 10);
 
        centerPanelConstraints.gridx = 0;
        centerPanelConstraints.gridy = 0;
        centerPanelConstraints.weightx = 1.0;
        centerPanelConstraints.weighty = 0.0;
        centerPanel.add(battlePromptLabel, centerPanelConstraints);
 
        centerPanelConstraints.gridx = 0;
        centerPanelConstraints.gridy = 1;
        centerPanelConstraints.weightx = 1.0;
        centerPanelConstraints.weighty = 1.0; 

        TextAreaOutputStream textAreaOutputStream = new TextAreaOutputStream(battlePromptTextArea);
        PrintStream printStream = new PrintStream(textAreaOutputStream);
        System.setOut(printStream);
        System.setErr(printStream);

        JScrollPane scrollPane = new JScrollPane(battlePromptTextArea);
        scrollPane.setPreferredSize(new Dimension(rightPanel.getWidth() - 20, 200));

        Color customTroughColor = new Color(179, 179, 179);
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI(customTroughColor));

        centerPanel.add(scrollPane, centerPanelConstraints);
        centerPanel.add(scrollPane, centerPanelConstraints);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10)); 
        attackButton = new JButton("ATTACK");
        JButton skillButton = new JButton("SKILL");
        JButton speechButton = new JButton("SPEECH");
        JButton itemButton = new JButton("ITEM");

        buttonPanel.add(attackButton);
        buttonPanel.add(skillButton);
        buttonPanel.add(speechButton);
        buttonPanel.add(itemButton);

        attackButton.setBorderPainted(false);
        skillButton.setBorderPainted(false);
        speechButton.setBorderPainted(false);
        itemButton.setBorderPainted(false);

        Color buttonBgColor = new Color(179, 179, 179); 
        Color buttonTextColor = new Color(50, 50, 50); 

        attackButton.setBackground(buttonBgColor);
        attackButton.setForeground(buttonTextColor);
        skillButton.setBackground(buttonBgColor);
        skillButton.setForeground(buttonTextColor);
        speechButton.setBackground(buttonBgColor);
        speechButton.setForeground(buttonTextColor);
        itemButton.setBackground(buttonBgColor);
        itemButton.setForeground(buttonTextColor);

        attackButton.setFont(customFont.deriveFont(Font.BOLD, 14f)); 
        skillButton.setFont(customFont.deriveFont(Font.BOLD, 14f));
        speechButton.setFont(customFont.deriveFont(Font.BOLD, 14f));
        itemButton.setFont(customFont.deriveFont(Font.BOLD, 14f));

        attackButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                attackButton.setBackground(new Color(200, 200, 200)); 
            }

            @Override
            public void mouseExited(MouseEvent e) {
                attackButton.setBackground(buttonBgColor); 
            }
        });
        skillButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                skillButton.setBackground(new Color(200, 200, 200)); 
            }

            @Override
            public void mouseExited(MouseEvent e) {
                skillButton.setBackground(buttonBgColor); 
            }
        });
        speechButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                speechButton.setBackground(new Color(200, 200, 200)); 
            }

            @Override
            public void mouseExited(MouseEvent e) {
                speechButton.setBackground(buttonBgColor); 
            }
        });
        itemButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                itemButton.setBackground(new Color(200, 200, 200)); 
            }

            @Override
            public void mouseExited(MouseEvent e) {
                itemButton.setBackground(buttonBgColor); 
            }
        });
        this.healthBar = new JProgressBar(0, player.getHealth()); 
        healthBar.setStringPainted(true); 
        healthBar.setForeground(Color.RED); 
        healthBar.setFont(customFont.deriveFont(14f));

        this.manaBar = new JProgressBar(0, player.getMana()); 
        manaBar.setStringPainted(true);
        manaBar.setForeground(Color.BLUE); 
        manaBar.setFont(customFont.deriveFont(14f));

        JPanel barsPanel = new JPanel(new GridLayout(2, 1, 5, 5)); 
        barsPanel.add(healthBar);
        barsPanel.add(manaBar);
        JPanel rightBottomPanel = new JPanel(new GridLayout(2, 1, 5, 15));
        rightBottomPanel.add(buttonPanel);
        rightBottomPanel.add(barsPanel);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 0.5;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(10, 10, 10, 10);
        add(rightBottomPanel, constraints);

        JPanel tabsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

        JButton homeButton = new JButton("HOME");
        JButton inventoryButton = new JButton("INVENTORY");
        JButton skillsButton = new JButton("SKILLS");
        JButton questsButton = new JButton("QUESTS");

        homeButton.setBorderPainted(false);
        homeButton.setContentAreaFilled(false);
        homeButton.setFocusPainted(false);

        inventoryButton.setBorderPainted(false);
        inventoryButton.setContentAreaFilled(false);
        inventoryButton.setFocusPainted(false);

        skillsButton.setBorderPainted(false);
        skillsButton.setContentAreaFilled(false);
        skillsButton.setFocusPainted(false);

        questsButton.setBorderPainted(false);
        questsButton.setContentAreaFilled(false);
        questsButton.setFocusPainted(false);

        homeButton.setFont(customFont.deriveFont(Font.BOLD, 14f));
        inventoryButton.setFont(customFont.deriveFont(Font.BOLD, 14f));
        skillsButton.setFont(customFont.deriveFont(Font.BOLD, 14f));
        questsButton.setFont(customFont.deriveFont(Font.BOLD, 14f));

        tabsPanel.add(homeButton);
        tabsPanel.add(inventoryButton);
        tabsPanel.add(skillsButton);
        tabsPanel.add(questsButton);
 
        JPanel cardsPanel = new JPanel(new CardLayout());

 
        JPanel homeCard = new JPanel(new BorderLayout());
        homeCard.add(centerPanel, BorderLayout.NORTH);
        homeCard.add(rightBottomPanel, BorderLayout.SOUTH);

        JPanel inventoryCard = new JPanel();
        JPanel skillsCard = new JPanel();
        JPanel questsCard = new JPanel();

        cardsPanel.add(homeCard, "homeCard");
        cardsPanel.add(inventoryCard, "inventoryCard");
        cardsPanel.add(skillsCard, "skillsCard");
        cardsPanel.add(questsCard, "questsCard");

        rightPanel.add(tabsPanel, BorderLayout.NORTH);
        rightPanel.add(cardsPanel, BorderLayout.CENTER);

        CardLayout cardLayout = (CardLayout) cardsPanel.getLayout();

        homeButton.addActionListener(e -> cardLayout.show(cardsPanel, "homeCard"));
        inventoryButton.addActionListener(e -> cardLayout.show(cardsPanel, "inventoryCard"));
        skillsButton.addActionListener(e -> cardLayout.show(cardsPanel, "skillsCard"));
        questsButton.addActionListener(e -> cardLayout.show(cardsPanel, "questsCard"));

        int squareSize = getHeight() / 2;

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.5;
        constraints.weighty = 1.0; 
        leftPanel.setPreferredSize(new Dimension(squareSize, squareSize));
        add(leftPanel, constraints);
 
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.5;
        constraints.weighty = 1.0;
        rightPanel.setPreferredSize(new Dimension(squareSize, squareSize));
        add(rightPanel, constraints);

        healthBar.setFocusable(false); 
        healthBar.setBorderPainted(false); 
        manaBar.setFocusable(false); 
        manaBar.setBorderPainted(false); 
        scrollPane.setFocusable(false); 
        scrollPane.setBorder(null);
        healthBar.setUI(new RoundedProgressBarUI());
        manaBar.setUI(new RoundedProgressBarUI());

        updateHealthBar();
        updateManaBar();
 
    }
    public void updateHealthBar() {
        int currentHealth = game.getPlayer().getHealth();
        int percentage = (int) (((double) currentHealth / maxPlayerHealth) * 100); 
        healthBar.setValue(percentage); 
        healthBar.setString(percentage + "%");
    }
    public void updateManaBar() {
        int currentMana = game.getPlayer().getMana();
        int manaPercentage = (int) (((double) currentMana / maxPlayerMana) * 100); 
        manaBar.setValue(manaPercentage); 
        manaBar.setString(manaPercentage + "%");
    }
    public void createEnemyInfoBox(Enemy enemy) {
        System.out.println("Creating enemy info box for enemy: " + enemy.getName());
        if (layeredPane != null) {
            enemyInfoBox = new EnemyInfoBox();
            skeletonAnimation = new SpriteAnimation("/resources/gifs/skeleton/skelly.gif", 100, 3.5);

            skeletonAnimation.setBounds(350, 240,  skeletonAnimation.getIcon().getIconWidth(), skeletonAnimation.getIcon().getIconHeight());
            skeletonAnimation.startAnimation();
            layeredPane.add(skeletonAnimation, JLayeredPane.PALETTE_LAYER);


            enemyInfoBox.setBounds(skeletonAnimation.getX(), skeletonAnimation.getY() - enemyInfoBox.getPreferredSize().height - 10, // Position above the sprite
                    enemyInfoBox.getPreferredSize().width, enemyInfoBox.getPreferredSize().height);
            layeredPane.add(enemyInfoBox, JLayeredPane.PALETTE_LAYER);
            updateEnemyInfoBox(enemy);
            leftPanel.revalidate();
            leftPanel.repaint();
        }
    }


    public void removeEnemyAndInfoBox() {
        System.out.println("RemoveEnemyAndInfoBox: Removing enemy info box and enemy sprite");
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (layeredPane != null) {
                    if (skeletonAnimation != null) layeredPane.remove(skeletonAnimation);
                    if (enemyInfoBox != null) layeredPane.remove(enemyInfoBox);
                    layeredPane.revalidate();
                    layeredPane.repaint();
                    skeletonAnimation = null;
                    enemyInfoBox = null;
                }
                Thread.sleep(500);
                return null;
            }
            @Override
            protected void done() {
                if (game.isPlayerPlaying()) { // Only continue the game loop if the player is still playing.
                    SwingUtilities.invokeLater(() -> run());
                }
            }
        };
        worker.execute();
    }
    public Battle getBattle() {
        return battle;
    }
    public void updateEnemyInfoBox(Enemy enemy) {
        System.out.println("Updating enemy info box for: " + enemy.getName());
        if (enemyInfoBox != null) { 
            enemyInfoBox.updateInfo(enemy); 
        } else {
            enemyInfoBox.updateInfo(null);
        }
    }
    public Scanner getScanner() {
        return scanner;
    }


    public void startGameLoop() {
        new Thread(this::run).start();
    }
    public void setBattle(Battle battle) {
        this.battle = null; // Clear previous battle reference
        this.battle = battle;
        attackButton.setEnabled(false);

        // Now you can add the action listener
        attackButton.addActionListener(e -> {
            if (game.isPlayerPlaying() && battle != null && battle.isPlayerTurn) { // Check if battle is still valid
                battle.playerTurn();
                SwingUtilities.invokeLater(() -> updateEnemyInfoBox(battle.getEnemy()));
            }
        });
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LifeQuest game = new LifeQuest();
            LifeQuestUI ui = new LifeQuestUI(game, game.getPlayer());
            ui.setVisible(true);
            ui.startGameLoop(); 
        });
    }
}
