import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;

public class GamePanel extends JPanel {
    private final String[] PLANET_NAMES = {
            "Earth (Home)",
            "Mercury Outpost",
            "Venus Station",
            "Moon Base Alpha",
            "Mars Colony",
            "Ceres Mining Hub",
            "Jupiter Orbital",
            "Saturn Ring City",
            "Titan Refinery",
            "Uranus Cloudbase",
            "Neptune Deep Station",
            "Pluto Research Post",
            "Eris Far Outpost",
            "Proxima Centauri b",
            "TRAPPIST-1e",
            "Kepler-452b",
            "HD 209458 b (Osiris)",
            "Gliese 667Cc",
            "WASP-12b",
            "55 Cancri e",
            "Kepler-22b",
            "LHS 1140 b",
            "K2-18b",
            "TOI-700 d",
            "Kepler-186f"
    };

    private final String[] PLANET_TYPES = {
            "Sol System", "Sol System", "Sol System", "Sol System", "Sol System",
            "Sol System", "Sol System", "Sol System", "Sol System", "Sol System",
            "Sol System", "Sol System", "Sol System",
            "Exoplanet", "Exoplanet", "Exoplanet", "Exoplanet", "Exoplanet",
            "Exoplanet", "Exoplanet", "Exoplanet", "Exoplanet", "Exoplanet",
            "Exoplanet", "Exoplanet"
    };

    private final int[] PLANET_DISTANCES = {
            0, 2, 5, 3, 15, 30, 50, 80, 82, 120, 160, 200, 250,
            4000, 5000, 6000, 7000, 8000, 9000, 10000, 12000, 14000, 16000, 18000, 20000
    };

    private final int[] BASE_PROFITS = {
            20, 40, 80, 60, 150, 300, 500, 800, 850, 1200, 1600, 2000, 2800,
            25000, 35000, 48000, 60000, 75000, 90000, 110000, 140000, 170000, 200000, 240000, 300000
    };

    private final int[] REPAIR_COSTS = {
            2, 3, 3, 4, 5, 8, 12, 15, 16, 20, 25, 30, 35,
            100, 120, 150, 180, 200, 220, 250, 300, 350, 400, 450, 500
    };

    private int cash = 500;
    private int fuel = 50;
    private int fuelTank = 100;
    private int hull = 100;
    private int maxHull = 100;
    private int engineLevel = 1;
    private int cargoLevel = 1;
    private int defenseLevel = 0;
    private int location = 0;
    private String message = "Welcome, Captain. Your journey begins at Earth.\n" +
            "Trade goods across the galaxy, upgrade your ship, and make a profit!";

    private JLabel cashLabel, fuelLabel, tankLabel, hullLabel;
    private JLabel engineLabel, cargoLabel, defenseLabel, locationLabel;
    private JTextArea messageArea;
    private JList<String> planetList;
    private JLabel selectedPlanetLabel, fuelCostLabel, profitLabel, riskLabel;

    public GamePanel() {
        setLayout(null);
        setupUI();
        updateScreen();
    }

