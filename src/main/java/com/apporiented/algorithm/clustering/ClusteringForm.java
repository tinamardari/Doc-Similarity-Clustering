package com.apporiented.algorithm.clustering;

import com.apporiented.algorithm.clustering.MDS.MDSAlgorithm;
import com.apporiented.algorithm.clustering.clustering.*;
import com.apporiented.algorithm.clustering.kmeans.KMeansCluster;
import com.apporiented.algorithm.clustering.kmeans.KMeansClustering;
import com.apporiented.algorithm.clustering.similarity.Corpus;
import com.apporiented.algorithm.clustering.similarity.Document;
import com.apporiented.algorithm.clustering.similarity.Similarity;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;
import com.sun.deploy.util.StringUtils;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import smile.clustering.KMeans;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ClusteringForm extends JFrame {
    private JButton chooseFilesButton;
    private JButton doneButton;
    public JPanel panelMain;
    private JLabel selectedFilesLabel;
    private JLabel selectedFilesTitle;
    private JCheckBox singleLinkageStrategyCheckBox;
    private JCheckBox completedLinkageStrategyCheckBox;
    private JCheckBox averageLinkageStrategyCheckBox;
    private JButton hierarhicalClusteringButton;
    private JPanel hierarhicalPanel;
    private JPanel kmeansPanel;
    private JTextField kValueEditText;
    private JButton kmeansButton;

    public String[] fileNames;
    public String path;
    double[][] similarities;

    public ClusteringForm() {
        setupFrame();

        chooseFilesButton.addActionListener(e -> onChooseFileClicked());
        doneButton.addActionListener(e -> onDoneClicked());
        hierarhicalClusteringButton.addActionListener(e -> onHierarhicalClicked());
        kmeansButton.addActionListener(e -> onKmeansClicked());
    }

    private void onChooseFileClicked() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileFilter(new FileNameExtensionFilter("TEXT FILES", "txt", "text"));
        chooser.showOpenDialog(chooseFilesButton);
        File[] files = chooser.getSelectedFiles();

        path = files[0].getParent() + "\\";
        fileNames = new String[files.length];
        for (int i = 0; i < files.length; i++){ fileNames[i] = files[i].getName();}
        String selectedFiles = StringUtils.join(Arrays.asList(fileNames), ", ");
        selectedFilesLabel.setText(selectedFiles);

        if (files.length >= 2){
            selectedFilesTitle.setVisible(true);
            selectedFilesLabel.setVisible(true);
            doneButton.setEnabled(true);
            pack();

        } else{
            hierarhicalPanel.setVisible(false);
            kmeansPanel.setVisible(false);
            pack();
        }
    }

    private void onKmeansClicked() {
        int kValue = Integer.parseInt(kValueEditText.getText());
        if (kValue < 2){
            JOptionPane.showMessageDialog(ClusteringForm.this, "You should select  k >= 2");
            return;
        }
        //Convert distance matrix to coordinates matrix
        double[][] points = MDSAlgorithm.run(similarities);
        //drawPointsPlot(points, files);

        //K-Meants Clustering
        kmeansClustering(points,kValue);
    }

    private void onHierarhicalClicked() {
        if (singleLinkageStrategyCheckBox.isSelected()){
            drawDendogram(similarities, fileNames, new SingleLinkageStrategy());
        }
        if (completedLinkageStrategyCheckBox.isSelected()){
            drawDendogram(similarities, fileNames, new CompleteLinkageStrategy());
        }
        if (averageLinkageStrategyCheckBox.isSelected()){
            drawDendogram(similarities, fileNames, new AverageLinkageStrategy());
        }
    }

    private void onDoneClicked() {
        //Declare documents
        //String[] files = new String[]{"test1.txt", "test2.txt", "test3.txt", "test4.txt", "test5.txt"};
        if(fileNames == null || fileNames.length < 2){
            JOptionPane.showMessageDialog(ClusteringForm.this, "You should select at least 2 files");
            return;
        }
        hierarhicalPanel.setVisible(true);
        kmeansPanel.setVisible(true);

        //Create corpus
        Corpus corpus = new Corpus(path, fileNames, true);

        //Find similarity between documents
        similarities = new double[fileNames.length][];

        for (int i = 0; i < fileNames.length; i++) {
            similarities[i] = calculateSimilarities(corpus, fileNames[i]);
        }

        System.out.println("Similaritatile dintre fisiere");
        Utils.printStrings(fileNames);
        Utils.printMatrix(fileNames, similarities);

        System.out.println();
        System.out.println("Distantele dintre fisiere");
        //Convert it to dinstances aproach
        for (int i = 0; i < similarities.length; i++) {
            for (int j = 0; j < similarities.length; j++) {
                similarities[i][j] = Utils.round(1 - similarities[i][j], 2);
            }
        }
        Utils.printStrings(fileNames);
        Utils.printMatrix(fileNames, similarities);
    }

    private void setupFrame() {
        setContentPane(panelMain);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(500, 300));
        setLocation(300,300);

        selectedFilesTitle.setVisible(false);
        selectedFilesLabel.setVisible(false);
        hierarhicalPanel.setVisible(false);
        kmeansPanel.setVisible(false);
        pack();
        setLocationRelativeTo(null);

    }

    private double[] calculateSimilarities(Corpus corpus, String file) {
        //Create similarity object witch needs corpus
        Similarity similarity = new Similarity(corpus, new Document(path, file));

        double[] cosSimilarityVector = similarity.getCosSimilarityVector();
        return cosSimilarityVector;
    }

    private void hierarchicalClustering(double[][] similarities, String[] files) {
        drawDendogram(similarities, files, new CompleteLinkageStrategy());
        drawDendogram(similarities, files, new SingleLinkageStrategy());
        drawDendogram(similarities, files, new AverageLinkageStrategy());
    }

    private void kmeansClustering(double[][] points, int k) {
        KMeans kMeans = new KMeans(points,k);
        List<KMeansCluster> clusters = KMeansClustering.run(points, kMeans.centroids());
        drawKmeansClusters(clusters, k);
    }

    public void drawKmeansClusters(List<KMeansCluster> clusters, int k){
        int a = 0;
        XYChart chart = new XYChartBuilder()
                .width(600).height(500)
                .title("Kmeans Clustering k = " + k)
                .xAxisTitle("X")
                .yAxisTitle("Y")
                .build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);

        for(KMeansCluster cluster: clusters){
            double[][] clusterPointsCoords = cluster.getClusterPointsCoords();
            double[] coordXs = new double[clusterPointsCoords.length];
            double[] coordYs = new double[clusterPointsCoords.length];

            for(int i =0; i < clusterPointsCoords.length; i++){
                coordXs[i] = clusterPointsCoords[i][0];
                coordYs[i] = clusterPointsCoords[i][1];
            }
            chart.addSeries("Cluster #"+ a++, coordXs, coordYs);
        }

        Thread t = new Thread(() -> {
            JFrame jFrame = new SwingWrapper(chart).displayChart();
            jFrame.setTitle("KMeans Clustering");
            jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        });
        t.start();

    }

    private void drawPointsPlot(double[][] points, String[] files) {
        XYChart chart = new XYChartBuilder()
                .width(600).height(500)
                .title("Coordinates")
                .xAxisTitle("X")
                .yAxisTitle("Y")
                .build();
        for(int i = 0; i< points.length; i++){
            chart.addSeries(files[i], Arrays.asList(points[i][0]), Arrays.asList(points[i][1]));
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                JFrame jFrame = new SwingWrapper(chart).displayChart();
                jFrame.setTitle("Coordinates Matrix");
            }

        });
        t.start();
    }

    public void drawDendogram(double[][] similarities, String[] filenames, LinkageStrategy linkageStrategy) {
        JFrame frame = new JFrame();
        frame.setSize(400, 300);
        frame.setLocation(400, 300);

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


}

