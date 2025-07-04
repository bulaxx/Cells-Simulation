import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.sql.SQLOutput;
import java.util.*;

public class Simulation {

    static int n=16; //all cells
    static int nHealth;
    static int s=150; //number step tidal
    static double D=2;  //dose
    Cell[][][] organism = new Cell[n][n][n];
    double dose_Pa [][][][] =  new double[n][n][n][s];
    private boolean logToFile;
    private final double stepAge = 0.002;
    static int stepTime =0;

    static int healthy, damaged, mutated, cancerous, dead;
    static double PHit,PD, PMD, PM, PR, PRDEM, PRC, PRD, PCRD, PB, PDD, PDM, PRMM, PCM, PAR;

    static Random rand = new Random();
    static double random, random1, random2;

    public void setN(int n){
        Simulation.n = n;
    }

    public void setStepTime(int stepTime){ Simulation.stepTime = stepTime; }
    public void setK(int k){
        Simulation.s = k;
    }
    public void setD(int d){
        Simulation.D = d/10;
    }

    double const_PHit = 0.04;
    public double PHit(){
        PHit = (1 - Math.exp(-const_PHit * D));
        return PHit;
    }

    //death of a healthy cell from natural causes
    public double getDEFAULT_t_PD(){ return DEFAULT_t_PD; }
    public double getDEFAULT_a_PD(){ return DEFAULT_a_PD; }
    public double getDEFAULT_n_PD(){ return DEFAULT_n_PD; }

    public double get_t_PD(){return t_PD; }
    public double get_a_PD(){return a_PD; }
    public double get_n_PD(){return n_PD; }

    public void setHealthyDeadNatural(double set_t_PD,double set_a_PD,double set_n_PD){
        t_PD = set_t_PD;
        a_PD = set_a_PD;
        n_PD = set_n_PD;
    }

    double DEFAULT_t_PD = 0.002;
    double DEFAULT_a_PD = 3*Math.pow(10, -7);
    double DEFAULT_n_PD = 3;
    double t_PD = 0.002;
    double a_PD =  3*Math.pow(10, -7);
    double n_PD = 3;
    public double Pd(int x, int y, int z) {
        PD = (1 - t_PD)*(1 - Math.exp(-a_PD*Math.pow(organism[x][y][z].age,n_PD))) + t_PD;
        return PD;
    }

    //death of a damaged cell from natural causes
    public double getDEFAULT_t_PDD(){ return DEFAULT_t_PDD; }
    public double getDEFAULT_a_PDD(){ return DEFAULT_a_PDD; }
    public double getDEFAULT_n_PDD(){ return DEFAULT_n_PDD; }

    public double get_t_PDD(){return t_PDD; }
    public double get_a_PDD(){return a_PDD; }
    public double get_n_PDD(){return n_PDD; }

    public void setDamagedDeadNatural(double set_t_PDD,double set_a_PDD,double set_n_PDD){
        t_PDD = set_t_PDD;
        a_PDD = set_a_PDD;
        n_PDD = set_n_PDD;
    }

    double DEFAULT_t_PDD = 0.003;
    double DEFAULT_a_PDD = 7*Math.pow(10, -7);
    double DEFAULT_n_PDD = 3;
    double t_PDD = 0.003;
    double a_PDD = 7*Math.pow(10, -7);
    double n_PDD = 3;
    public double Pdd(int x, int y, int z) {
        PDD = (1 - t_PDD)*(1 - Math.exp(-a_PDD*Math.pow(organism[x][y][z].age,n_PDD))) + t_PDD;
        return PDD;
    }

    //death of a mutated cell from natural causes
    public double getDEFAULT_t_PMD(){ return DEFAULT_t_PMD; }
    public double getDEFAULT_a_PMD(){ return DEFAULT_a_PMD; }
    public double getDEFAULT_n_PMD(){ return DEFAULT_n_PMD; }

