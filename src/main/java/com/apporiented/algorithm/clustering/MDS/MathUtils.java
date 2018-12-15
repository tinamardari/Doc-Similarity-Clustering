package com.apporiented.algorithm.clustering.MDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MathUtils {
    public static List<Double> findMaxPos(double[] vector){
        double max = Double.MIN_NORMAL;
        double maxPos = 0;
        //Find the first two greater values and its vectors
        for (int i = 0; i < vector.length; i++) {
            if(vector[i] > max){
                max =  vector[i];
                maxPos = i;
            }
        }
       /* System.out.println("MAX FOUND = "+ max);
        System.out.println("MAX POSITION = "+ maxPos);*/
        return Arrays.asList(max, maxPos);
    }

    public  static void printVector(double[] vector){
        for (int i = 0; i < vector.length; i++) {
            System.out.println(vector[i] + " ");
        }
    }
    public  static void printVector(int[] vector){
        for (int i = 0; i < vector.length; i++) {
            System.out.println(vector[i] + " ");
        }
    }

    public static double[][] powMatrix(double[][] initial){
        double powInital[][] = new double[ initial.length][initial[0].length];
        for(int i =0; i < initial.length; i++){
            for (int j=0; j <initial[i].length; j++){
                powInital[i][j] = initial[i][j] * initial[i][j];
            }
        }
        return powInital;
    }

    public static int[][] copyFromDoubleArray(double[][] source) {
        int[][] dest = new int[source.length][source.length];
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source.length; j++) {
                dest[i][j] = (int) (source[i][j] * 100);
            }
        }
        return dest;
    }

    public static double[] toPrimitiveDouble(ArrayList<Double> doubles){
        double[] target = new double[doubles.size()];
        for (int i = 0; i < target.length; i++) {
            target[i] = doubles.get(i);  // java 1.4 style
        }
        return target;
    }


    public static double findMax(double[] vector) {
        Double max = Double.MIN_NORMAL;
        //Find the first two greater values and its vectors
        for (int i = 0; i < vector.length; i++) {
            if(vector[i] > max){
                max =  vector[i];
            }
        }
        return max;
    }

    public static double[] convertColumnToVector(int columnIndex, double[][] points){
        double[] row = new double[points.length];
        for(int i =0;i < points.length; i++){
            row[i]=(points[i][columnIndex]);
        }
        return row;
    }
}
