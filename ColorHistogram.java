
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorHistogram {
    private int dBits;
    private ColorImage image;
    private double[] histogram;
    private int totalPixels;

    // Constructor for d-bit images
    public ColorHistogram(int d) {
        this.dBits = d;

    }
    // Constructor to load histogram from a file
    //This is geared in particular while we are reading from the database of images
    //with already computed values
    public ColorHistogram(String filename) {
        // Implementation to read the histogram from a file
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Skip the first line
            reader.readLine();

            // Initialize a list to store the histogram values
            List<Double> histogramList = new ArrayList<>();

            // Read the second line and subsequent lines
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line by spaces to get individual values
                String[] values = line.trim().split("\\s+");
                for (String value : values) {
                    if (!value.isEmpty()) {
                        double intValue = Double.parseDouble(value);
                        histogramList.add(intValue); // Add values to the list
                    }
                }
            }
/*            // Debugging: Print size of histogramList
            System.out.println("Size of histogramList: " + histogramList.size());*/

            // Convert the list to an array
            histogram = new double[histogramList.size()];
            for (int i = 0; i < histogramList.size(); i++) {
                histogram[i] = histogramList.get(i);
            }

/*            // Debugging: Print contents of histogram array
            System.out.println("Histogram array contents: " + Arrays.toString(histogram));*/

        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + filename, e);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException: " + e.getMessage());
        }
    }

    public void normalizeBaseHistogram(){//this is called after using the setter to setImage()
        double sum = 0;
        for (double value : histogram) {
            sum += value;
        }

        // Check to prevent division by zero.
        if (sum == 0) {
            return;
        }

        // Normalize each element in the histogram.
        for (int i = 0; i < histogram.length; i++) {
            histogram[i] =(histogram[i] / sum) ;
        }

    }

    // Associate an image with this histogram instance
    public void setImage(ColorImage image) {
        this.image = image;
        this.totalPixels = image.getHeight() * image.getWidth();
    }
    // Get the normalized histogram of the image
    public double[] getHistogram() {
        if (this.histogram == null && this.image != null) {
            computeHistogram();
        }
        return this.histogram;
    }

    // Compute the histogram
    public void computeHistogram() {
        int numBins = (int) Math.pow(2, dBits * 3); //Bins here refer to number of entries in the histogram
        this.histogram = new double[numBins];

        // Process each pixel to fill the histogram
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                int[] pixel = image.getReducedPixel(i , j ); // Assuming 1-indexed
                int index = (pixel[0] << (2 * dBits)) + (pixel[1] << dBits) + pixel[2];
                histogram[index]++;
            }
        }

        // Normalize the histogram so thaat the sum is equal to 1
        for (int i = 0; i < numBins; i++) {
            histogram[i] /= totalPixels;
        }
    }

    // Comparing with another histogram to see similarity of pictures
    public double compare(ColorHistogram hist) {
        double intersection = 0.0;
        for (int i = 0; i < this.histogram.length; i++) {
            intersection += Math.min( this.histogram[i], hist.histogram[i] );
        }
        return intersection;
    }

    // Saving the histogram to a file in the txt format
    public void saveColorHistogram(String filename) {
        try (  BufferedWriter writer = new BufferedWriter( new FileWriter(filename) )  ) {
            // Write the number of entries at the beginning of the histogram
            writer.write(histogram.length + "\n"); //then skipping to next line like in the format

            // Write histogram data
            for (int i = 0; i < histogram.length; i++) {
                writer.write(histogram[i] + (i < histogram.length - 1 ? " " : ""));
            }
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 }