    public double get_t_PMD(){return t_PMD; }
    public double get_a_PMD(){return a_PMD; }
    public double get_n_PMD(){return n_PMD; }

    public void setMutatedDeadNatural(double set_t_PMD,double set_a_PMD,double set_n_PMD){
        t_PMD = set_t_PMD;
        a_PMD = set_a_PMD;
        n_PMD = set_n_PMD;
    }

    double DEFAULT_t_PMD = 0.003;
    double DEFAULT_a_PMD = 7*Math.pow(10, -7);
    double DEFAULT_n_PMD = 3;
    double t_PMD = 0.003;
    double a_PMD = 7*Math.pow(10, -7);
    double n_PMD = 3;
    public double Pmd(int x, int y, int z) {
        PMD = (1 - t_PMD)*(1 - Math.exp(-a_PMD*Math.pow(organism[x][y][z].age,n_PMD))) + t_PMD;
        return PMD;
    }

    //Death as a result of precise irradiation
    public double getDEFAULT_const_PRD(){return DEFAULT_const_PRD ;}
    public double get_const_PRD(){return const_PRD;}
    public void set_const_PRD(double set_PDM){const_PRD = set_PDM;}
    double DEFAULT_const_PRD =  6*Math.pow(10, -5);
    double const_PRD = 6*Math.pow(10, -5);
    public double P_RD(){
        PRD = 1 - Math.exp(-const_PRD*D);
        return PRD;
    }

    //formation of spontaneous cell damage
    public double getDEFAULT_t_PM(){ return DEFAULT_t_PM; }
    public double getDEFAULT_a_PM(){ return DEFAULT_a_PM; }
    public double getDEFAULT_n_PM(){ return DEFAULT_n_PM; }

    public double get_t_PM(){return t_PM; }
    public double get_a_PM(){return a_PM; }
    public double get_n_PM(){return n_PM; }

    public void setPM(double set_t_PM,double set_a_PM,double set_n_PM){
        t_PM = set_t_PM;
        a_PM = set_a_PM;
        n_PM = set_n_PM;
    }

    double DEFAULT_t_PM = 0.0004;
    double DEFAULT_a_PM = Math.pow(10, -8);
    double DEFAULT_n_PM = 3;
    double t_PM = 0.0004;
    double a_PM = Math.pow(10, -8);
    double n_PM = 3;
    public double Pm(int x, int y, int z) {
        PM = (1 - t_PM)*(1 - Math.exp(-a_PM*Math.pow(organism[x][y][z].age,n_PM))) + t_PM;
        return PM;
    }

    //radiation damage
    public double getDEFAULT_const_PRDEM(){return DEFAULT_const_PRDEM ;}
    public double get_const_PRDEM(){return const_PRDEM;}
    public void set_const_PRDEM(double set_PRDEM){const_PRDEM = set_PRDEM;}
    double DEFAULT_const_PRDEM =  0.2;
    double const_PRDEM = 0.2;
    public double PRDEM(){
        PRDEM = 1 - Math.exp(-const_PRDEM*D);
        return PRDEM;
    }

    //natural repair of damage
    public double getDEFAULT_q_PR(){ return DEFAULT_q_PR; }
    public double getDEFAULT_a_PR(){ return DEFAULT_a_PR; }
    public double getDEFAULT_n_PR(){ return DEFAULT_n_PR; }

    public double get_q_PR(){return q_PR; }
    public double get_a_PR(){return a_PR; }
    public double get_n_PR(){return n_PR; }

    public void setPR(double set_q_PR,double set_a_PR,double set_n_PR){
        q_PR = set_q_PR;
        a_PR = set_a_PR;
        n_PR = set_n_PR;
    }

    double DEFAULT_q_PR = Math.pow(10, -4);
    double DEFAULT_a_PR = Math.pow(10, -5);
    double DEFAULT_n_PR = 4;
    double q_PR = Math.pow(10, -4);
    double a_PR = Math.pow(10, -5);
    double n_PR = 4;
    public double Pr(int x, int y, int z) {
        PR = q_PR*Math.exp(-a_PR*Math.pow(organism[x][y][z].age,n_PR));
        return PR;
    }

