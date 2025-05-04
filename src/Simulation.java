import java.lang.Math;

public class Simulation {

    static int n = 10; //all cells
    static int nHealth = 4;
    static int k = 80; //number step tidal
    static double D = 0.5;  //dose
    Cell[][][] organism = new Cell[n][n][n];
    static double[][][][] dose = new double[n][n][n][k];

    static int healthy, damaged, mutated, cancerous, dead;
    static double PHit,PD, PMD, PM, PR, PA, PDEM, PRC, PRD, PCRD, PB, PA1, PDD, PDM, PRMM;

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

    //Death as a result of precision irradiation
    double const_PRD = 6*Math.pow(10, -5);
    public double P_RD(){
        PRD = 1 - Math.exp(-const_PRD*D);
        return PRD;
    }

    //formation of spontaneous damage
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

    //natural reparation of damage
    double q_PR = Math.pow(10, -4);
    double a_PR = Math.pow(10, -5);
    double n_PR = 4;
    public double Ppr(int x, int y, int z) {
        PR = q_PR*Math.exp(-a_PR*Math.pow(organism[x][y][z].age,n_PR));
        return PR;
    }

    //mutation formation from damaged cell to mutated cell
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
                    for(int t = 0; t<k; k++) {
                        organism[i][j][k] = new Cell();
                        organism[i][k][k].status = "empty";
                        organism[i][k][k].age = 0.0;
                        organism[i][k][k].mutation = 0.0;
                        organism[i][j][k].mutationNumber = 0.0;
                        organism[i][k][k].damage = 0.0;
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

}
