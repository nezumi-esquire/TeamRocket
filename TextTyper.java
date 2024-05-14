import java.util.concurrent.TimeUnit;

public class TextTyper {
    public static void typeText(String text, long delayMillis) {
        for (char c : text.toCharArray()) {
            System.out.print(c);
            System.out.flush();
            try {
                TimeUnit.MILLISECONDS.sleep(delayMillis); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); 
            }
        }
        System.out.println();
    }
}
