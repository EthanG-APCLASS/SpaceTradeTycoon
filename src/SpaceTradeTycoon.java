import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SpaceTradeTycoon extends JFrame {

    private final GameState state;
    private JLabel moneyLabel;
    private JLabel fuelLabel;
    private JLabel locationLabel;
    private JLabel engineLabel;
    private JLabel cargoLabel;
    private JLabel messageLabel;

    private JTable destinationTable;
    private DefaultTableModel tableModel;

    private final Random random = new Random();

    public SpaceTradeTycoon() {
        state = new GameState();
        setTitle("Space Trade Tycoon");
        setSize(980, 640);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        refreshUI();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        moneyLabel = new JLabel();
        fuelLabel = new JLabel();
        locationLabel = new JLabel();
        engineLabel = new JLabel();
        cargoLabel = new JLabel();
        messageLabel = new JLabel("Welcome captain. Start trading to grow your business.");

        panel.add(moneyLabel);
        panel.add(fuelLabel);
        panel.add(locationLabel);
        panel.add(engineLabel);
        panel.add(cargoLabel);
        panel.add(messageLabel);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        String[] columns = {"Place", "Type", "Distance", "Fuel Cost", "Trade Value", "Profit Hint"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        destinationTable = new JTable(tableModel);
        destinationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        destinationTable.setRowHeight(24);

        panel.add(new JScrollPane(destinationTable), BorderLayout.CENTER);
        panel.add(createButtonPanel(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 8, 8));

        JButton travelButton = new JButton("Travel");
        JButton buyFuelButton = new JButton("Buy Fuel");
        JButton engineButton = new JButton("Upgrade Engine");
        JButton cargoButton = new JButton("Upgrade Cargo");
        JButton shieldButton = new JButton("Upgrade Shields");
        JButton repairButton = new JButton("Repair Ship");
        JButton randomEventButton = new JButton("Check Market");
        JButton resetButton = new JButton("New Game");

        travelButton.addActionListener(e -> travelToSelectedDestination());
        buyFuelButton.addActionListener(e -> buyFuel());
        engineButton.addActionListener(e -> buyUpgrade(UpgradeType.ENGINE));
        cargoButton.addActionListener(e -> buyUpgrade(UpgradeType.CARGO));
        shieldButton.addActionListener(e -> buyUpgrade(UpgradeType.SHIELDS));
        repairButton.addActionListener(e -> repairShip());
        randomEventButton.addActionListener(e -> marketUpdate());
        resetButton.addActionListener(e -> resetGame());

        panel.add(travelButton);
        panel.add(buyFuelButton);
        panel.add(engineButton);
        panel.add(cargoButton);
        panel.add(shieldButton);
        panel.add(repairButton);
        panel.add(randomEventButton);
        panel.add(resetButton);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        JTextArea helpArea = new JTextArea(
                "Goal: travel between planets and moons, manage fuel, and make enough money to upgrade your ship.\n" +
                        "Tip: each destination has a different profit hint, but longer trips cost more fuel."
        );
        helpArea.setEditable(false);
        helpArea.setLineWrap(true);
        helpArea.setWrapStyleWord(true);
        helpArea.setBackground(getBackground());
        helpArea.setFont(new Font("SansSerif", Font.PLAIN, 13));

        panel.add(helpArea, BorderLayout.CENTER);
        return panel;
    }

    private void refreshUI() {
        moneyLabel.setText("Cash: $" + state.cash);
        fuelLabel.setText("Fuel: " + state.fuel + "/" + state.ship.maxFuel);
        locationLabel.setText("Location: " + state.currentLocation.name);
        engineLabel.setText("Engine Level: " + state.ship.engineLevel + "   Speed: " + state.ship.getSpeedText());
        cargoLabel.setText("Cargo Bay: " + state.ship.cargo + "/" + state.ship.maxCargo);

        tableModel.setRowCount(0);
        for (Planet p : state.destinations) {
            int fuelNeeded = state.ship.getFuelNeeded(p.distanceFromStart);
            int profitHint = p.tradeValue - fuelNeeded * 2;
            tableModel.addRow(new Object[]{
                    p.name,
                    p.isMoon ? "Moon" : "Planet",
                    p.distanceFromStart,
                    fuelNeeded,
                    "$" + p.tradeValue,
                    (profitHint >= 0 ? "+" : "") + profitHint
            });
        }

        if (state.gameOver) {
            messageLabel.setText("Game over: " + state.gameOverReason);
        }
    }

    private void travelToSelectedDestination() {
        if (state.gameOver) {
            showMessage("The game is over. Start a new game to continue.");
            return;
        }

        int row = destinationTable.getSelectedRow();
        if (row < 0) {
            showMessage("Pick a destination first.");
            return;
        }

        Planet target = state.destinations.get(row);
        if (target.name.equals(state.currentLocation.name)) {
            showMessage("You are already there.");
            return;
        }

        int fuelNeeded = state.ship.getFuelNeeded(target.distanceFromStart);
        if (state.fuel < fuelNeeded) {
            showMessage("Not enough fuel for that trip.");
            return;
        }

        state.fuel -= fuelNeeded;
        state.currentLocation = target;

        int income = target.tradeValue;
        int cargoBonus = state.ship.cargo * 2;
        int totalIncome = income + cargoBonus;
        state.cash += totalIncome;

        // Small random event for variety.
        if (random.nextInt(100) < 20) {
            int extra = 25 + random.nextInt(76);
            state.cash += extra;
            showMessage("You found a bonus contract at " + target.name + ": +$" + extra + ". Earned $" + totalIncome + " from trade.");
        } else {
            showMessage("Traveled to " + target.name + ". Earned $" + totalIncome + " from trade.");
        }

        checkGameOver();
        refreshUI();
    }

    private void buyFuel() {
        if (state.gameOver) {
            showMessage("The game is over. Start a new game to continue.");
            return;
        }

        int fuelToBuy = 20;
        int cost = fuelToBuy * state.fuelPrice;
        if (state.cash < cost) {
            showMessage("Not enough cash to buy fuel.");
            return;
        }

        int space = state.ship.maxFuel - state.fuel;
        if (space <= 0) {
            showMessage("Fuel tank is already full.");
            return;
        }

        int actualFuel = Math.min(fuelToBuy, space);
        int actualCost = actualFuel * state.fuelPrice;
        state.cash -= actualCost;
        state.fuel += actualFuel;
        showMessage("Bought " + actualFuel + " fuel for $" + actualCost + ".");
        refreshUI();
    }

    private void buyUpgrade(UpgradeType type) {
        if (state.gameOver) {
            showMessage("The game is over. Start a new game to continue.");
            return;
        }

        Upgrade upgrade = Upgrade.getUpgrade(type, state);
        if (state.cash < upgrade.cost) {
            showMessage("You need $" + upgrade.cost + " for that upgrade.");
            return;
        }

        state.cash -= upgrade.cost;
        if (type == UpgradeType.ENGINE) {
            state.ship.engineLevel++;
            state.ship.maxFuel += 10;
        } else if (type == UpgradeType.CARGO) {
            state.ship.cargo += 5;
            state.ship.maxCargo += 5;
        } else if (type == UpgradeType.SHIELDS) {
            state.ship.shieldLevel++;
            state.ship.maxFuel += 5;
        }

        showMessage("Purchased upgrade: " + upgrade.name + ".");
        refreshUI();
    }

    private void repairShip() {
        if (state.gameOver) {
            showMessage("The game is over. Start a new game to continue.");
            return;
        }

        int repairCost = 60;
        if (state.cash < repairCost) {
            showMessage("You need $60 to repair the ship.");
            return;
        }

        state.cash -= repairCost;
        state.damage = Math.max(0, state.damage - 25);
        showMessage("Ship repaired. Damage reduced.");
        refreshUI();
    }

    private void marketUpdate() {
        if (state.gameOver) {
            showMessage("The game is over. Start a new game to continue.");
            return;
        }

        int change = random.nextInt(7) - 3; // -3 to +3
        state.fuelPrice = Math.max(1, state.fuelPrice + change);
        showMessage("Market update: fuel price is now $" + state.fuelPrice + " per unit.");
        refreshUI();
    }

    private void resetGame() {
        state.reset();
        showMessage("New game started.");
        refreshUI();
    }

    private void checkGameOver() {
        if (state.cash <= 0 && state.fuel <= 0) {
            state.gameOver = true;
            state.gameOverReason = "You ran out of money and fuel.";
        }
        if (state.damage >= 100) {
            state.gameOver = true;
            state.gameOverReason = "Your ship was destroyed by too much damage.";
        }
    }

    private void showMessage(String text) {
        messageLabel.setText(text);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SpaceTradeTycoon game = new SpaceTradeTycoon();
            game.setVisible(true);
        });
    }
}

