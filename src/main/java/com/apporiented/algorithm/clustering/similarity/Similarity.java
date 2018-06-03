package com.apporiented.algorithm.clustering.similarity;

import java.util.ArrayList;
import java.util.List;

public class Similarity {

    private static final int WORDS_HEADER = 19;
    private static final int DOCUMENTS_HEADER = 23;
    private static final int ALL_DOCUMENTS_HEADER = 20;

    private List<Document> allDocuments;
    private Document queryDocument;
    private List<String> allWords;
    private int numDocuments;
    private int numWords;
    private Corpus corpus;

    private int[][] freqMatrix;
    private float[][] tfMatrix;
    private int[] freqOfWordsInAllDocs;
    private float[] idfVector;
    private float[][] tfidfMatrix;

    private int[] freqOfQuery;
    private float[] tfOfQuery;
    private float[] tfIdfOfQuery;

    private float[] lenghtsOfDocs;
    private float[] similarityVector;
    private double[] cosSimilarityVector;

    public Similarity(Corpus corpus, Document queryDocument) {
        this.corpus = corpus;
        this.queryDocument = queryDocument;

        freqMatrix = corpus.getFreqMatrix();
        allWords = corpus.getAllWords();
        allDocuments = corpus.getDocuments();

        numDocuments = allDocuments.size();
        numWords = allWords.size();

        //Step 1. Create frequency matrix
        calculateTfMatrix();
        //Step 2. Create idf vector
        initFreqOfWordsInAllDocs();
        calculateIdfVector();
        //Step 3. Create idf-tf-idf matrix
        calculateTfIdf();
        //Step 4. Create freq, tf, tf-idf of query doc
        calculateTfIdfForQuery();
        //Step 5. Length of documents
        calculateLengthsOfDocs();
        //Step 4. Similariy with every doc
        calculateSimilarity();
        //Step 6. CosSim of docs
        calculateCosSimilarity();
    }

    private void calculateCosSimilarity() {
        int lastDocument = numDocuments;
        cosSimilarityVector = new double[numDocuments];
        for (int d = 0; d < numDocuments; d++) {
            cosSimilarityVector[d] = similarityVector[d] / (lenghtsOfDocs[d] * lenghtsOfDocs[lastDocument]);
        }
    }

    private void calculateSimilarity() {
        float sum;
        similarityVector = new float[numDocuments];

        for (int d = 0; d < numDocuments; d++) {
            sum = 0;
            for (int w = 0; w < numWords; w++) {
                sum = sum + (tfidfMatrix[d][w] * tfIdfOfQuery[w]);
            }
            similarityVector[d] = sum;
        }
    }

    private void calculateLengthsOfDocs() {
        float sum;
        lenghtsOfDocs = new float[numDocuments + ((queryDocument != null) ? 1 : 0)];

        //Length of documents from corpus
        for (int d = 0; d < numDocuments; d++) {
            sum = 0;
            for (int w = 0; w < numWords; w++) {
                sum += Math.pow(tfidfMatrix[d][w], 2);
            }
            lenghtsOfDocs[d] = (float) Math.sqrt(sum);
        }

        //Length of query doccument
        sum = 0;
        for (int w = 0; w < numWords; w++) {
            sum += Math.pow(tfIdfOfQuery[w], 2);
        }
        lenghtsOfDocs[lenghtsOfDocs.length - 1] = (float) Math.sqrt(sum);
    }

    private void calculateTfIdfForQuery() {
        int sum;
        //1. Parse the doc
        queryDocument.parse();

        //2. create Freq Of Query
        freqOfQuery = new int[numWords];
        for (int i = 0; i < numWords; i++) {
            freqOfQuery[i] = queryDocument.frequencyOfWord(allWords.get(i));
        }

        //3. create Tf Of Query
        tfOfQuery = new float[numWords];
        for (int w = 0; w < numWords; w++) {
            for (int d = 0; d < numDocuments; d++) {
                sum = 0;
                for (int k = 0; k < numDocuments; k++) {
                    sum += Math.pow(freqMatrix[k][w], 2);                        //TODO: ? - That's bad formula
                }
                sum += Math.pow(freqOfQuery[w], 2);
                tfOfQuery[w] = (float) (freqOfQuery[w] / Math.sqrt(sum));
            }
        }

        //4. Create Tf-Idf for query
        tfIdfOfQuery = new float[numWords];
        for (int w = 0; w < numWords; w++) {
            tfIdfOfQuery[w] = tfOfQuery[w] * idfVector[w];
        }

    }

    private void calculateTfMatrix() {
        tfMatrix = new float[numDocuments][numWords];
        int sum;
        for (int w = 0; w < numWords; w++) {
            for (int d = 0; d < numDocuments; d++) {
                sum = 0;
                for (int k = 0; k < numDocuments; k++) {
                    sum += freqMatrix[k][w];
                }
                tfMatrix[d][w] = (float) (freqMatrix[d][w] / Math.sqrt(sum));
            }
        }
    }

    private void initFreqOfWordsInAllDocs() {
        freqOfWordsInAllDocs = new int[numWords];
        int counterOfDocs;
        for (int j = 0; j < numWords; j++) {
            counterOfDocs = 0;
            for (int i = 0; i < numDocuments; i++) {
                if(freqMatrix[i][j] != 0){
                    counterOfDocs++;
                }
            }
            freqOfWordsInAllDocs[j] = counterOfDocs;
        }
    }

