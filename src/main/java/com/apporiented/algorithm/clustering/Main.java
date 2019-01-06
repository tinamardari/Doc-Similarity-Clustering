package com.apporiented.algorithm.clustering;

import javax.swing.*;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(() -> {
            ClusteringForm form = new ClusteringForm();
            form.setVisible(true);
        });
    }
}
