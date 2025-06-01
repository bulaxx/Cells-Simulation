import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.util.Random;

public class Simulation {

    static int n=15; //all cells
    static int nHealth;
    static int s=50; //number step tidal
    static double D=0.1;  //dose
    Cell[][][] organism = new Cell[n][n][n];
    double dose_Pa [][][][] =  new double[n][n][n][s];
    private boolean logToFile;
    private final double stepAge = 0.002;

    static int healthy, damaged, mutated, cancerous, dead;
    static double PHit,PD, PMD, PM, PR, PRDEM, PRC, PRD, PCRD, PB, PDD, PDM, PRMM, PCM, PAR;

    static Random rand = new Random();
    static double random, random1, random2;

    public void setN(int n){
        Simulation.n = n;
    }

    public void setK(int k){
        Simulation.s = k;
    }
    public void setD(int d){
        Simulation.D = d;
    }

    double const_PHit = 0.04;
    public double PHit(){
        PHit = (1 - Math.exp(-const_PHit * D));
        return PHit;
    }

    //death of a healthy cell from natural causes
    double t_PD = 0.002;
    double a_PD = 3*Math.pow(10, -7);
    double n_PD = 3;
    public double Pd(int x, int y, int z) {
        PD = (1 - t_PD)*(1 - Math.exp(-a_PD*Math.pow(organism[x][y][z].age,n_PD))) + t_PD;
        return PD;
    }

    //death of a damaged cell from natural causes
    double t_PDD = 0.003;
    double a_PDD = 7*Math.pow(10, -7);
    double n_PDD = 3;
    public double Pdd(int x, int y, int z) {
        PDD = (1 - t_PDD)*(1 - Math.exp(-a_PDD*Math.pow(organism[x][y][z].age,n_PDD))) + t_PDD;
        return PDD;
    }

    //death of a mutated cell from natural causes
    double t_PMD = 0.003;
    double a_PMD = 7*Math.pow(10, -7);
    double n_PMD = 3;
    public double Pmd(int x, int y, int z) {
        PMD = (1 - t_PMD)*(1 - Math.exp(-a_PMD*Math.pow(organism[x][y][z].age,n_PMD))) + t_PMD;
        return PMD;
    }

    //Death as a result of precise irradiation
    double const_PRD = 6*Math.pow(10, -5);
    public double P_RD(){
        PRD = 1 - Math.exp(-const_PRD*D);
        return PRD;
    }

    //formation of spontaneous cell damage
    double t_PM = 0.0004;
    double a_PM = Math.pow(10, -8);
    double n_PM = 3;
    public double Pm(int x, int y, int z) {
        PM = (1 - t_PM)*(1 - Math.exp(-a_PM*Math.pow(organism[x][y][z].age,n_PM))) + t_PM;
        return PM;
    }

    //radiation damage
    double constP_DEM = 0.2;
    public double PRDEM(){
        PRDEM = 1 - Math.exp(-constP_DEM*D);
        return PRDEM;
    }

    //natural repair of damage
    double q_PR = Math.pow(10, -4);
    double a_PR = Math.pow(10, -5);
    double n_PR = 4;
    public double Pr(int x, int y, int z) {
        PR = q_PR*Math.exp(-a_PR*Math.pow(organism[x][y][z].age,n_PR));
        return PR;
    }

    //formation of mutation converting a damaged cell into a mutated cell
    double a_PDM = 0.002; //zamiast 0.002
    public double PDM(int x, int y, int z) {
        PDM = 1 - Math.exp(-a_PDM*organism[x][y][z].damage);
        return PDM;
    }

    //transformation of a mutant cell into a cancerous one
    double a_PRC = 0.002; //zamiast 0.002
    double n_PRC = 5;
    public double Prc(int x, int y, int z) {
        PRC = 1 - Math.exp(-a_PRC*Math.pow(organism[x][y][z].mutation,n_PRC));
        return PRC;
    }

    double a_PRMM = 0.002; //zamiast 0.002
    double n_PRMM = 2;
    public double PRMM(int x, int y, int z) {
        PRMM = 1 - Math.exp(-a_PRMM*Math.pow(organism[x][y][z].mutation, n_PRMM));
        return PRMM;
    }

    double beta1 = 0.001;
    double beta2 = 0.00001;
    public double PCM(int x, int y, int z) {
        PCM = beta1 + organism[x][y][z].age * beta2;
        return PCM;
    }

    //Cancer cell death due to radio-sensitivity
    double const_PCRD = 6*Math.pow(10, -6);
    public double PCRD() {
        PCRD = 1 - Math.exp(-const_PCRD*D);
        return PCRD;
    }