class GameState {
    int cash;
    int fuel;
    int fuelPrice;
    int damage;
    boolean gameOver;
    String gameOverReason;

    Ship ship;
    Planet currentLocation;
    List<Planet> destinations;

    GameState() {
        reset();
    }

    void reset() {
        cash = 500;
        fuel = 50;
        fuelPrice = 4;
        damage = 0;
        gameOver = false;
        gameOverReason = "";

        ship = new Ship();
        currentLocation = new Planet("Earth Station", "Home Base", 0, 20, false);
        destinations = new ArrayList<>();

        destinations.add(new Planet("Mercury Outpost", "Planet", 12, 70, false));
        destinations.add(new Planet("Venus Harbor", "Planet", 18, 90, false));
        destinations.add(new Planet("Moon Base Alpha", "Moon", 3, 25, true));
        destinations.add(new Planet("Mars Colony", "Planet", 25, 120, false));
        destinations.add(new Planet("Europa Dock", "Moon", 35, 150, true));
        destinations.add(new Planet("Titan Port", "Moon", 45, 180, true));
        destinations.add(new Planet("Jupiter Relay", "Planet", 50, 220, false));
    }
}

class Ship {
    int engineLevel;
    int shieldLevel;
    int cargo;
    int maxCargo;
    int maxFuel;

