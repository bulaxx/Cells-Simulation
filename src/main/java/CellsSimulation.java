import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;


public class CellsSimulation extends JFrame{

    JSlider numberOfCells;
    JSlider numberOfStep;
    JSlider dose;
    JButton saveImg;
    JButton saveText;
    JButton buttonOnOff;
    JButton buttonReset;
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem parameterSelection;
    JCheckBox healthyBox;
    JCheckBox deadBox;
    JCheckBox damagedBox;
    JCheckBox mutatedBox;
    JCheckBox cancerBox;
    JCheckBox emptyBox;
    JMenu menuL;
    JMenuBar menuBarL;
    JMenuItem itemPL;
    JMenuItem itemENl;
    private Set<String> visibleStatuses;
    private PaintPanel paintPanel;
    private static Simulation simulation;
    private Cell[][][] organism;
    int n;
    private boolean run = false;
    private int step = 0;
    private Timer timer;
    private File logFile;
    private JLabel stepLabel;

    static JTextField textAHealth;
    static JTextField textTHealth;
    static JTextField textNHealth;
    static JTextField textDivisionHealth;

    static JTextField textADamaged;
    static JTextField textTDamaged;
    static JTextField textNDamaged;
    static JTextField textDivisionDamaged;
    static JTextField textMutationOccurrence;

    static JTextField textAMutated;
    static JTextField textTMutated;
    static JTextField textNMutated;
    static JTextField textDivisionMutated;
    static JTextField textAddMutationA;
    static JTextField textAddMutationN;

    static JTextField textNaturalDeath;
    static JTextField textDivisionCancer;
    static JTextField textRadiationCancer;
    static JTextField textAddMutationCancerB1;
    static JTextField textAddMutationCancerB2;
    static JTextField textMutantToCancerA;
    static JTextField textMutantToCancerN;

    static JTextField textSponDamageT;
    static JTextField textSponDamageA;
    static JTextField textSponDamageN;
    static JTextField textDeadPrecise;
    static JTextField textNaturalRepairQ;
    static JTextField textNaturalRepairA;
    static JTextField textNaturalRepairN;
    static JTextField textRadiationDamage;
    static JTextField textAdaptiveA0;
    static JTextField textAdaptiveA1;
    static JTextField textAdaptiveA2;