    private void setupUI() {
        JLabel title = new JLabel("SPACE TRADE TYCOON", SwingConstants.CENTER);
        title.setBounds(0, 10, 900, 40);
        title.setFont(new Font("Monospaced", Font.BOLD, 28));
        add(title);

        int sy = 60;
        int sx = 20;
        int sh = 22;

        cashLabel = createStatLabel(sx, sy); sy += sh;
        fuelLabel = createStatLabel(sx, sy); sy += sh;
        tankLabel = createStatLabel(sx, sy); sy += sh;
        hullLabel = createStatLabel(sx, sy); sy += sh + 5;
        engineLabel = createStatLabel(sx, sy); sy += sh;
        cargoLabel = createStatLabel(sx, sy); sy += sh;
        defenseLabel = createStatLabel(sx, sy); sy += sh;
        locationLabel = createStatLabel(sx, sy); sy += sh + 10;

        add(cashLabel); add(fuelLabel); add(tankLabel); add(hullLabel);
        add(engineLabel); add(cargoLabel); add(defenseLabel); add(locationLabel);

        JLabel listTitle = new JLabel("DESTINATIONS");
        listTitle.setBounds(250, 60, 200, 22);
        listTitle.setFont(new Font("Monospaced", Font.BOLD, 14));
        add(listTitle);

        planetList = new JList<>(PLANET_NAMES);
        planetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        planetList.setFont(new Font("Monospaced", Font.PLAIN, 13));
        planetList.addListSelectionListener(this::onPlanetSelect);

        JScrollPane scroll = new JScrollPane(planetList);
        scroll.setBounds(250, 85, 280, 280);
        add(scroll);

        selectedPlanetLabel = createInfoLabel(250, 375, 280, 22);
        fuelCostLabel = createInfoLabel(250, 400, 280, 22);
        profitLabel = createInfoLabel(250, 425, 280, 22);
        riskLabel = createInfoLabel(250, 450, 280, 22);
        add(selectedPlanetLabel); add(fuelCostLabel); add(profitLabel); add(riskLabel);

        int bx = 550;
        int by = 60;
        int bw = 180;
        int bh = 35;
        int bg = 42;

        JButton travelBtn = createActionButton("Travel", bx, by, bw, bh);
        by += bg;
        JButton fuelBtn = createActionButton("Buy Fuel (+25)", bx, by, bw, bh);
        by += bg;
        JButton tankBtn = createActionButton("Upgrade Fuel Tank", bx, by, bw, bh);
        by += bg;
        JButton engineBtn = createActionButton("Upgrade Engine", bx, by, bw, bh);
        by += bg;
        JButton cargoBtn = createActionButton("Upgrade Cargo", bx, by, bw, bh);
        by += bg;
        JButton defenseBtn = createActionButton("Upgrade Defense", bx, by, bw, bh);
        by += bg;
        JButton repairBtn = createActionButton("Repair Hull", bx, by, bw, bh);
        by += bg;
        JButton resetBtn = createActionButton("New Game", bx, by, bw, bh);

        add(travelBtn); add(fuelBtn); add(tankBtn); add(engineBtn);
        add(cargoBtn); add(defenseBtn); add(repairBtn); add(resetBtn);

        travelBtn.addActionListener(e -> doTravel());
        fuelBtn.addActionListener(e -> buyFuel());
        tankBtn.addActionListener(e -> upgradeTank());
        engineBtn.addActionListener(e -> upgradeEngine());
        cargoBtn.addActionListener(e -> upgradeCargo());
        defenseBtn.addActionListener(e -> upgradeDefense());
        repairBtn.addActionListener(e -> repairHull());
        resetBtn.addActionListener(e -> resetGame());

        JLabel logTitle = new JLabel("MISSION LOG");
        logTitle.setBounds(20, 485, 200, 20);
        logTitle.setFont(new Font("Monospaced", Font.BOLD, 14));
        add(logTitle);

        messageArea = new JTextArea(5, 50);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setEditable(false);
        messageArea.setText(message);

        JScrollPane msgScroll = new JScrollPane(messageArea);
        msgScroll.setBounds(20, 508, 710, 140);
        add(msgScroll);

        JLabel help = new JLabel("Tip: Further destinations = more profit but higher pirate risk!");
        help.setBounds(20, 655, 600, 18);
        help.setFont(new Font("Monospaced", Font.ITALIC, 11));
        add(help);
    }

    private JLabel createStatLabel(int x, int y) {
        JLabel l = new JLabel();
        l.setBounds(x, y, 220, 22);
        l.setFont(new Font("Monospaced", Font.BOLD, 13));
        return l;
    }

    private JLabel createInfoLabel(int x, int y, int w, int h) {
        JLabel l = new JLabel(" ");
        l.setBounds(x, y, w, h);
        l.setFont(new Font("Monospaced", Font.BOLD, 12));
        return l;
    }

    private JButton createActionButton(String text, int x, int y, int w, int h) {
        JButton b = new JButton(text);
        b.setBounds(x, y, w, h);
        b.setFont(new Font("Monospaced", Font.BOLD, 12));
        return b;
    }

    private void onPlanetSelect(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int idx = planetList.getSelectedIndex();
        if (idx < 0) return;
        updateSelectionInfo(idx);
    }

    private void updateSelectionInfo(int dest) {
        if (dest == location) {
            selectedPlanetLabel.setText(">>> CURRENT LOCATION <<<");
            fuelCostLabel.setText("Fuel: --");
            profitLabel.setText("Profit: --");
            riskLabel.setText("Pirate Risk: --");
            return;
        }
        int fuelNeed = calcFuelNeeded(dest);
        int prof = calcBaseProfit(dest);
        double risk = calcPirateRisk(dest);

        selectedPlanetLabel.setText("Dest: " + PLANET_NAMES[dest] + " [" + PLANET_TYPES[dest] + "]");
        fuelCostLabel.setText("Fuel Cost: " + fuelNeed + " (have " + fuel + ")");
        profitLabel.setText("Est. Profit: ~$" + prof);
        riskLabel.setText(String.format("Pirate Risk: %.1f%%", risk * 100));
    }

