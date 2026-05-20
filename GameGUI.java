import javax.swing.*;
import java.awt.*;

public class GameGUI extends JFrame {

    private static final String WINDOW_TITLE  = "Life & Finance Simulator";
    private static final int    WINDOW_WIDTH  = 900;
    private static final int    WINDOW_HEIGHT = 600;

    private JLabel labelName;
    private JLabel labelAge;
    private JLabel labelNetWorth;
    private JLabel labelCashFlow; // <-- NEW
    private JPanel actionPanel;

    private Person player;

    public GameGUI(String playerName) {
        this.player = new Person(playerName);

        setTitle(WINDOW_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        buildFrame();
        updateActionPanel(player.getAge());
        setVisible(true);
    }

    private void buildFrame() {
        setLayout(new BorderLayout(10, 10));
        add(buildStatsPanel(),       BorderLayout.NORTH);
        add(buildActionPanelShell(), BorderLayout.CENTER);
        add(buildTimeControlPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Player Stats"));
        panel.setBackground(new Color(30, 30, 50));

        labelName     = makeStatLabel("Name: "      + player.getName());
        labelAge      = makeStatLabel("Age: "       + player.getAge());
        labelNetWorth = makeStatLabel("Net Worth: $" + (int)player.getNetWorth());
        labelCashFlow = makeStatLabel("Cash Flow: $" + (int)GameEngine.getAnnualCashFlow(player) + "/yr");

        panel.add(labelName);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(labelAge);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(labelNetWorth);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(labelCashFlow);

        return panel;
    }

    private JLabel makeStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Monospaced", Font.BOLD, 15));
        return label;
    }

    private JPanel buildActionPanelShell() {
        actionPanel = new JPanel(new GridBagLayout()); 
        actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        actionPanel.setBackground(new Color(20, 20, 40));
        return actionPanel;
    }

    // --- BOTTOM TIME CONTROLS (Only these move time forward now!) ---
    private JPanel buildTimeControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Time Controls"));
        panel.setBackground(new Color(30, 30, 50));

        JButton btnSim1  = makeIconButton("Simulate 1 Year",  "sim1.png",  new Color(150, 200, 150));
        JButton btnSim10 = makeIconButton("Simulate 10 Years","sim10.png", new Color(150, 170, 220));

        btnSim1.addActionListener(e -> {
            GameEngine.simulateOneYear(player);
            refreshAll();
        });

        btnSim10.addActionListener(e -> {
            GameEngine.simulateTenYears(player);
            refreshAll();
        });

        panel.add(btnSim1);
        panel.add(btnSim10);
        return panel;
    }

    // --- CENTER DYNAMIC MENU (Instant actions!) ---
    public void updateActionPanel(int age) {
        actionPanel.removeAll();
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonRow.setOpaque(false);

        if (age >= 16 && age <= 21) {
            JButton btnJob  = makeIconButton("Get Part-Time Job", "job.png",  new Color(220, 180, 100));
            // Save is handled passively now by time!
            
            btnJob.addActionListener(e -> { GameEngine.takeInstantAction(player, "Get Part-Time Job"); refreshAll(); });
            buttonRow.add(btnJob);

        } else if (age >= 22 && age <= 29) {
            JButton btnCareer = makeIconButton("Start Career",       "job.png",   new Color(130, 180, 220));
            JButton btnLoans  = makeIconButton("Pay Student Loans",  "loans.png", new Color(220, 130, 130));
            JButton btnInvest = makeIconButton("Invest 50%",         "invest.png",new Color(130, 200, 150));

            btnCareer.addActionListener(e -> { GameEngine.takeInstantAction(player, "Start Career"); refreshAll(); });
            btnLoans.addActionListener(e -> { GameEngine.takeInstantAction(player, "Pay Student Loans"); refreshAll(); });
            btnInvest.addActionListener(e -> { GameEngine.takeInstantAction(player, "Invest 50%"); refreshAll(); });

            buttonRow.add(btnCareer);
            buttonRow.add(btnLoans);
            buttonRow.add(btnInvest);

        } else if (age >= 30 && age <= 50) {
            JButton btnHouse  = makeIconButton("Buy House",  "house.png",  new Color(200, 160, 100));
            JButton btnKids   = makeIconButton("Have Kids",  "kids.png",   new Color(220, 140, 180));
            JButton btnInvest = makeIconButton("Invest 50%", "invest.png", new Color(130, 200, 150));

            btnHouse.addActionListener(e -> { GameEngine.takeInstantAction(player, "Buy House"); refreshAll(); });
            btnKids.addActionListener(e -> { GameEngine.takeInstantAction(player, "Have Kids"); refreshAll(); });
            btnInvest.addActionListener(e -> { GameEngine.takeInstantAction(player, "Invest 50%"); refreshAll(); });

            buttonRow.add(btnHouse);
            buttonRow.add(btnKids);
            buttonRow.add(btnInvest);

        } else {
            JButton btnRetire = makeIconButton("Max Retirement", "retirement.png", new Color(180, 140, 220));
            btnRetire.addActionListener(e -> { GameEngine.takeInstantAction(player, "Max Retirement"); refreshAll(); });
            buttonRow.add(btnRetire);
        }

        JLabel ageLabel = new JLabel(getLifeStageName(age), SwingConstants.CENTER);
        ageLabel.setForeground(new Color(180, 180, 220));
        ageLabel.setFont(new Font("Serif", Font.ITALIC, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10,0,4,0);
        actionPanel.add(ageLabel, gbc);
        gbc.gridy = 1;
        actionPanel.add(buttonRow, gbc);

        actionPanel.revalidate();
        actionPanel.repaint();
    }

    private String getLifeStageName(int age) {
        if      (age <= 21) return "Life Stage: Teen / Young Adult";
        else if (age <= 29) return "Life Stage: Young Professional";
        else if (age <= 50) return "Life Stage: Mid-Life";
        else                return "Life Stage: Pre-Retirement";
    }

    public void refreshAll() {
        labelName.setText("Name: " + player.getName());
        labelAge.setText("Age: " + player.getAge());
        labelNetWorth.setText("Net Worth: $" + (int)player.getNetWorth());
        labelCashFlow.setText("Cash Flow: $" + (int)GameEngine.getAnnualCashFlow(player) + "/yr");
        
        updateActionPanel(player.getAge());
    }

    private JButton makeIconButton(String label, String imageFile, Color bgColor) {
        JButton button = new JButton(label); 
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK); 
        button.setFont(new Font("SansSerif", Font.BOLD, 14)); 
        button.setFocusPainted(false);
        button.setOpaque(true); 
        button.setBorderPainted(true);
        button.setPreferredSize(new Dimension(170, 60)); 
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void main(String[] args) {
        String name = JOptionPane.showInputDialog(null, "Enter your name to begin your life:");
        if(name == null || name.trim().isEmpty()) name = "Player";
        
        String finalName = name;
        SwingUtilities.invokeLater(() -> new GameGUI(finalName));
    }
}