package com.apporiented.algorithm.clustering.kmeans;

public class Point {
    private double pointX;
    private double pointY;

    public Point(double pointX, double pointY) {
        this.pointX = pointX;
        this.pointY = pointY;
    }

    public double getPointX() {
        return pointX;
    }

    public double getPointY() {
        return pointY;
    }

    @Override
    public String toString() {
        return "Point{" +
                "pointX=" + pointX +
                ", pointY=" + pointY +
                '}';
    }
}