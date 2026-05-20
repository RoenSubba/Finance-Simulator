import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
/**
 * GameGUI.java
 * ============
 * Frontend skeleton for the 50-Year Life & Finance Simulator.
 *
 * Author:  Partner B (Frontend / Graphics)
 * Partner: Partner A (Backend — BackendLogic.java)
 *
 * HANDSHAKE CONTRACT:
 *   All backend calls are stubbed as static method calls on BackendLogic.
 *   Partner A must implement a class named BackendLogic with (at minimum):
 *     - static void simulateYears(int years)
 *     - static void getPartTimeJob()
 *     - static void saveMoneyToAccount()
 *     - static void startCareer()
 *     - static void payStudentLoans()
 *     - static void investMoney()
 *     - static void buyHouse()
 *     - static void haveKids()
 *     - static void maxRetirementFund()
 *   Each method should update the shared game state, then Partner B will
 *   call refreshStats() after every action to re-sync the UI labels.
 *
 * IMAGE ASSETS:
 *   Drop PNG files into the /assets/ folder next to the compiled .class files.
 *   File names expected:
 *     assets/job.png          — part-time job / career button
 *     assets/save.png         — save money button
 *     assets/loans.png        — pay student loans button
 *     assets/invest.png       — invest button
 *     assets/house.png        — buy house button
 *     assets/kids.png         — have kids button
 *     assets/retirement.png   — max retirement fund button
 *     assets/sim1.png         — simulate 1 year button
 *     assets/sim10.png        — simulate 10 years button
 *   If an asset file is missing, the button falls back to text-only gracefully.
 */
public class GameGUI extends JFrame {
 
    // -------------------------------------------------------------------------
    // CONSTANTS — tweak freely without touching the layout code
    // -------------------------------------------------------------------------
    private static final String WINDOW_TITLE  = "Life & Finance Simulator";
    private static final int    WINDOW_WIDTH  = 900;
    private static final int    WINDOW_HEIGHT = 600;
    private static final String ASSET_PATH    = "assets/"; // relative to working dir
 
    // -------------------------------------------------------------------------
    // STATS PANEL — labels updated by refreshStats()
    // -------------------------------------------------------------------------
    private JLabel labelName;
    private JLabel labelAge;
    private JLabel labelNetWorth;
 
    // -------------------------------------------------------------------------
    // DYNAMIC ACTION PANEL — rebuilt by updateActionPanel(int age)
    // -------------------------------------------------------------------------
    private JPanel actionPanel;
 
    // -------------------------------------------------------------------------
    // LOCAL MIRROR of shared state
    //   Partner A owns the canonical values inside BackendLogic / game model.
    //   These are kept here ONLY so the GUI can display them without a
    //   round-trip call.  Call refreshStats() after every backend mutation.
    // -------------------------------------------------------------------------
    private String playerName  = "Player";   // replace with BackendLogic getter
    private int    playerAge   = 16;          // starting age
    private double playerNetWorth = 0.0;      // starting net worth
 
 
    // =========================================================================
    //  CONSTRUCTOR
    // =========================================================================
    public GameGUI(String playerName) {
        this.playerName = playerName;
 
        setTitle(WINDOW_TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen
 
        buildFrame();
 
        // Populate action buttons for the initial age
        updateActionPanel(playerAge);
 
        setVisible(true);
    }
 
 
    // =========================================================================
    //  FRAME ASSEMBLY  (BorderLayout skeleton)
    // =========================================================================
 
    /**
     * Assembles the three permanent regions: NORTH stats bar, CENTER dynamic
     * action panel, SOUTH time-control panel.
     */
    private void buildFrame() {
        setLayout(new BorderLayout(10, 10));
 
        add(buildStatsPanel(),       BorderLayout.NORTH);
        add(buildCenterSplit(),      BorderLayout.CENTER);
        add(buildTimeControlPanel(), BorderLayout.SOUTH);
    }
 
 
    // =========================================================================
    //  NORTH — Stats Panel
    // =========================================================================
 
    /**
     * Builds the permanent top bar displaying Name, Age, and Net Worth.
     * Call refreshStats() any time the backend mutates those values.
     */
    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Player Stats"));
        panel.setBackground(new Color(30, 30, 50));
 
        labelName     = makeStatLabel("Name: "      + playerName);
        labelAge      = makeStatLabel("Age: "       + playerAge);
        labelNetWorth = makeStatLabel("Net Worth: $" + String.format("%.2f", playerNetWorth));
 
        panel.add(labelName);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(labelAge);
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        panel.add(labelNetWorth);
 
        return panel;
    }
 
