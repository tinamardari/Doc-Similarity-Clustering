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
import org.knowm.xchart.style.markers.SeriesMarkers;
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
    private JButton plotButton;
    private JButton similarityMatrixButton;
    private JTextArea textArea1;
    private JScrollPane scrollablePane;
    private JScrollPane selectedFilesScrollPanel;

    public String[] fileNames;
    public String path;
    double[][] similarities;
    double[][] points;
    boolean shouldHideMatrix = false;

    public ClusteringForm() {
        setupFrame();

        chooseFilesButton.addActionListener(e -> onChooseFileClicked());
        doneButton.addActionListener(e -> onDoneClicked());
        hierarhicalClusteringButton.addActionListener(e -> onHierarhicalClicked());
        kmeansButton.addActionListener(e -> onKmeansClicked());
        plotButton.addActionListener(e -> drawPointsPlot(points, fileNames));
        similarityMatrixButton.addActionListener(e -> {
            onSimilarityStateChanged();
        });
    }

    private void onSimilarityStateChanged() {
        scrollablePane.setVisible(!shouldHideMatrix);
        revalidate();
        shouldHideMatrix = !shouldHideMatrix;
        similarityMatrixButton.setText(shouldHideMatrix ? "Hide Similarity Matrix" : "Show Similarity Matrix");
        setPreferredSize(new Dimension(500, shouldHideMatrix ? 500 : 350));
        pack();
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
            selectedFilesScrollPanel.setVisible(true);
            doneButton.setEnabled(true);
            revalidate();
            pack();

        } else{
            hierarhicalPanel.setVisible(false);
            kmeansPanel.setVisible(false);
            plotButton.setVisible(false);
            similarityMatrixButton.setVisible(false);
            //textArea1.setVisible(false);
            scrollablePane.setVisible(false);
            pack();
        }
    }

    private void onKmeansClicked() {
        int kValue = Integer.parseInt(kValueEditText.getText());
        if (kValue < 2 || kValue > fileNames.length){
            JOptionPane.showMessageDialog(ClusteringForm.this, "You should select  k >= 2 and k <= "+fileNames.length);
            return;
        }

        //K-Meants Clustering
        kmeansClustering(points,kValue);
    }

    private void onHierarhicalClicked() {
        if (singleLinkageStrategyCheckBox.isSelected()){
            drawDendogram(similarities, fileNames, new SingleLinkageStrategy(),0);
        }
        if (completedLinkageStrategyCheckBox.isSelected()){
            drawDendogram(similarities, fileNames, new CompleteLinkageStrategy(),1);
        }
        if (averageLinkageStrategyCheckBox.isSelected()){
            drawDendogram(similarities, fileNames, new AverageLinkageStrategy(),2);
        }
    }

    private void onDoneClicked() {
        //Declare documents
        //String[] files = new String[]{"test1.txt", "test2.txt", "test3.txt", "test4.txt", "test5.txt"};
        setPreferredSize(new Dimension(500, 350));
        pack();

        if(fileNames == null || fileNames.length < 2){
            JOptionPane.showMessageDialog(ClusteringForm.this, "You should select at least 2 files");
            return;
        }
        hierarhicalPanel.setVisible(true);
        kmeansPanel.setVisible(true);
        plotButton.setVisible(true);
        similarityMatrixButton.setVisible(true);


        //Create corpus
        Corpus corpus = new Corpus(path, fileNames, true);

        //Find similarity between documents
        similarities = new double[fileNames.length][];

        for (int i = 0; i < fileNames.length; i++) {
            similarities[i] = calculateSimilarities(corpus, fileNames[i]);
        }

        System.out.println("Similaritatile dintre fisiere");
        StringBuilder filesHeader = Utils.printStrings(fileNames);
        StringBuilder similaritiesTextMatrix = Utils.printMatrix(fileNames, similarities);

        System.out.println();
        System.out.println("Distantele dintre fisiere");
        //Convert it to dinstances aproach
        for (int i = 0; i < similarities.length; i++) {
            for (int j = 0; j < similarities.length; j++) {
                similarities[i][j] = Utils.round(1 - similarities[i][j], 2);
            }
        }
        Utils.printStrings(fileNames);
        StringBuilder distancesTextMatrix = Utils.printMatrix(fileNames, similarities);

        textArea1.setText(String.valueOf(
                corpus.getCorpusStats() + "\nSimilarities\n" + filesHeader + similaritiesTextMatrix + "\n\nDistances\n"+filesHeader + distancesTextMatrix));

        //Convert distance matrix to coordinates matrix
        points = MDSAlgorithm.run(similarities);
    }

    private void setupFrame() {
        setTitle("Documents Clustering");
        setContentPane(panelMain);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(500, 120));
        setLocation(50,50);

        selectedFilesTitle.setVisible(false);
        selectedFilesScrollPanel.setVisible(false);
        hierarhicalPanel.setVisible(false);
        plotButton.setVisible(false);
        kmeansPanel.setVisible(false);
        similarityMatrixButton.setVisible(false);
        //textArea1.setVisible(false);
        scrollablePane.setVisible(false);
        pack();
    }

    private double[] calculateSimilarities(Corpus corpus, String file) {
        //Create similarity object witch needs corpus
        Similarity similarity = new Similarity(corpus, new Document(path, file));

        double[] cosSimilarityVector = similarity.getCosSimilarityVector();
        return cosSimilarityVector;
    }

    private void kmeansClustering(double[][] points, int k) {
        KMeans kMeans = new KMeans(points,k);
        List<KMeansCluster> clusters = KMeansClustering.run(points, kMeans.centroids());
        drawKmeansClusters(clusters, k);
    }

    public void drawKmeansClusters(List<KMeansCluster> clusters, int k){
        int a = 0;
        XYChart chart = new XYChartBuilder()
                .width(500).height(400)
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
            jFrame.setLocation(600,50);
            jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        });
        t.start();

    }

    private void drawPointsPlot(double[][] points, String[] files) {
        XYChart chart = new XYChartBuilder()
                .width(500).height(400)
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
                jFrame.setLocation(600,50);
                jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            }

        });
        t.start();
    }

    public void drawDendogram(double[][] similarities, String[] filenames, LinkageStrategy linkageStrategy, int position) {
        JFrame frame = new JFrame();
        frame.setSize(400, 300);
        frame.setLocation(50 + 400*position, 400);

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

