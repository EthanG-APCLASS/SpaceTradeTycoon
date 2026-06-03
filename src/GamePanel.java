import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel
{
    private int cash = 500;
    private int fuel = 50;
    private int engineLevel = 1;
    private int cargoLevel = 1;
    private int location = 0; // 0 = Earth, 1 = Moon, 2 = Mars
    private String message = "Welcome captain. Choose a destination.";

    private JLabel cashLabel;
    private JLabel fuelLabel;
    private JLabel engineLabel;
    private JLabel cargoLabel;
    private JLabel locationLabel;
    private JLabel messageLabel;

    public GamePanel()
    {
        setLayout(null);

        cashLabel = new JLabel();
        fuelLabel = new JLabel();
        engineLabel = new JLabel();
        cargoLabel = new JLabel();
        locationLabel = new JLabel();
        messageLabel = new JLabel();

        cashLabel.setBounds(40, 20, 200, 25);
        fuelLabel.setBounds(40, 45, 200, 25);
        engineLabel.setBounds(40, 70, 200, 25);
        cargoLabel.setBounds(40, 95, 200, 25);
        locationLabel.setBounds(40, 120, 300, 25);
        messageLabel.setBounds(40, 150, 700, 25);

        add(cashLabel);
        add(fuelLabel);
        add(engineLabel);
        add(cargoLabel);
        add(locationLabel);
        add(messageLabel);

        JButton earthButton = new JButton("Earth");
        JButton moonButton = new JButton("Moon Base");
        JButton marsButton = new JButton("Mars Colony");
        JButton fuelButton = new JButton("Buy Fuel");
        JButton engineButton = new JButton("Upgrade Engine");
        JButton cargoButton = new JButton("Upgrade Cargo");
        JButton resetButton = new JButton("New Game");

        earthButton.setBounds(40, 220, 150, 40);
        moonButton.setBounds(210, 220, 150, 40);
        marsButton.setBounds(380, 220, 150, 40);
        fuelButton.setBounds(40, 280, 150, 40);
        engineButton.setBounds(210, 280, 150, 40);
        cargoButton.setBounds(380, 280, 150, 40);
        resetButton.setBounds(550, 220, 150, 100);

        add(earthButton);
        add(moonButton);
        add(marsButton);
        add(fuelButton);
        add(engineButton);
        add(cargoButton);
        add(resetButton);

        earthButton.addActionListener(e -> travel(0));
        moonButton.addActionListener(e -> travel(1));
        marsButton.addActionListener(e -> travel(2));
        fuelButton.addActionListener(e -> buyFuel());
        engineButton.addActionListener(e -> upgradeEngine());
        cargoButton.addActionListener(e -> upgradeCargo());
        resetButton.addActionListener(e -> resetGame());

        updateScreen();
    }

    private void travel(int destination)
    {
        if (destination == location)
        {
            message = "You are already there.";
            updateScreen();
            return;
        }

        int fuelNeeded = getFuelNeeded(destination);
        if (fuel < fuelNeeded)
        {
            message = "Not enough fuel for that trip.";
            updateScreen();
            return;
        }

        fuel -= fuelNeeded;
        location = destination;

        int profit = getProfit(destination);
        cash += profit;

        message = "Traveled and earned $" + profit + ".";
        updateScreen();
    }

    private void buyFuel()
    {
        int cost = 50;
        if (cash < cost)
        {
            message = "Not enough cash to buy fuel.";
        }
        else
        {
            cash -= cost;
            fuel += 20;
            message = "Bought 20 fuel for $50.";
        }
        updateScreen();
    }

    private void upgradeEngine()
    {
        int cost = 100 + (engineLevel * 50);
        if (cash < cost)
        {
            message = "Need more cash for engine upgrade.";
        }
        else
        {
            cash -= cost;
            engineLevel++;
            message = "Engine upgraded to level " + engineLevel + ".";
        }
        updateScreen();
    }

    private void upgradeCargo()
    {
        int cost = 80 + (cargoLevel * 40);
        if (cash < cost)
        {
            message = "Need more cash for cargo upgrade.";
        }
        else
        {
            cash -= cost;
            cargoLevel++;
            message = "Cargo upgraded to level " + cargoLevel + ".";
        }
        updateScreen();
    }

    private void resetGame()
    {
        cash = 500;
        fuel = 50;
        engineLevel = 1;
        cargoLevel = 1;
        location = 0;
        message = "New game started.";
        updateScreen();
    }

    private int getFuelNeeded(int destination)
    {
        int baseFuel;

        if (destination == 0)
        {
            baseFuel = 0;
        }
        else if (destination == 1)
        {
            baseFuel = 10;
        }
        else
        {
            baseFuel = 18;
        }

        int fuelDiscount = engineLevel - 1;
        int fuelNeeded = baseFuel - fuelDiscount;

        if (fuelNeeded < 1 && destination != 0)
        {
            fuelNeeded = 1;
        }

        return fuelNeeded;
    }

    private int getProfit(int destination)
    {
        int baseProfit;

        if (destination == 0)
        {
            baseProfit = 20;
        }
        else if (destination == 1)
        {
            baseProfit = 60;
        }
        else
        {
            baseProfit = 120;
        }

        int cargoBonus = cargoLevel * 10;
        int randomBonus = (int)(Math.random() * 21); // 0 to 20
        return baseProfit + cargoBonus + randomBonus;
    }

    private void updateScreen()
    {
        cashLabel.setText("Cash: $" + cash);
        fuelLabel.setText("Fuel: " + fuel);
        engineLabel.setText("Engine Level: " + engineLevel);
        cargoLabel.setText("Cargo Level: " + cargoLevel);

        if (location == 0)
        {
            locationLabel.setText("Location: Earth");
        }
        else if (location == 1)
        {
            locationLabel.setText("Location: Moon Base");
        }
        else
        {
            locationLabel.setText("Location: Mars Colony");
        }

        messageLabel.setText(message);
        repaint();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.drawString("Space Trade Tycoon", 360, 70);
    }
}