    //formation of mutation converting a damaged cell into a mutated cell
    public double getDEFAULT_a_PDM(){return DEFAULT_a_PDM ;}
    public double get_a_PDM(){return a_PDM;}
    public void setPDM(double set_PDM){a_PDM = set_PDM;}
    double DEFAULT_a_PDM = 0.002;
    double a_PDM = 0.002; //zamiast 0.002
    public double PDM(int x, int y, int z) {
        PDM = 1 - Math.exp(-a_PDM*organism[x][y][z].damage);
        return PDM;
    }

    //transformation of a mutant cell into a cancerous one
    public double getDEFAULT_a_PRC(){return DEFAULT_a_PRC ;}
    public double get_a_PRC(){return a_PRC;}
    public void set_a_PRC(double set_a_PRC){a_PRC = set_a_PRC;}
    double DEFAULT_a_PRC = 0.002;
    public double getDEFAULT_n_PRC(){return DEFAULT_n_PRC ;}
    public double get_n_PRC(){return n_PRC;}
    public void set_n_PRC(double set_n_PRC){n_PRC = set_n_PRC;}
    double DEFAULT_n_PRC = 5;
    double a_PRC = 0.002; //zamiast 0.002
    double n_PRC = 5;
    public double Prc(int x, int y, int z) {
        PRC = 1 - Math.exp(-a_PRC*Math.pow(organism[x][y][z].mutation,n_PRC));
        return PRC;
    }

    //additional mutation
    public double getDEFAULT_a_PRMM(){return DEFAULT_a_PRMM ;}
    public double get_a_PRMM(){return a_PRMM;}
    public void set_a_PRMM(double set_a_PRMM){a_PRMM = set_a_PRMM;}
    double DEFAULT_a_PRMM = 0.002;
    public double getDEFAULT_n_PRMM(){return DEFAULT_n_PRMM ;}
    public double get_n_PRMM(){return n_PRMM;}
    public void set_n_PRMM(double set_n_PRMM){n_PRMM = set_n_PRMM;}
    double DEFAULT_n_PRMM = 2;
    double a_PRMM = 0.002; //zamiast 0.002
    double n_PRMM = 2;
    public double PRMM(int x, int y, int z) {
        PRMM = 1 - Math.exp(-a_PRMM*Math.pow(organism[x][y][z].mutation, n_PRMM));
        return PRMM;
    }

    //PCM additional mutation cancer
    public double getDEFAULT_PCM_beta1(){return DEFAULT_PCM_beta1 ;}
    public double get_PCM_beta1(){return PCM_beta1;}
    public void set_PCM_beta1(double set_PCM_beta1){PCM_beta1 = set_PCM_beta1;}
    double DEFAULT_PCM_beta1 = 0.001;
    public double getDEFAULT_PCM_beta2(){return DEFAULT_PCM_beta2 ;}
    public double get_PCM_beta2(){return PCM_beta2;}
    public void set_PCM_beta2(double set_PCM_beta2){PCM_beta2 = set_PCM_beta2;}
    double DEFAULT_PCM_beta2 = 0.00001;
    double PCM_beta1 = 0.001;
    double PCM_beta2 = 0.00001;
    public double PCM(int x, int y, int z) {
        PCM = PCM_beta1 + organism[x][y][z].age * PCM_beta2;
        return PCM;
    }

    //Cancer cell death due to radio-sensitivity
    public double getDEFAULT_PCRD(){return DEFAULT_PCRD ;}
    public double get_PCRD(){return const_PCRD;}
    public void setPCRD(double set_PCRD){const_PCRD = set_PCRD;}
    double DEFAULT_PCRD = 6*Math.pow(10, -6);
    double const_PCRD = 6*Math.pow(10, -6);
    public double PCRD() {
        PCRD = 1 - Math.exp(-const_PCRD*D);
        return PCRD;
    }