    private int calcBaseProfit(int dest) {
        int base = BASE_PROFITS[dest];
        int cargoBonus = base * cargoLevel / 4;
        return base + cargoBonus;
    }

    private void doTravel() {
        int dest = planetList.getSelectedIndex();
        if (dest < 0) {
            log("Select a destination from the list first.");
            return;
        }
        if (dest == location) {
            log("You are already at " + PLANET_NAMES[location] + ".");
            return;
        }

        int fuelNeed = calcFuelNeeded(dest);
        if (fuel < fuelNeed) {
            log("Not enough fuel! Need " + fuelNeed + " fuel for this trip. You have " + fuel + ".");
            return;
        }

        fuel -= fuelNeed;
        location = dest;

        int profit = calcProfit(dest);

        double risk = calcPirateRisk(dest);
        boolean attacked = Math.random() < risk;

        if (attacked) {
            int lossPercent = 30 + (int) (Math.random() * 41);
            int lostProfit = profit * lossPercent / 100;
            profit -= lostProfit;

            int hullDamage = 10 + (int) (Math.random() * 21);
            hull -= hullDamage;

            log("PIRATE ATTACK! Your ship was ambushed en route to " + PLANET_NAMES[dest] + "!");
            log("  Pirates stole " + lossPercent + "% of your cargo profit (-$" + lostProfit + ").");
            log("  Hull took " + hullDamage + " damage. Hull integrity: " + Math.max(0, hull) + "/" + maxHull);

            if (hull <= 0) {
                hull = 0;
                cash += profit;
                log("CRITICAL: Your ship's hull has been breached! Emergency repairs cost everything.");
                log("You barely made it to port with $" + profit + " in salvaged goods.");
                updateScreen();
                checkGameOver();
                return;
            }
        } else {
            log("Travel successful. Arrived at " + PLANET_NAMES[dest] + ".");
        }

        cash += profit;
        if (!attacked) {
            log("Delivered cargo and earned $" + profit + ". Cash: $" + cash);
        } else {
            log("After the attack, you earned $" + profit + ". Cash: $" + cash);
        }

        planetList.setSelectedIndex(location);
        updateScreen();
    }

    private void buyFuel() {
        int cost = 50;
        if (cash < cost) {
            log("Not enough cash! Fuel costs $" + cost + ".");
            return;
        }
        if (fuel >= fuelTank) {
            log("Fuel tank is full! (" + fuel + "/" + fuelTank + ") Upgrade tank for more capacity.");
            return;
        }
        int toAdd = 25;
        int actualAdd = Math.min(toAdd, fuelTank - fuel);
        cash -= cost;
        fuel += actualAdd;
        log("Bought " + actualAdd + " fuel for $" + cost + ". (" + fuel + "/" + fuelTank + ")");
        updateScreen();
    }

    private void upgradeTank() {
        int cost = 200 + (fuelTank / 50) * 150;
        if (cash < cost) {
            log("Need $" + cost + " to upgrade fuel tank. You have $" + cash + ".");
            return;
        }
        cash -= cost;
        fuelTank += 50;
        log("Fuel tank upgraded! Capacity: " + fuelTank + " (cost: $" + cost + ")");
        updateScreen();
    }

    private void upgradeEngine() {
        int cost = (int) (100 * Math.pow(1.15, engineLevel));
        if (cash < cost) {
            log("Need $" + cost + " for Engine Lv." + (engineLevel + 1) + ". You have $" + cash + ".");
            return;
        }
        cash -= cost;
        engineLevel++;
        log("Engine upgraded to Level " + engineLevel + "! Fuel efficiency improved. (cost: $" + cost + ")");
        updateScreen();
    }

    private void upgradeCargo() {
        int cost = (int) (80 * Math.pow(1.12, cargoLevel));
        if (cash < cost) {
            log("Need $" + cost + " for Cargo Lv." + (cargoLevel + 1) + ". You have $" + cash + ".");
            return;
        }
        cash -= cost;
        cargoLevel++;
        log("Cargo hold upgraded to Level " + cargoLevel + "! More profit per trip. (cost: $" + cost + ")");
        updateScreen();
    }

