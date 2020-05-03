package ru.spbstu.icc.model;

import com.badlogic.gdx.math.Rectangle;

public class Drone {
    private Rectangle rectangle;
    private double angle;

    public Drone(Rectangle drone, double angle) {
        this.rectangle = drone;
        this.angle = angle;
    }

    public void setX(double x){
        this.rectangle.x = (float) x;
    }

    public void setY(double y){
        this.rectangle.y = (float) y;
    }

    public float getX() {
        return rectangle.x;
    }

    public float getY() {
        return rectangle.y;
    }

    public double getAngle() {
        return angle;
    }

    public Rectangle getRectangle(){
        return rectangle;
    }
}
