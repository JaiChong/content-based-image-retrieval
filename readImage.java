/*
 * CSS 484 Project 1
 * Last edited by Jaimi and Pranshu on 10/09/2023.
 */

import java.awt.image.BufferedImage;
// import java.lang.Object.*;
// import javax.swing.*;
import java.io.*;
// import java.util.*;
import javax.imageio.ImageIO;
import java.awt.*;


public class readImage {

  int imageCount = 1;
  int intensityBins [] = new int [26];
  int intensityMatrix [][] = new int[100][26];
  int colorCodeBins [] = new int [65];
  int colorCodeMatrix [][] = new int[100][65];

  /* Each image is retrieved from the file.  The height and width are found for the image and the getIntensity and
   * getColorCode methods are called.
   */
  public readImage()
  {
    while(imageCount < 101){
      try
      {
        // Reads individual image files
        BufferedImage image = ImageIO.read(getClass().getResource("images/" + imageCount + ".jpg"));
        
        // Applies algorithms to individual images
        getIntensity(image, image.getHeight(), image.getWidth());
        getColorCode(image, image.getHeight(), image.getWidth());
        imageCount++;
      } 
      catch (IOException e)
      {
        System.out.println("Error occurred when reading the file.");
        e.printStackTrace();
      }
    }
    
    // Writes resulting algorithm values to text files
    writeIntensity();
    writeColorCode(); 
  }
  
  // intensity method 
  public void getIntensity(BufferedImage image, int height, int width){
    // Stores image size in intensityBins[0]
    intensityBins[0] = height * width;

    // Obtains the RGB values from each pixel and then translates those values into intensity
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        Color color = new Color(image.getRGB(i, j));

        double intensity = 0.299*(color.getRed()) + 0.587*(color.getGreen()) + 0.114*(color.getBlue());
        int binIndex = (int)(intensity/10);
        if(binIndex >= 25){
          binIndex = 24;
        }
        //intensity Values are being stored in the intensity bins, accounting for image size occupying [0]
        intensityBins[binIndex + 1]++;

      }
    }
    //intensity matrix is updated with the bin being added to the current image index
    intensityMatrix[imageCount - 1] = intensityBins;
    intensityBins = new int [26];
  }
  
  // color code method
  public void getColorCode(BufferedImage image, int height, int width){
    // Stores image size in colorCodeBins[0]
    colorCodeBins[0] = height * width;

    // Obtains the 6-bit RGB color codes, stores in coorCodeMatrix, and refreshes colorCodeBins
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        // Obtains 24-bit RGB color code within 64-bit BufferedImage.getRGB() return value
        int color = image.getRGB(i, j);

        // Concatenates 2 most significant bits from each 8-bit color channel code
        int r2Bits = (color & 0xC00000) >> 18;
        int g2Bits = (color & 0xC000)   >> 12;
        int b2Bits = (color & 0xC0)     >> 6;
        color = r2Bits + g2Bits + b2Bits;

        // Stores concatenation in colorCodeBins, accounting for image size occupying [0]
        colorCodeBins[color + 1]++;
      }
    }
    colorCodeMatrix[imageCount - 1] = colorCodeBins;
    colorCodeBins = new int [65];
  }
  
  // This method writes the contents of the intensity matrix to a file named intensity.txt.
  public void writeIntensity(){
    try
    {
      // Creates a new text file intensity.txt, or replaces it if it already exists
      File text = new File("intensity.txt");
      if (!text.createNewFile()) {
        text.delete();
        text = new File("intensity.txt");
      }

      // Outputs the intensity bin values stored in intensityMatrix[][] to intensity.txt.
      //  1 image/row, 1 bin/column.
      FileWriter writer = new FileWriter("intensity.txt");
      for (int i = 0; i < intensityMatrix.length; i++) {
        for (int j = 0; j < intensityMatrix[i].length; j++) {
          writer.write(intensityMatrix[i][j] + " ");
        }
        writer.write("\n");
      }
      writer.close();
    }
    catch (IOException e)
    {
      System.out.println("An I/O error occurred while creating intensity.txt.");
      e.printStackTrace();
    }
  }
  
  // This method writes the contents of the colorCode matrix to a file called colorCodes.txt
  public void writeColorCode(){
    try
    {
      // Creates a new text file colorCodes.txt, or replaces it if it already exists
      File text = new File("colorCodes.txt");
      if (!text.createNewFile()) {
        text.delete();
        text = new File("colorCodes.txt");
      }
      
      // Outputs the color code bin values stored in colorCodeMatrix[][] to colorCodes.txt.
      //  1 image/row, 1 bin/column.
      FileWriter writer = new FileWriter("colorCodes.txt");
      for (int i = 0; i < colorCodeMatrix.length; i++) {
        for (int j = 0; j < colorCodeMatrix[i].length; j++) {
          writer.write(colorCodeMatrix[i][j] + " ");
        }
        writer.write("\n");
      }
      writer.close();
    }
    catch (IOException e)
    {
      System.out.println("An I/O error occurred while creating colorCodes.txt.");
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args)
  {
    new readImage();
  }


}