    private void upgradeDefense() {
        int cost = (int) (150 * Math.pow(1.25, defenseLevel));
        if (cash < cost) {
            log("Need $" + cost + " for Defense Lv." + (defenseLevel + 1) + ". You have $" + cash + ".");
            return;
        }
        cash -= cost;
        defenseLevel++;
        double newReduction = defenseLevel * 2.5;
        log("Defenses upgraded to Level " + defenseLevel + "! Pirate chance reduced by " + newReduction + "%. (cost: $" + cost + ")");
        updateScreen();
    }

    private void repairHull() {
        if (hull >= maxHull) {
            log("Hull is at full integrity (" + hull + "/" + maxHull + "). No repairs needed.");
            return;
        }
        int damage = maxHull - hull;
        int costPerPoint = REPAIR_COSTS[location];
        int maxAfford = cash / costPerPoint;
        int toRepair = Math.min(damage, maxAfford);

        if (toRepair <= 0) {
            log("Cannot afford repairs here. Cost: $" + costPerPoint + "/point. You have $" + cash + ".");
            return;
        }

        int cost = toRepair * costPerPoint;
        cash -= cost;
        hull += toRepair;
        log("Repaired " + toRepair + " hull points for $" + cost + " ($" + costPerPoint + "/point).");
        log("Hull: " + hull + "/" + maxHull);
        updateScreen();
    }

    private void resetGame() {
        cash = 500;
        fuel = 50;
        fuelTank = 100;
        hull = 100;
        maxHull = 100;
        engineLevel = 1;
        cargoLevel = 1;
        defenseLevel = 0;
        location = 0;
        log("=== NEW GAME STARTED ===");
        log("Welcome back, Captain. Build your trading empire from Earth!");
        planetList.setSelectedIndex(0);
        updateScreen();
    }

    private int calcFuelNeeded(int dest) {
        int dist = Math.abs(PLANET_DISTANCES[dest] - PLANET_DISTANCES[location]);
        if (dist == 0) return 0;
        double efficiency = 1.0 + (engineLevel / 10.0);
        int needed = (int) Math.ceil(dist / efficiency);
        return Math.max(1, needed);
    }

    private int calcProfit(int dest) {
        int base = BASE_PROFITS[dest];
        int cargoBonus = base * cargoLevel / 4;
        int randomBonus = (int) (Math.random() * (base / 10));
        return base + cargoBonus + randomBonus;
    }

    private double calcPirateRisk(int dest) {
        int dist = Math.abs(PLANET_DISTANCES[dest] - PLANET_DISTANCES[location]);
        double baseRisk = Math.min(0.35, dist / 5000.0 + 0.02);
        double reduction = defenseLevel * 0.025;
        return Math.max(0.03, baseRisk - reduction);
    }

    private void updateScreen() {
        cashLabel.setText("Cash:       $" + cash);
        fuelLabel.setText("Fuel:       " + fuel + "/" + fuelTank);
        tankLabel.setText("Fuel Tank:  " + fuelTank + " cap");

        hullLabel.setText("Hull:       " + hull + "/" + maxHull);
        if (hull > 60) hullLabel.setForeground(new Color(0, 128, 0));
        else if (hull > 30) hullLabel.setForeground(new Color(200, 150, 0));
        else hullLabel.setForeground(new Color(180, 0, 0));

        engineLabel.setText("Engine:     Lv." + engineLevel);
        cargoLabel.setText("Cargo:      Lv." + cargoLevel);
        defenseLabel.setText("Defense:    Lv." + defenseLevel);
        locationLabel.setText("Location:   " + PLANET_NAMES[location]);

        int sel = planetList.getSelectedIndex();
        if (sel >= 0) updateSelectionInfo(sel);
        repaint();
    }

    private void log(String msg) {
        messageArea.append("\n" + msg);
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }
    private void checkGameOver() {
        if (hull <= 0) {
            log("");
            log("========================================");
            log("  GAME OVER - Your ship was destroyed!");
            log("  Final Cash: $" + cash);
            log("  Engine Lv:  " + engineLevel);
            log("  Furthest:   " + PLANET_NAMES[location]);
            log("========================================");
            JOptionPane.showMessageDialog(this,
                    "Your ship has been destroyed by pirates!\n" +
                            "Final Cash: $" + cash + "\nEngine Level: " + engineLevel + "\n" +
                            "Furthest Reach: " + PLANET_NAMES[location],
                    "Game Over", JOptionPane.ERROR_MESSAGE);
        }
    }

    // No custom paintComponent - removed starfield and decorative borders

    // ===================== MAIN ENTRY =====================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Deep Space Trade Tycoon");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(760, 720);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);

            GamePanel panel = new GamePanel();
            frame.add(panel);
            frame.setVisible(true);
        });
    }
}