    Ship() {
        engineLevel = 1;
        shieldLevel = 1;
        cargo = 10;
        maxCargo = 10;
        maxFuel = 60;
    }

    int getFuelNeeded(int distance) {
        int base = Math.max(1, distance / 2);
        int engineBonus = engineLevel - 1;
        int fuelNeeded = base - engineBonus;
        return Math.max(1, fuelNeeded);
    }

    String getSpeedText() {
        if (engineLevel == 1) return "Slow";
        if (engineLevel == 2) return "Moderate";
        if (engineLevel == 3) return "Fast";
        return "Very Fast";
    }
}

class Planet {
    String name;
    String type;
    int distanceFromStart;
    int tradeValue;
    boolean isMoon;

    Planet(String name, String type, int distanceFromStart, int tradeValue, boolean isMoon) {
        this.name = name;
        this.type = type;
        this.distanceFromStart = distanceFromStart;
        this.tradeValue = tradeValue;
        this.isMoon = isMoon;
    }
}

enum UpgradeType {
    ENGINE,
    CARGO,
    SHIELDS
}

class Upgrade {
    String name;
    int cost;

    Upgrade(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    static Upgrade getUpgrade(UpgradeType type, GameState state) {
        if (type == UpgradeType.ENGINE) {
            int cost = 150 + (state.ship.engineLevel * 100);
            return new Upgrade("Engine Upgrade", cost);
        }
        if (type == UpgradeType.CARGO) {
            int cost = 120 + (state.ship.maxCargo * 10);
            return new Upgrade("Cargo Bay Expansion", cost);
        }
        int cost = 100 + (state.ship.shieldLevel * 90);
        return new Upgrade("Shield Upgrade", cost);
    }
}
