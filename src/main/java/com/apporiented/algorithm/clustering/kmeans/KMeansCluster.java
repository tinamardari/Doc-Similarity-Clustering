package com.apporiented.algorithm.clustering.kmeans;

import java.util.ArrayList;

public class KMeansCluster {
    private Point clusterCentroidCoords;
    private ArrayList<Point> clusterPointsCoords = new ArrayList<>();

    public void setClusterCentroidCoords(Point clusterCentroidCoords) {
        this.clusterCentroidCoords = clusterCentroidCoords;
    }

    public void addClusterPointCoord(Point point) {
        clusterPointsCoords.add(point);
    }

    public Point getClusterCentroidCoords() {
        return clusterCentroidCoords;
    }

    public double[][] getClusterPointsCoords() {
        double[][] coords = new double[clusterPointsCoords.size()][2];

        for (int i = 0; i < clusterPointsCoords.size(); i++) {
            Point actualCoord = clusterPointsCoords.get(i);
            coords[i][0] = actualCoord.getPointX();
            coords[i][1] = actualCoord.getPointY();
        }
        return coords;
    }

    @Override
    public String toString() {
        return "KMeansCluster{" +
                "clusterCentroidCoords=" + clusterCentroidCoords +
                ", clusterPointsCoords=" + clusterPointsCoords +
                '}' +
                '\n';
    }
}
