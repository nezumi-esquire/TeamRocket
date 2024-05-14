import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.util.ArrayList;
import java.util.List;

public class SpriteAnimation extends JLabel {
    private List<ImageIcon> frames = new ArrayList<>();
    private int currentFrame = 0;
    private Timer timer;
    private int maxWidth = 0;
    private int maxHeight = 0;

    public SpriteAnimation(String gifPath, int delay, double scaleFactor) {
        this.timer = new Timer(delay, e -> {
            currentFrame = (currentFrame + 1) % frames.size();
            setIcon(frames.get(currentFrame));
        }); 

        loadFramesFromGif(gifPath, scaleFactor);
        setIcon(frames.get(0));

        int maxWidth = 0, maxHeight = 0;
        for (ImageIcon icon : frames) {
            maxWidth = Math.max(maxWidth, icon.getIconWidth());
            maxHeight = Math.max(maxHeight, icon.getIconHeight());
        }
        setPreferredSize(new Dimension(maxWidth, maxHeight));
    }
    public SpriteAnimation(BufferedImage spriteSheet, int frameWidth, int frameHeight,
                           int numFrames, int delay, double scaleFactor,
                           int startCol, int startRow, int endCol, int endRow) { 
        this.timer = new Timer(delay, e -> {
            currentFrame = (currentFrame + 1) % frames.size();
            setIcon(frames.get(currentFrame));
        });

        loadFramesFromSpriteSheet(spriteSheet, frameWidth, frameHeight, startCol, startRow, endCol, endRow, scaleFactor); 
        setIcon(frames.get(0));

        int maxWidth = 0, maxHeight = 0;
        for (ImageIcon icon : frames) {
            maxWidth = Math.max(maxWidth, icon.getIconWidth());
            maxHeight = Math.max(maxHeight, icon.getIconHeight());
        }
        setPreferredSize(new Dimension(maxWidth, maxHeight));
    }
    private void loadFramesFromGif(String gifPath, double scaleFactor) {
        try {
            ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("gif").next();
            reader.setInput(ImageIO.createImageInputStream(getClass().getResourceAsStream(gifPath)));
            int numFrames = reader.getNumImages(true);
            for (int i = 0; i < numFrames; i++) {
                BufferedImage frame = reader.read(i);
 
                int newWidth = (int) (frame.getWidth() * scaleFactor);
                int newHeight = (int) (frame.getHeight() * scaleFactor);
                Image scaledFrame = frame.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                frames.add(new ImageIcon(scaledFrame));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadFramesFromSpriteSheet(
            BufferedImage spriteSheet, int frameWidth, int frameHeight, int startCol, int startRow, int endCol, int endRow, double scaleFactor) {
        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                int x = col * frameWidth;
                int y = row * frameHeight;
                BufferedImage frame = spriteSheet.getSubimage(x, y, frameWidth, frameHeight);

 
                int newWidth = (int) (frame.getWidth() * scaleFactor);
                int newHeight = (int) (frame.getHeight() * scaleFactor);
                if (scaleFactor != 1.0) {
                    Image scaledFrame = frame.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    frames.add(new ImageIcon(scaledFrame));
                } else {
                    frames.add(new ImageIcon(frame));
                }
            }
        }
    }
    public void startAnimation() {
        timer.start();
    }

    public void stopAnimation() {
        timer.stop();
    }
 
 
 

 
 
 
 
 
}