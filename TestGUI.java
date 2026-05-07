// We have to import the Swing library to get the graphics tools
import javax.swing.*; 

public class TestGUI {
    public static void main(String[] args) {
        
        // 1. Create the Frame (The Window itself)
        JFrame window = new JFrame("My First Java Window!");
        window.setSize(400, 300); // Width, Height in pixels
        
        // This makes sure the program actually stops running when you click the red 'X'
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

        // 2. Create the Panel (The invisible canvas)
        JPanel panel = new JPanel();

        // 3. Create your Components (Text and a Button)
        JLabel titleText = new JLabel("Welcome to the visual Finance Simulator!");
        JButton clickButton = new JButton("Invest $1,000");

        // 4. Glue everything together!
        panel.add(titleText); // Put text on the canvas
        panel.add(clickButton); // Put button on the canvas
        window.add(panel); // Put the canvas inside the window

        // 5. Turn the lights on! (If you forget this, nothing appears)
        window.setVisible(true);
    }
}