    //adaptive response
    public double getDEFAULT_a0_PAR(){ return DEFAULT_a0_PAR; }
    public double getDEFAULT_a1_PAR(){ return DEFAULT_a1_PAR; }
    public double getDEFAULT_a2_PAR(){ return DEFAULT_a2_PAR; }

    public double get_a0_PAR(){return a0_PAR; }
    public double get_a1_PAR(){return a1_PAR; }
    public double get_a2_PAR(){return a2_PAR; }

    public void setPAR(double set_a0_PAR,double set_a1_PAR,double set_a2_PAR){
        a0_PAR = set_a0_PAR;
        a1_PAR = set_a1_PAR;
        a2_PAR = set_a2_PAR;
    }

    double DEFAULT_a0_PAR = 22.94;
    double DEFAULT_a1_PAR = 79.45;
    double DEFAULT_a2_PAR = 0.0832;
    double a0_PAR = 22.94;
    double a1_PAR = 79.45;
    double a2_PAR = 0.0832;
    double PAR1;
    public double PAR(int x, int y, int z) {
        double Dk =0;
        double K = organism[x][y][z].age;
        double k =stepAge;
        double diff = 0;
        double thisDose = 0;
        int step = (int)(K/k);
        for(int i =0;i<step;i++) {
            if(dose_Pa[x][y][z][i] > 0){
                PAR1 = 0;
                thisDose = dose_Pa[x][y][z][i];
                diff = K-i*k;
                PAR1 = a0_PAR * (thisDose*thisDose)*(diff*diff)*Math.exp(2-a1_PAR*thisDose-a2_PAR*diff);
                PAR += PAR1;
            }
        }

        return PAR;
    }

    //healthy cell division
    public double getDEFAULT_PS(){return DEFAULT_PS ;}
    public double get_PS(){return PS;}
    public void setPS(double set_PS){PS = set_PS;}
    double DEFAULT_PS = 0.003;
    double PS = 0.003;

    //damaged cell division
    public double getDEFAULT_PDS(){return DEFAULT_PDS ;}
    public double get_PDS(){return PDS;}
    public void setPDS(double set_PDS){PDS = set_PDS;}
    double DEFAULT_PDS = 0.002;
    double PDS = 0.002;

    //mutated cell division
    public double getDEFAULT_PMS(){return DEFAULT_PMS ;}
    public double get_PMS(){return PMS;}
    public void setPMS(double set_PMS){PMS = set_PMS;}
    double DEFAULT_PMS = 0.002;
    double PMS = 0.002;

    //cancer cell division
    public double getDEFAULT_PCS(){return DEFAULT_PCS ;}
    public double get_PCS(){return PCS;}
    public void setPCS(double set_PCS){PCS = set_PCS;}
    double DEFAULT_PCS = 0.0021;
    double PCS = 0.0021;

    //death of a cancer cell from natural causes
    public double getDEFAULT_PCD(){return DEFAULT_PCD ;}
    public double get_PCD(){return PCD;}
    public void setPCD(double set_PCD){PCD = set_PCD;}
    double DEFAULT_PCD = 0.002;
    double PCD = 0.002;




