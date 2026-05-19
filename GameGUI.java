import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * GameGUI.java  —  Frontend for the 50-Year Life & Finance Simulator
 * ==================================================================
 * Author  : Partner B (Frontend / Graphics)
 * Backend : Partner A  →  Person.java, Budget.java, LifeEvent.java,
 *                         GameEngine.java, Main.java
 *
 * WIRING CONTRACT (all calls reference Partner A's EXACT class/method names):
 * ┌─────────────────────────────────────────────────────────────────────┐
 * │  Person      → getName(), getAge(), getCash(), getInvestments(),    │
 * │                getNetWorth(), getHappiness()                        │
 * │  Budget      → getTotalMonthlyExpenses(), calculateAnnualSavings()  │
 * │  LifeEvent   → generateRandomEvent(), getEventName(),               │
 * │                getFinancialImpact()                                  │
 * │  GameEngine  → simulateOneYear(Person, String)                      │
 * │                simulateTenYears(Person, String)                     │
 * └─────────────────────────────────────────────────────────────────────┘
 *
 * LAYOUT (BorderLayout):
 *   NORTH  → Stats bar  : Name | Age | Cash | Investments | Net Worth | Happiness
 *   CENTER → Action panel: age-gated buttons (rebuilt by updateActionPanel)
 *   SOUTH  → Time controls: Simulate 1 Year | Simulate 10 Years
 *
 * IMAGE ASSETS  (drop into  assets/  folder next to .class files):
 *   assets/job.png         assets/save.png      assets/loans.png
 *   assets/invest.png      assets/house.png      assets/kids.png
 *   assets/retirement.png  assets/sim1.png       assets/sim10.png
 *   assets/vacation.png    assets/career.png
 *   Missing files fall back to text-only buttons — no crash.
 */
public class GameGUI extends JFrame {

    // ── Window constants ────────────────────────────────────────────────────
    private static final String TITLE        = "LifeFinance Simulator";
    private static final int    WIN_W        = 960;
    private static final int    WIN_H        = 620;
    private static final String ASSET_PATH   = "assets/";

    // ── Color palette ───────────────────────────────────────────────────────
    private static final Color BG_DARK       = new Color(15,  17,  26);
    private static final Color BG_MID        = new Color(24,  28,  45);
    private static final Color BG_PANEL      = new Color(32,  38,  60);
    private static final Color ACCENT_GOLD   = new Color(255, 196, 0);
    private static final Color ACCENT_TEAL   = new Color(0,   210, 190);
    private static final Color ACCENT_ROSE   = new Color(255,  80, 110);
    private static final Color TEXT_LIGHT    = new Color(220, 225, 245);
    private static final Color TEXT_DIM      = new Color(130, 140, 170);

    // ── Backend objects ──────────────────────────────────────────────────────
    /**
     * The single Person object that lives for the whole session.
     * Uses Person(String name) → age=16, cash=1000, happiness=100.
     */
    private Person player;

    /**
     * Fixed budget matching Main.java defaults.
     * Uses Budget(double rent, double food, double transport, double entertainment).
     */
    private Budget playerBudget = new Budget(1200.0, 400.0, 300.0, 200.0);

    /** Mirrors Main.java's lifeHistory ArrayList. */
    private ArrayList<String> lifeHistory = new ArrayList<>();

    /**
     * Action string queued by the player's button click.
     * Passed into GameEngine.simulateOneYear(player, pendingAction).
     * Strings must match the switch cases in GameEngine.processAction():
     *   "Part-Time Job" | "Start Career" | "Invest 50%" | "Vacation"
     * NOTE: "Pay Student Loans", "Buy House", "Have Kids", "Max Retirement Fund"
     *       are stubs — Partner A adds those cases to GameEngine when ready.
     */
    private String pendingAction = "Part-Time Job";

    // ── UI components ────────────────────────────────────────────────────────
    private JLabel lblName, lblAge, lblCash, lblInvestments, lblNetWorth, lblHappiness;
    private JPanel actionPanel;
    private JTextArea eventLog;


    // =========================================================================
    //  CONSTRUCTOR
    // =========================================================================
    public GameGUI() {
        String name = JOptionPane.showInputDialog(
            null, "Enter your character's name:", "New Game", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.isBlank()) name = "Player";

        // Person(String name) — starts at age 16 with $1,000 cash
        player = new Person(name);

        setTitle(TITLE + "  —  " + player.getName());
        setSize(WIN_W, WIN_H);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        buildFrame();
        updateActionPanel(player.getAge());
        refreshStats();

        setVisible(true);
    }


    // =========================================================================
    //  FRAME ASSEMBLY  (BorderLayout)
    // =========================================================================
    private void buildFrame() {
        setLayout(new BorderLayout(0, 0));
        add(buildStatsPanel(),       BorderLayout.NORTH);
        add(buildCenterSplit(),      BorderLayout.CENTER);
        add(buildTimeControlPanel(), BorderLayout.SOUTH);
    }

    /** CENTER: action panel (left) + event log sidebar (right). */
    private JSplitPane buildCenterSplit() {
        actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setBackground(BG_MID);

        eventLog = new JTextArea("── Event Log ──\n");
        eventLog.setEditable(false);
        eventLog.setBackground(new Color(12, 14, 22));
        eventLog.setForeground(TEXT_DIM);
        eventLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        eventLog.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(eventLog);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(12, 14, 22));

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(new Color(12, 14, 22));
        JLabel logTitle = new JLabel("  Life Events", SwingConstants.LEFT);
        logTitle.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 13));
        logTitle.setForeground(ACCENT_GOLD);
        logTitle.setBorder(new EmptyBorder(8, 8, 8, 8));
        logPanel.add(logTitle, BorderLayout.NORTH);
        logPanel.add(scroll,   BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, actionPanel, logPanel);
        split.setDividerLocation(680);
        split.setDividerSize(3);
        split.setBorder(BorderFactory.createEmptyBorder());
        return split;
    }


    // =========================================================================
    //  NORTH — Stats Panel
    // =========================================================================
    private JPanel buildStatsPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(BG_DARK);
        outer.setBorder(new MatteBorder(0, 0, 2, 0, ACCENT_GOLD));

        JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 10));
        inner.setBackground(BG_DARK);

        lblName        = statLabel("──");
        lblAge         = statLabel("──");
        lblCash        = statLabel("──");
        lblInvestments = statLabel("──");
        lblNetWorth    = statLabel("──");
        lblHappiness   = statLabel("──");

        inner.add(captionBlock("PLAYER",    lblName));
        inner.add(vDivider());
        inner.add(captionBlock("AGE",       lblAge));
        inner.add(vDivider());
        inner.add(captionBlock("CASH",      lblCash));
        inner.add(vDivider());
        inner.add(captionBlock("INVESTED",  lblInvestments));
        inner.add(vDivider());
        inner.add(captionBlock("NET WORTH", lblNetWorth));
        inner.add(vDivider());
        inner.add(captionBlock("HAPPINESS", lblHappiness));

        outer.add(inner, BorderLayout.CENTER);
        return outer;
    }

    private JLabel statLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Monospaced", Font.BOLD, 15));
        l.setForeground(TEXT_LIGHT);
        return l;
    }

    private JPanel captionBlock(String caption, JLabel val) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel cap = new JLabel(caption);
        cap.setFont(new Font("SansSerif", Font.PLAIN, 9));
        cap.setForeground(TEXT_DIM);
        cap.setAlignmentX(Component.LEFT_ALIGNMENT);
        val.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(cap); p.add(val);
        return p;
    }

    private JSeparator vDivider() {
        JSeparator s = new JSeparator(SwingConstants.VERTICAL);
        s.setPreferredSize(new Dimension(1, 40));
        s.setForeground(new Color(50, 55, 80));
        return s;
    }


    // =========================================================================
    //  SOUTH — Time Control Panel
    // =========================================================================
    private JPanel buildTimeControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 12));
        panel.setBackground(BG_DARK);
        panel.setBorder(new MatteBorder(2, 0, 0, 0, new Color(40, 45, 70)));

        JButton btn1  = timeButton("▶  Simulate 1 Year",   "sim1.png",  ACCENT_TEAL);
        JButton btn10 = timeButton("▶▶ Simulate 10 Years", "sim10.png", ACCENT_GOLD);

        // ── Simulate 1 Year ──────────────────────────────────────────────────
        btn1.addActionListener(e -> {
            // Roll a random life event via LifeEvent.generateRandomEvent()
            LifeEvent event = LifeEvent.generateRandomEvent();

            // Log it using LifeEvent.getEventName() and getFinancialImpact()
            String logLine = "Age " + player.getAge() + ": "
                    + event.getEventName()
                    + "  ($" + String.format("%+.0f", event.getFinancialImpact()) + ")";
            appendLog(logLine);
            lifeHistory.add("Age " + player.getAge() + ": " + event.getEventName());

            // Apply the one-off financial impact directly to cash
            player.addCash(event.getFinancialImpact());

            // Delegate the full year simulation to Partner A's GameEngine:
            //   - processAction(player, pendingAction)  ← queued by action button
            //   - applyCompoundInterest(player)         ← 4% on cash
            //   - simulateMarketYear(player)            ← Gaussian stock model
            //   - player.addAge(1)
            GameEngine.simulateOneYear(player, pendingAction);

            refreshStats();
            updateActionPanel(player.getAge());
            checkGameOver();
        });

        // ── Simulate 10 Years ────────────────────────────────────────────────
        btn10.addActionListener(e -> {
            for (int i = 0; i < 10; i++) {
                LifeEvent event = LifeEvent.generateRandomEvent();
                String logLine = "Age " + player.getAge() + ": "
                        + event.getEventName()
                        + "  ($" + String.format("%+.0f", event.getFinancialImpact()) + ")";
                appendLog(logLine);
                lifeHistory.add("Age " + player.getAge() + ": " + event.getEventName());
                player.addCash(event.getFinancialImpact());

                // GameEngine.simulateOneYear handles interest + market + age each loop
                GameEngine.simulateOneYear(player, pendingAction);
            }
            refreshStats();
            updateActionPanel(player.getAge());
            checkGameOver();
        });

        panel.add(btn1);
        panel.add(btn10);
        return panel;
    }


    // =========================================================================
    //  CENTER — Dynamic Action Panel  (age-gated, rebuilds on each simulate)
    // =========================================================================

    /**
     * Clears and rebuilds the CENTER action panel for the given age.
     *
     * Age gates and their pending action strings
     * (must match GameEngine.processAction() switch cases):
     *
     *   16–21 → "Part-Time Job"       (GameEngine ✓)
     *           "Save"                (TODO Partner A: add case)
     *   22–29 → "Start Career"        (GameEngine ✓)
     *           "Pay Student Loans"   (TODO Partner A: add case)
     *           "Invest 50%"          (GameEngine ✓)
     *   30–50 → "Buy House"           (TODO Partner A: add case)
     *           "Have Kids"           (TODO Partner A: add case)
     *           "Invest 50%"          (GameEngine ✓)
     *           "Vacation"            (GameEngine ✓)
     *   51+   → "Max Retirement Fund" (TODO Partner A: add case)
     *
     * @param age  Current age from Person.getAge()
     */
    public void updateActionPanel(int age) {
        actionPanel.removeAll();

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        // Life-stage header
        JLabel stageLabel = new JLabel(getLifeStageName(age), SwingConstants.CENTER);
        stageLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 18));
        stageLabel.setForeground(ACCENT_GOLD);
        stageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        stageLabel.setBorder(new EmptyBorder(18, 0, 4, 0));
        container.add(stageLabel);

        JLabel hint = new JLabel("Select an action, then hit Simulate →", SwingConstants.CENTER);
        hint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        hint.setForeground(TEXT_DIM);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        hint.setBorder(new EmptyBorder(0, 0, 16, 0));
        container.add(hint);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        buttonRow.setOpaque(false);

        if (age >= 16 && age <= 21) {
            // ── Teen / Young Adult ───────────────────────────────────────────
            JButton btnJob  = actionButton("Get Part-Time Job", "job.png",  new Color(220, 150, 30));
            JButton btnSave = actionButton("Save",              "save.png", new Color(30, 160, 100));

            btnJob.addActionListener(e -> {
                pendingAction = "Part-Time Job"; // GameEngine.processAction() case
                highlightSelected(buttonRow, btnJob);
                appendLog("→ Action queued: Part-Time Job (+$5,000, happiness -5)");
            });
            btnSave.addActionListener(e -> {
                pendingAction = "Save"; // TODO Partner A: add "Save" case to GameEngine
                highlightSelected(buttonRow, btnSave);
                appendLog("→ Action queued: Save");
            });

            buttonRow.add(btnJob);
            buttonRow.add(btnSave);

        } else if (age >= 22 && age <= 29) {
            // ── Young Professional ───────────────────────────────────────────
            JButton btnCareer = actionButton("Start Career",      "career.png", new Color(30,  120, 210));
            JButton btnLoans  = actionButton("Pay Student Loans", "loans.png",  new Color(200,  70,  70));
            JButton btnInvest = actionButton("Invest 50%",        "invest.png", new Color(30,  180,  90));

            btnCareer.addActionListener(e -> {
                pendingAction = "Start Career"; // GameEngine case: +$50,000, happiness -10
                highlightSelected(buttonRow, btnCareer);
                appendLog("→ Action queued: Start Career (+$50,000, happiness -10)");
            });
            btnLoans.addActionListener(e -> {
                pendingAction = "Pay Student Loans"; // TODO Partner A: add case
                highlightSelected(buttonRow, btnLoans);
                appendLog("→ Action queued: Pay Student Loans");
            });
            btnInvest.addActionListener(e -> {
                pendingAction = "Invest 50%"; // GameEngine case: moves 50% cash → investments
                highlightSelected(buttonRow, btnInvest);
                appendLog("→ Action queued: Invest 50% of cash");
            });

            buttonRow.add(btnCareer);
            buttonRow.add(btnLoans);
            buttonRow.add(btnInvest);

        } else if (age >= 30 && age <= 50) {
            // ── Mid-Life ─────────────────────────────────────────────────────
            JButton btnHouse  = actionButton("Buy House",   "house.png",    new Color(160,  90,  20));
            JButton btnKids   = actionButton("Have Kids",   "kids.png",     new Color(200,  60, 130));
            JButton btnInvest = actionButton("Invest 50%",  "invest.png",   new Color(30,  180,  90));
            JButton btnVacay  = actionButton("Vacation",    "vacation.png", new Color(70,  130, 200));

            btnHouse.addActionListener(e -> {
                pendingAction = "Buy House"; // TODO Partner A: add case
                highlightSelected(buttonRow, btnHouse);
                appendLog("→ Action queued: Buy House");
            });
            btnKids.addActionListener(e -> {
                pendingAction = "Have Kids"; // TODO Partner A: add case
                highlightSelected(buttonRow, btnKids);
                appendLog("→ Action queued: Have Kids");
            });
            btnInvest.addActionListener(e -> {
                pendingAction = "Invest 50%"; // GameEngine case ✓
                highlightSelected(buttonRow, btnInvest);
                appendLog("→ Action queued: Invest 50% of cash");
            });
            btnVacay.addActionListener(e -> {
                pendingAction = "Vacation"; // GameEngine case: -$3,000, happiness +20
                highlightSelected(buttonRow, btnVacay);
                appendLog("→ Action queued: Vacation (-$3,000, happiness +20)");
            });

            buttonRow.add(btnHouse);
            buttonRow.add(btnKids);
            buttonRow.add(btnInvest);
            buttonRow.add(btnVacay);

        } else {
            // ── Pre-Retirement (age > 50) ────────────────────────────────────
            JButton btnRetire = actionButton("Max Retirement Fund", "retirement.png",
                                             new Color(120, 60, 200));

            btnRetire.addActionListener(e -> {
                pendingAction = "Max Retirement Fund"; // TODO Partner A: add case
                highlightSelected(buttonRow, btnRetire);
                appendLog("→ Action queued: Max Retirement Fund");
            });

            buttonRow.add(btnRetire);
        }

        container.add(buttonRow);
        container.add(buildBudgetStrip()); // uses Budget.getTotalMonthlyExpenses()

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        actionPanel.add(container, gbc);

        actionPanel.revalidate();
        actionPanel.repaint();
    }

    /** Info strip at the bottom of the action panel — reads from Budget object. */
    private JPanel buildBudgetStrip() {
        JPanel strip = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 6));
        strip.setOpaque(false);
        strip.setBorder(new MatteBorder(1, 0, 0, 0, new Color(40, 45, 70)));

        // Budget.getTotalMonthlyExpenses()  →  rent+food+transport+entertainment
        double monthly = playerBudget.getTotalMonthlyExpenses();
        // Budget.calculateAnnualSavings(yearlyIncome) = yearlyIncome - (monthly * 12)
        // TODO: replace 50000.0 with a real income getter if Partner A adds one to Person
        double annualSavings = playerBudget.calculateAnnualSavings(50000.0);

        JLabel mo = new JLabel("Monthly Expenses: $" + String.format("%.0f", monthly));
        JLabel sa = new JLabel("Est. Annual Savings: $" + String.format("%.0f", annualSavings));
        mo.setFont(new Font("Monospaced", Font.PLAIN, 11));
        sa.setFont(new Font("Monospaced", Font.PLAIN, 11));
        mo.setForeground(TEXT_DIM);
        sa.setForeground(new Color(100, 210, 140));

        strip.add(mo);
        strip.add(sa);
        return strip;
    }


    // =========================================================================
    //  STATS REFRESH — reads from Person getters
    // =========================================================================

    /**
     * Syncs NORTH stats bar with the live Person object.
     * Call this after every GameEngine mutation.
     *
     * Reads: getName(), getAge(), getCash(), getInvestments(),
     *        getNetWorth(), getHappiness()
     */
    public void refreshStats() {
        lblName       .setText(player.getName());
        lblAge        .setText(player.getAge()   + " yrs");
        lblCash       .setText("$" + String.format("%,.0f", player.getCash()));
        lblInvestments.setText("$" + String.format("%,.0f", player.getInvestments()));
        lblNetWorth   .setText("$" + String.format("%,.0f", player.getNetWorth()));

        // Person.getHappiness() is clamped 0–100 by modifyHappiness()
        int h = player.getHappiness();
        lblHappiness.setText(h + " / 100");
        lblHappiness.setForeground(h >= 70 ? new Color(80, 220, 120)
                                 : h >= 40 ? ACCENT_GOLD : ACCENT_ROSE);

        lblNetWorth.setForeground(player.getNetWorth() >= 0
                ? new Color(80, 220, 120) : ACCENT_ROSE);
    }


    // =========================================================================
    //  GAME OVER
    // =========================================================================
    private void checkGameOver() {
        if (player.getAge() >= 66) showEndScreen();
    }

    private void showEndScreen() {
        StringBuilder sb = new StringBuilder();
        sb.append("🎉  SIMULATION COMPLETE!\n\n");
        sb.append("Final Report — ").append(player.getName()).append("\n");
        sb.append("  Age       : ").append(player.getAge()).append("\n");
        sb.append("  Cash      : $").append(String.format("%,.2f", player.getCash())).append("\n");
        sb.append("  Invested  : $").append(String.format("%,.2f", player.getInvestments())).append("\n");
        sb.append("  Net Worth : $").append(String.format("%,.2f", player.getNetWorth())).append("\n");
        sb.append("  Happiness : ").append(player.getHappiness()).append(" / 100\n\n");
        sb.append("── Life History ──\n");
        for (String entry : lifeHistory) sb.append("  ").append(entry).append("\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(480, 360));
        JOptionPane.showMessageDialog(this, sp, "Game Over — Final Report",
                JOptionPane.INFORMATION_MESSAGE);
    }


    // =========================================================================
    //  HELPERS
    // =========================================================================
    private void appendLog(String line) {
        eventLog.append(line + "\n");
        eventLog.setCaretPosition(eventLog.getDocument().getLength());
    }

    private void highlightSelected(JPanel row, JButton selected) {
        for (Component c : row.getComponents()) {
            if (c instanceof JButton btn) {
                btn.setBorder(BorderFactory.createLineBorder(
                        c == selected ? ACCENT_GOLD : new Color(60, 65, 90), 2));
            }
        }
    }

    private String getLifeStageName(int age) {
        if      (age <= 21) return "Teen / Young Adult  (16–21)";
        else if (age <= 29) return "Young Professional  (22–29)";
        else if (age <= 50) return "Mid-Life            (30–50)";
        else                return "Pre-Retirement      (51+)";
    }

    // ── Button factories ──────────────────────────────────────────────────────
    private JButton actionButton(String label, String img, Color accent) {
        return buildButton(label, img, accent, 155, 72, new Font("SansSerif", Font.BOLD, 13));
    }
    private JButton timeButton(String label, String img, Color accent) {
        return buildButton(label, img, accent, 210, 44, new Font("SansSerif", Font.BOLD, 14));
    }

    private JButton buildButton(String label, String imageFile,
                                Color accent, int w, int h, Font font) {
        ImageIcon icon = loadIcon(imageFile);
        JButton btn;
        if (icon != null) {
            Image scaled = icon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
            btn = new JButton(label, new ImageIcon(scaled));
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        } else {
            btn = new JButton(label);
        }
        btn.setPreferredSize(new Dimension(w, h));
        btn.setBackground(accent.darker().darker());
        btn.setForeground(TEXT_LIGHT);
        btn.setFont(font);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(accent.darker(), 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Color hoverBg  = accent.darker();
        Color normalBg = accent.darker().darker();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverBg);
                btn.setBorder(BorderFactory.createLineBorder(accent, 2));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(normalBg);
                btn.setBorder(BorderFactory.createLineBorder(accent.darker(), 1));
            }
        });
        return btn;
    }

    private ImageIcon loadIcon(String filename) {
        try {
            java.net.URL url = getClass().getResource(ASSET_PATH + filename);
            if (url != null) return new ImageIcon(url);
            java.io.File f = new java.io.File(ASSET_PATH + filename);
            if (f.exists()) return new ImageIcon(f.getAbsolutePath());
        } catch (Exception ex) {
            System.err.println("[GameGUI] Missing icon: " + filename);
        }
        return null;
    }


    // =========================================================================
    //  ENTRY POINT
    // =========================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameGUI::new);
    }
}
