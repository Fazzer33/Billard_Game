package at.fhv.sysarch.lab2.game;

public class Cue {
    private double startX;
    private double endX;
    private double startY;
    private double endY;
    private boolean dragged = false;

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    public boolean cueIsDragged(){
        return dragged;
    }

    public void setIsDragged(){
        dragged = !dragged;
        if (dragged == false) {
            startX = 0;
            startY = 0;
            endX = 0;
            endY = 0;
        }
    }

}
