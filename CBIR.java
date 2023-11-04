/*
 * CSS 484 Assignment 2
 * Last edited by Jaimi and Pranshu on 11/3/2023.
 * 
 * =======================
 * == Table of Contents ==
 * =======================
 *          class CBIR {
 * 
 * 1.         INITIALIZATION (of GUI and Sorting Methods) [
 * 1.1.         Global Variables
 * 1.2.         main()
 * 1.3.         CBIR() {
 * 1.3.1.         Initialize GUI
 * 1.3.2.         Read Image Features
 * 1.3.3.         Boot GUI and Initialize Sorting Methods
 *              }
 * 1.4.         readIntensityFile()
 * 1.5.         readColorCodeFile()
 * 1.6.         initializeNormalizedMatrix()
 *            ]
 * 
 * 2.         UPDATES (to GUI and Relevance Feedback) [
 * 2.1.         displayFirstPage()
 * 2.2.         class IconButtonHandler
 * 2.3.         class nextPageHandler
 * 2.4.         class prevPageHandler
 * 2.5.         class sortMethodsDisplayHandler {
 * 2.5.1.         Update GUI Options
 * 2.5.2.         Update GUI Selected Image
 * 2.5.3.         Update GUI Image-Sorting
 *              }
 *            ]
 * 
 *          }
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.List;

import javax.swing.*;


// ================
// == class CBIR ==
// ================
//
public class CBIR extends JFrame {

  // =======================================================
  // == 1. INITIALIZATION (of GUI and Sorting Methods) =====
  // =======================================================
  //
  // ================================
  // == 1.1. Global Variables =======
  // ================================

  // Constants
  private final int imagesTotal = 100;
  private final int imagesPerPage = 20;

  // GUI
  private JButton [] button;                                  // Holds 100 ImageIcons from "root/images/"
  private JCheckBox [][] checkbox;                            // Holds a checkmark associated with each image, per image.
  private JPanel [] imageCards;                               // Holds 100 sets of a JLabel, an element of button[], and an element of checkbox[][]
  private int [] buttonOrder = new int [imagesTotal+1];       // Tracks order for button[] based on current sorting method
  private int [] imageSize = new int[imagesTotal+1];          // Holds ImageIcon resolutions
  private JPanel panelNorth;                                  // Holds JPanels panelNorthDisplayed, panelNorthSouth, and panelNorthSelected
  private JPanel panelNorthDisplayed;                         // Holds JPanel imageCardDisplayed
  private JPanel panelNorthSouth;                             // Holds JPanel panelNorthSouthSorts
  private JPanel panelNorthSouthSorts;                        // Holds JButtons sortByFilename, sortByIntensity, sortByColorCode, and sortByRelevanceFeedback
  private JPanel panelNorthSelected;                          // Holds JPanel imageCardSelected
  private JPanel panelCenterPics;                             // Holds 20 imageCards per page, cycled in and out on page change and sort method change.
  private JPanel panelSouthPages;                             // Holds JButtons pagePrev, pagePos, and pageNext
  private GridLayout gridNorth;                               // Organizes JPanel panelNorth
  private GridLayout gridNorthSouthSorts;                     // Organizes JPanel panelNorthSouthSorts 
  private GridLayout gridCenterPics;                          // Organizes JPanel panelCenterPics
  private GridLayout gridSouthPages;                          // Organizes JPanel panelSouthPages
  private JLabel photographLabelDisplayed = new JLabel();     // Holds ImageIcon under "DISPLAYING RESULTS FOR"
  private JLabel photographLabelSelected = new JLabel();      // Holds ImageIcon under "SELECTED FOR NEXT SORT"
  int sortMethod = 0;                                         // Tracks displayed sorting method for sortMethodsHandler
  JButton sortByFilename = new JButton();                     // Tracked as sortMethod = 0
  JButton sortByIntensity = new JButton();                    // Tracked as sortMethod = 1
  JButton sortByColorCode = new JButton();                    // Tracked as sortMethod = 2
  JButton sortByRelevanceFeedback = new JButton();            // Tracked as sortMethod = 3
  JButton pagePrev = new JButton();                           // Decrements page of displayed results
  JLabel pagePos = new JLabel("", JLabel.CENTER);        // Displays int pageNo
  JButton pageNext = new JButton();                           // Incremements page of displayed results
  
  // Matrices for holding values used by Sorting Methods
  private Map <Double, Integer> map;
  private int [][] intensityMatrix = new int [imagesTotal+1][25];
  private int [][] colorCodeMatrix = new int [imagesTotal+1][64];
  private double [][] normalizedMatrix = new double [imagesTotal+1][89];
  
  // Image storage
  int picNoSelected = 0;                    // Tracks image number of ImageIcon in photographLabelSelected
  int picNoDisplayed = 0;                   // Tracks image number of ImageIcon in photographLabelDisplayed
  JLabel titleSelected;                     // Displays picNoSelected above photographLabelSelected
  JLabel titleDisplayed;                    // Displays picNoDisplayed above photographLabelDisplayed
  JPanel imageCardSelected = new JPanel();  // Holds titleSelected and photographLabelSelected
  JPanel imageCardDisplayed = new JPanel(); // Holds titleDisplayed and photographLabelDisplayed
  int imageCount = 1;                       // Tracks image number of last displayed ImageIcon on page
  int pageNo = 1;                           // Tracks page position of displayed results


  // ======================
  // == 1.2. main() =======
  // ======================
  //
  public static void main(String args[]) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        CBIR app = new CBIR();
        app.setVisible(true);
      }
    });
  }


  // ======================
  // == 1.3. CBIR() =======
  // ======================
  //
  public CBIR() {
    
    // ==================================
    // == 1.3.1. Initialize GUI =========
    // ==================================
    
    // Change window settings
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("Image Sorter: Results sorted by FILENAME");
    
    // Initialize JPanels and GridLayouts
    panelNorth = new JPanel();
    panelNorthDisplayed = new JPanel();
    panelNorthSouth = new JPanel();
    panelNorthSouthSorts = new JPanel();
    panelNorthSelected = new JPanel();
    panelCenterPics = new JPanel();
    panelSouthPages = new JPanel();
    gridNorth = new GridLayout(1, 3, 5, 5);
    gridNorthSouthSorts = new GridLayout(4, 1, 5, 5);
    gridCenterPics = new GridLayout(4, 5, 5, 5);
    gridSouthPages = new GridLayout(1, 3, 5, 5);
    
    // Builds the window
    setLayout(new BorderLayout());
    add(panelNorth, BorderLayout.NORTH);
    add(panelCenterPics, BorderLayout.CENTER);
    add(panelSouthPages, BorderLayout.SOUTH);
    panelNorth.setLayout(gridNorth);
    panelNorth.add(panelNorthDisplayed);
    panelNorth.add(panelNorthSouth);
    panelNorth.add(panelNorthSelected);
    panelNorthDisplayed.setLayout(new BorderLayout());
    panelNorthSouth.setLayout(new BorderLayout());
    panelNorthSouth.add(panelNorthSouthSorts, BorderLayout.SOUTH);
    panelNorthSouthSorts.setLayout(gridNorthSouthSorts);
    panelNorthSelected.setLayout(new BorderLayout());
    panelCenterPics.setLayout(gridCenterPics);
    panelSouthPages.setLayout(gridSouthPages);

    // Builds panelNorthDisplayed and panelNorthSeleected
    titleDisplayed = new JLabel("DISPLAYED RESULTS FOR: (select a sort method!)");
    titleSelected = new JLabel("SELECTED FOR NEXT SORT: (select an image!)");
    titleDisplayed.setHorizontalAlignment(JLabel.CENTER);
    titleSelected.setHorizontalAlignment(JLabel.CENTER);
    imageCardDisplayed.setLayout(new BorderLayout());
    imageCardSelected.setLayout(new BorderLayout());
    imageCardDisplayed.add(titleDisplayed, BorderLayout.SOUTH);
    imageCardSelected.add(titleSelected, BorderLayout.NORTH);
    panelNorthDisplayed.add(imageCardDisplayed, BorderLayout.NORTH);
    panelNorthSelected.add(imageCardSelected, BorderLayout.NORTH);

    // Builds panelNorthSouthSorts
    sortByFilename.setText("Sort by: FILENAME");
    sortByIntensity.setText("Sort by: INTENSITY");
    sortByColorCode.setText("Sort by: COLORCODE");
    sortByRelevanceFeedback.setText("Sort by: INTENSITY + COLOR CODE");
    sortByFilename.setEnabled(false);
    sortByIntensity.setEnabled(false);
    sortByColorCode.setEnabled(false);
    sortByRelevanceFeedback.setEnabled(false);
    sortByFilename.setToolTipText("Display results for a different sorting method first!");
    sortByIntensity.setToolTipText("Select an image first!");
    sortByColorCode.setToolTipText("Select an image first!");
    sortByRelevanceFeedback.setToolTipText("Select an image first!");
    sortByFilename.addActionListener(new sortMethodsHandler(0));
    sortByIntensity.addActionListener(new sortMethodsHandler(1));
    sortByColorCode.addActionListener(new sortMethodsHandler(2));
    sortByRelevanceFeedback.addActionListener(new sortMethodsHandler(3));
    panelNorthSouthSorts.add(sortByFilename);
    panelNorthSouthSorts.add(sortByIntensity);
    panelNorthSouthSorts.add(sortByColorCode);
    panelNorthSouthSorts.add(sortByRelevanceFeedback);
    
    // Builds panelSouthPages
    pagePrev.setText("Previous Page");
    pageNext.setText("Next Page");
    pagePrev.addActionListener(new previousPageHandler());
    pageNext.addActionListener(new nextPageHandler());
    pagePrev.setEnabled(false);
    panelSouthPages.add(pagePrev);
    panelSouthPages.add(pagePos);
    panelSouthPages.add(pageNext);
    
    // Set the window size and centers it on the screen
    setSize(1280, 960);
    setLocationRelativeTo(null);


    // =======================================
    // == 1.3.2. Read Image Features =========
    // =======================================

    // Constructs a readImage to make new intensity.txt and colorCode.txt files
    new readImage();

    /* This for loop goes through the images in the database and stores them as
     * icons, adds the images to JButtons and then to the JButton array,
     * initializes JCheckBoxes to add to the JCheckbox array, and initializes
     * imageCards to hold labels and buttons (checkboxes are added in
     * displayFirstPage) to add to the JPanels imageCards array.
     */
    button = new JButton[imagesTotal+1];
    checkbox = new JCheckBox[imagesTotal+1][imagesTotal+1];
    imageCards = new JPanel[imagesTotal+1];
    
    for (int i = 1; i < imagesTotal+1; i++) {
      ImageIcon icon;
      icon = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));

      if(icon != null) {
        button[i] = new JButton(icon);
        button[i].addActionListener(new IconButtonHandler(i, icon));
        buttonOrder[i] = i;

        for (int j = 1; j < imagesTotal+1; j++) {
          checkbox[i][j] = new JCheckBox("Relevance to images/" + i + ".jpg");
          checkbox[i][j].setEnabled(false);
        }

        imageCards[i] = new JPanel();
        imageCards[i].setLayout(new BorderLayout());
        JLabel title = new JLabel("images/" + i + ".jpg");
        title.setHorizontalAlignment(JLabel.CENTER);
        imageCards[i].add(title, BorderLayout.NORTH);
        imageCards[i].add(button[i], BorderLayout.CENTER);
      }
    }


    // ===========================================================
    // == 1.3.3. Boot GUI and Initialize Sorting Methods =========
    // ===========================================================
    
    // Calls the remaining functions necessary for bootup.
    readIntensityFile();
    readColorCodeFile();
    initializeNormalizedMatrix();
    displayFirstPage();
  }


  // ===================================
  // == 1.4. readIntensityFile() =======
  // ===================================
  //
  /* This method opens the intensity text file containing the intensity matrix
   * with the histogram bin values for each image.  The contents of the matrix
   * are processed and stored in a 2D array called intensityMatrix.
   */
  public void readIntensityFile() {
    StringTokenizer token;
    Scanner read;
    int intensityBin = 0;
    int lineNumber = 1;

    try
    {
      read = new Scanner(new File ("intensity.txt"));
      while (read.hasNextLine()) {
        // Stores the first token in imageSize[], and every token thereafter in
        // intensityMatrix[]
        token = new StringTokenizer(read.nextLine());
        if (token.hasMoreTokens()) {
          imageSize[lineNumber] = Integer.valueOf(token.nextToken());
        }
        while (token.hasMoreTokens()) {
          intensityMatrix[lineNumber][intensityBin++] = Integer.valueOf(token.nextToken());
        }
        // Sets values for the next loop
        intensityBin = 0;
        lineNumber++;
      }
    }
    catch(FileNotFoundException EE) {
      System.out.println("FileNotFoundException: intensity.txt does not exist in project directory.");
    }
  }


  // ===================================
  // == 1.5. readColorCodeFile() =======
  // ===================================
  //
  /* This method opens the color code text file containing the color code
     matrix with the histogram bin values for each image.  The contents of the
     matrix are processed and stored in a 2D array called colorCodeMatrix.
   */
  private void readColorCodeFile() {
    StringTokenizer token;
    Scanner read;
    int colorCodeBin = 0;
    int lineNumber = 1;

    try
    {
      read = new Scanner(new File ("colorCodes.txt"));
      while (read.hasNextLine()) {
        // Stores the first token in imageSize[], and every token thereafter in
        // intensityMatrix[]
        token = new StringTokenizer(read.nextLine());
        if (token.hasMoreTokens()) {
          imageSize[lineNumber] = Integer.valueOf(token.nextToken());
        }
        while (token.hasMoreTokens()) {
          colorCodeMatrix[lineNumber][colorCodeBin++] = Integer.valueOf(token.nextToken());
        }

        // Sets values for the next loop
        colorCodeBin = 0;
        lineNumber++;
      }
    }
    catch(FileNotFoundException EE) {
      System.out.println("FileNotFoundException: colorCodes.txt does not exist in project directory.");
    }
  }


  // ==========================================
  // == 1.6. initializeNormalizedMatrix =======
  // ==========================================
  //
  /* This method is for initializing the normalized matrix using the combined
   * values from the intensity matrix and the color code matrix it calculates
   * the averages and standard deviations from each feature and uses these
   * values to normalize the data into the normalized matrix.
   */
  private void initializeNormalizedMatrix() {
    double [] average = new double[89];
    double [] std = new double[89];

    // This loop transfers all the data from the intensity and color code
    // matrix into the normalized matrix.
    for(int i = 1; i < imagesTotal+1; i++){
      for(int j = 0; j < 25; j++){
        normalizedMatrix[i][j] = intensityMatrix[i][j];
        normalizedMatrix[i][j] /= imageSize[i];
      }
      for(int j = 25; j < 89; j++){
        normalizedMatrix[i][j] = colorCodeMatrix[i][j - 25];
        normalizedMatrix[i][j] /= imageSize[i];
      }
    }

    //This loop is for finding the average for features.
    for(int i = 0; i < 89; i++){
      average[i] = 0;
      for(int j = 1; j < imagesTotal+1; j++){
        average[i] += normalizedMatrix[j][i];
      }
      average[i] /= imagesTotal;
    }

    //This loop is for finding the standard deviation for features.
    for(int i = 0; i < 89; i++){
      std[i] = 0;
      for(int j = 1; j < imagesTotal+1; j++){
        double calc = normalizedMatrix[j][i] - average[i];
        std[i] += (calc * calc);
      }
      std[i] /= imagesTotal-1;
      std[i] = Math.sqrt(std[i]);
    }

    //This loop converts the matrix into a normalized matrix.
    for(int i = 0; i < 89; i++){
      for(int j = 1; j < imagesTotal+1; j++){
        if(std[i] != 0) {
          normalizedMatrix[j][i] = ((normalizedMatrix[j][i] - average[i]) / std[i]);
        }
      }

    }
  }

  
  // ===================================================
  // == 2. UPDATES (to GUI and Relevance Feedback) =====
  // ===================================================
  //
  // ==================================
  // == 2.1. displayFirstPage() =======
  // ==================================
  //
  /* This method displays the first twenty images in the panelCenterPics.  The
   * for loop starts at number one and gets the image number stored in
   * buttonOrder[] and assigns the value to imageButNo.  The button, a label,
   * and a checkbox associated with the image are then added to panelCenterPic,
   * which in turn is added to panelCenterPics.  The for loop continues this
   * process until twenty images are displayed in the panelCenterPics.
   */
  private void displayFirstPage() {
    int imageButNo = 0;
    imageCount = 1;
    panelCenterPics.removeAll();
    for (int i = 1; i < imagesPerPage+1; i++) {
      imageButNo = buttonOrder[i];
      panelCenterPics.add(imageCards[imageButNo]);
      imageCount++;
    }
    panelCenterPics.revalidate();
    panelCenterPics.repaint();

    // Updates JButtons and JLabels
    pagePrev.setEnabled(false);
    pageNext.setEnabled(true);
    pageNo = 1;
    pagePos.setText("Page " + pageNo + "/" + (int) Math.ceil((double) imagesTotal / imagesPerPage));
  }


  // =================================================================
  // == 2.2. class IconButtonHandler implements ActionListener =======
  // =================================================================
  //
  /* This class implements an ActionListener for each iconButton.  When an icon
   * button is clicked, the image on the the button is added to the
   * photographLabelSelected and the picNoSelected is set to the image number
   * selected and being displayed.
   */
  private class IconButtonHandler implements ActionListener {

    int pNo = 0;
    ImageIcon iSelected;

    IconButtonHandler(int i, ImageIcon j) {
      pNo = i;
      iSelected = j;
    }

    public void actionPerformed(ActionEvent e) {
      
      if (picNoSelected != pNo) {
        sortByIntensity.setEnabled(true);
        sortByColorCode.setEnabled(true);
        sortByRelevanceFeedback.setEnabled(true);
        switch (sortMethod) {
          case 1:
          sortByIntensity.setEnabled(false);
          case 2:
          sortByColorCode.setEnabled(false);
        }
        
        picNoSelected = pNo;
        
        if (checkbox[picNoSelected][1] != null && !checkbox[picNoSelected][1].isEnabled()) {
          sortByRelevanceFeedback.setText("Sort by: INTENSITY + COLOR CODE");
        }
        else {
          sortByRelevanceFeedback.setText("(Sort by: INTENSITY + COLOR CODE) && (Update: RF)");
        }
        
        imageCardSelected.removeAll();
        titleSelected.setText("SELECTED FOR NEXT SORT: images/" + picNoSelected + ".jpg");
        photographLabelSelected.setIcon(iSelected);
        photographLabelSelected.setHorizontalAlignment(JLabel.CENTER);
        imageCardSelected.add(titleSelected, BorderLayout.NORTH);
        imageCardSelected.add(photographLabelSelected, BorderLayout.CENTER);
        
        panelNorthSelected.removeAll();
        panelNorthSelected.add(imageCardSelected, BorderLayout.SOUTH);
        panelNorthSelected.revalidate();
        panelNorthSelected.repaint();

        BorderLayout layoutRef;
        for (int i = 1; i < checkbox[pNo].length; i++) {
          layoutRef = (BorderLayout) imageCards[i].getLayout();
          if (layoutRef.getLayoutComponent(BorderLayout.SOUTH) != null) {
            imageCards[i].remove(layoutRef.getLayoutComponent(BorderLayout.SOUTH));
          }
          imageCards[i].add(checkbox[pNo][i], BorderLayout.SOUTH);
          imageCards[i].revalidate();
          imageCards[i].repaint();
        }
      }
      else {
        picNoSelected = 0;
        sortByIntensity.setEnabled(false);
        sortByColorCode.setEnabled(false);
        sortByRelevanceFeedback.setEnabled(false);
        
        imageCardSelected.removeAll();
        titleSelected.setText("SELECTED FOR NEXT SORT: (select an image!)");
        photographLabelSelected.setIcon(null);
        imageCardSelected.add(titleSelected, BorderLayout.NORTH);
        
        panelNorthSelected.remove(imageCardSelected);
        panelNorthSelected.add(imageCardSelected);
        panelNorthSelected.revalidate();
        panelNorthSelected.repaint();

        for (int i = 1; i < checkbox[pNo].length; i++) {
          BorderLayout layoutRef = (BorderLayout) imageCards[i].getLayout();
          if (layoutRef.getLayoutComponent(BorderLayout.SOUTH) != null) {
            imageCards[i].remove(layoutRef.getLayoutComponent(BorderLayout.SOUTH));
          }
          imageCards[i].revalidate();
          imageCards[i].repaint();
        }
      }

      boolean enabled = true;
      if (picNoSelected == picNoDisplayed) {
        enabled = false;
      }
      switch (sortMethod) {
        case 1: sortByIntensity.setEnabled(enabled); break;
        case 2: sortByColorCode.setEnabled(enabled); break;
        case 3: sortByRelevanceFeedback.setEnabled(enabled); break;
      }
    }

  }


  // ===============================================================
  // == 2.3. class nextPageHandler implements ActionListener =======
  // ===============================================================
  //
  /* This class implements an ActionListener for JButton pageNext.  The last
   * image number to be displayed is set to the current image count plus
   * imagesPerPage.  If the endImage number equals imagesTotal+1, then the next
   * page button does not display any new images because there are only 100
   * images to be displayed.  The first picture on the next page is the image
   * located in the buttonOrder array at the imageCount.
   */
  private class nextPageHandler implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      int imageButNo = 0;
      int endImage = imageCount + imagesPerPage;
      pagePrev.setEnabled(true);
      panelCenterPics.removeAll();

      // if (on the last page), disables JButton and updates for loop stopper
      if (endImage >= buttonOrder.length) {
        pageNext.setEnabled(false);
        endImage = buttonOrder.length;
      }
      
      /* Goes through the buttonOrder array starting with the imageCount value,
       * retrieves the image at that place, and then adds the imageCard to the
       * panelPics.
       */
      for (int i = imageCount; i < endImage; i++) {//3
        imageButNo = buttonOrder[i];
        panelCenterPics.add(imageCards[imageButNo]);
      }
      imageCount += imagesPerPage;  // Maintains count prevPageHandler expects even for non-full last pages
      panelCenterPics.revalidate();
      panelCenterPics.repaint();

      // Updates page number
      pageNo++;
      pagePos.setText("Page " + pageNo + "/" + (int) Math.ceil((double) imagesTotal / imagesPerPage));
    }

  }


  // ===============================================================
  // == 2.4. class PrevPageHandler implements ActionListener =======
  // ===============================================================
  //
  /* This class implements an ActionListener for JButton pagePrev.  The
   * last image number to be displayed is set to the current image count minus
   * imagesPerPage*2.  If the endImage number is less than 1, then the previous
   * page button does not display any new images because the starting image is
   * 1.  The first picture on the next page is the image located in the
   * buttonOrder array at the imageCount.
   */
  private class previousPageHandler implements ActionListener{

    public void actionPerformed(ActionEvent e){
      int imageButNo = 0;
      int startImage = imageCount - imagesPerPage*2;
      int endImage = imageCount - imagesPerPage;
      pageNext.setEnabled(true);
      panelCenterPics.removeAll();

      // if (on the first page), disables JButton
      if (startImage == 1) {
        pagePrev.setEnabled(false);
      }

      /* Goes through the buttonOrder array starting with the startImage
        * value, retrieves the image at that place, and then adds the imageCard
        * to the panelPics.
        */
      for (int i = startImage; i < endImage; i++) {
        imageButNo = buttonOrder[i];
        panelCenterPics.add(imageCards[imageButNo]);
        imageCount--;
      }
      panelCenterPics.revalidate();
      panelCenterPics.repaint();

      // Updates page number
      pageNo--;
      pagePos.setText("Page " + pageNo + "/" + (int) Math.ceil((double) imagesTotal / imagesPerPage));
    }

  }


  // ==================================================================
  // == 2.5. class sortMethodsHandler implements ActionListener =======
  // ==================================================================
  //
  /* This class implements an ActionListener when the user selects a sorting
   * method button.  The displayed image is updated to match the image selected
   * before the button was hit.  The size of the image is retrieved from
   * imageSize[].  The selected image's intensity, colorCode, or
   * normalized/weight bin values are compared to all the other image's bin
   * values and a score is determined for how well the images compare.
   * The images are then arranged from most similar to the least.
   */
  private class sortMethodsHandler implements ActionListener {

    // 0 = Filename sort
    // 1 = Intensity sort
    // 2 = ColorCode sort
    // 3 = RelevanceFeedback update+sort
    int sNo = -1;
    
    sortMethodsHandler(int i) {
      sNo = i;
    }
    
    public void actionPerformed(ActionEvent e) {
      
      // =============================================
      // == 2.5.1. Update GUI Selected Image =========
      // =============================================
      
      // Displays selected image
      if (sNo == 0) {
        picNoDisplayed = picNoSelected;
        
        imageCardDisplayed.removeAll();
        titleDisplayed.setText("DISPLAYING RESULTS FOR: (select a sort method!)");
        imageCardDisplayed.add(titleDisplayed, BorderLayout.NORTH);

        panelNorthDisplayed.removeAll();
        panelNorthDisplayed.add(imageCardDisplayed, BorderLayout.NORTH);
        panelNorthDisplayed.revalidate();
        panelNorthDisplayed.repaint();
      }
      else {
        picNoDisplayed = picNoSelected;
        
        imageCardDisplayed.removeAll();
        titleDisplayed.setText("DISPLAYING RESULTS FOR: images/" + picNoDisplayed + ".jpg");
        photographLabelDisplayed.setIcon(photographLabelSelected.getIcon());
        photographLabelDisplayed.setHorizontalAlignment(JLabel.CENTER);
        imageCardDisplayed.add(titleDisplayed, BorderLayout.NORTH);
        imageCardDisplayed.add(photographLabelDisplayed, BorderLayout.CENTER);

        panelNorthDisplayed.removeAll();
        panelNorthDisplayed.add(imageCardDisplayed, BorderLayout.NORTH);
        panelNorthDisplayed.revalidate();
        panelNorthDisplayed.repaint();
      }
      
      
      // ======================================
      // == 2.5.2. Update GUI Options =========
      // ======================================
      
      // Updates window title and enables/disables appropriate JRadioButton and JButtons
      sortByFilename.setEnabled(true);
      sortByIntensity.setEnabled(true);
      sortByColorCode.setEnabled(true);
      sortByRelevanceFeedback.setEnabled(true);
      String titleText = "";
      switch (sNo) {
        case 0:
          titleText = "sorted by FILENAME";
          sortByFilename.setEnabled(false);
          break;
        
        case 1:
          titleText = "sorted by INTENSITY for \"images/" + picNoDisplayed + ".jpg\"";
          sortByIntensity.setEnabled(false);
          break;
        
        case 2:
          titleText = "sorted by COLOR CODE for \"images/" + picNoDisplayed + ".jpg\"";
          sortByColorCode.setEnabled(false);
          break;
        
        case 3:
          // if (first RF iteration for this image)
          if (checkbox[picNoDisplayed][1] != null && !checkbox[picNoDisplayed][1].isEnabled()) {
            titleText = "sorted by INTENSITY + COLOR CODE  for \"images/" + picNoDisplayed + ".jpg\"";
            sortByRelevanceFeedback.setText("(Sort by: INTENSITY + COLOR CODE) && (Update: RF)");
            for (int i = 1; i < checkbox[picNoDisplayed].length; i++) {
              checkbox[picNoDisplayed][i].setEnabled(true);
            }
          }
          else {
            titleText = "sorted by INTENSITY + COLOR CODE  for \"images/" + picNoDisplayed + ".jpg\", and updated with RELEVANCE FEEDBACK";
          }
          break;
      }
      setTitle("Image Sorter: Results " + titleText);

      
      // ============================================
      // == 2.5.3. Update GUI Image-Sorting =========
      // ============================================
      
      // if (Sorting by Filename)
      if (sNo == 0) {
        // Adds the images to buttonOrder[] in numerical order by filename
        buttonOrder = new int[imagesTotal+1];
        for (int j = 1; j < buttonOrder.length; j++) {
          buttonOrder[j] = j;
        }
      }
      else {
        double[] distance = new double[imagesTotal+1];    // stores Manhattan Distances across loops
        map = new HashMap<Double, Integer>();             // Keys = Manhattan Distances, Values = image numbers
        double d = 0;                                     // Manhattan Distance of current compareImage
        int compareImage = 1;                             // loop counter
        int picSize = imageSize[picNoDisplayed];
        
        // All cases calculate and store the Manhattan Distance for current
        // loop's compareImage
        switch (sNo) {
          case 1: // (Display results sorted by Intensity similarity to the selected image)
            while (compareImage < intensityMatrix.length) {
              if (picNoDisplayed == compareImage) {
                d = -1;
              }
              else {
                for (int j = 0; j < intensityMatrix[compareImage].length; j++) {
                  d += Math.abs(((double) intensityMatrix[picNoDisplayed][j] / picSize) - ((double) intensityMatrix[compareImage][j] / imageSize[compareImage]));
                }
              }
              map.put(d, compareImage);
              distance[compareImage] = d;
              d = 0;
              compareImage++;
            } 
            break;
            
          case 2: // (Display results sorted by ColorCode similarity to the selected image)
              while (compareImage < colorCodeMatrix.length) {
              if (picNoDisplayed == compareImage) {
                d = -1;
              }
              else {
                for (int j = 0; j < colorCodeMatrix[compareImage].length; j++) {
                  d += Math.abs(((double) colorCodeMatrix[picNoDisplayed][j] / picSize) - ((double) colorCodeMatrix[compareImage][j] / imageSize[compareImage]));
                }
              }
              map.put(d, compareImage);
              distance[compareImage] = d;
              d = 0;
              compareImage++;
            } 
            break;

          case 3: // (Display results sorted by RelevanceFeedback similarity to the selected image)

            double [] weights = new double[89];
            double [] average = new double[89];
            double [] std = new double[89];
            boolean areSelected = false;
            int totalSelected = 0;
            double totalWeight = 0.0;

            double defaultWeight = 1.0/89;
            for (int k = 0; k < 89; k++){
              weights[k] =  defaultWeight;
            }

            for (int i = 1; i < checkbox.length; i++) {
              if(i != picNoDisplayed) {
                if(checkbox[picNoDisplayed][i].isSelected()) {
                  if(areSelected != true) {
                    areSelected = true;
                    average[picNoDisplayed] = 0;
                    for(int j = 0; j < 89; j++){
                      average[j] = normalizedMatrix[picNoDisplayed][j];
                    }
                    totalSelected++;
                  }
                  for(int j = 0; j < 89; j++){
                    average[j] += normalizedMatrix[i][j];
                  }
                  totalSelected++;
                }
              }
            }

            if(areSelected == true){
              for(int i = 0; i < average.length; i++){
                average[i] = average[i]/totalSelected;
              }

              for(int i = 0; i < 89; i++){
                double calc = normalizedMatrix[picNoDisplayed][i] - average[i];
                std[i] = (calc * calc);
              }

              for (int i = 1; i < checkbox.length; i++) {
                if(i != picNoDisplayed) {
                  if(checkbox[picNoDisplayed][i].isSelected()) {
                    for(int j = 0; j < 89; j++){
                      double calc = normalizedMatrix[i][j] - average[j];
                      std[j] += (calc * calc);
                    }
                  }
                }
              }

              for(int i = 0; i < std.length; i++){
                std[i] /= totalSelected-1;
                std[i] = Math.sqrt(std[i]);

              }

              for(int i = 0; i < std.length; i++){
                if(std[i] == 0.0){
                  if(average[i] != 0.0){
                    double min = std[0];
                    for(int j = 1; j < std.length; j++){
                      if(std[j] != 0.0){
                        if(std[j] < min){
                          min = std[j];
                        }
                      }
                    }
                    std[i] = 0.5*min;
                  }
                }
              }

              for(int i = 0; i < std.length; i++){
                if(std[i] != 0.0) {
                  std[i] = 1.0/std[i];
                }
                totalWeight += std[i];
              }

              for (int i = 0; i < 89; i++){
                weights[i] = std[i]/totalWeight;
              }

            }

            while (compareImage < normalizedMatrix.length) {
              if (picNoDisplayed == compareImage) {
                d = -1;
              }
              else {
                for (int j = 0; j < normalizedMatrix[compareImage].length; j++) {
                  d += weights[j] * (Math.abs(normalizedMatrix[picNoDisplayed][j] - normalizedMatrix[compareImage][j]));
                }
              }
              map.put(d, compareImage);
              distance[compareImage] = d;
              d = 0;
              compareImage++;
            }
            break; 
        }

        // Adds the image numbers to buttonOrder[] in descending order of
        // Manhattan Distances
        Arrays.sort(distance);
        buttonOrder = new int[imagesTotal];
        int imageNo;

        // k=2 skips what were distance[picNo]=-1 and distance[1]=0 before
        // Arrays.sort(distance)
        int loopAdjust = 0;
        if (sNo == 3) {
          buttonOrder[1] = map.get(distance[0]);
          loopAdjust++;
        }
        for (int k = 2; k < distance.length - loopAdjust; k++) {
          imageNo = map.get(distance[k]);
          if (picNoDisplayed != imageNo) {
            buttonOrder[k - 1 + loopAdjust] = imageNo;
          }
        }
      }
      
      displayFirstPage();
    }

  }

}