    private void calculateIdfVector() {
        idfVector = new float[numWords];
        for (int i = 0; i < numWords; i++) {
            idfVector[i] = (float) (Math.log((double) numDocuments / (double) freqOfWordsInAllDocs[i]) / Math.log(2));
        }
    }

    private void calculateTfIdf() {
        tfidfMatrix = new float[numDocuments][numWords];
        float idfValue;
        for (int i = 0; i < numWords; i++) {
            idfValue = idfVector[i];
            for (int j = 0; j < numDocuments; j++) {
                tfidfMatrix[j][i] = tfMatrix[j][i] * idfVector[i];
            }
        }
    }

    public void getFrequency() {
        printMatrix("FREQUENCY MATRIX", WORDS_HEADER, freqMatrix);
    }

    public void getTF() {
        printMatrix("TF MATRIX", WORDS_HEADER, tfMatrix);
    }

    public void getFreqOfWordsInAllDocs() {
        printVector("TOTAL FREQUENCY IN ALL DOCUMENTS", WORDS_HEADER, freqOfWordsInAllDocs);
    }

    public void getIDF() {
        printVector("IDF VECTOR", WORDS_HEADER, idfVector);
    }

    public void getTFIDF() {
        printMatrix("TF - IDF", WORDS_HEADER, tfidfMatrix);
    }

    public void getFrequencyOfQuery() {
        printVector("FREQUENCY FOR QUERY", WORDS_HEADER, freqOfQuery);
    }

    public void getTfOfQuery() {
        printVector("TF FOR QUERY", WORDS_HEADER, tfOfQuery);
    }

    public void getTFIDFOfQuery() {
        printVector("TF-IDF FOR QUERY", WORDS_HEADER, tfIdfOfQuery);
    }

    public void getLenghts() {
        printVector("DOCUMENTS' LENGTHS", ALL_DOCUMENTS_HEADER, lenghtsOfDocs);
    }

    public void getSimilarityVector() {
        printVector("DOCUMENTS' SIMILARITY", DOCUMENTS_HEADER, similarityVector);
    }

    public void getCosSimilarity() {
        printVector("COS SIMILARITY of "+queryDocument.getFileName(), DOCUMENTS_HEADER, cosSimilarityVector);
    }

    public double[] getCosSimilarityVector() {
        return cosSimilarityVector;
    }

    private void printVector(String title, int header, int[] vector) {
        StringBuilder finalRow = new StringBuilder();
        System.out.println("\n" + title);

        finalRow.append(String.format("%30s", ""));
        checkHeader(header);

        for (int num : vector) {
            finalRow.append(String.format("%30d", num));
        }
        System.out.println(finalRow);
    }

    private void printVector(String title, int header, float[] vector) {
        StringBuilder finalRow = new StringBuilder();
        System.out.println("\n" + title);

        finalRow.append(String.format("%30s", ""));
        checkHeader(header);

        for (float num : vector) {
            finalRow.append(String.format("%30.2f", num));
        }
        System.out.println(finalRow);
    }

    private void printVector(String title, int header, double[] vector) {
        StringBuilder finalRow = new StringBuilder();
        System.out.println("\n" + title);

        finalRow.append(String.format("%30s", ""));
        checkHeader(header);

        for (double num : vector) {
            finalRow.append(String.format("%30.2f", num));
        }
        System.out.println(finalRow);
    }

    private void printMatrix(String title, int header, int[][] freqMatrix) {
        StringBuilder finalRow;
        System.out.println("\n" + title);
        checkHeader(header);

        for (int i = 0; i < numDocuments; i++) {
            finalRow = new StringBuilder();
            finalRow.append(String.format("%30s", allDocuments.get(i).getFileName()));
            for (int j = 0; j < numWords; j++) {
                finalRow.append(String.format("%30d", freqMatrix[i][j]));
            }
            System.out.println(finalRow);
        }
    }

    private void printMatrix(String title, int header, float[][] matrix) {
        StringBuilder finalRow;
        System.out.println("\n" + title);
        checkHeader(header);

        for (int i = 0; i < numDocuments; i++) {
            finalRow = new StringBuilder();
            finalRow.append(String.format("%30s", allDocuments.get(i).getFileName()));
            for (int j = 0; j < numWords; j++) {
                finalRow.append(String.format("%30.2f", matrix[i][j]));
            }
            System.out.println(finalRow);
        }
    }

    private void checkHeader(int header) {
        if (header == WORDS_HEADER) {
            printAllWords();
        } else if (header == ALL_DOCUMENTS_HEADER) {
            printAllDocuments();
        } else if (header == DOCUMENTS_HEADER) {
            printDocuments();
        }
    }

    public void printAllWords() {
        printStrings(allWords);
    }

    public void printDocuments() {
        List<String> docsNames = new ArrayList<>();
        for (int i = 0; i < numDocuments; i++) {
            docsNames.add(allDocuments.get(i).getFileName());
        }
        printStrings(docsNames);
    }

    public void printAllDocuments() {
        List<String> docsNames = new ArrayList<>();
        for (int i = 0; i < numDocuments; i++) {
            docsNames.add(allDocuments.get(i).getFileName());
        }
        docsNames.add(queryDocument.getFileName());
        printStrings(docsNames);
    }

    public void printStrings(List<String> stringList) {
        StringBuilder finalString = new StringBuilder();
        finalString.append(String.format("%30s", ""));
        for (String str : stringList) {
            str = String.format("%30s", str);
            finalString.append(str);
        }
        System.out.println(finalString);
    }
}
