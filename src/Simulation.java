import java.lang.Math;
import java.util.Random;

public class Simulation {

    static int n=15; //all cells
    static int nHealth;
    static int k=40; //number step tidal
    static double D=1;  //dose
    Cell[][][] organism = new Cell[n][n][n];
    static double[][][][] dose = new double[n][n][n][k];

    static int healthy, damaged, mutated, cancerous, dead;
    static double PHit,PD, PMD, PM, PR, PDEM, PRC, PRD, PCRD, PB, PDD, PDM;

    static Random rand = new Random();
    static double random, random1, random2;

    public void setN(int n){
        Simulation.n = n;
    }

    public void setK(int k){
        Simulation.k = k;
    }
    public void setD(int d){
        Simulation.D = d;
    }

    double const_PHit = 0.04;
    public double PHit(){
        return (1 - Math.exp(-const_PHit * D));
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
    double constP_DEM = 0.5;
    public double P_DEM(){
        PDEM = 1 - Math.exp(-constP_DEM*D);
        return PDEM;
    }

    //natural repair of damage
    double q_PR = Math.pow(10, -4);
    double a_PR = Math.pow(10, -5);
    double n_PR = 4;
    public double Ppr(int x, int y, int z) {
        PR = q_PR*Math.exp(-a_PR*Math.pow(organism[x][y][z].age,n_PR));
        return PR;
    }

    //formation of mutation converting a damaged cell into a mutated cell
    double a_PDM = 0.002;
    public double PDM(int x, int y, int z) {
        PDM = 1 - Math.exp(-a_PDM*organism[x][y][z].damage);
        return PDM;
    }

    //transformation of a mutant cell into a cancerous one
    double a_PRC = 0.001;
    double n_PRC = 5;
    public double Prc(int x, int y, int z) {
        PRC = 1 - Math.exp(-a_PRC*Math.pow(organism[x][y][z].mutationNumber,n_PRC));
        return PRC;
    }

    //Cancer cell death due to radio-sensitivity
    double const_PCRD = 6*Math.pow(10, -6);
    public double PRD() {
        PCRD = 1 - Math.exp(-const_PCRD*D);
        return PCRD;
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


    public Cell[][][] inicializeOrganism(){
        for(int i= 0; i < n; i++){
            for(int j = 0; j < n; j++){
                for(int k = 0; k < n; k++){
                    for(int t = 0; t<n; t++) {
                        organism[i][j][k] = new Cell();
                        organism[i][j][k].status = "healthy";
                        organism[i][j][k].age = 0.0;
                        organism[i][j][k].mutation = 0.0;
                        organism[i][j][k].mutationNumber = 0.0;
                        organism[i][j][k].damage = 0.0;

                    }
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
        for(int a = 0; a < k; a++){
            healthy = 0;
            damaged = 0;
            mutated = 0;
            cancerous = 0;
            dead = 0;

            for(int i = 1; i<n-1; i++){
                for(int j = 1; j<n-1; j++){
                    for(int k = 1; k < n-1; k++){
                        organism[i][j][k].age += 0.002;
                        PHit();
                        if(organism[i][j][k].status.equals("healthy")){
                            healthy++;
                            random1 = rand.nextDouble();
                            //healthy hit
                            if(random1 <= PHit){
                                random2 = rand.nextDouble();
                                PRD();
                                Pd(i, j,k);
                                if(random2 <= PRD+PD){
                                    organism[i][j][k].status = "dead";
                                    continue;
                                }

                                Pm(i, j, k);
                                if(random2>=PRD+PD && random2<=PRD+PD+PM){
                                    organism[i][j][k].damage();
                                    continue;
                                }
                                if(random2>= PD+PRD+PM+PS && random2<=PD+PRD+PM+PS+PB){
                                    //add reproduction
                                }

                                P_DEM();
                                if(random2>=PD+PRD+PM+PS+PB && random2<=PD+PRD+PM+PS+PB+PDEM){
                                    organism[i][j][k].damage();
                                    continue;
                                }

                                if(random2>=PD+PRD+PM+PS+PB+PDEM){
                                    continue;
                                }

                                //healthy dont hit

                            }
                        }
                    }
                }
            }

        }


        return organism;
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