    //adaptive response
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
                diff = K-i;
                PAR1 = a0_PAR * (thisDose*thisDose)*(diff*diff)*Math.exp(2-a1_PAR*thisDose-a2_PAR*diff);
                PAR += PAR1;
            }
        }

        return PAR;
    }

    //healthy cell division
    double PS = 0.003;

    //damaged cell division
    double PDS = 0.002;

    //mutated cell division
    double PMS = 0.002;

    //cancer cell division
    double PCS = 0.0021;

    //death of a cancer cell from natural causes
    double PCD = 0.002;

    public Cell[][][] reproduction(int x , int y, int z) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    if ("empty".equals(organism[i][j][k].status)) {
                        String parentStatus = organism[x][y][z].status;

                        if ("healthy".equals(parentStatus)) {
                            organism[i][j][k].status = "healthy";
                            i = j = k = n;
                        } else if ("damaged".equals(parentStatus)) {
                            organism[i][j][k].status = "damaged";
                            i = j = k = n;
                        } else if ("mutated".equals(parentStatus)) {
                            organism[i][j][k].status = "mutated";
                            i = j = k = n;
                        } else if ("cancer".equals(parentStatus)) {
                            organism[i][j][k].status = "cancer";
                            i = j = k = n;
                        }
                    }
                }
            }
        }
        return organism;
    }



    public Cell[][][] inicializeOrganism(){
        for(int i= 0; i < n; i++){
            for(int j = 0; j < n; j++){
                for(int k = 0; k < n; k++){
                        organism[i][j][k] = new Cell();
                        organism[i][j][k].status = "healthy";
                        organism[i][j][k].age = 0.0;
                        organism[i][j][k].mutation = 0;
                        organism[i][j][k].mutationNumber = 0.0;
                        organism[i][j][k].damage = 0;
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
        for(int a = 0; a < s; a++){
            healthy = 0;
            damaged = 0;
            mutated = 0;
            cancerous = 0;
            dead = 0;

            for(int i = 0; i<=n; i++){
                for(int j = 0; j<=n; j++){
                    for(int k = 0; k <= n; k++){
                        organism[i][j][k].age += 0.0001;
                        //organism[i][j][k].age = Math.round(organism[i][j][k].age * 1000.0) / 1000.0;
                        PHit();
                        if(organism[i][j][k].status.equals("healthy")) {
                            healthy++;
                            random1 = rand.nextDouble();
                            //healthy hit
                            if (random1 <= PHit) {
                                dose_Pa[i][j][k][a] = D;
                                random2 = rand.nextDouble();
                                P_RD();
                                Pd(i, j, k);
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
                                dose_Pa[i][j][k][a] = D;
                                random2=rand.nextDouble();
                                Pdd(i, j, k);
                                P_RD();
                                if(random2 <= PRD + PD){
                                    organism[i][j][k].status = "dead";
                                    continue;
                                }
                                Pm(i, j, k);
                                if(random2>= PRD + PD && random2 <= PRD + PD + PM){
                                    organism[i][j][k].damage();
                                    continue;
                                }
                                Pr(i, j, k);
                                if(random2>= PRD+PD+PM && random2 <= PRD+PD+PM+PR){
                                    if(organism[i][j][k].damage == 1){
                                        organism[i][j][k].damage -=1;
                                        organism[i][j][k].status = "healthy";
                                    }
                                    if(organism[i][j][k].damage >1){
                                        organism[i][j][k].damage -=1;
                                    }

                                }
                                if(random2 >= PRD + PD+PM+PR && random2 <= PRD+PD+PM+PR+PDS){
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
                                Pr(i, j, k);
                                if(random2 >= PDD + PM && random2 <= PDD + PM + PR){
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
                                dose_Pa[i][j][k][a] = D;
                                random2=rand.nextDouble();
                                Pmd(i, j, k);
                                if(random2 <= PMD){
                                    organism[i][j][k].status = "dead";
                                    continue;
                                }
                                P_RD();
                                if(random2 >= PMD && random2 <= PMD + PRD){
                                    organism[i][j][k].status = "dead";
                                    continue;
                                }
                                Pm(i, j, k);
                                if(random2 >= PMD+PRD && random2 <= PMD+ PRD + PM){
                                    organism[i][j][k].damage();
                                    continue;
                                }
                                Pr(i, j, k);
                                if(random2 >= PMD+PRD +PM && random2 <= PMD + PRD + PM+ PR){
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
                                Pr(i, j, k);
                                if(random2 >= PMD+PM && random2 <= PMD + PM+PR){
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
                                dose_Pa[i][j][k][a] = D;
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
            writeToFile(s, organism);

        }


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