    public CellsSimulation() {
        //Messages.setLocale(new Locale("en", "US")); // ustawia PL
        setTitle(Messages.get("title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500,1500);
        setLayout(new BorderLayout());

        stepLabel = new JLabel(Messages.get("Step"));
        stepLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        stepLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel stepPanel = new JPanel(new BorderLayout());
        stepPanel.add(stepLabel, BorderLayout.WEST);
        add(stepPanel, BorderLayout.NORTH);
        menuL = new JMenu(Messages.get("changeLanguage"));
        menuBarL = new JMenuBar();
        itemPL = new JMenuItem(Messages.get("itemPL"));
        itemPL.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeLanguage("pl", "PL");
            }
        });
        itemENl = new JMenuItem(Messages.get("itemENl"));
        itemENl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeLanguage("en", "US");
            }
        });
        menuL.add(itemPL);
        menuL.add(itemENl);
        // menuBarL.add(menuL);

        //left Panel - cells
        simulation = new Simulation();
        simulation.inicializeOrganism();
        //simulation.setWall();
        organism = new Cell[n][n][n];
        paintPanel = new PaintPanel(simulation.organism, simulation.n);
        paintPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        paintPanel.setPreferredSize(new Dimension(1000, 800));
        add(paintPanel, BorderLayout.CENTER);
        pack();

        //left Panel
        visibleStatuses = new HashSet<>(Set.of("healthy", "dead", "damaged", "mutated", "cancer"));

        JPanel checkPanel = new JPanel();
        healthyBox = new JCheckBox(Messages.get("Healthy"), true);
        deadBox = new JCheckBox(Messages.get("Dead"), true);
        damagedBox = new JCheckBox(Messages.get("Damaged"), true);
        mutatedBox = new JCheckBox(Messages.get("Mutated"), true);
        cancerBox = new JCheckBox(Messages.get("Cancer"), true);
        emptyBox = new JCheckBox(Messages.get("Empty"), true);
        ItemListener listener = e->{
            visibleStatuses.clear();
            if (healthyBox.isSelected()) visibleStatuses.add("healthy");
            if (deadBox.isSelected()) visibleStatuses.add("dead");
            if (damagedBox.isSelected()) visibleStatuses.add("damaged");
            if (mutatedBox.isSelected()) visibleStatuses.add("mutated");
            if (cancerBox.isSelected()) visibleStatuses.add("cancer");
            if (emptyBox.isSelected()) visibleStatuses.add("empty");

            paintPanel.setVisibleStatuses(visibleStatuses);
        };
        healthyBox.addItemListener(listener);
        deadBox.addItemListener(listener);
        damagedBox.addItemListener(listener);
        mutatedBox.addItemListener(listener);
        cancerBox.addItemListener(listener);
        emptyBox.addItemListener(listener);
        checkPanel.add(healthyBox);
        checkPanel.add(deadBox);
        checkPanel.add(damagedBox);
        checkPanel.add(mutatedBox);
        checkPanel.add(cancerBox);
        checkPanel.add(emptyBox);
        add(checkPanel, BorderLayout.SOUTH);

        //right Panel - parameters
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        //JLabel parametersLabel = new JLabel(Messages.get("Parameters"));
        numberOfCells = new JSlider(JSlider.HORIZONTAL, 1, 16, 16);
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
        numberOfCells.setBorder(BorderFactory.createTitledBorder(Messages.get("NumberC")));

        numberOfStep = new JSlider(JSlider.HORIZONTAL, 0, 150, 150);
        numberOfStep.setMajorTickSpacing(30);
        numberOfStep.setMinorTickSpacing(15);
        numberOfStep.setPaintTicks(true);
        numberOfStep.setPaintLabels(true);
        numberOfStep.setBorder(BorderFactory.createTitledBorder(Messages.get("NumberS")));
        numberOfStep.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                simulation.setK(numberOfStep.getValue());
            }
        });

        dose = new JSlider(JSlider.HORIZONTAL, 0,20, 20);
        dose.setMajorTickSpacing(5);
        dose.setMinorTickSpacing(1);
        dose.setPaintTicks(true);
        dose.setPaintLabels(true);
        dose.setBorder(BorderFactory.createTitledBorder(Messages.get("Dose")));
        dose.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                simulation.setD(dose.getValue());
            }
        });
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        for (int i = 0; i <= 20; i++) {
            if(i == 0 || i==5 || i == 10 || i==15 || i==20)
                labelTable.put(i, new JLabel(String.format("%.1f", i / 10.0)));
        }
        dose.setLabelTable(labelTable);


        JPanel parametersPanel = new JPanel();
        parametersPanel.setLayout(new GridLayout(3,1,5,5));
        parametersPanel.add(numberOfCells);
        parametersPanel.add(numberOfStep);
        parametersPanel.add(dose);

        JLabel saveLabel = new JLabel(Messages.get("Results"));
        saveImg = new JButton(Messages.get("SaveIMG"));
        saveImg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paintPanel.saveIMG();
            }
        });
        saveText = new JButton(Messages.get("SaveT"));
        saveText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int option = chooser.showSaveDialog(CellsSimulation.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    logFile = chooser.getSelectedFile();
                    try {
                        if (!logFile.exists()) {
                            logFile.createNewFile();
                        }
                        JOptionPane.showMessageDialog(CellsSimulation.this, "File selected: " + logFile.getAbsolutePath());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(CellsSimulation.this, "Error creating file: " + ex.getMessage());
                    }
                }
            }
        });

        JPanel savePanel = new JPanel();
        savePanel.setLayout(new GridLayout(3,1,5,5));
        savePanel.add(saveLabel);
        savePanel.add(saveImg);
        savePanel.add(saveText);

        JPanel startPanel = new JPanel();
        buttonOnOff = new JButton("ON/OFF");
        buttonOnOff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!run){
                    simulation.setN(numberOfCells.getValue());
                    simulation.setK(numberOfStep.getValue());
                    simulation.setD(dose.getValue());
                    //simulation.inicializeOrganism();
                    //simulation.setWall();
                    //simulation.simulation();
                    startSimulation();
                    //paintPanel.updateOrganism(simulation.organism, simulation.n);
                    run = true;
                    buttonOnOff.setText("OFF");
                }
                else{
                    stopSimulation();
                    run = false;
                    buttonOnOff.setText("ON");
                }

            }
        });
        buttonReset = new JButton("Reset");
        buttonReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetSimulation();
            }
        });


        timer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("Timer: " +step);
                stepLabel.setText(Messages.get("Step")+ ": " + step);
                simulation.setStepTime(step);
                if(step>= simulation.s){
                    stopSimulation();
                    buttonOnOff.setText("ON");
                    return;
                }
                simulation.simulation();
                paintPanel.updateOrganism(simulation.organism, simulation.n);
                if(logFile != null){
                    System.out.println("Saving log file: " + logFile.getAbsolutePath());
                    logToFile(logFile, step);
                }else{
                    System.out.println("Null log file");
                }

                step++;
            }
        });

        startPanel.add(buttonOnOff);
        startPanel.add(buttonReset);

        //rightPanel.add(parametersLabel);
        rightPanel.add(parametersPanel);
        rightPanel.add(startPanel);
        rightPanel.add(savePanel);
        add(rightPanel, BorderLayout.EAST);


        //Menu - parameter selection
        menuBar = new JMenuBar();
        menu = new JMenu("Menu");
        parameterSelection = new JMenuItem(Messages.get("ParameterS"));
        parameterSelection.addActionListener(e -> openParameterSelection(this));
        menu.add(parameterSelection);
        menuBar.add(menu);
        menuBar.add(menuL);
        setJMenuBar(menuBar);


    }

    private void changeLanguage(String lang, String country) {
        Locale newLocale = new Locale(lang, country);
        Messages.setLocale(newLocale);
        // ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", newLocale);

        // paintPanel.repaint();
        this.dispose();
        CellsSimulation newWindow = new CellsSimulation();
        newWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
        newWindow.setVisible(true);
    }



    private void startSimulation() {
        //simulation.inicializeOrganism();
        //simulation.simulation();
        //paintPanel.updateOrganism(simulation.organism, simulation.n);
        simulation.inicializeOrganism();
        //simulation.setWall();
        paintPanel.updateOrganism(simulation.organism, simulation.n);
        timer.start();
    }

    private void stopSimulation() {
        run = false;
        timer.stop();
    }

    private void resetSimulation(){
        simulation.setD(dose.getValue());
        simulation.setK(numberOfStep.getValue());
        simulation.setN(numberOfCells.getValue());
        simulation.inicializeOrganism();
        step = 0;
        stepLabel.setText(Messages.get("Step") + ": 0");
        paintPanel.updateOrganism(simulation.organism, simulation.n);
    }

    private void logToFile(File file, int step){
        try(PrintWriter out = new PrintWriter(new FileOutputStream(file, true))) {
            out.println("Step: " + step + ":");
            for(int i = 0; i < simulation.n; i++){
                for(int j = 0; j < simulation.n; j++){
                    for(int k = 0; k < simulation.n; k++){
                        out.println("[" + i + "][" + j + "][" + k + "]" + simulation.organism[i][j][k].status + "\tage:" + String.format("%.6f",simulation.organism[i][j][k].age) + "\tdamage:" + simulation.organism[i][j][k].damage + "\tmutation: " + simulation.organism[i][j][k].mutation+ "\t dose:"+ simulation.dose_Pa[i][j][k][step] + "\n");
                        /*if(simulation.organism[i][j][k].status.equals("healthy")){
                            out.printf("rand1 = %.4f \t rand2 =%.4f \t", simulation.random1, simulation.random2);
                            out.printf("\tPRD = %.4f \t PD = %.4f \t PM = %.4f \t PRDEM = %.4f \n",
                            simulation.P_RD(), simulation.Pd(i, j, k), simulation.Pm(i, j, k), simulation.PRDEM());

                        }
                        else if(simulation.organism[i][j][k].status.equals("damaged")){
                            out.printf("\tPRD = %.4f \t PD = %.4f \t PM = %.4f \t PR = %.4f \t PRDEM = %.4f, PDM = %.4f \n",
                                    simulation.P_RD(), simulation.Pdd(i, j, k), simulation.Pm(i, j, k), simulation.Pr(i, j,k), simulation.PRDEM(), simulation.PDM(i,j,k));

                        }
                        else if(simulation.organism[i][j][k].status.equals("mutated")){
                            out.printf("\tPRD = %.4f \t PMD = %.4f \t PM = %.4f \t PR = %.4f \t PRDEM = %.4f, PRMM = %.4f \n",
                                    simulation.P_RD(), simulation.Pmd(i, j, k), simulation.Pm(i, j, k), simulation.Pr(i, j,k), simulation.PRDEM(), simulation.PRMM(i,j,k));

                        }
                        else if(simulation.organism[i][j][k].status.equals("cancer")){
                            out.printf("\tPRD = %.4f \t PCRD = %.4f \t PCM = %.4f  \n",
                                    simulation.P_RD(), simulation.PCRD, simulation.PCM(i, j,k));

                        }*/
                    }
                }
            }
            out.println();
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static JScrollPane getOtherScrollPane() {
        JPanel panel = createOtherPanel();
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }


    private static void openParameterSelection(JFrame parent) {   //passes references to the head window
        JDialog parametersDialog = new JDialog(parent, Messages.get("ParameterS"), true);
        parametersDialog.setSize(500, 500);
        parametersDialog.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane(); //create bookmarks
        tabbedPane.addTab(Messages.get("Healthy"), createHealthPanel());
        tabbedPane.addTab(Messages.get("Damaged"), createDamagedPanel());
        tabbedPane.addTab(Messages.get("Mutated"), createMutatedPanel());
        tabbedPane.addTab(Messages.get("Cancer"), createCancerPanel());
        tabbedPane.addTab(Messages.get("Other"), getOtherScrollPane());

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton(Messages.get("Save"));
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    double tH = Double.parseDouble(textTHealth.getText());
                    double aH = Double.parseDouble(textAHealth.getText());
                    double nH = Double.parseDouble(textNHealth.getText());
                    simulation.setHealthyDeadNatural(tH, aH, nH);
                    double ps = Double.parseDouble(textDivisionHealth.getText());
                    simulation.setPS(ps);

                    double tD = Double.parseDouble(textTDamaged.getText());
                    double aD = Double.parseDouble(textADamaged.getText());
                    double nD = Double.parseDouble(textNDamaged.getText());
                    simulation.setDamagedDeadNatural(tD, aD, nD);
                    double pds = Double.parseDouble(textDivisionDamaged.getText());
                    simulation.setPDS(pds);

                    double tM = Double.parseDouble(textTMutated.getText());
                    double aM = Double.parseDouble(textAMutated.getText());
                    double nM = Double.parseDouble(textNMutated.getText());
                    double pms = Double.parseDouble(textDivisionMutated.getText());
                    simulation.setPMS(pms);
                    simulation.setMutatedDeadNatural(tM, aM, nM);
                    double pdm = Double.parseDouble(textMutationOccurrence.getText());
                    simulation.setPDM(pdm);
                    double armm =  Double.parseDouble(textAddMutationA.getText());
                    simulation.set_a_PRMM(armm);
                    double nrmm =  Double.parseDouble(textAddMutationN.getText());
                    simulation.set_n_PRMM(nrmm);

                    double pcd = Double.parseDouble(textNaturalDeath.getText());
                    simulation.setPCD(pcd);
                    double pcs = Double.parseDouble(textDivisionCancer.getText());
                    simulation.setPCS(pcs);
                    double pcrd = Double.parseDouble(textRadiationCancer.getText());
                    simulation.setPCRD(pcrd);
                    double beta1PCM = Double.parseDouble(textAddMutationCancerB1.getText());
                    simulation.set_PCM_beta1(beta1PCM);
                    double beta2PCM = Double.parseDouble(textAddMutationCancerB2.getText());
                    simulation.set_PCM_beta2(beta2PCM);
                    double aprc = Double.parseDouble(textMutantToCancerA.getText());
                    simulation.set_a_PRC(aprc);
                    double nprc = Double.parseDouble(textMutantToCancerN.getText());
                    simulation.set_n_PRC(nprc);

                    double tpm = Double.parseDouble(textSponDamageT.getText());
                    double apm = Double.parseDouble(textSponDamageA.getText());
                    double npm = Double.parseDouble(textSponDamageN.getText());
                    simulation.setPM(tpm, apm, npm);
                    double prd = Double.parseDouble(textDeadPrecise.getText());
                    simulation.set_const_PRD(prd);

                    double qpr = Double.parseDouble(textNaturalRepairQ.getText());
                    double apr = Double.parseDouble(textNaturalRepairA.getText());
                    double npr = Double.parseDouble(textNaturalRepairN.getText());
                    simulation.setPR(qpr, apr, npr);
                    double prdem = Double.parseDouble(textRadiationDamage.getText());
                    simulation.set_const_PRDEM(prdem);
                    double a0par = Double.parseDouble(textAdaptiveA0.getText());
                    double a1par = Double.parseDouble(textAdaptiveA1.getText());
                    double a2par = Double.parseDouble(textAdaptiveA2.getText());
                    simulation.setPAR(a0par, a1par, a2par);

                    //parametersDialog.dispose(); // zamknij okno
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(parametersDialog, "Invalid number format", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textTHealth.setText(String.valueOf(simulation.getDEFAULT_t_PD()));
                textAHealth.setText(String.valueOf(simulation.getDEFAULT_a_PD()));
                textNHealth.setText(String.valueOf(simulation.getDEFAULT_n_PD()));
                simulation.setHealthyDeadNatural(simulation.getDEFAULT_t_PD(), simulation.getDEFAULT_a_PD(), simulation.getDEFAULT_n_PD());
                textDivisionHealth.setText(String.valueOf(simulation.getDEFAULT_PS()));
                simulation.setPS(simulation.get_PS());

                textTDamaged.setText(String.valueOf(simulation.getDEFAULT_t_PDD()));
                textADamaged.setText(String.valueOf(simulation.getDEFAULT_a_PDD()));
                textNDamaged.setText(String.valueOf(simulation.getDEFAULT_n_PDD()));
                simulation.setDamagedDeadNatural(simulation.getDEFAULT_t_PDD(), simulation.getDEFAULT_a_PDD(), simulation.getDEFAULT_n_PDD());
                textDivisionDamaged.setText(String.valueOf(simulation.getDEFAULT_PDS()));
                simulation.setPCS(simulation.get_PDS());
                textMutationOccurrence.setText(String.valueOf(simulation.getDEFAULT_a_PDM()));
                simulation.setPDM(simulation.get_a_PDM());

                textTMutated.setText(String.valueOf(simulation.getDEFAULT_t_PMD()));
                textAMutated.setText(String.valueOf(simulation.getDEFAULT_a_PMD()));
                textNMutated.setText(String.valueOf(simulation.getDEFAULT_n_PMD()));
                simulation.setMutatedDeadNatural(simulation.getDEFAULT_t_PMD(), simulation.getDEFAULT_a_PMD(), simulation.getDEFAULT_n_PMD());
                textDivisionMutated.setText(String.valueOf(simulation.getDEFAULT_PMS()));
                simulation.setPMS(simulation.get_PMS());
                textAddMutationA.setText(String.valueOf(simulation.getDEFAULT_a_PRMM()));
                simulation.set_a_PRMM(simulation.get_a_PRMM());
                textAddMutationN.setText(String.valueOf(simulation.getDEFAULT_n_PRMM()));
                simulation.set_n_PRMM(simulation.get_n_PRMM());

                textNaturalDeath.setText(String.valueOf(simulation.getDEFAULT_PCD()));
                simulation.setPCD(simulation.get_PCD());
                textDivisionCancer.setText(String.valueOf(simulation.getDEFAULT_PCS()));
                simulation.setPCS(simulation.get_PCS());
                textRadiationCancer.setText(String.valueOf(simulation.getDEFAULT_PCRD()));
                simulation.setPCRD(simulation.get_PCRD());
                textAddMutationCancerB1.setText(String.valueOf(simulation.getDEFAULT_PCM_beta1()));
                simulation.set_PCM_beta1(simulation.getDEFAULT_PCM_beta1());
                textAddMutationCancerB2.setText(String.valueOf(simulation.getDEFAULT_PCM_beta2()));
                simulation.set_PCM_beta2(simulation.getDEFAULT_PCM_beta2());
                textMutantToCancerA.setText(String.valueOf(simulation.getDEFAULT_a_PRC()));
                simulation.set_a_PRC(simulation.getDEFAULT_a_PRC());
                textMutantToCancerN.setText(String.valueOf(simulation.getDEFAULT_n_PRC()));
                simulation.set_n_PRC(simulation.getDEFAULT_n_PRC());

                textSponDamageT.setText(String.valueOf(simulation.getDEFAULT_t_PM()));
                textSponDamageA.setText(String.valueOf(simulation.getDEFAULT_a_PM()));
                textSponDamageN.setText(String.valueOf(simulation.getDEFAULT_n_PM()));
                simulation.setPM(simulation.getDEFAULT_t_PM(), simulation.getDEFAULT_a_PM(), simulation.getDEFAULT_n_PM());
                textDeadPrecise.setText(String.valueOf(simulation.getDEFAULT_const_PRD()));
                simulation.set_const_PRD(simulation.getDEFAULT_const_PRD());
                textNaturalRepairQ.setText(String.valueOf(simulation.getDEFAULT_q_PR()));
                textNaturalRepairA.setText(String.valueOf(simulation.getDEFAULT_a_PR()));
                textNaturalRepairN.setText(String.valueOf(simulation.getDEFAULT_n_PR()));
                simulation.setPR(simulation.getDEFAULT_q_PR(), simulation.getDEFAULT_a_PR(), simulation.getDEFAULT_n_PR());
                textRadiationDamage.setText(String.valueOf(simulation.getDEFAULT_const_PRDEM()));
                simulation.set_const_PRDEM(simulation.getDEFAULT_const_PRDEM());
                textAdaptiveA0.setText(String.valueOf(simulation.getDEFAULT_a0_PAR()));
                textAdaptiveA1.setText(String.valueOf(simulation.getDEFAULT_a1_PAR()));
                textAdaptiveA2.setText(String.valueOf(simulation.getDEFAULT_a2_PAR()));
                simulation.setPAR(simulation.getDEFAULT_a0_PAR(), simulation.getDEFAULT_a1_PAR(), simulation.getDEFAULT_a2_PAR());

            }
        });
        buttonPanel.add(saveButton);
        buttonPanel.add(resetButton);
        parametersDialog.add(tabbedPane, BorderLayout.CENTER);
        parametersDialog.add(buttonPanel, BorderLayout.SOUTH);
        parametersDialog.setLocationRelativeTo(parent);
        parametersDialog.setVisible(true);
    }

    private static JPanel createHealthPanel() {
        JPanel healthPanel = new JPanel();
        healthPanel.setBorder(BorderFactory.createTitledBorder( Messages.get("Parameters")));
        healthPanel.setLayout(new BoxLayout(healthPanel, BoxLayout.Y_AXIS));

        JLabel labelT  = new JLabel("t_D:");
        textTHealth = new JTextField(String.valueOf(simulation.get_t_PD()), 5);
        textTHealth.setPreferredSize(new Dimension(10, 2));

        JLabel labelA = new JLabel("a_D:");
        textAHealth = new JTextField(String.valueOf(simulation.get_a_PD()), 5);
        textAHealth.setPreferredSize(new Dimension(10, 2));

        JLabel labelN  = new JLabel("n_D:");
        textNHealth = new JTextField(String.valueOf(simulation.get_n_PD()), 5);
        textNHealth.setPreferredSize(new Dimension(10, 2));

        JPanel panelNaturalDeath = new JPanel(new GridLayout(3,2,5,5));
        panelNaturalDeath.setBorder(BorderFactory.createTitledBorder(Messages.get("NaturalD")));
        panelNaturalDeath.add(labelT);
        panelNaturalDeath.add(textTHealth);
        panelNaturalDeath.add(labelA);
        panelNaturalDeath.add(textAHealth);
        panelNaturalDeath.add(labelN);
        panelNaturalDeath.add(textNHealth);
        healthPanel.add(panelNaturalDeath);

        JLabel labelDivision = new JLabel("P_S: ");
        textDivisionHealth = new JTextField(String.valueOf(simulation.getDEFAULT_PS()), 5);
        JPanel panelDivision = new JPanel(new GridLayout(1,2,5,5));
        panelDivision.setBorder(BorderFactory.createTitledBorder(Messages.get("CellDivision")));
        panelDivision.add(labelDivision);
        panelDivision.add(textDivisionHealth);
        healthPanel.add(panelDivision);


        return healthPanel;
    }

    private static JPanel createDamagedPanel() {
        JPanel damagedPanel = new JPanel();
        damagedPanel.setBorder(BorderFactory.createTitledBorder(Messages.get("Parameters")));
        damagedPanel.setLayout(new BoxLayout(damagedPanel, BoxLayout.Y_AXIS));

        JLabel labelT  = new JLabel("t_DD:");
        textTDamaged = new JTextField(String.valueOf(simulation.get_t_PDD()), 5);
        textTDamaged.setPreferredSize(new Dimension(10, 2));

        JLabel labelA = new JLabel("a_DD:");
        textADamaged = new JTextField(String.valueOf(simulation.get_a_PDD()), 5);
        textADamaged.setPreferredSize(new Dimension(10, 2));

        JLabel labelN  = new JLabel("n_DD:");
        textNDamaged = new JTextField(String.valueOf(simulation.get_n_PDD()), 5);
        textNDamaged.setPreferredSize(new Dimension(10, 2));

        JPanel panelNaturalDeath = new JPanel(new GridLayout(3,2,5,5));
        panelNaturalDeath.setBorder(BorderFactory.createTitledBorder(Messages.get("NaturalD")));
        panelNaturalDeath.add(labelT);
        panelNaturalDeath.add(textTDamaged);
        panelNaturalDeath.add(labelA);
        panelNaturalDeath.add(textADamaged);
        panelNaturalDeath.add(labelN);
        panelNaturalDeath.add(textNDamaged);
        damagedPanel.add(panelNaturalDeath);


        JLabel labelDivision = new JLabel("P_DS: ");
        textDivisionDamaged = new JTextField(String.valueOf(simulation.get_PDS()), 5);
        JPanel panelDivision = new JPanel(new GridLayout(1,2,5,5));
        panelDivision.setBorder(BorderFactory.createTitledBorder(Messages.get("CellDivision")));
        panelDivision.add(labelDivision);
        panelDivision.add(textDivisionDamaged);
        damagedPanel.add(panelDivision);

        JLabel labelMutationOccurrence = new JLabel("a_DM: ");
        textMutationOccurrence = new JTextField(String.valueOf(simulation.get_a_PDM()), 5);
        JPanel panelMutationOccurrence = new JPanel(new GridLayout(1,2,5,5));
        panelMutationOccurrence.setBorder(BorderFactory.createTitledBorder(Messages.get("MutationO")));
        panelMutationOccurrence.add(labelMutationOccurrence);
        panelMutationOccurrence.add(textMutationOccurrence);
        damagedPanel.add(panelMutationOccurrence);
        return damagedPanel;
    }

    private static JPanel createMutatedPanel() {
        JPanel mutatedPanel = new JPanel();
        mutatedPanel.setBorder(BorderFactory.createTitledBorder(Messages.get("Parameters")));
        mutatedPanel.setLayout(new BoxLayout(mutatedPanel, BoxLayout.Y_AXIS));

        JLabel labelT  = new JLabel("t_MD:");
        textTMutated = new JTextField(String.valueOf(simulation.get_t_PMD()), 5);
        textTMutated.setPreferredSize(new Dimension(10, 2));

        JLabel labelA = new JLabel("a_MD:");
        textAMutated = new JTextField(String.valueOf(simulation.get_a_PMD()), 5);
        textAMutated.setPreferredSize(new Dimension(10, 2));

        JLabel labelN  = new JLabel("n_MD:");
        textNMutated = new JTextField(String.valueOf(simulation.get_n_PMD()), 5);
        textNMutated.setPreferredSize(new Dimension(10, 2));

        JPanel panelNaturalDeath = new JPanel(new GridLayout(3,2,5,5));
        panelNaturalDeath.setBorder(BorderFactory.createTitledBorder(Messages.get("NaturalD")));
        panelNaturalDeath.add(labelT);
        panelNaturalDeath.add(textTMutated);
        panelNaturalDeath.add(labelA);
        panelNaturalDeath.add(textAMutated);
        panelNaturalDeath.add(labelN);
        panelNaturalDeath.add(textNMutated);
        mutatedPanel.add(panelNaturalDeath);

        JLabel labelDivision = new JLabel("P_MS: ");
        textDivisionMutated = new JTextField(String.valueOf(simulation.get_PMS()), 5);
        JPanel panelDivision = new JPanel(new GridLayout(1,2,5,5));
        panelDivision.setBorder(BorderFactory.createTitledBorder(Messages.get("CellDivision")));
        panelDivision.add(labelDivision);
        panelDivision.add(textDivisionMutated);
        mutatedPanel.add(panelDivision);

        JLabel labelTransA = new JLabel("a_RMM:");
        textAddMutationA = new JTextField(String.valueOf(simulation.get_a_PRMM()), 5);
        JLabel labelTransN = new JLabel("n_RMM:");
        textAddMutationN = new JTextField(String.valueOf(simulation.get_n_PRMM()));

        JPanel panelTransA = new JPanel(new GridLayout(2,2,5,5));
        panelTransA.setBorder(BorderFactory.createTitledBorder(Messages.get("AdditionalMutation")));
        panelTransA.add(labelTransA);
        panelTransA.add(textAddMutationA);
        panelTransA.add(labelTransN);
        panelTransA.add(textAddMutationN);
        mutatedPanel.add(panelTransA);

        JLabel labelMutantA = new JLabel("a_PRC:");
        textMutantToCancerA = new JTextField(String.valueOf(simulation.get_a_PRC()), 5);
        JLabel labelMutantN = new JLabel("n_PRC:");
        textMutantToCancerN = new JTextField(String.valueOf(simulation.get_n_PRC()));

        JPanel panelMutantToCancer = new JPanel(new GridLayout(2,2,5,5));
        panelMutantToCancer.setBorder(BorderFactory.createTitledBorder(Messages.get("TransformationToCancer")));
        panelMutantToCancer.add(labelMutantA);
        panelMutantToCancer.add(textMutantToCancerA);
        panelMutantToCancer.add(labelMutantN);
        panelMutantToCancer.add(textMutantToCancerN);
        mutatedPanel.add(panelMutantToCancer);

        return mutatedPanel;
    }

    private static JPanel createCancerPanel() {
        JPanel cancerPanel = new JPanel();
        cancerPanel.setBorder(BorderFactory.createTitledBorder(Messages.get("Parameters")));
        cancerPanel.setLayout(new BoxLayout(cancerPanel, BoxLayout.Y_AXIS));

        JLabel labelNaturalDeath = new JLabel("P_CD: ");
        textNaturalDeath = new JTextField(String.valueOf(simulation.get_PCD()));
        JPanel panelNaturalDeath = new JPanel(new GridLayout(1,2,5,5));
        panelNaturalDeath.setBorder(BorderFactory.createTitledBorder(Messages.get("NaturalD")));
        panelNaturalDeath.add(labelNaturalDeath);
        panelNaturalDeath.add(textNaturalDeath);
        cancerPanel.add(panelNaturalDeath);

        JLabel labelDivision = new JLabel("P_CS: ");
        textDivisionCancer = new JTextField(String.valueOf(simulation.get_PCS()));
        JPanel panelDivision = new JPanel(new GridLayout(1,2,5,5));
        panelDivision.setBorder(BorderFactory.createTitledBorder(Messages.get("CellDivision")));
        panelDivision.add(labelDivision);
        panelDivision.add(textDivisionCancer);
        cancerPanel.add(panelDivision);

        JLabel labelRadiation = new JLabel("P_CRD: ");
        textRadiationCancer = new JTextField(String.valueOf(simulation.get_PCRD()));
        JPanel panelRadiation = new JPanel(new GridLayout(1,2,5,5));
        panelRadiation.setBorder(BorderFactory.createTitledBorder(Messages.get("Radiation")));
        panelRadiation.add(labelRadiation);
        panelRadiation.add(textRadiationCancer);
        cancerPanel.add(panelRadiation);

        JLabel labelMutation1 = new JLabel("beta1:");
        textAddMutationCancerB1 = new JTextField(String.valueOf(simulation.get_PCM_beta1()), 5);
        JLabel labelMutation2 = new JLabel("beta2");
        textAddMutationCancerB2 = new JTextField(String.valueOf(simulation.get_PCM_beta2()));

        JPanel panelMutation = new JPanel(new GridLayout(2,2,5,5));
        panelMutation.setBorder(BorderFactory.createTitledBorder("PCM"));
        panelMutation.add(labelMutation1);
        panelMutation.add(textAddMutationCancerB1);
        panelMutation.add(labelMutation2);
        panelMutation.add(textAddMutationCancerB2);
        cancerPanel.add(panelMutation);

        return cancerPanel;
    }

    private static JPanel createOtherPanel(){
        JPanel otherPanel = new JPanel();
        otherPanel.setBorder(BorderFactory.createTitledBorder(Messages.get("Parameters")));
        otherPanel.setLayout(new BoxLayout(otherPanel, BoxLayout.Y_AXIS));

        JLabel labelT2  = new JLabel("t_M:");
        textSponDamageT = new JTextField(String.valueOf(simulation.get_t_PM()), 5);
        textSponDamageT.setPreferredSize(new Dimension(10, 2));

        JLabel labelA2 = new JLabel("a_M:");
        textSponDamageA = new JTextField(String.valueOf(simulation.get_a_PM()), 5);
        textSponDamageA.setPreferredSize(new Dimension(10, 2));

        JLabel labelN2  = new JLabel("n_M:");
        textSponDamageN = new JTextField(String.valueOf(simulation.get_n_PM()), 5);
        textSponDamageN.setPreferredSize(new Dimension(10, 2));

        JPanel panelNaturalDamageRepair = new JPanel(new GridLayout(3,2,5,5));
        panelNaturalDamageRepair.setBorder(BorderFactory.createTitledBorder(Messages.get("SpontaneousDamage")));
        panelNaturalDamageRepair.add(labelT2);
        panelNaturalDamageRepair.add(textSponDamageT);
        panelNaturalDamageRepair.add(labelA2);
        panelNaturalDamageRepair.add(textSponDamageA);
        panelNaturalDamageRepair.add(labelN2);
        panelNaturalDamageRepair.add(textSponDamageN);
        otherPanel.add(panelNaturalDamageRepair);

        JLabel labelDivision = new JLabel("P_RD: ");
        textDeadPrecise = new JTextField(String.valueOf(simulation.get_const_PRD()), 5);
        JPanel panelDivision = new JPanel(new GridLayout(1,2,5,5));
        panelDivision.setBorder(BorderFactory.createTitledBorder(Messages.get("PreciseIrr")));
        panelDivision.add(labelDivision);
        panelDivision.add(textDeadPrecise);
        otherPanel.add(panelDivision);

        JLabel labelT3  = new JLabel("q_R:");
        textNaturalRepairQ = new JTextField(String.valueOf(simulation.get_q_PR()), 5);
        textNaturalRepairQ.setPreferredSize(new Dimension(10, 2));

        JLabel labelA3 = new JLabel("a_R:");
        textNaturalRepairA = new JTextField(String.valueOf(simulation.get_a_PR()), 5);
        textNaturalRepairA.setPreferredSize(new Dimension(10, 2));

        JLabel labelN3  = new JLabel("n_R:");
        textNaturalRepairN = new JTextField(String.valueOf(simulation.get_n_PR()), 5);
        textNaturalRepairN.setPreferredSize(new Dimension(10, 2));

        JPanel panelNaturalRepair = new JPanel(new GridLayout(3,2,5,5));
        panelNaturalRepair.setBorder(BorderFactory.createTitledBorder(Messages.get("NaturalRepair")));
        panelNaturalRepair.add(labelT3);
        panelNaturalRepair.add(textNaturalRepairQ);
        panelNaturalRepair.add(labelA3);
        panelNaturalRepair.add(textNaturalRepairA);
        panelNaturalRepair.add(labelN3);
        panelNaturalRepair.add(textNaturalRepairN);
        otherPanel.add(panelNaturalRepair);

        JLabel labelRadiationDamage = new JLabel("P_RDEM: ");
        textRadiationDamage = new JTextField(String.valueOf(simulation.get_const_PRDEM()), 5);
        JPanel panelRadiationDamage = new JPanel(new GridLayout(1,2,5,5));
        panelRadiationDamage.setBorder(BorderFactory.createTitledBorder(Messages.get("RadiationDamage")));
        panelRadiationDamage.add(labelRadiationDamage);
        panelRadiationDamage.add(textRadiationDamage);
        otherPanel.add(panelRadiationDamage);

        JLabel labelT4  = new JLabel("a0_PAR:");
        textAdaptiveA0 = new JTextField(String.valueOf(simulation.get_a0_PAR()), 5);
        textAdaptiveA0.setPreferredSize(new Dimension(10, 2));

        JLabel labelA4 = new JLabel("a1_PAR:");
        textAdaptiveA1 = new JTextField(String.valueOf(simulation.get_a1_PAR()), 5);
        textAdaptiveA1.setPreferredSize(new Dimension(10, 2));

        JLabel labelN4  = new JLabel("a2_PAR:");
        textAdaptiveA2 = new JTextField(String.valueOf(simulation.get_a2_PAR()), 5);
        textAdaptiveA2.setPreferredSize(new Dimension(10, 2));

        JPanel panelAdaptiveResponse = new JPanel(new GridLayout(3,2,5,5));
        panelAdaptiveResponse.setBorder(BorderFactory.createTitledBorder(Messages.get("Adaptive")));
        panelAdaptiveResponse.add(labelT4);
        panelAdaptiveResponse.add(textAdaptiveA0);
        panelAdaptiveResponse.add(labelA4);
        panelAdaptiveResponse.add(textAdaptiveA1);
        panelAdaptiveResponse.add(labelN4);
        panelAdaptiveResponse.add(textAdaptiveA2);
        otherPanel.add(panelAdaptiveResponse);

        return otherPanel;
    }



    public static void main(String[] args) {
        CellsSimulation cells = new CellsSimulation();
        cells.setExtendedState(JFrame.MAXIMIZED_BOTH);
        cells.setVisible(true);
    }
}