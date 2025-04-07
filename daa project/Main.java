import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        new WelcomeScreen();
    }
}

// ------------------- Welcome Screen -------------------
class WelcomeScreen extends JFrame {
    public WelcomeScreen() {
        setTitle("Smart Journey Planner");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Smart Journey Planner 🚀", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.CENTER);

        JButton startButton = new JButton("Start Planning");
        startButton.setBackground(new Color(0, 120, 215));
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.addActionListener(e -> {
            dispose();
            new CitySelectionScreen();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(startButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}

// ------------------- City Selection -------------------
class CitySelectionScreen extends JFrame {
    String[] cities = {"Mumbai", "Delhi", "Pune", "Bangalore", "Chennai", "Kolkata"};
    JComboBox<String> sourceBox, destinationBox;

    public CitySelectionScreen() {
        setTitle("Select Your Journey");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1, 10, 10));

        JLabel heading = new JLabel("Choose Your Cities", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        add(heading);

        sourceBox = new JComboBox<>(cities);
        destinationBox = new JComboBox<>(cities);

        JPanel cityPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        cityPanel.add(new JLabel("From:", JLabel.RIGHT));
        cityPanel.add(sourceBox);
        cityPanel.add(new JLabel("To:", JLabel.RIGHT));
        cityPanel.add(destinationBox);
        add(cityPanel);

        JButton nextBtn = new JButton("Find Route");
        nextBtn.setBackground(new Color(34, 139, 34));
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFont(new Font("Arial", Font.BOLD, 16));
        nextBtn.addActionListener(e -> {
            String source = (String) sourceBox.getSelectedItem();
            String dest = (String) destinationBox.getSelectedItem();
            if (source.equals(dest)) {
                JOptionPane.showMessageDialog(null, "Source and Destination can't be the same!");
            } else {
                dispose();
                new RouteFinderScreen(source, dest);
            }
        });

        JPanel btnPanel = new JPanel();
        btnPanel.add(nextBtn);
        add(btnPanel);

        setVisible(true);
    }
}

// ------------------- Route Finder -------------------
class RouteFinderScreen extends JFrame {
    private String source, destination;
    private JLabel resultLabel;
    private Map<String, Map<String, Integer>> cityGraph = new HashMap<>();

    public RouteFinderScreen(String src, String dest) {
        this.source = src;
        this.destination = dest;

        setTitle("Finding Shortest Route");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initGraph();

        JLabel heading = new JLabel("Shortest Path Finder", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        add(heading, BorderLayout.NORTH);

        resultLabel = new JLabel("Finding best route from " + source + " to " + destination + "...", JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(resultLabel, BorderLayout.CENTER);

        JButton summaryButton = new JButton("Show Summary");
        summaryButton.setBackground(new Color(70, 130, 180));
        summaryButton.setForeground(Color.WHITE);
        summaryButton.setFont(new Font("Arial", Font.BOLD, 14));
        summaryButton.addActionListener(e -> {
            java.util.List<String> path = shortestPath();
            if (path.size() <= 1 || !path.get(0).equals(source)) {
                JOptionPane.showMessageDialog(null, "No valid route found between selected cities.");
            } else {
                dispose();
                new TripSummaryScreen(source, destination, path, getTotalDistance(path));
            }
        });

        JPanel bottom = new JPanel();
        bottom.add(summaryButton);
        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
        resultLabel.setText("Route ready! Click 'Show Summary' to view.");
    }

    private void initGraph() {
        addRoute("Mumbai", "Pune", 150);
        addRoute("Mumbai", "Delhi", 1400);
        addRoute("Pune", "Bangalore", 850);
        addRoute("Delhi", "Kolkata", 1500);
        addRoute("Kolkata", "Chennai", 1700);
        addRoute("Bangalore", "Chennai", 350);
        addRoute("Pune", "Delhi", 1200);
        addRoute("Mumbai", "Kolkata", 1900);
        addRoute("Delhi", "Chennai", 2200);
        addRoute("Bangalore", "Delhi", 2000);
        addRoute("Pune", "Chennai", 1000);
        addRoute("Mumbai", "Chennai", 1300);
    }

    private void addRoute(String city1, String city2, int distance) {
        cityGraph.computeIfAbsent(city1, k -> new HashMap<>()).put(city2, distance);
        cityGraph.computeIfAbsent(city2, k -> new HashMap<>()).put(city1, distance);
    }

    private java.util.List<String> shortestPath() {
        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(dist::get));

        for (String city : cityGraph.keySet()) dist.put(city, Integer.MAX_VALUE);
        dist.put(source, 0);
        pq.add(source);

        while (!pq.isEmpty()) {
            String current = pq.poll();
            if (visited.contains(current)) continue;
            visited.add(current);

            for (Map.Entry<String, Integer> entry : cityGraph.get(current).entrySet()) {
                String neighbor = entry.getKey();
                int newDist = dist.get(current) + entry.getValue();
                if (newDist < dist.get(neighbor)) {
                    dist.put(neighbor, newDist);
                    prev.put(neighbor, current);
                    pq.add(neighbor);
                }
            }
        }

        java.util.List<String> path = new ArrayList<>();
        for (String at = destination; at != null; at = prev.get(at)) path.add(at);
        Collections.reverse(path);
        return path;
    }

    private int getTotalDistance(java.util.List<String> path) {
        int total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            total += cityGraph.get(path.get(i)).get(path.get(i + 1));
        }
        return total;
    }
}

// ------------------- Trip Summary -------------------
class TripSummaryScreen extends JFrame {
    public TripSummaryScreen(String source, String destination, java.util.List<String> path, int distance) {
        setTitle("Trip Summary");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel heading = new JLabel("Trip Summary 📋", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        add(heading, BorderLayout.NORTH);

        StringBuilder pathText = new StringBuilder("<html><center><br>Source: " + source +
                "<br>Destination: " + destination + "<br><br>Route:<br>");

        for (String city : path) {
            pathText.append("➤ ").append(city).append("<br>");
        }

        int time = distance / 60;
        int cost = distance * 2;

        pathText.append("<br>Total Distance: ").append(distance).append(" km");
        pathText.append("<br>Estimated Time: ").append(time).append(" hours");
        pathText.append("<br>Estimated Cost: ₹").append(cost).append("</center></html>");

        JLabel summary = new JLabel(pathText.toString(), JLabel.CENTER);
        summary.setFont(new Font("Arial", Font.PLAIN, 14));
        add(summary, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Exit");
        closeBtn.setBackground(Color.RED);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        closeBtn.addActionListener(e -> System.exit(0));

        JPanel bottom = new JPanel();
        bottom.add(closeBtn);
        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }
}