    public Cell[][][] reproduction(int x, int y, int z) {
        int[][] offsets = {
                {-1,-1,-1}, {0,-1,-1}, {1,-1,-1},
                {-1, 0,-1}, {0, 0,-1}, {1, 0,-1},
                {-1, 1,-1}, {0, 1,-1}, {1, 1,-1},
                {-1,-1, 0}, {0,-1, 0}, {1,-1, 0},
                {-1, 0, 0}, {1, 0, 0}, {1, 1, 1},
                {-1, 1, 0}, {0, 1, 0}, {1, 1, 0},
                {-1,-1, 1}, {0,-1, 1}, {1,-1, 1},
                {-1, 0, 1}, {0, 0, 1}, {1, 0, 1},
                {-1, 1, 1}, {0, 1, 1}
        };

        List<int[]> shuffled = new ArrayList<>(Arrays.asList(offsets));
        Collections.shuffle(shuffled);

        for (int[] offset : shuffled) {
            int i = x + offset[0];
            int j = y + offset[1];
            int k = z + offset[2];

            if (i < 0 || j < 0 || k < 0 || i >= n || j >= n || k >= n)
                continue;

            if ("empty".equals(organism[i][j][k].status) || "dead".equals(organism[i][j][k].status)) {
                organism[i][j][k].status = organism[x][y][z].status;
                organism[i][j][k].age = 0.0;
                organism[i][j][k].mutation = organism[x][y][z].mutation;
                organism[i][j][k].mutationNumber = organism[x][y][z].mutationNumber;
                organism[i][j][k].damage = organism[x][y][z].damage;
                break;
            }
        }
        return organism;
    }



    public Cell[][][] inicializeOrganism(){
        for(int i= 0; i < n; i++){
            for(int j = 0; j < n; j++){
                for(int k = 0; k < n; k++){
                    organism[i][j][k] = new Cell();
                    if(i==0 || j==0 || k==0 || i==(n-1) || j==(n-1) || k==(n-1)
                            || i==1 || j==1 || k==1 || i==n-2 || j==n-2 || k==n-2){
                        organism[i][j][k].status = "empty";
                        organism[i][j][k].age = 0.0;
                        organism[i][j][k].mutation = 0;
                        organism[i][j][k].mutationNumber = 0.0;
                        organism[i][j][k].damage = 0;
                    }
                    else {
                        organism[i][j][k].status = "healthy";
                        organism[i][j][k].age = 0.0;
                        organism[i][j][k].mutation = 0;
                        organism[i][j][k].mutationNumber = 0.0;
                        organism[i][j][k].damage = 0;
                    }

                           /* organism[i][j][k].status = "healthy";
                            organism[i][j][k].age = 0.0;
                            organism[i][j][k].mutation = 0;
                            organism[i][j][k].mutationNumber = 0.0;
                            organism[i][j][k].damage = 0;*/
                }
            }
        }


        return organism;
    }

    public Cell[][][] setWall(){
        for(int i= 0; i < n; i++){
            for(int j = 0; j < n; j++){
                for(int k = 0; k < n; k++){
                    if(i==0 || j==0 || k==0 || i==(n-1) || j==(n-1) || k==(n-1)){
                        organism[i][j][k].status = "wall";
                    }
                }
            }
        }
        return organism;
    }

