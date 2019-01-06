package com.apporiented.algorithm.clustering;

import com.apporiented.algorithm.clustering.MDS.MDSAlgorithm;
import com.apporiented.algorithm.clustering.clustering.*;
import com.apporiented.algorithm.clustering.similarity.Corpus;
import com.apporiented.algorithm.clustering.similarity.Document;
import com.apporiented.algorithm.clustering.similarity.Similarity;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import smile.clustering.KMeans;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Main {
    private static String PATH = "C:\\Users\\admin\\Desktop\\Master\\clustering\\fisiere\\";

    public static void main(String[] args) {

        //Declare documents
        String[] files = new String[]{"test1.txt", "test2.txt", "test3.txt", "test4.txt"};

        //Create corpus
        Corpus corpus = new Corpus(PATH, files, true);

        //Find similarity between documents
        double[][] similarities = new double[files.length][];

        for (int i = 0; i < files.length; i++) {
            similarities[i] = calculateSimilarities(corpus, files[i]);
        }

        System.out.println("Similaritatile dintre fisiere");
        Utils.printStrings(files);
        Utils.printMatrix(files, similarities);

        System.out.println();
        System.out.println("Distantele dintre fisiere");
        //Convert it to dinstances aproach
        for (int i = 0; i < similarities.length; i++) {
            for (int j = 0; j < similarities.length; j++) {
                similarities[i][j] = Utils.round(1 - similarities[i][j], 2);
            }
        }
        Utils.printStrings(files);
        Utils.printMatrix(files, similarities);

        //Hierarchical Agglomerative Clustering
        dendogram(similarities, files, new CompleteLinkageStrategy());
        dendogram(similarities, files, new SingleLinkageStrategy());
        dendogram(similarities, files, new AverageLinkageStrategy());

       //Convert distance matrix to coordinates matrix
        double[][] points = MDSAlgorithm.run(similarities);
        drawDistancesPlot(points, files);

        //K-Meants Clustering
        kmeansMethod(points);
    }

    private static void drawDistancesPlot(double[][] points, String[] files) {
        XYChart chart = new XYChartBuilder()
                .width(600).height(500)
                .title("Gaussian Blobs")
                .xAxisTitle("X")
                .yAxisTitle("Y")
                .build();
        for(int i = 0; i< points.length; i++){
            chart.addSeries(files[i], Arrays.asList(points[i][0]), Arrays.asList(points[i][1]));
        }
        JFrame jFrame = new SwingWrapper(chart).displayChart();
        jFrame.setTitle("Coordinates Matrix");
    }

    private static void kmeansMethod(double[][] points) {
        //Smile library ? still confusing about it
        KMeans kMeans = new KMeans(points,2,10,1);
        kMeans.distortion();
        int[] clusterLabel = kMeans.getClusterLabel();
    }

    public static void dendogram(double[][] similarities, String[] filenames, LinkageStrategy linkageStrategy) {
        JFrame frame = new JFrame();
        frame.setSize(400, 300);
        frame.setLocation(400, 300);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel content = new JPanel();
        DendrogramPanel dp = new DendrogramPanel();

        frame.setContentPane(content);
        frame.setTitle(linkageStrategy.getStrategyTitle());
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
        Cluster cluster = alg.performClustering(similarities, filenames, linkageStrategy);
        cluster.toConsole(0);

        dp.setModel(cluster);
        frame.setVisible(true);
    }

    private static double[] calculateSimilarities(Corpus corpus, String file) {
        //Create similarity object witch needs corpus
        Similarity similarity = new Similarity(corpus, new Document(PATH, file));

        double[] cosSimilarityVector = similarity.getCosSimilarityVector();
        return cosSimilarityVector;
    }


}
