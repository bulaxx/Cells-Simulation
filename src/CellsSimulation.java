import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CellsSimulation extends JFrame{

    JSlider numberOfCells;
    JSlider numberOfStep;
    JSlider dose;
    JButton saveImg;
    JButton saveText;
    JButton buttonOnOff;
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem parameterSelection;
    private PaintPanel paintPanel;
    private Simulation simulation;
    private Cell[][][] organism;
    int n;

    public CellsSimulation() {
        setTitle("Cells simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        setLayout(new BorderLayout());

        //left Panel - cells
        simulation = new Simulation();
        simulation.inicializeOrganism();
        //simulation.setWall();
        organism = new Cell[n][n][n];
        paintPanel = new PaintPanel(simulation.organism, simulation.n);
        paintPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        paintPanel.setPreferredSize(new Dimension(800, 800));
        add(paintPanel, BorderLayout.CENTER);
        pack();

        //right Panel - parameters
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        JLabel parametersLabel = new JLabel("Parameters : ");
        numberOfCells = new JSlider(JSlider.HORIZONTAL, 1, 15, 15);
        numberOfCells.setMajorTickSpacing(5); //glowna podzialka
        numberOfCells.setMinorTickSpacing(1); //najmniejszy odstep
        numberOfCells.setPaintTicks(true);  //rysuje podzialke
        numberOfCells.setPaintLabels(true);  //pisze numerki
        numberOfCells.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                simulation.setN(numberOfCells.getValue());
                simulation.inicializeOrganism();
                //simulation.setWall();
                paintPanel.updateOrganism(simulation.organism, simulation.n);
            }
        });
        numberOfCells.setBorder(BorderFactory.createTitledBorder("Number of Cells"));

        numberOfStep = new JSlider(JSlider.HORIZONTAL, 1, 21, 10);
        numberOfStep.setMajorTickSpacing(5);
        numberOfStep.setMinorTickSpacing(1);
        numberOfStep.setPaintTicks(true);
        numberOfStep.setPaintLabels(true);
        numberOfStep.setBorder(BorderFactory.createTitledBorder("Number of Steps"));
        numberOfStep.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                simulation.setK(numberOfStep.getValue());
            }
        });

        dose = new JSlider(JSlider.HORIZONTAL, 1, 21, 10);
        dose.setMajorTickSpacing(5);
        dose.setMinorTickSpacing(1);
        dose.setPaintTicks(true);
        dose.setPaintLabels(true);
        dose.setBorder(BorderFactory.createTitledBorder("Dose"));
        dose.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                simulation.setD(dose.getValue());
            }
        });

        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new GridLayout(3,1,5,5));
        parametersPanel.add(numberOfCells);
        parametersPanel.add(numberOfStep);
        parametersPanel.add(dose);

        JLabel saveLabel = new JLabel("Results: ");
        saveImg = new JButton("Save as image");
        saveImg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paintPanel.saveIMG();
            }
        });
        saveText = new JButton("Save as text file");
        JPanel savePanel = new JPanel();
        savePanel.setLayout(new GridLayout(3,1,5,5));
        savePanel.add(saveLabel);
        savePanel.add(saveImg);
        savePanel.add(saveText);

        JPanel startPanel = new JPanel();
        buttonOnOff = new JButton("ON/OFF");
        buttonOnOff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                simulation.inicializeOrganism();
                //simulation.setWall();
                simulation.simulation();
                startSimulation();
                paintPanel.updateOrganism(simulation.organism, simulation.n);
            }
        });
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

    private void startSimulation() {
        new Thread(() ->{
           simulation.inicializeOrganism();
           //simulation.setWall();

            for (int step = 0; step < simulation.s; step++) {
                simulation.simulation();
                paintPanel.updateOrganism(simulation.organism, simulation.n);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void openParameterSelection(JFrame parent) {   //passes references to the head window
        JDialog parametersDialog = new JDialog(parent, "Parameter Selection", true);
        parametersDialog.setSize(500, 500);
        parametersDialog.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane(); //create bookmarks
        tabbedPane.addTab("Healthy Cells", createHealthPanel());
        tabbedPane.addTab("Damaged Cells", createDamagedPanel());
        tabbedPane.addTab("Mutated Cells", createMutatedPanel());
        tabbedPane.addTab("Cancerous Cells", createCancerPanel());
        tabbedPane.addTab("Other", createOtherPanel());

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
        healthPanel.setBorder(BorderFactory.createTitledBorder("Parameters: "));
        healthPanel.setLayout(new BoxLayout(healthPanel, BoxLayout.Y_AXIS));

        JLabel labelT  = new JLabel("t:");
        JTextField textT = new JTextField(1);
        textT.setPreferredSize(new Dimension(10, 2));

        JLabel labelA = new JLabel("a:");
        JTextField textA = new JTextField(1);
        textA.setPreferredSize(new Dimension(10, 2));

        JLabel labelN  = new JLabel("n:");
        JTextField textN = new JTextField(1);
        textN.setPreferredSize(new Dimension(10, 2));

        JPanel panelNaturalDeath = new JPanel(new GridLayout(3,2,5,5));
        panelNaturalDeath.setBorder(BorderFactory.createTitledBorder("Natural Cell Death"));
        panelNaturalDeath.add(labelT);
        panelNaturalDeath.add(textT);
        panelNaturalDeath.add(labelA);
        panelNaturalDeath.add(textA);
        panelNaturalDeath.add(labelN);
        panelNaturalDeath.add(textN);
        healthPanel.add(panelNaturalDeath);

        JLabel labelDivision = new JLabel("P: ");
        JTextField textDivision = new JTextField(10);
        JPanel panelDivision = new JPanel(new GridLayout(1,2,5,5));
        panelDivision.setBorder(BorderFactory.createTitledBorder("Cell Division"));
        panelDivision.add(labelDivision);
        panelDivision.add(textDivision);
        healthPanel.add(panelDivision);

        JLabel labelRadiation = new JLabel("P: ");
        JTextField textRadiation = new JTextField(10);
        JPanel panelRadiation = new JPanel(new GridLayout(1,2,5,5));
        panelRadiation.setBorder(BorderFactory.createTitledBorder("Radiation damage"));
        panelRadiation.add(labelRadiation);
        panelRadiation.add(textRadiation);
        healthPanel.add(panelRadiation);


        return healthPanel;
    }

    private static JPanel createDamagedPanel() {
        JPanel damagedPanel = new JPanel();
        damagedPanel.setBorder(BorderFactory.createTitledBorder("Parameters: "));
        damagedPanel.setLayout(new BoxLayout(damagedPanel, BoxLayout.Y_AXIS));

        JLabel labelT  = new JLabel("t:");
        JTextField textT = new JTextField(1);
        textT.setPreferredSize(new Dimension(10, 2));

        JLabel labelA = new JLabel("a:");
        JTextField textA = new JTextField(1);
        textA.setPreferredSize(new Dimension(10, 2));

        JLabel labelN  = new JLabel("n:");
        JTextField textN = new JTextField(1);
        textN.setPreferredSize(new Dimension(10, 2));

        JPanel panelNaturalDeath = new JPanel(new GridLayout(3,2,5,5));
        panelNaturalDeath.setBorder(BorderFactory.createTitledBorder("Natural Cell Death"));
        panelNaturalDeath.add(labelT);
        panelNaturalDeath.add(textT);
        panelNaturalDeath.add(labelA);
        panelNaturalDeath.add(textA);
        panelNaturalDeath.add(labelN);
        panelNaturalDeath.add(textN);
        damagedPanel.add(panelNaturalDeath);

        JLabel labelT2  = new JLabel("t:");
        JTextField textT2 = new JTextField(1);
        textT.setPreferredSize(new Dimension(10, 2));

        JLabel labelA2 = new JLabel("a:");
        JTextField textA2 = new JTextField(1);
        textA.setPreferredSize(new Dimension(10, 2));

        JLabel labelN2  = new JLabel("n:");
        JTextField textN2 = new JTextField(1);
        textN.setPreferredSize(new Dimension(10, 2));

        JPanel panelNaturalDamageRepair = new JPanel(new GridLayout(3,2,5,5));
        panelNaturalDamageRepair.setBorder(BorderFactory.createTitledBorder("Natural Damage Repair"));
        panelNaturalDamageRepair.add(labelT2);
        panelNaturalDamageRepair.add(textT2);
        panelNaturalDamageRepair.add(labelA2);
        panelNaturalDamageRepair.add(textA2);
        panelNaturalDamageRepair.add(labelN2);
        panelNaturalDamageRepair.add(textN2);
        damagedPanel.add(panelNaturalDamageRepair);

        JLabel labelDivision = new JLabel("P: ");
        JTextField textDivision = new JTextField(10);
        JPanel panelDivision = new JPanel(new GridLayout(1,2,5,5));
        panelDivision.setBorder(BorderFactory.createTitledBorder("Cell Division"));
        panelDivision.add(labelDivision);
        panelDivision.add(textDivision);
        damagedPanel.add(panelDivision);

        JLabel labelMutationOccurrence = new JLabel("a: ");
        JTextField textMutationOccurrence = new JTextField(10);
        JPanel panelMutationOccurrence = new JPanel(new GridLayout(1,2,5,5));
        panelMutationOccurrence.setBorder(BorderFactory.createTitledBorder("Mutation Occurrence"));
        panelMutationOccurrence.add(labelMutationOccurrence);
        panelMutationOccurrence.add(textMutationOccurrence);
        damagedPanel.add(panelMutationOccurrence);
        return damagedPanel;
    }

    private static JPanel createMutatedPanel() {
        JPanel mutatedPanel = new JPanel();
        mutatedPanel.setBorder(BorderFactory.createTitledBorder("Parameters: "));
        mutatedPanel.setLayout(new BoxLayout(mutatedPanel, BoxLayout.Y_AXIS));

        JLabel labelT  = new JLabel("t:");
        JTextField textT = new JTextField(1);
        textT.setPreferredSize(new Dimension(10, 2));

        JLabel labelA = new JLabel("a:");
        JTextField textA = new JTextField(1);
        textA.setPreferredSize(new Dimension(10, 2));

        JLabel labelN  = new JLabel("n:");
        JTextField textN = new JTextField(1);
        textN.setPreferredSize(new Dimension(10, 2));

        JPanel panelNaturalDeath = new JPanel(new GridLayout(3,2,5,5));
        panelNaturalDeath.setBorder(BorderFactory.createTitledBorder("Natural Cell Death"));
        panelNaturalDeath.add(labelT);
        panelNaturalDeath.add(textT);
        panelNaturalDeath.add(labelA);
        panelNaturalDeath.add(textA);
        panelNaturalDeath.add(labelN);
        panelNaturalDeath.add(textN);
        mutatedPanel.add(panelNaturalDeath);

        JLabel labelDivision = new JLabel("P: ");
        JTextField textDivision = new JTextField(10);
        JPanel panelDivision = new JPanel(new GridLayout(1,2,5,5));
        panelDivision.setBorder(BorderFactory.createTitledBorder("Cell Division"));
        panelDivision.add(labelDivision);
        panelDivision.add(textDivision);
        mutatedPanel.add(panelDivision);

        JLabel labelTransA = new JLabel("a:");
        JTextField textTransA = new JTextField(1);
        JLabel labelTransN = new JLabel("n:");
        JTextField textTransN = new JTextField(1);

        JPanel panelTransA = new JPanel(new GridLayout(2,2,5,5));
        panelTransA.setBorder(BorderFactory.createTitledBorder("Transformation into a cancerous cell"));
        panelTransA.add(labelTransA);
        panelTransA.add(textTransA);
        panelTransA.add(labelTransN);
        panelTransA.add(textTransN);
        mutatedPanel.add(panelTransA);

        return mutatedPanel;
    }

    private static JPanel createCancerPanel() {
        JPanel cancerPanel = new JPanel();
        cancerPanel.setBorder(BorderFactory.createTitledBorder("Parameters: "));
        cancerPanel.setLayout(new BoxLayout(cancerPanel, BoxLayout.Y_AXIS));

        JLabel labelNaturalDeath = new JLabel("P: ");
        JTextField textNaturalDeath = new JTextField(1);
        JPanel panelNaturalDeath = new JPanel(new GridLayout(1,2,5,5));
        panelNaturalDeath.setBorder(BorderFactory.createTitledBorder("Natural Cell Death"));
        panelNaturalDeath.add(labelNaturalDeath);
        panelNaturalDeath.add(textNaturalDeath);
        cancerPanel.add(panelNaturalDeath);

        JLabel labelDivision = new JLabel("P: ");
        JTextField textDivision = new JTextField(10);
        JPanel panelDivision = new JPanel(new GridLayout(1,2,5,5));
        panelDivision.setBorder(BorderFactory.createTitledBorder("Cell Division"));
        panelDivision.add(labelDivision);
        panelDivision.add(textDivision);
        cancerPanel.add(panelDivision);

        JLabel labelRadiation = new JLabel("P: ");
        JTextField textRadiation = new JTextField(10);
        JPanel panelRadiation = new JPanel(new GridLayout(1,2,5,5));
        panelRadiation.setBorder(BorderFactory.createTitledBorder("Radiation-induced cell death"));
        panelRadiation.add(labelRadiation);
        panelRadiation.add(textRadiation);
        cancerPanel.add(panelRadiation);

        return cancerPanel;
    }

    private static JPanel createOtherPanel(){
        JPanel otherPanel = new JPanel();
        otherPanel.setBorder(BorderFactory.createTitledBorder("Parameters: "));
        otherPanel.setLayout(new BoxLayout(otherPanel, BoxLayout.Y_AXIS));

        JLabel labelCellHit =  new JLabel("P: ");
        JTextField textCellHit = new JTextField(10);
        JPanel panelCellHit = new JPanel(new GridLayout(1,2,5,5));
        panelCellHit.setBorder(BorderFactory.createTitledBorder("Cell Hit"));
        panelCellHit.add(labelCellHit);
        panelCellHit.add(textCellHit);
        otherPanel.add(panelCellHit);

        JLabel labelRadiation = new JLabel("P: ");
        JTextField textRadiation = new JTextField(10);
        JPanel panelRadiation = new JPanel(new GridLayout(1,2,5,5));
        panelRadiation.setBorder(BorderFactory.createTitledBorder("Death due to irradiation precision"));
        panelRadiation.add(labelRadiation);
        panelRadiation.add(textRadiation);
        otherPanel.add(panelRadiation);

        return otherPanel;
    }



    public static void main(String[] args) {
        CellsSimulation cells = new CellsSimulation();
        cells.setVisible(true);
    }
}
