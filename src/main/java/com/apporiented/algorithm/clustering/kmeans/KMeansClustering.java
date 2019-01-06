package com.apporiented.algorithm.clustering.kmeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KMeansClustering {
    public static List<KMeansCluster> run(double[][] pointsArray, double[][] centroidsArray) {
        List<KMeansCluster> kMeansClusters = new ArrayList<>();
        HashMap<Integer, ArrayList<Point>> clusterHashMap = new HashMap<>();

        for (int i = 0; i < centroidsArray.length; i++) {
            clusterHashMap.put(i, new ArrayList<>());
        }

        for (int i = 0; i < pointsArray.length; i++) {
            double distance = Double.MAX_VALUE;
            int pointCentroidIndex = 0;

            for (int j = 0; j < centroidsArray.length; j++) {
                double distanceBetween = distanceBetween(new Point(pointsArray[i][0], pointsArray[i][1]),
                        new Point(centroidsArray[j][0], centroidsArray[j][1]));
                if (distanceBetween < distance) {
                    distance = distanceBetween;
                    pointCentroidIndex = j;
                }
            }

            ArrayList<Point> points = clusterHashMap.get(pointCentroidIndex);
            points.add(new Point(pointsArray[i][0], pointsArray[i][1]));
        }

        for (int key : clusterHashMap.keySet()) {
            KMeansCluster kMeansCluster = new KMeansCluster();
            kMeansCluster.setClusterCentroidCoords(new Point(
                    centroidsArray[key][0],
                    centroidsArray[key][1])
            );

            for (Point point : clusterHashMap.get(key)) {
                kMeansCluster.addClusterPointCoord(point);
            }
            kMeansClusters.add(kMeansCluster);
        }

        return kMeansClusters;
    }

    public static double distanceBetween(Point point, Point centroid) {
        return Math.sqrt(Math.pow(point.getPointX() - centroid.getPointX(), 2)
                + Math.pow(point.getPointY() - centroid.getPointY(), 2));
    }
}