    public Cell[][][] simulation(){
        //for(int a = 0; a < s; a++){
        healthy = 0;
        damaged = 0;
        mutated = 0;
        cancerous = 0;
        dead = 0;

        for(int i = 0; i<n; i++){
            for(int j = 0; j<n; j++){
                for(int k = 0; k < n; k++){
                    organism[i][j][k].age += 0.001;
                    //organism[i][j][k].age = Math.round(organism[i][j][k].age * 1000.0) / 1000.0;
                    PHit();
                    if(organism[i][j][k].status.equals("healthy")) {
                        healthy++;
                        random1 = rand.nextDouble();
                        //healthy hit
                        if (random1 <= PHit) {
                            dose_Pa[i][j][k][stepTime] = D;
                            random2 = rand.nextDouble();
                            P_RD();
                            Pd(i, j, k);
                            //System.out.println("Healthy");
                            //System.out.println(PRD+" " + PD);
                            if (random2 <= PRD + PD) {
                                organism[i][j][k].status = "dead";
                                continue;
                            }

                            Pm(i, j, k);
                            if (random2 >= PRD + PD && random2 <= PRD + PD + PM) {
                                organism[i][j][k].damage();
                                continue;
                            }
                            if (random2 >= PD + PRD + PM  && random2 <= PD + PRD + PM + PS) {
                                reproduction(i, j, k);
                                continue;
                            }

                            PRDEM();
                            if (random2 >= PD + PRD + PM + PS && random2 <= PD + PRD + PM + PS + PRDEM) {
                                organism[i][j][k].damage();
                                continue;
                            }

                            if (random2 >= PD + PRD + PM + PS + PRDEM) {
                                continue;
                            }

                        }

                        //healthy dont hit
                        if (random1 >= PHit) {
                            random2 = rand.nextDouble();
                            Pd(i, j, k);
                            if (random2 <= PD) {
                                organism[i][j][k].status = "dead";
                                continue;
                            }

                            Pm(i, j, k);
                            if (random2 >= PD && random2 <= PM + PD) {
                                organism[i][j][k].damage();
                                continue;
                            }

                            if (random2 >= PD + PM && random2 <= PM + PD + PS) {
                                reproduction(i,j,k);
                                continue;
                            }

                            if (random2 >= PD + PM + PS) {
                                continue;
                            }

                        }

                    }

                    if("damaged".equals(organism[i][j][k].status)){
                        damaged++;
                        random1=rand.nextDouble();
                        if (random1 <= PHit) {
                            dose_Pa[i][j][k][stepTime] = D;
                            random2=rand.nextDouble();
                            Pdd(i, j, k);
                            P_RD();
                            //System.out.println("Damaged");
                            //System.out.println(PRD + " " + PDD +" "+ PHit +" "+ random2);
                            if(random2 <= PRD + PDD){
                                organism[i][j][k].status = "dead";

                                continue;
                            }
                            Pm(i, j, k);
                            if(random2>= PRD + PD && random2 <= PRD + PD + PM){
                                organism[i][j][k].damage();
                                continue;
                            }
                            PAR(i,j,k);
                            if(random2>= PRD+PD+PM && random2 <= PRD+PD+PM+PAR){
                                if(organism[i][j][k].damage == 1){
                                    organism[i][j][k].damage -=1;
                                    organism[i][j][k].status = "healthy";
                                }
                                if(organism[i][j][k].damage >1){
                                    organism[i][j][k].damage -=1;
                                }

                            }
                            Pr(i, j, k);
                            if(random2>= PRD+PD+PM+PAR && random2 <= PRD+PD+PM+PAR+PR){
                                if(organism[i][j][k].damage == 1){
                                    organism[i][j][k].damage -=1;
                                    organism[i][j][k].status = "healthy";
                                }
                                if(organism[i][j][k].damage >1){
                                    organism[i][j][k].damage -=1;
                                }

                            }
                            Prc(i, j, k);
                            if(random2 >= PRD + PD+PM+PR && random2 <= PRD+PD+PM+PR+PRC){
                                organism[i][j][k].status = "cancer";
                                continue;
                            }

                            if(random2 >= PRD + PD+PM+PR+ PRC && random2 <= PRD+PD+PM+PR+PRC+PDS){
                                reproduction(i,j,k);
                                continue;
                            }
                            PRDEM();
                            if(random2 >= PD+PRD+PM+PR+PDS && random2 <= PRD+PD+PS+PM+PDS+PRDEM){
                                organism[i][j][k].damage();
                                continue;
                            }

                            PDM(i, j, k);
                            if(random2 >= PD+PRD+PS+PR+PDS+PRDEM && random2 <= PRD+PD+PS+PR+PDS+PRDEM+PDM){
                                organism[i][j][k].mutation();
                                continue;
                            }

                        }

                        if(random1 >= PHit) {
                            random1 = rand.nextDouble();
                            Pdd(i, j, k);
                            if(random2 <= PDD){
                                organism[i][j][k].status = "dead";
                                continue;
                            }
                            Pm(i, j, k);
                            if(random2 >= PDD && random2 <= PDD + PM){
                                organism[i][j][k].damage();
                                continue;
                            }
                            PAR(i,j,k);
                            if(random2 >= PDD + PM && random2 <= PDD + PM + PAR){
                                if(organism[i][j][k].damage == 1){
                                    organism[i][j][k].damage -=1;
                                    organism[i][j][k].status = "healthy";
                                }
                                if(organism[i][j][k].damage >1){
                                    organism[i][j][k].damage -=1;
                                }
                            }
                            Pr(i, j, k);
                            if(random2 >= PDD + PM+PAR && random2 <= PDD + PM + PR+PAR){
                                if(organism[i][j][k].damage == 1){
                                    organism[i][j][k].damage -=1;
                                    organism[i][j][k].status = "healthy";
                                }
                                if(organism[i][j][k].damage >1){
                                    organism[i][j][k].damage -=1;
                                }
                            }
                            if(random2>= PDD+PM+PR && random2 <= PDD+PM+PR+PDS){
                                reproduction(i,j,k);
                                continue;
                            }
                            PDM(i, j, k);
                            if(random2>= PDD+PM+PR+PDS && random2 <= PDD+PM+PR+PDS+PDM){
                                organism[i][j][k].mutation();
                                continue;
                            }
                        }
                    }

                    if("mutated".equals(organism[i][j][k].status)){
                        mutated++;
                        random1 = rand.nextDouble();
                        PHit();
                        if(random1 <= PHit){
                            dose_Pa[i][j][k][stepTime] = D;
                            random2=rand.nextDouble();
                            Pmd(i, j, k);
                            if(random2 <= PMD){
                                organism[i][j][k].status = "dead";
                                continue;
                            }
                            P_RD();
                            //System.out.println("mutated");
                            //System.out.println(PRD);
                            if(random2 >= PMD && random2 <= PMD + PRD){
                                organism[i][j][k].status = "dead";
                                continue;
                            }
                            Pm(i, j, k);
                            if(random2 >= PMD+PRD && random2 <= PMD+ PRD + PM){
                                organism[i][j][k].damage();
                                continue;
                            }
                            PAR(i,j,k);
                            if(random2 >= PMD+PRD +PM && random2 <= PMD + PRD + PM+ PAR){
                                if(organism[i][j][k].damage == 1){
                                    organism[i][j][k].damage -=1;
                                    organism[i][j][k].status = "healthy";
                                }
                                if(organism[i][j][k].damage >1){
                                    organism[i][j][k].damage -=1;
                                }
                            }
                            Pr(i, j, k);
                            if(random2 >= PMD+PRD +PM+PAR && random2 <= PMD + PRD + PM+ PAR+PR){
                                if(organism[i][j][k].damage == 1){
                                    organism[i][j][k].damage -=1;
                                    organism[i][j][k].status = "healthy";
                                }
                                if(organism[i][j][k].damage >1){
                                    organism[i][j][k].damage -=1;
                                }
                            }

                            if(random2 >= PMD+PRD +PM+PR && random2 <= PMD + PRD + PM+ PR+PMS){
                                reproduction(i,j,k);
                                continue;
                            }
                            PRDEM();
                            if(random2 >= PMD+PRD +PM+PR+PMS && random2 <= PMD + PRD + PM+ PR+PMS+PRDEM){
                                organism[i][j][k].damage();
                                continue;
                            }

                            PRMM(i, j, k);
                            if(random2 >= PMD+PRD +PM+PR+PMS+PRDEM && random2 <= PMD + PRD + PM+ PR+PMS+PRDEM+PRMM){
                                organism[i][j][k].mutation();
                                continue;
                            }
                        }

                        if(random1 <= PHit){
                            random2 = rand.nextDouble();
                            Pmd(i, j, k);
                            if(random2 <= PMD){
                                organism[i][j][k].status = "dead";
                                continue;
                            }
                            Pm(i, j, k);
                            if(random2 >= PMD && random2 <= PMD + PM){
                                organism[i][j][k].damage();
                                continue;
                            }
                            PAR(i,j,k);
                            if(random2 >= PMD+PM && random2 <= PMD + PM+PAR){
                                if(organism[i][j][k].damage == 1){
                                    organism[i][j][k].damage -=1;
                                    organism[i][j][k].status = "healthy";
                                }
                                if(organism[i][j][k].damage >1){
                                    organism[i][j][k].damage -=1;
                                }
                            }
                            Pr(i, j, k);
                            if(random2 >= PMD+PM+PAR && random2 <= PMD + PM+PAR+PR){
                                if(organism[i][j][k].damage == 1){
                                    organism[i][j][k].damage -=1;
                                    organism[i][j][k].status = "healthy";
                                }
                                if(organism[i][j][k].damage >1){
                                    organism[i][j][k].damage -=1;
                                }
                            }
                            if(random2 >= PMD+PM+PR && random2 <= PMD + PM + PR+PMS){
                                reproduction(i,j,k);
                                continue;
                            }

                            PRMM(i, j, k);
                            if(random2>= PMD+PM+PR+PMS && random2 <= PMD + PM+ PR+PMS+PRMM){
                                organism[i][j][k].mutation();
                                continue;
                            }
                        }
                    }

                    if("cancer".equals(organism[i][j][k].status)){
                        cancerous++;
                        random1 = rand.nextDouble();
                        PHit();
                        if(random1 <= PHit){
                            dose_Pa[i][j][k][stepTime] = D;
                            random2 = rand.nextDouble();
                            if(random2 <= PCD){
                                organism[i][j][k].status = "dead";
                                continue;
                            }
                            P_RD();
                            if(random2 >= PCD && random2 <= PCD + PRD){
                                organism[i][j][k].status = "dead";
                                continue;
                            }
                            if(random2 >= PCD+PRD && random2 <= PCD + PRD+PCS){
                                reproduction(i,j,k);
                                continue;
                            }
                            PCRD();
                            if(random2 >= PCD+PRD+PCS && random2 <= PCD + PRD + PCS+PCRD){
                                organism[i][j][k].status = "dead";
                                continue;
                            }
                            PCM(i, j, k);
                            if(random2 >= PCD+PRD+PCS+PCRD && random2 <= PCD + PRD + PCS+PCRD+PCM){
                                organism[i][j][k].mutation();
                                continue;
                            }

                        }

                        if(random1 >= PHit){
                            random2 = rand.nextDouble();
                            if(random2 <= PCD){
                                organism[i][j][k].status = "dead";
                                continue;
                            }
                            if(random2 >= PCD && random2 <= PCD + PCS){
                                reproduction(i,j,k);
                                continue;
                            }
                            PCM(i, j, k);
                            if(random2 >= PCD+PCS && random2 <= PCD + PCS+PCM){
                                organism[i][j][k].mutation();
                                continue;
                            }
                        }
                    }
                }
            }
        }
        //System.out.println(stepTime + ": "+ "healthy: "+ healthy+ " " + "damaged" +" "+ damaged+ "dead "+ dead + " "+ "dose" + " "+ D);
        writeToFile(s, organism);

        //}


        return organism;
    }

    //tego nie uzywam
    public void writeToFile(int step, Cell[][][] organism) {
        if(!logToFile) return;

        try(FileWriter writer= new FileWriter("cell_log.txt", true)){
            for(int i = 0; i<n; i++){
                for(int j = 0; j<n; j++){
                    for(int k = 0; k<n; k++){
                        if(organism[i][j][k].status != null){
                            writer.write(String.format("t=%d; x=%d; y=%d; z=%d; age=%.2f; damage=%d; mutation=%d; status=%s%n", step, i, j, k, organism[i][j][k].age, organism[i][j][k].damage, organism[i][j][k].mutation, organism[i][j][k].status));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void randomizeOrganism() {
        Random rand = new Random();
        String[] statuses = {"healthy", "damaged", "mutated", "cancer", "dead", "empty"};

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    int idx = rand.nextInt(statuses.length);
                    organism[i][j][k].status = statuses[idx];
                }
            }
        }
    }

}
