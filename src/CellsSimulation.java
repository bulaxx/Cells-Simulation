import javax.swing.*;
import java.awt.*;

public class CellsSimulation extends JFrame{

    JTextField numberOfCells;
    JTextField numberOfStep;
    JTextField dose;
    JButton saveImg;
    JButton saveText;
    JButton buttonOnOff;
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem parameterSelection;

    public CellsSimulation() {
        setTitle("Cells simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLayout(new BorderLayout());

        //left Panel - cells
        JPanel cellsPanel = new JPanel();
        cellsPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        add(cellsPanel, BorderLayout.WEST);

        //right Panel - parameters
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        JLabel parametersLabel = new JLabel("Parameters : ");
        numberOfCells = new JTextField(10);
        numberOfCells.setBorder(BorderFactory.createTitledBorder("Number of Cells"));

        numberOfStep = new JTextField("");
        numberOfStep.setBorder(BorderFactory.createTitledBorder("Number of Step"));

        dose = new JTextField(10);
        dose.setBorder(BorderFactory.createTitledBorder("Dose"));

        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new GridLayout(3,1,5,5));
        parametersPanel.add(numberOfCells);
        parametersPanel.add(numberOfStep);
        parametersPanel.add(dose);

        JLabel saveLabel = new JLabel("Results: ");
        saveImg = new JButton("Save as image");
        saveText = new JButton("Save as a text file");
        JPanel savePanel = new JPanel();
        savePanel.setLayout(new GridLayout(3,1,5,5));
        savePanel.add(saveLabel);
        savePanel.add(saveImg);
        savePanel.add(saveText);

        JPanel startPanel = new JPanel();
        buttonOnOff = new JButton("ON/OFF");
        startPanel.add(buttonOnOff);

        rightPanel.add(parametersLabel);
        rightPanel.add(parametersPanel);
        rightPanel.add(startPanel);
        rightPanel.add(savePanel);
        add(rightPanel, BorderLayout.EAST);


        //Menu - parameter selection
        menuBar = new JMenuBar();
        menu = new JMenu("Menu");
        parameterSelection = new JMenuItem("Parameter Selection");
        parameterSelection.addActionListener(e -> openParameterSelection(this));
        menu.add(parameterSelection);
        menuBar.add(menu);
        setJMenuBar(menuBar);

    }

    private static void openParameterSelection(JFrame parent) {   //passes references to the head window
        JDialog parametersDialog = new JDialog(parent, "Parameter Selection", true);
        parametersDialog.setSize(500, 500);
        parametersDialog.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane(); //create bookmarks
        tabbedPane.addTab("Health", createHealthPanel());
        tabbedPane.addTab("Damaged", createDamagedPanel());
        tabbedPane.addTab("Mutated", createMutatedPanel());
        tabbedPane.addTab("Cancer", createCancerPanel());

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton resetButton = new JButton("Reset");
        buttonPanel.add(saveButton);
        buttonPanel.add(resetButton);
        parametersDialog.add(tabbedPane, BorderLayout.CENTER);
        parametersDialog.add(buttonPanel, BorderLayout.SOUTH);
        parametersDialog.setLocationRelativeTo(parent);
        parametersDialog.setVisible(true);
    }

    private static JPanel createHealthPanel() {
        JPanel healthPanel = new JPanel();
        healthPanel.add(new JLabel("Parameters: "));
        return healthPanel;
    }

    private static JPanel createDamagedPanel() {
        JPanel damagedPanel = new JPanel();
        damagedPanel.add(new JLabel("Parameters: "));
        return damagedPanel;
    }

    private static JPanel createMutatedPanel() {
        JPanel mutatedPanel = new JPanel();
        mutatedPanel.add(new JLabel("Parameters: "));
        return mutatedPanel;
    }

    private static JPanel createCancerPanel() {
        JPanel cancerPanel = new JPanel();
        cancerPanel.add(new JLabel("Parameters: "));
        return cancerPanel;
    }

    public static void main(String[] args) {
        CellsSimulation simulation = new CellsSimulation();
        simulation.setVisible(true);
    }
}
