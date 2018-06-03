package com.apporiented.algorithm.clustering;

import com.apporiented.algorithm.clustering.clustering.Cluster;
import com.apporiented.algorithm.clustering.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.clustering.CompleteLinkageStrategy;
import com.apporiented.algorithm.clustering.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.similarity.Corpus;
import com.apporiented.algorithm.clustering.similarity.Document;
import com.apporiented.algorithm.clustering.similarity.Similarity;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;

import javax.swing.*;
import java.awt.*;

public class Main {
    private static String PATH = "C:\\Users\\admin\\Desktop\\clustering\\fisiere\\";

    private static double[] calculateSimilarities(Corpus corpus, String file) {

        //Create similarity object witch needs corpus
        Similarity similarity = new Similarity(corpus, new Document(PATH, file));

        //1. Check Word Matrix
       /* corpus.saveWordsFromDocsInFile();

        //2. Analyze results
        similarity.getIDF();

        similarity.getFrequency();
        similarity.getFrequencyOfQuery();


        similarity.getTF();
        similarity.getTfOfQuery();

        similarity.getTFIDF();
        similarity.getTFIDFOfQuery();

        similarity.getLenghts();

        similarity.getSimilarityVector();
        similarity.getCosSimilarityVector();*/

        double[] cosSimilarityVector = similarity.getCosSimilarityVector();
        return cosSimilarityVector;
    }

    public static void main(String[] args) {

        //Declare documents
        String[] files = new String[]{"test1.txt", "test2.txt", "test3.txt", "test4.txt"};

        //Create corpus
        Corpus corpus = new Corpus(PATH, files, true);

        //Find similarity between documents
        double[][] similarities = new double[files.length][];

        for (int i = 0; i < files.length; i++) {
            similarities[i] =  calculateSimilarities(corpus, files[i]);
        }

        System.out.println("Similaritatile dintre fisiere");
        printStrings(files);
        printMatrix(files,similarities);


        System.out.println();
        System.out.println("Distantele dintre fisiere");
        //Convert it to dinstances aproach
        for(int i =0; i < similarities.length; i++){
            for(int j = 0; j < similarities.length; j++){
                similarities[i][j] = round(1 - similarities[i][j],2);
            }
        }
        printStrings(files);
        printMatrix(files,similarities);


        dendogram(similarities, files);
    }

    public static void dendogram(double[][] similarities, String[] filenames){
        JFrame frame = new JFrame();
        frame.setSize(400, 300);
        frame.setLocation(400, 300);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel content = new JPanel();
        DendrogramPanel dp = new DendrogramPanel();

        frame.setContentPane(content);
        content.setBackground(Color.red);
        content.setLayout(new BorderLayout());
        content.add(dp, BorderLayout.CENTER);
        dp.setBackground(Color.WHITE);
        dp.setLineColor(Color.BLACK);
        dp.setScaleValueDecimals(0);
        dp.setScaleValueInterval(1);
        dp.setShowDistances(false);

        ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
        //You can chnage the strategy
        Cluster cluster = alg.performClustering(similarities, filenames, new CompleteLinkageStrategy());
        cluster.toConsole(0);

        dp.setModel(cluster);
        frame.setVisible(true);
    }


    static public void printVector(String title, int header, double[] vector) {
        StringBuilder finalRow = new StringBuilder();
        finalRow.append(String.format("%15s", ""));

        for (double num : vector) {
            finalRow.append(String.format("%15f", num));
        }
        System.out.println(finalRow);
    }

    private static void printStrings(String[] stringList) {
        StringBuilder finalString = new StringBuilder();
        finalString.append(String.format("%25s", ""));
        for (String str : stringList) {
            str = String.format("%25s", str);
            finalString.append(str);
        }
        System.out.println(finalString);
    }

    private  static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private static void printMatrix(String[] files,  double[][] matrix) {
        StringBuilder finalRow;

        for (int i = 0; i < files.length; i++) {
            finalRow = new StringBuilder();
            finalRow.append(String.format("%25s", files[i]));
            for (int j = 0; j < matrix[i].length; j++) {
                finalRow.append(String.format("%25.2f", matrix[i][j]));
            }
            System.out.println(finalRow);
        }
    }

}
