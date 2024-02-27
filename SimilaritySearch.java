

import java.io.*;
import java.util.*;

public class SimilaritySearch {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java SimilaritySearch <queryImageFilename> <datasetDirectory>");
            return;
        }

        String queryImageFilename = args[0];
        String datasetDirectory = args[1]; // Use the dataset directory from command-line argument

        //In the case that the user decides to put a jpg file instead of a ppm we will remove
        //the jpg and make the String name to a .ppm to fix this issue which won't break the beautiful algorithm
        if( queryImageFilename.endsWith("jpg") ){
            // Replace the ".jpg" extension with ".ppm"
            queryImageFilename = queryImageFilename.replaceFirst("\\.jpg$", ".ppm");
        }

        // Load and process the query image
        ColorImage queryImage = new ColorImage(queryImageFilename);
        queryImage.reduceColor(3); // using 3-bit color reduction

        ColorHistogram queryHistogram = new ColorHistogram(3);
        queryHistogram.setImage(queryImage);
        queryHistogram.computeHistogram();

        // Directory reading
        File folder = new File(datasetDirectory);
        File[] listOfFiles = folder.listFiles();

        // Map to store image filenames and their similarity scores
        Map<String, Double> similarityScores = new HashMap<>();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().endsWith(".txt")) { // Check for .txt files only
                    String filename = file.getName();
                    ColorHistogram datasetHistogram = new ColorHistogram(datasetDirectory + File.separator + filename);
                    datasetHistogram.normalizeBaseHistogram(); // Normalizing

                    double score = queryHistogram.compare(datasetHistogram);
                    similarityScores.put(filename, score);
                }
            }
        } else {
            System.out.println("The directory does not exist or is not a directory.");
            return;
        }

        // Sort the results based on similarity score in descending order
        List<Map.Entry<String, Double>> list = new ArrayList<>(similarityScores.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // Output the top 5 similar images
        System.out.println("Top 5 similar images:");
        for (int i = 0; i < Math.min(5, list.size()); i++) {
            System.out.println(list.get(i).getKey() + " - Score: " + list.get(i).getValue());
        }
    }
}


/*

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimilaritySearch {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java SimilaritySearch <queryImageFilename> <datasetDirectory>");
            return;
        }

        String queryImageFilename = args[0];
        String datasetDirectory = args[1];

        // Convert .jpg filename to .ppm if necessary
        if (queryImageFilename.endsWith(".jpg")) {
            queryImageFilename = queryImageFilename.substring(0, queryImageFilename.length() - 4) + ".ppm";
        }

        try {
            // Load and process the query image
            ColorImage queryImage = new ColorImage(queryImageFilename);
            queryImage.reduceColor(3); // Perform color reduction

            ColorHistogram queryHistogram = new ColorHistogram(3);
            queryHistogram.setImage(queryImage);
            queryHistogram.computeHistogram();
            queryHistogram.normalizeBaseHistogram(); // Ensure histogram is normalized

            File datasetDir = new File(datasetDirectory);
            File[] histogramFiles = datasetDir.listFiles((dir, name) -> name.endsWith(".txt"));

            if (histogramFiles == null) {
                System.out.println("No histogram files found.");
                return;
            }

            Map<String, Double> similarityScores = new HashMap<>();
            for (File file : histogramFiles) {
                ColorHistogram histogram = new ColorHistogram(file.getAbsolutePath());
                histogram.normalizeBaseHistogram(); // Normalize histogram from file
                double score = queryHistogram.compare(histogram);
                similarityScores.put(file.getName(), score);
            }

            // Sort similarity scores
            List<Map.Entry<String, Double>> sortedScores = new ArrayList<>(similarityScores.entrySet());
            Collections.sort(sortedScores, new Comparator<Map.Entry<String, Double>>() {
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                    return o2.getValue().compareTo(o1.getValue()); // Descending order
                }
            });

            // Display top 5 similar images
            System.out.println("Top 5 similar images:");
            for (int i = 0; i < Math.min(5, sortedScores.size()); i++) {
                System.out.println(sortedScores.get(i).getKey() + " - Score: " + sortedScores.get(i).getValue());
            }

        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }
}
*/
