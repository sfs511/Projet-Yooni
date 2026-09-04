package sn.delivery.model;

public class Route {

    private Region depart;
    private Region destination;
    private double distance;
    private int tempsMinutes;

    public Route(Region depart, Region destination, double distance, int tempsMinutes) {
        this.depart = depart;
        this.destination = destination;
        this.distance = distance;
        this.tempsMinutes = tempsMinutes;
    }

    public Region getDepart() { return depart; }
    public Region getDestination() { return destination; }
    public double getDistance() { return distance; }
    public int getTempsMinutes() { return tempsMinutes; }
}