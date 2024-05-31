import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.List;
public class LifeQuestUI extends JFrame {
 
    JPanel leftPanel, rightPanel;
    private Font customFont;
    public LifeQuest game;
    CharacterAnimation playerAnimation;
    SpriteAnimation playerAttackAnimation;
    private SpriteAnimation playerRunAnimation;
    private JProgressBar healthBar;
    private JProgressBar manaBar;
    private JProgressBar levelBar;
    private int maxPlayerHealth;
    private int maxPlayerMana;
    private Scanner scanner;
    private EnemyInfoBox enemyInfoBox;
    JLayeredPane layeredPane;
    CharacterAnimation skeletonAnimation;
    private Battle battle;
    public JButton attackButton;
    SpriteAnimation playerHurtAnimation;
    public HashMap<String, CharacterAnimation> enemyAnimations;
    private DatabaseConnector databaseConnector;
    private List<String> initialItems = new ArrayList<>();
    public static JPanel inventoryGridPanel;
    private JPanel inventoryCard;
    private JPanel cardsPanel;
    private JPanel questsCard;
    private int backgroundX = 0;
    private ImageIcon backgroundIcon;
    private Thread backgroundAnimationThread;
    private volatile boolean bgPlaying = true;
    private void run() {
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
    public LifeQuestUI(LifeQuest game, Map<String, Object> playerData) {
        this.game = game;
        game.setUi(this);
        this.scanner = new Scanner(System.in);
        this.maxPlayerHealth = (int) playerData.get("health");
        this.maxPlayerMana = (int)playerData.get("mana");
        this.levelBar = new JProgressBar(0, game.getPlayer().getExperienceToNextLevel());
        enemyAnimations = new HashMap<>();
        databaseConnector = new DatabaseConnector();
        levelBar.setValue(game.getPlayer().getExperience());
        levelBar.setString("Lvl " + game.getPlayer().getLevel() + " : " + game.getPlayer().getExperience() + " / " + game.getPlayer().getExperienceToNextLevel());
        try {
            InputStream fontStream = getClass().getResourceAsStream("resources/myFont.TTF");
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        backgroundIcon = new ImageIcon(getClass().getResource("resources/bg-green.png"));
        ImageIcon menuBackground = new ImageIcon(getClass().getResource("resources/tiles/menu.png"));
        setTitle("LifeQuest"); 
        setSize(1024, 514); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setResizable(false);
        initialItems = new ArrayList<>();
        initialItems.add("Hero Sword");
        initialItems.add("Hero Armor");
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH; 
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image backgroundImage = backgroundIcon.getImage();
                int imageWidth = backgroundImage.getWidth(null);
                int imageHeight = backgroundImage.getHeight(null);

                int panelWidth = getWidth();
                int panelHeight = getHeight();

                int x = backgroundX;
                while (x < panelWidth) {
                    int drawWidth = Math.min(imageWidth, panelWidth - x);
                    g.drawImage(backgroundImage, x, 0, x + drawWidth, panelHeight, 0, 0, drawWidth, imageHeight, null);
                    x += imageWidth;
                }
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
            playerAnimation = new CharacterAnimation(
                    new SpriteAnimation(spriteSheet, frameWidth, frameHeight, totalFrames, 350, scaleFactor, startCol, startRow, endCol, endRow),
                    layeredPane,
                    this,
                    enemyAnimations
            );
            playerAttackAnimation = new SpriteAnimation("/resources/attack.gif", 100, 3.0);
            playerRunAnimation = new SpriteAnimation("resources/run.gif", 100, 3.0);
            playerHurtAnimation = new SpriteAnimation("resources/hurt.gif", 100, 3.0);
            playerAnimation.setHurtAnimation(playerHurtAnimation);

            playerAnimation.setRunAnimation(playerRunAnimation);
            playerAnimation.setAttackAnimation(playerAttackAnimation);
            playerAnimation.startAnimation();
            CharacterAnimation skellyAnimationSet = new CharacterAnimation(
                    new SpriteAnimation("/resources/gifs/skeleton/skelly.gif", 100, 3.5),
                    layeredPane,
                    this,
                    enemyAnimations
            );
            skellyAnimationSet.setEnemyWalkAnimation("/resources/gifs/skeleton/skelly walk.gif", 100, 3.5);
            skellyAnimationSet.setAttackAnimation("/resources/gifs/skeleton/skelly attack.gif", 100, 3.5);
            skellyAnimationSet.setHurtAnimation("/resources/gifs/skeleton/skelly hurt.gif", 100, 3.5);
            skellyAnimationSet.setDeathAnimation("/resources/gifs/skeleton/skelly death.gif", 100, 3.5);
            enemyAnimations.put("Skelly", skellyAnimationSet);
            CharacterAnimation slimeyAnimationSet = new CharacterAnimation(
                    new SpriteAnimation("/resources/gifs/slime/slime idle.gif", 100, 3.5),
                    layeredPane,
                    this,
                    enemyAnimations
            );
            slimeyAnimationSet.setEnemyWalkAnimation("/resources/gifs/slime/slime walk.gif", 200, 3.5);
            slimeyAnimationSet.setAttackAnimation("/resources/gifs/slime/slime attack.gif", 200, 3.5);
            slimeyAnimationSet.setHurtAnimation("/resources/gifs/slime/slime hurt.gif", 200, 3.5);
            slimeyAnimationSet.setDeathAnimation("/resources/gifs/slime/slime death.gif", 200, 3.5);
            enemyAnimations.put("Mr. Slime", slimeyAnimationSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        levelBar.setBounds(10, 10, 300, 15);
        levelBar.setStringPainted(true);
        levelBar.setForeground(new Color(255, 165, 0));
        levelBar.setFont(customFont.deriveFont(14f));
        levelBar.setUI(new RoundedProgressBarUI());

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
        this.healthBar = new JProgressBar(0, (int) playerData.get("health"));
        healthBar.setStringPainted(true); 
        healthBar.setForeground(Color.RED); 
        healthBar.setFont(customFont.deriveFont(14f));
        this.manaBar = new JProgressBar(0, (int) playerData.get("mana"));
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
        JButton questsButton = new JButton("QUESTS");
        homeButton.setBorderPainted(false);
        homeButton.setContentAreaFilled(false);
        homeButton.setFocusPainted(false);
        inventoryButton.setBorderPainted(false);
        inventoryButton.setContentAreaFilled(false);
        inventoryButton.setFocusPainted(false);
        questsButton.setBorderPainted(false);
        questsButton.setContentAreaFilled(false);
        questsButton.setFocusPainted(false);
        homeButton.setFont(customFont.deriveFont(Font.BOLD, 14f));
        inventoryButton.setFont(customFont.deriveFont(Font.BOLD, 14f));
        questsButton.setFont(customFont.deriveFont(Font.BOLD, 14f));
        tabsPanel.add(homeButton);
        tabsPanel.add(inventoryButton);
        tabsPanel.add(questsButton);
        cardsPanel = new JPanel(new CardLayout());
 
        JPanel homeCard = new JPanel(new BorderLayout());
        homeCard.add(centerPanel, BorderLayout.NORTH);
        homeCard.add(rightBottomPanel, BorderLayout.SOUTH);
        inventoryGridPanel = new JPanel(new GridLayout(5, 5, 5, 5));
        inventoryCard = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(0, 50));
        inventoryCard.add(topPanel, BorderLayout.NORTH);
        JPanel lowerPanel = new JPanel();
        lowerPanel.setPreferredSize(new Dimension(0, 50));
        inventoryCard.add(lowerPanel, BorderLayout.SOUTH);
        JPanel invleftPanel = new JPanel();
        invleftPanel.setPreferredSize(new Dimension(50, 0));
        inventoryCard.add(invleftPanel, BorderLayout.WEST);
        JPanel invrightPanel = new JPanel();
        invrightPanel.setPreferredSize(new Dimension(50, 0));
        inventoryCard.add(invrightPanel, BorderLayout.EAST);
        inventoryCard.add(inventoryGridPanel, BorderLayout.CENTER);
        for (int i = 0; i < 25; i++) {
            InventorySlot slot = new InventorySlot(this, game.getPlayer());
            slot.setBorder(new ThickBorder(10));
            slot.setToolTipText("Slot " + i);
            inventoryGridPanel.add(slot);
        }
        loadInitialItems();
        questsCard = new JPanel();
        cardsPanel.add(homeCard, "homeCard");
        cardsPanel.add(inventoryCard, "inventoryCard");
        cardsPanel.add(questsCard, "questsCard");
        rightPanel.add(tabsPanel, BorderLayout.NORTH);
        rightPanel.add(cardsPanel, BorderLayout.CENTER);
        CardLayout cardLayout = (CardLayout) cardsPanel.getLayout();
        homeButton.addActionListener(e -> cardLayout.show(cardsPanel, "homeCard"));
        inventoryButton.addActionListener(e -> cardLayout.show(cardsPanel, "inventoryCard"));
        questsButton.addActionListener(e -> cardLayout.show(cardsPanel, "questsCard"));
        JPanel mainMenuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(menuBackground.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainMenuPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel titleLabel = new JLabel("LifeQUEST");
        titleLabel.setFont(customFont.deriveFont(Font.BOLD, 26f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        mainMenuPanel.add(titleLabel, gbc);
        JButton startGameButton = new JButton("Start Game");
        startGameButton.setFont(customFont.deriveFont(Font.BOLD, 14f));
        startGameButton.addActionListener(e -> {
            game.setPlayerIsPlaying(true);
            remove(mainMenuPanel);
            add(rightPanel, constraints);
            cardLayout.first(cardsPanel);
            revalidate();
            repaint();
            game.loadQuests(this);
            playerAnimation.stopRunAnimation();
            playerAnimation.resetToIdleAnimation();
            bgPlaying = false;
            layeredPane.add(levelBar, JLayeredPane.DEFAULT_LAYER);
            game.startGameLoop(this);
        });
        JButton continueButton = new JButton("Continue Game");
        continueButton.setFont(customFont.deriveFont(Font.BOLD, 14f));
        continueButton.addActionListener(e -> {
            remove(mainMenuPanel);
            add(rightPanel, constraints);
            cardLayout.first(cardsPanel);
            revalidate();
            repaint();
        });
        JButton quitButton = new JButton("Quit");
        quitButton.setFont(customFont.deriveFont(Font.BOLD, 14f));
        quitButton.addActionListener(e -> {
            System.exit(0);
        });
        startGameButton.setFocusable(false);
        continueButton.setFocusable(false);
        quitButton.setFocusable(false);
        startGameButton.setBorderPainted(false);
        startGameButton.setFocusPainted(false);
        continueButton.setBorderPainted(false);
        continueButton.setFocusPainted(false);
        quitButton.setBorderPainted(false);
        quitButton.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        mainMenuPanel.add(startGameButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainMenuPanel.add(continueButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        mainMenuPanel.add(quitButton, gbc);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0.5;
        constraints.weighty = 1.0;
        add(mainMenuPanel, constraints);
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
        healthBar.setFocusable(false); 
        healthBar.setBorderPainted(false); 
        manaBar.setFocusable(false); 
        manaBar.setBorderPainted(false);
        levelBar.setFocusable(false);
        levelBar.setBorderPainted(false);
        scrollPane.setFocusable(false); 
        scrollPane.setBorder(null);
        healthBar.setUI(new RoundedProgressBarUI());
        manaBar.setUI(new RoundedProgressBarUI());
        updateHealthBar();
        updateManaBar();
        playerAnimation.playRunAnimationLoop();
        backgroundAnimationThread = new Thread(() -> {
            while (bgPlaying) {
                updateBackgroundPosition(5);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        backgroundAnimationThread.start();
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
    public void updateLevelBar() {
        levelBar.setMaximum(game.getPlayer().getExperienceToNextLevel());
        levelBar.setValue(game.getPlayer().getExperience());
        levelBar.setString("Lvl " + game.getPlayer().getLevel() + " : " + game.getPlayer().getExperience() + " / " + game.getPlayer().getExperienceToNextLevel());
    }
    private void loadInitialItems() {
        try {
            databaseConnector.loadItems(initialItems, this);
            List<Item> loadedItems = databaseConnector.loadItems(initialItems, this);
            game.getPlayer().getInventory().addAll(loadedItems);
            SwingUtilities.invokeLater(() -> {
                updateInventoryUI();
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void updateInventoryUI() {
        List<Item> inventory = game.getPlayer().getInventory();
        int numSlots = inventoryGridPanel.getComponentCount();
        for (int i = 0; i < numSlots; i++) {
            InventorySlot slot = (InventorySlot) inventoryGridPanel.getComponent(i);
            if (i < inventory.size()) {
                Item item = inventory.get(i);
                slot.setItem(item);
            } else {
                slot.setItem(null);
            }
        }
    }
    public void createEnemyInfoBox(Map<String, Object> enemyData) {
        if (layeredPane != null) {
            enemyInfoBox = new EnemyInfoBox();
            String enemyName = (String) enemyData.get("name");
            CharacterAnimation animationSet = enemyAnimations.get(enemyName);

            skeletonAnimation = new CharacterAnimation(animationSet.animation, layeredPane, this, enemyAnimations);
            int enemyYOffset = 0;
            if (enemyName.equals("Mr. Slime")) {
                enemyYOffset = 50;
            }
            skeletonAnimation.setYOffset(enemyYOffset);

            skeletonAnimation.getCurrentAnimation().setBounds(
                    325, 240,
                    skeletonAnimation.getCurrentAnimation().getIcon().getIconWidth(),
                    skeletonAnimation.getCurrentAnimation().getIcon().getIconHeight()
            );

            skeletonAnimation.startEnemyAnimation(325, 240);


            enemyInfoBox.setBounds(
                    skeletonAnimation.getEnemyX(),
                    skeletonAnimation.getEnemyY() - enemyInfoBox.getPreferredSize().height - 10,
                    enemyInfoBox.getPreferredSize().width,
                    enemyInfoBox.getPreferredSize().height
            );

            updateEnemyInfoBox(enemyData);
            layeredPane.add(enemyInfoBox, JLayeredPane.PALETTE_LAYER);
            leftPanel.revalidate();
            leftPanel.repaint();
        }
    }
    public void removeEnemyAndInfoBox() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (layeredPane != null) {
                    if (skeletonAnimation != null) layeredPane.remove(skeletonAnimation.getCurrentAnimation());
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
            }
        };
        worker.execute();
    }
    public void updateQuestsUI() {
        questsCard.removeAll();
        for (int i = 0; i < game.activeQuests.size(); i++) {
            Quest quest = game.activeQuests.get(i);
            JPanel questPanel = createQuestPanel(quest, i);
            questsCard.add(questPanel);
        }
        questsCard.revalidate();
        questsCard.repaint();
    }
    private JPanel createQuestPanel(Quest quest, int questIndex) {
        JPanel questPanel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel(quest.getName());
        JTextArea descriptionArea = new JTextArea(quest.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);

        questPanel.add(nameLabel, BorderLayout.NORTH);
        questPanel.add(descriptionArea, BorderLayout.CENTER);
        questPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showQuestPopupMenu(quest, questIndex, questPanel, e);
            }
        });
        return questPanel;
    }
    private void showQuestPopupMenu(Quest quest, int questIndex, JPanel questPanel, MouseEvent e) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem completeMenuItem = new JMenuItem("Complete");
        JMenuItem declineMenuItem = new JMenuItem("Decline");
        if (!quest.isCompleted() && !quest.isDeclined()) {
            popupMenu.add(completeMenuItem);
            popupMenu.add(declineMenuItem);
        } else {

            JMenuItem statusMenuItem = new JMenuItem(quest.isCompleted() ? "Completed" : "Declined");
            statusMenuItem.setEnabled(false);
            popupMenu.add(statusMenuItem);
        }
        completeMenuItem.addActionListener(e1 -> {
            if (!quest.isCompleted() && !quest.isDeclined()) {

                game.completeQuest(questIndex);

                updateQuestsUI();

                updateLevelBar();
            }
        });
        declineMenuItem.addActionListener(e2 -> {
            if (!quest.isCompleted() && !quest.isDeclined()) {
                game.declineQuest(questIndex);
                updateQuestsUI();
            }
        });
        popupMenu.show(questPanel, e.getX(), e.getY());
    }
    private void updateBackgroundPosition(int speed) {
        backgroundX -= speed;
        if (backgroundX < -backgroundIcon.getImage().getWidth(null)) {
            backgroundX += backgroundIcon.getImage().getWidth(null);
        }
        leftPanel.repaint();
    }
    public Battle getBattle() {
        return battle;
    }
    public void updateEnemyInfoBox(Map<String, Object> enemyData) {
        if (enemyInfoBox != null) {
            enemyInfoBox.updateInfo(enemyData);
        }
    }
    public Scanner getScanner() {
        return scanner;
    }
    public void startGameLoop() {
        new Thread(this::run).start();
    }
    public void setBattle(Battle battle) {
        this.battle = battle;
        attackButton.setEnabled(false);
        battle.setEnemyAnimations(enemyAnimations);
        attackButton.addActionListener(e -> {
            if (game.isPlayerPlaying() && battle != null && battle.isPlayerTurn) {
                String enemyName = (String) battle.getEnemyData().get("name");
                CharacterAnimation enemyAnimation = enemyAnimations.get(enemyName);
                if (enemyAnimation != null) {
                    int targetX = enemyAnimation.getEnemyX() - 100;
                    playerAnimation.startRunAnimation(targetX);
                }
            }
        });
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            DatabaseConnector databaseConnector = new DatabaseConnector();
            try {

                Map<String, Object> playerData = databaseConnector.loadPlayerData("Hero");

                LifeQuest game = new LifeQuest(playerData, databaseConnector);

                LifeQuestUI ui = new LifeQuestUI(game, playerData);
                ui.setVisible(true);
                ui.startGameLoop();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}