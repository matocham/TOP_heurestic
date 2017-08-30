package pl.edu.pb.graph;

/**
 * Created by Dominik on 2017-05-07.
 */
public class Point {
    int id;
    double x, y, profit;
    String description;

    public Point(double x, double y, double profit, int id) {
        this.x = x;
        this.y = y;
        this.profit = profit;
        this.description = "";
        this.id = id;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public Point(double x, double y, double profit, String description, int id) {
        this.x = x;
        this.y = y;
        this.profit = profit;
        this.description = description;
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getProfit() {
        return profit;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        if(!(obj instanceof Point)){
            return false;
        }
        return  this.hashCode() == obj.hashCode();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        return ((new Double(profit).hashCode())*prime+id)*prime;
    }

    @Override
    public String toString() {
        return "id:"+id+" p:"+profit;
    }
}