    /** Convenience factory for a styled stats label. */
    private JLabel makeStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Monospaced", Font.BOLD, 16));
        return label;
    }
 
 
    // =========================================================================
    //  CENTER — Dynamic Action Panel shell
    // =========================================================================
 
    /**
     * Creates the empty container that updateActionPanel() will populate.
     * Using a shell here keeps the BorderLayout stable across rebuilds.
     */
    private JPanel buildActionPanelShell() {
        actionPanel = new JPanel(new GridBagLayout()); // GridBagLayout centers content nicely
        actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        actionPanel.setBackground(new Color(20, 20, 40));
        return actionPanel;
    }
 
 
    // =========================================================================
    //  SOUTH — Time Control Panel
    // =========================================================================
 
    /**
     * Two permanent simulation buttons that are always visible regardless of age.
     */
    private JPanel buildTimeControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Time Controls"));
        panel.setBackground(new Color(30, 30, 50));
 
        JButton btnSim1  = makeIconButton("Simulate 1 Year",  "sim1.png",  new Color(60, 120, 60));
        JButton btnSim10 = makeIconButton("Simulate 10 Years","sim10.png", new Color(60, 80, 140));
 
        // --- Simulate 1 Year ---
        btnSim1.addActionListener(e -> {
            // TODO Partner A: implement BackendLogic.simulateYears(1)
            BackendLogic.simulateYears(1);
            playerAge++;
            refreshStats();
            updateActionPanel(playerAge);
        });
 
        // --- Simulate 10 Years ---
        btnSim10.addActionListener(e -> {
            // TODO Partner A: implement BackendLogic.simulateYears(10)
            BackendLogic.simulateYears(10);
            playerAge += 10;
            refreshStats();
            updateActionPanel(playerAge);
        });
 
        panel.add(btnSim1);
        panel.add(btnSim10);
 
        return panel;
    }
 
 
    // =========================================================================
    //  DYNAMIC ACTION PANEL  — age-gated button logic
    // =========================================================================
 
    /**
     * Clears and rebuilds the CENTER action panel based on the player's
     * current age.  Call this after every year simulation.
     *
     * Age ranges:
     *   16–21  → teen / young adult  (part-time job, save)
     *   22–29  → young professional  (career, student loans, invest)
     *   30–50  → mid-life            (house, kids, invest)
     *   51+    → pre-retirement      (max retirement fund)
     *
     * @param age  The player's current age.
     */
    public void updateActionPanel(int age) {
        actionPanel.removeAll();
 
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonRow.setOpaque(false);

        if (age >= 16 && age <= 21) {
            // ---- TEEN / YOUNG ADULT ----------------------------------------
            JButton btnJob  = makeIconButton("Get Part-Time Job", "job.png",  new Color(180, 120, 30));
            JButton btnSave = makeIconButton("Save",              "save.png", new Color(30, 140, 100));
 
            btnJob.addActionListener(e -> {
                // TODO Partner A: implement BackendLogic.getPartTimeJob()
                BackendLogic.getPartTimeJob();
                refreshStats();
            });
 
            btnSave.addActionListener(e -> {
                // TODO Partner A: implement BackendLogic.saveMoneyToAccount()
                BackendLogic.saveMoneyToAccount();
                refreshStats();
            });
 
            buttonRow.add(btnJob);
            buttonRow.add(btnSave);

        } else if (age >= 22 && age <= 29) {
            // ---- YOUNG PROFESSIONAL ----------------------------------------
            JButton btnCareer = makeIconButton("Start Career",       "job.png",   new Color(30, 100, 180));
            JButton btnLoans  = makeIconButton("Pay Student Loans",  "loans.png", new Color(180, 60, 60));
            JButton btnInvest = makeIconButton("Invest",             "invest.png",new Color(30, 160, 80));
 
            btnCareer.addActionListener(e -> {
                // TODO Partner A: implement BackendLogic.startCareer()
                BackendLogic.startCareer();
                refreshStats();
            });
 
            btnLoans.addActionListener(e -> {
                // TODO Partner A: implement BackendLogic.payStudentLoans()
                BackendLogic.payStudentLoans();
                refreshStats();
            });
 
            btnInvest.addActionListener(e -> {
                // TODO Partner A: implement BackendLogic.investMoney()
                BackendLogic.investMoney();
                refreshStats();
            });
 
            buttonRow.add(btnCareer);
            buttonRow.add(btnLoans);
            buttonRow.add(btnInvest);

        } else if (age >= 30 && age <= 50) {
            // ---- MID-LIFE --------------------------------------------------
            JButton btnHouse  = makeIconButton("Buy House",  "house.png",  new Color(140, 80, 20));
            JButton btnKids   = makeIconButton("Have Kids",  "kids.png",   new Color(180, 60, 120));
            JButton btnInvest = makeIconButton("Invest",     "invest.png", new Color(30, 160, 80));
 
            btnHouse.addActionListener(e -> {
                // TODO Partner A: implement BackendLogic.buyHouse()
                BackendLogic.buyHouse();
                refreshStats();
            });
 
            btnKids.addActionListener(e -> {
                // TODO Partner A: implement BackendLogic.haveKids()
                BackendLogic.haveKids();
                refreshStats();
            });
 
            btnInvest.addActionListener(e -> {
                // TODO Partner A: implement BackendLogic.investMoney()
                BackendLogic.investMoney();
                refreshStats();
            });
 
            buttonRow.add(btnHouse);
            buttonRow.add(btnKids);
            buttonRow.add(btnInvest);
 
        } else {
            // ---- PRE-RETIREMENT (age > 50) ---------------------------------
            JButton btnRetire = makeIconButton("Max Retirement Fund", "retirement.png", new Color(100, 60, 180));
 
            btnRetire.addActionListener(e -> {
                // TODO Partner A: implement BackendLogic.maxRetirementFund()
                BackendLogic.maxRetirementFund();
                refreshStats();
            });
 
            buttonRow.add(btnRetire);
        }
 
        // Add an age-range label above the buttons for clarity
        JLabel ageLabel = new JLabel(getLifeStageName(age), SwingConstants.CENTER);
        ageLabel.setForeground(new Color(180, 180, 220));
        ageLabel.setFont(new Font("Serif", Font.ITALIC, 14));
 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10,0,4,0);
        actionPanel.add(ageLabel, gbc);
 
        gbc.gridy = 1;
        actionPanel.add(buttonRow, gbc);
 
        // --- Commit the rebuild ---
        actionPanel.revalidate();
        actionPanel.repaint();
    }
 
    /** Maps an age to a human-readable life-stage string for the panel header. */
    private String getLifeStageName(int age) {
        if      (age <= 21) return "Life Stage: Teen / Young Adult";
        else if (age <= 29) return "Life Stage: Young Professional";
        else if (age <= 50) return "Life Stage: Mid-Life";
        else                return "Life Stage: Pre-Retirement";
    }
 
 
    // =========================================================================
    //  STATS REFRESH
    // =========================================================================
 
    /**
     * Re-syncs the NORTH stats bar with the current game state.
     *
     * TODO: Replace the local field reads with calls to Partner A's getters, e.g.:
     *   playerAge      = BackendLogic.getPlayerAge();
     *   playerNetWorth = BackendLogic.getPlayerNetWorth();
     *   playerName     = BackendLogic.getPlayerName();
     */
    public void refreshStats() {
        // TODO: pull live values from BackendLogic once Partner A's getters exist
        // playerAge      = BackendLogic.getPlayerAge();
        // playerNetWorth = BackendLogic.getPlayerNetWorth();
 
        labelName    .setText("Name: "       + playerName);
        labelAge     .setText("Age: "        + playerAge);
        labelNetWorth.setText("Net Worth: $" + String.format("%.2f", playerNetWorth));
    }
 
 
    // =========================================================================
    //  UTILITY — Icon Button Factory
    // =========================================================================
 
    /**
     * Creates a styled JButton with an optional ImageIcon loaded from ASSET_PATH.
     * If the image file does not exist, the button falls back to text-only gracefully.
     *
     * @param label      Button label text (always shown as tooltip; shown as text if no icon).
     * @param imageFile  Filename inside ASSET_PATH, e.g. "invest.png".
     * @param bgColor    Background colour for the button.
     * @return           Configured JButton ready to have an ActionListener attached.
     */
    private JButton makeIconButton(String label, String imageFile, Color bgColor) {
        JButton button;
 
        ImageIcon icon = loadIcon(imageFile);
        if (icon != null) {
            button = new JButton(icon);   // icon-only button
            button.setToolTipText(label); // text lives in tooltip for icon buttons
        } else {
            button = new JButton(label);  // graceful text fallback
        }
 
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setPreferredSize(new Dimension(160, 60));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
 
        return button;
    }
 
    /**
     * Attempts to load an ImageIcon from ASSET_PATH.
     * Returns null (no exception thrown) if the file is missing — callers
     * should always check for null and fall back to text.
     *
     * @param filename  e.g. "invest.png"
     * @return          ImageIcon, or null if not found.
     */
    private ImageIcon loadIcon(String filename) {
        try {
            java.net.URL imgURL = getClass().getResource(ASSET_PATH + filename);
            if (imgURL != null) {
                return new ImageIcon(imgURL);
            }
            // Fallback: try file system path (useful during development)
            java.io.File imgFile = new java.io.File(ASSET_PATH + filename);
            if (imgFile.exists()) {
                return new ImageIcon(imgFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("[GameGUI] Warning: could not load icon '" + filename + "' — " + e.getMessage());
        }
        return null; // graceful fallback
    }
 
 
    // =========================================================================
    //  STUB — BackendLogic placeholder so this file compiles standalone
    //  DELETE this inner class once Partner A provides BackendLogic.java
    // =========================================================================
 
    /**
     * TEMPORARY STUB — Partner A replaces this with the real BackendLogic class.
     *
     * This inner class exists solely so GameGUI.java compiles and runs by itself
     * for UI testing.  Once Partner A delivers BackendLogic.java, remove this
     * entire static inner class and ensure BackendLogic.java is on the classpath.
     */
    static class BackendLogic {
        public static void simulateYears(int years)    { System.out.println("[STUB] simulateYears("    + years + ")"); }
        public static void getPartTimeJob()             { System.out.println("[STUB] getPartTimeJob()");                }
        public static void saveMoneyToAccount()         { System.out.println("[STUB] saveMoneyToAccount()");           }
        public static void startCareer()                { System.out.println("[STUB] startCareer()");                  }
        public static void payStudentLoans()            { System.out.println("[STUB] payStudentLoans()");              }
        public static void investMoney()                { System.out.println("[STUB] investMoney()");                  }
        public static void buyHouse()                   { System.out.println("[STUB] buyHouse()");                     }
        public static void haveKids()                   { System.out.println("[STUB] haveKids()");                     }
        public static void maxRetirementFund()          { System.out.println("[STUB] maxRetirementFund()");            }
        // TODO Partner A: add getPlayerAge(), getPlayerNetWorth(), getPlayerName()
    }
 
 
    // =========================================================================
    //  ENTRY POINT
    // =========================================================================
    public static void main(String[] args) {
        // Run on the Event Dispatch Thread as required by Swing
        SwingUtilities.invokeLater(() -> new GameGUI("Alex"));
    }
}
