


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ColorImage {
    public int width;
    public int height;
    public int depth;

    private String name;
    private int[][][] imageMatrix; // To store the image matrix
    private int[][][] reducedColorMatrix; // To store the reduced color matrix

    public ColorImage(String filename) {
        this.name = filename;
        this.width = findWidth(filename);
        this.height = findHeight(filename);
        this.depth = findBitsPerPixel(filename);

        imageMatrix = new int[height][width][3];
        loadImageMatrix();
        // reducedColorMatrix will be populated in reduceColor method
    }

    public int[] getPixel(int i, int j) {
        if (i >= 0 && i <= height && j >= 0 && j <= width) {
            return imageMatrix[i][j];
        } else {
            throw new IllegalArgumentException("Invalid pixel coordinates");
        }
    }

    public void reduceColor(int d) {
        reducedColorMatrix = new int[height][width][3]; // Instantiate the reduceColorMatrix

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                for (int k = 0; k < 3; k++) {
                    // Apply right bit shift
                    reducedColorMatrix[i][j][k] = imageMatrix[i][j][k] >> (8 - d);
                }
            }
        }
    }


    public int[] getReducedPixel(int i, int j) { //column i row j
        if (reducedColorMatrix == null) {
            throw new IllegalStateException("Reduced color matrix has not been initialized.");
        }

        if (i >= 0 && i <= height && j >= 0 && j <= width) {
            return reducedColorMatrix[i][j];
        } else {
            throw new IllegalArgumentException("Invalid pixel coordinates");
        }
    }

    private void loadImageMatrix() {
        try (BufferedReader br = new BufferedReader(new FileReader(name))) {
            // Skip header lines
            br.readLine(); // P3 header which we skip
            br.readLine(); // Comment which we skip
            br.readLine(); // Dimensions which we skip
            br.readLine(); // Max color value which we again skip

            String line;
            int row = 0;
            List<Integer> rgbList = new ArrayList<>();

            while ((line = br.readLine()) != null && row < height) {
                String[] currentLineValues = line.trim().split("\\s+");
                for (String value : currentLineValues) {
                    rgbList.add(Integer.parseInt(value));

                    // Check if we have collected enough values for a full row
                    if (rgbList.size() >= width * 3) {
                        // Process the collected values into the imageMatrix
                        for (int col = 0; col < width; col++) {
                            int r = rgbList.remove(0);
                            int g = rgbList.remove(0);
                            int b = rgbList.remove(0);
                            imageMatrix[row][col] = new int[]{r, g, b};
                        }
                        row++;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Methods for findWidth, findHeight, findBitsPerPixel not implemented here for brevity
    // Assuming these methods are implemented elsewhere or provided by the user
    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    public int getDepth(){
        return this.depth;
    }

    public String getFileNameUsed(){
        return this.name;
    }

    //Meethod used
    public int findWidth(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            // Skip the first two lines
            br.readLine(); // Skipping the format identifier (P3)
            br.readLine(); // Skip the comment line

            // The next line should contain width index 0 and height index 1
            String line = br.readLine();
            String[] dimensions = line.split("\\s+");
            return Integer.parseInt(dimensions[0] );
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // Return -1 for debugging purposes
        }
    }

    public int findHeight(String filename){
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            // Skip the first two lines
            br.readLine(); // Skip  P3
            br.readLine(); // Skip the comment line

            // The next line should contain width at index 0 and height at index 1
            String line = br.readLine();
            String[] dimensions = line.split("\\s+");
            return Integer.parseInt(dimensions[1]);
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // Return -1 for debugging
        }
    }
    //Method to Find the Max Value from the File
    public int findMaxValue(String filename){
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            // Skip the first two lines
            br.readLine(); // Skip the format identifier (P3)
            br.readLine(); // Skip the comment line
            br.readLine();//skip the line showing width and height

            // The next line should contain width at index 0 and height at index 1
            String line = br.readLine();
            String[] dimensions = line.split("\\s+");
            return Integer.parseInt(dimensions[0]) ;
        } catch (IOException e) {
            e.printStackTrace();
            return -1; // Return -1 for debugging purposes
        }
    }
    public int FindBitsPerChannel(String filename){
        int maxValue= findMaxValue(filename);
        //We will loop until the binary equivalent is right above max value
        //which would allow us to find the number of bits per channel
        int power=0;
        while(Math.pow(2,power) <= maxValue){
            power++;
        }
        return power;
    }

    //Multiplying by three to obtain the number of Bits per pixel which is depth
    public int findBitsPerPixel(String filename){
        return FindBitsPerChannel(filename) *3 ;
    }
}
