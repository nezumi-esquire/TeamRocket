import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private Battle battle;
    private static final int GROUND_LEVEL_Y = 245;

    public CharacterAnimation(SpriteAnimation animation, JLayeredPane layeredPane, LifeQuestUI ui) {
        this.animation = animation;
        this.layeredPane = layeredPane;
        this.ui = ui;
        this.x = animation.getX();
        this.y = GROUND_LEVEL_Y;
        isRunning = false;
        isAttacking = false;
        battle = ui.getBattle();

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
        attackAnimation.stopAnimation();
        layeredPane.remove(attackAnimation);
        layeredPane.add(animation, JLayeredPane.PALETTE_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
        animation.startAnimation();
    }
    public void startEnemyAttackAnimation(int delay) {
        Timer attackDelayTimer = new Timer();
        TimerTask attackDelayTask = new TimerTask() {
            @Override
            public void run() {
                if (animation != null) {
                    animation.stopAnimation();
                    layeredPane.remove(animation);
                    if (!layeredPane.isAncestorOf(attackAnimation)) {
                        attackAnimation.setBounds(
                                animation.getX(), animation.getY(),
                                attackAnimation.getIcon().getIconWidth(),
                                attackAnimation.getIcon().getIconHeight()
                        );
                        layeredPane.add(attackAnimation, JLayeredPane.PALETTE_LAYER);
                    }
                    attackAnimation.startAnimation();
                }

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
        };
        attackDelayTimer.schedule(attackDelayTask, delay);
    }
    public void startEnemyAnimation(int x, int y) {
        animation.setBounds(x, y, animation.getIcon().getIconWidth(), animation.getIcon().getIconHeight());
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
