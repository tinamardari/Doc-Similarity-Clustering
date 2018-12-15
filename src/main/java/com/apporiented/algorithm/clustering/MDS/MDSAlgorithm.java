package com.apporiented.algorithm.clustering.MDS;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import java.util.Arrays;
import java.util.List;

public class MDSAlgorithm {

    static Matrix eigenValues = null;
    static Matrix eigenValuesSqrt = null;
    static Matrix eigenVectors = null;

    public static double[][] run(double[][] initial) {
        int N = initial.length;
        double helper[][] = /*{ { 0,1}, { -2,-3}};*/
                {{0.25, 0.25, 0.25, 0.25},
                        {0.25, 0.25, 0.25, 0.25},
                        {0.25, 0.25, 0.25, 0.25},
                        {0.25, 0.25, 0.25, 0.25}};

        Matrix P = Matrix.constructWithCopy(MathUtils.powMatrix(initial));
        Matrix J = Matrix.identity(4, 4).minus(Matrix.constructWithCopy(helper));
        Matrix B = J.times(-0.5).times(P).times(J);
        //Matrix A = new Matrix(B.getArrayCopy());
        //A = A.transpose().times(A);

        // compute the spectral decomposition
        EigenvalueDecomposition e = B.eig();
        Matrix V = e.getV();
        Matrix D = e.getD();

        System.out.print("P =");
        P.print(0, 3);
        System.out.print("J =");
        J.print(0, 3);
        System.out.print("B =");
        B.print(0, 3);
        System.out.print("D =");
        D.print(0, 4);
        System.out.print("V =");
        V.print(0, 4);

        first2LargestEigenVectors(e);

        System.out.print("eigenVectors =");
        eigenVectors.print(0, 4);

        System.out.print("eigenValues =");
        eigenValues.print(0, 4);

        Matrix X = eigenVectors.times(eigenValuesSqrt).times(-1);
        System.out.print("X =");
        X.print(1,4);

      /*  System.out.print("X sqrt =");
        Matrix.constructWithCopy(MathUtils.powMatrix(X.getArrayCopy())).print(1,4);*/


       // return   MathUtils.powMatrix(X.getArrayCopy());
        return  X.getArrayCopy();
    }

    private static void first2LargestEigenVectors(EigenvalueDecomposition eig) {
        eigenValues = Matrix.identity(2, 2);
        eigenValuesSqrt = Matrix.identity(2, 2);
        eigenVectors = Matrix.random(4, 2);

        double[] largestEigenValues = first2LargestEigenValuesPos(eig.getRealEigenvalues().clone()).get(0);
        double[] largestEigenValuesPos = first2LargestEigenValuesPos(eig.getRealEigenvalues().clone()).get(1);
        Matrix V = eig.getV();

        for (int i = 0; i < V.getRowDimension(); i++) {
            for (int j = 0; j < V.getColumnDimension(); j++) {
                for (int k = 0; k < largestEigenValuesPos.length; k++) {
                    if (j == largestEigenValuesPos[k]) {
                        eigenVectors.set(i, k, V.get(i, j));
                    }
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (i == j){
                    eigenValues.set(i,j, largestEigenValues[i]);
                    eigenValuesSqrt.set(i,j, Math.sqrt(largestEigenValues[i]));
                }
            }
        }
    }

    private static List<double[]> first2LargestEigenValuesPos(double[] realEigenvalues) {
        double[] maxPositions = new double[2];
        double[] maxValues = new double[2];

        double max = MathUtils.findMaxPos(realEigenvalues).get(0);
        double maxPos = MathUtils.findMaxPos(realEigenvalues).get(1);
        maxValues[0] = (max);
        maxPositions[0] = (maxPos);

        realEigenvalues[(int) maxPos] = Double.MIN_VALUE;
        max = MathUtils.findMaxPos(realEigenvalues).get(0);
        maxPos = MathUtils.findMaxPos(realEigenvalues).get(1);
        maxValues[1] = (max);
        maxPositions[1] = (maxPos);

        MathUtils.printVector(maxPositions);
        MathUtils.printVector(maxValues);

        return Arrays.asList(maxValues, maxPositions);
    }


    double mat[][] = /*{ { 0,1}, { -2,-3}};*/
            {{5035.0625, -1553.0625, 258.9375, -3740.938},
                    {-1553.0625, 507.8125, 5.3125, 1039.938},
                    {258.9375, 5.3125, 2206.8125, -2471.062},
                    {-3740.9375, 1039.9375, -2471.0625, 5172.062}};


}
