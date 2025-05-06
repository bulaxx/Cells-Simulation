public class Cell {
    public double age;
    public double mutation;
    public double damage;
    public double mutationNumber;
    public String status;

    public void damage(){
        damage += 1;
        status = "damage";
    }
}
