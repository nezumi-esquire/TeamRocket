import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class CharacterAnimation {
    private SpriteAnimation animation;
    private JLayeredPane layeredPane;
    private LifeQuestUI ui;
    private int x, y;
    private boolean isRunning;
    private boolean isAttacking;
    private SpriteAnimation runAnimation;
    private SpriteAnimation attackAnimation;
    private SpriteAnimation hurtAnimation;
    private Battle battle;
    private static final int GROUND_LEVEL_Y = 245;
    private SpriteAnimation EnemyWalkAnimation;
    private SpriteAnimation deathAnimation;
    private HashMap<String, CharacterAnimation> enemyAnimations;

    public CharacterAnimation(SpriteAnimation animation, JLayeredPane layeredPane, LifeQuestUI ui) {
        this.animation = animation;
        this.layeredPane = layeredPane;
        this.ui = ui;
        this.x = animation.getX();
        this.y = GROUND_LEVEL_Y;
        isRunning = false;
        isAttacking = false;
        battle = ui.getBattle();
        this.enemyAnimations = ui.getEnemyGenerator().getEnemyAnimations();

    }
    public SpriteAnimation getCurrentAnimation() {
        return animation; // Return the current animation (idle or attack)
    }
    public void startAnimation() {
        animation.setBounds(x, GROUND_LEVEL_Y, animation.getIcon().getIconWidth(), animation.getIcon().getIconHeight());
        animation.startAnimation();
        layeredPane.add(animation, JLayeredPane.PALETTE_LAYER);
    }

    public void setRunAnimation(SpriteAnimation runAnimation) {
        this.runAnimation = runAnimation;
    }

    public void setAttackAnimation(SpriteAnimation attackAnimation) {
        this.attackAnimation = attackAnimation;
    }
    public void setEnemyWalkAnimation(SpriteAnimation EnemyWalkAnimation) {
        this.EnemyWalkAnimation = EnemyWalkAnimation;
    }
    public void setAnimation(SpriteAnimation animation) {
        this.animation = animation;
    }
    public void setHurtAnimation(SpriteAnimation hurtAnimation) {
        this.hurtAnimation = hurtAnimation;
    }
    public void setDeathAnimation(SpriteAnimation deathAnimation) {
        this.deathAnimation = deathAnimation;
    }
    public void startRunAnimation(int targetX) {
        if (!isRunning && runAnimation != null) {
            isRunning = true;
            animation.stopAnimation();
            layeredPane.remove(animation);

            runAnimation.setBounds(x, GROUND_LEVEL_Y, runAnimation.getIcon().getIconWidth(), runAnimation.getIcon().getIconHeight());
            layeredPane.add(runAnimation, JLayeredPane.PALETTE_LAYER);
            layeredPane.revalidate();
            layeredPane.repaint();
            runAnimation.startAnimation();

            int playerSpeed = 5;
            Timer moveTimer = new Timer();
            TimerTask moveTask = new TimerTask() {
                @Override
                public void run() {
                    int currentX = runAnimation.getX();
                    if (currentX<targetX) {
                        runAnimation.setLocation(currentX + playerSpeed, y);
                    } else {
                        startAttackAnimation();
                        moveTimer.cancel();
                    }
                }
            };
            moveTimer.scheduleAtFixedRate(moveTask, 0, 30);
        }
    }

    public void startAttackAnimation() {
        if (!isAttacking && attackAnimation != null) {
            isAttacking = true;
            runAnimation.stopAnimation();
            layeredPane.remove(runAnimation);

            attackAnimation.setBounds(runAnimation.getX(), GROUND_LEVEL_Y, attackAnimation.getIcon().getIconWidth(), attackAnimation.getIcon().getIconHeight());
            layeredPane.add(attackAnimation, JLayeredPane.PALETTE_LAYER);
            layeredPane.revalidate();
            layeredPane.repaint();
            attackAnimation.startAnimation();

            Timer attackTimer = new Timer();
            TimerTask attackTask = new TimerTask() {
                int attackFrameCount = 0;
                @Override
                public void run() {
                    if (attackFrameCount >= attackAnimation.frames.size()) {
                        attackTimer.cancel(); // Cancel the TimerTask and Timer
                        resetToIdleAnimation();
                        if (ui.getBattle() != null) { // Check if battle is still ongoing
                            ui.getBattle().playerTurn();
                            SwingUtilities.invokeLater(() -> ui.updateEnemyInfoBox(ui.getBattle().getEnemy()));
                        }
                    }
                    attackFrameCount++;
                }
            };
            attackTimer.scheduleAtFixedRate(attackTask, 0, 100);
        }
    }
    public void resetToIdleAnimation() {
        isRunning = false;
        isAttacking = false;
        if (layeredPane.isAncestorOf(hurtAnimation)) {
            hurtAnimation.stopAnimation();
            layeredPane.remove(hurtAnimation);
        }
        if (!layeredPane.isAncestorOf(animation)) {
            layeredPane.add(animation, JLayeredPane.PALETTE_LAYER);
        }
        attackAnimation.stopAnimation();
        layeredPane.remove(attackAnimation);
        layeredPane.revalidate();
        layeredPane.repaint();
        animation.startAnimation();
    }
    public void startEnemyWalkAnimation(int targetX) {
        if (!isRunning && enemyAnimations.get(ui.getBattle().getEnemy().getName()).animation != null) {  // Access walk animation from map
            isRunning = true;
            animation.stopAnimation();
            layeredPane.remove(animation);

            SpriteAnimation walkAnimation = (SpriteAnimation) enemyAnimations.get(ui.getBattle().getEnemy().getName()).animation;
            walkAnimation.setBounds(animation.getX(), animation.getY(), walkAnimation.getIcon().getIconWidth(), walkAnimation.getIcon().getIconHeight());
            layeredPane.add(walkAnimation, JLayeredPane.PALETTE_LAYER);
            layeredPane.revalidate();
            layeredPane.repaint();
            walkAnimation.startAnimation();

            int enemySpeed = 4;
            Timer moveTimer = new Timer();
            TimerTask moveTask = new TimerTask() {
                @Override
                public void run() {
                    int currentX = walkAnimation.getX(); // Use walkAnimation for position
                    if (currentX > targetX) {
                        walkAnimation.setLocation(currentX - enemySpeed, y);
                    } else {
                        walkAnimation.stopAnimation();
                        layeredPane.remove(walkAnimation);
                        startEnemyAttackAnimation(100);
                        moveTimer.cancel();
                    }
                }
            };
            moveTimer.scheduleAtFixedRate(moveTask, 0, 30);
        }
    }
    public void playHurtAnimation(int delay) {
        Timer hurtDelayTimer = new Timer();
        TimerTask hurtDelayTask = new TimerTask() {
            @Override
            public void run() {
                if (animation != null && hurtAnimation != null) {
                    animation.stopAnimation();
                    layeredPane.remove(animation);
                    hurtAnimation.setBounds(animation.getX(), animation.getY(), hurtAnimation.getIcon().getIconWidth(), hurtAnimation.getIcon().getIconHeight());
                    layeredPane.add(hurtAnimation, JLayeredPane.PALETTE_LAYER);

                    if (!layeredPane.isAncestorOf(hurtAnimation)) {
                        hurtAnimation.setBounds(animation.getX(), animation.getY(), hurtAnimation.getIcon().getIconWidth(), hurtAnimation.getIcon().getIconHeight());
                        layeredPane.add(hurtAnimation, JLayeredPane.PALETTE_LAYER);
                    }
                    hurtAnimation.startAnimation();

                    // Timer for the hurt animation duration
                    Timer hurtTimer = new Timer();
                    TimerTask hurtTask = new TimerTask() {
                        int hurtFrameCount = 0;

                        @Override
                        public void run() {
                            if (hurtFrameCount >= hurtAnimation.frames.size()) {
                                hurtTimer.cancel();
                                resetToIdleAnimation(); // Return to idle after the hurt animation
                            }
                            hurtFrameCount++;
                        }
                    };

                    hurtTimer.scheduleAtFixedRate(hurtTask, 0, 100); // Adjust timing as needed
                }
            }
        };
        hurtDelayTimer.schedule(hurtDelayTask, delay);
    }
    public void playDeathAnimation() {
        if (animation != null && deathAnimation != null) {
            animation.stopAnimation();
            layeredPane.remove(animation);
            deathAnimation.setBounds(animation.getX(), animation.getY(), deathAnimation.getIcon().getIconWidth(), deathAnimation.getIcon().getIconHeight());
            layeredPane.add(deathAnimation, JLayeredPane.PALETTE_LAYER);
            deathAnimation.startAnimation();

            // Timer to remove the death animation after it finishes
            Timer deathTimer = new Timer();
            TimerTask deathTask = new TimerTask() {
                @Override
                public void run() {
                    deathAnimation.stopAnimation();
                    layeredPane.remove(deathAnimation);
                    layeredPane.revalidate();
                    layeredPane.repaint();
                }
            };
            deathTimer.schedule(deathTask, deathAnimation.timer.getDelay() * deathAnimation.frames.size()); // Schedule the timer to run once after the animation duration
        }
    }
    public void startEnemyAttackAnimation(int delay) {
        Timer attackDelayTimer = new Timer();
        TimerTask attackDelayTask = new TimerTask() {
            @Override
            public void run() {
                if (animation != null) {
                    animation.stopAnimation();
                    layeredPane.remove(animation);

                    SpriteAnimation attackAnimation = (SpriteAnimation) enemyAnimations.get(ui.getBattle().getEnemy().getName()).attackAnimation;
                    if (attackAnimation != null && !layeredPane.isAncestorOf(attackAnimation)) {
                        int xOffset = (animation.getIcon().getIconWidth() - attackAnimation.getIcon().getIconWidth()) / 2;
                        int yOffset = (animation.getIcon().getIconHeight() - attackAnimation.getIcon().getIconHeight()) / 2;

                        // Access EnemyWalkAnimation from the map

                        SpriteAnimation walkAnimation = (SpriteAnimation) enemyAnimations.get(ui.getBattle().getEnemy().getName()).animation;
                        attackAnimation.setBounds(
                                walkAnimation.getX() + xOffset,
                                walkAnimation.getY() + yOffset,
                                attackAnimation.getIcon().getIconWidth(),
                                attackAnimation.getIcon().getIconHeight()
                        );
                        layeredPane.add(attackAnimation, JLayeredPane.PALETTE_LAYER);
                        attackAnimation.startAnimation(); // Start the attack animation

                        Timer attackTimer = new Timer();
                        TimerTask attackTask = new TimerTask() {
                            int attackFrameCount = 0;
                            @Override
                            public void run() {
                                if (attackFrameCount >= attackAnimation.frames.size()) {
                                    attackTimer.cancel();
                                    resetToIdleAnimation();
                                }
                                attackFrameCount++;
                            }
                        };
                        attackTimer.scheduleAtFixedRate(attackTask, 0, 100);
                    }
                }
            }
        };
        attackDelayTimer.schedule(attackDelayTask, delay);
    }
    public void startEnemyAnimation(int x, int y, int offsetX, int offsetY) {
        animation.setBounds(x + offsetX, y + offsetY, animation.getIcon().getIconWidth(), animation.getIcon().getIconHeight());
        animation.startAnimation();
        layeredPane.add(animation, JLayeredPane.PALETTE_LAYER);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getEnemyX() {
        return animation.getX();  // Get x-coordinate of the current animation
    }

    public int getEnemyY() {
        return animation.getY();  // Get y-coordinate of the current animation
    }
}
