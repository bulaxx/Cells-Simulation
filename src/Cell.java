public class Cell {
    public double age;
    public int mutation;
    public int damage;
    public double mutationNumber;
    public String status;

    public void damage(){
        this.damage += 1;
        this.status = "damaged";
    }

    public void mutation(){
        this.mutation += 1;
        this.status = "mutated";
    }
}
