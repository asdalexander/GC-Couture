import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

// Class for creating the GUI
public class SAMProcessorGUI {

    // The main method
    public static void main(String[] args) {
        // Creating the main window of the application
        JFrame frame = new JFrame("GC Couture");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Setting the close operation
        frame.setSize(600, 360); // Setting the size of the window

        // Creating a panel with GridBagLayout to place components in a grid
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL; // Making components expand horizontally
        constraints.insets = new Insets(5, 5, 5, 5); // Setting padding around components



        // Adding logo to the top center of the GUI
        ImageIcon logoIcon = new ImageIcon(SAMProcessorGUI.class.getResource("/logo.png"));
        JLabel logoLabel = new JLabel(logoIcon);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3; // Span across three columns
        constraints.anchor = GridBagConstraints.CENTER; // Center align in its cell
        panel.add(logoLabel, constraints);

        // Reset constraints for other components
        constraints.gridwidth = 1; // Reset to default grid width
        constraints.anchor = GridBagConstraints.LINE_START; // Reset alignment




        // Creating and adding components to the panel
        // Label and text field for input file path
        JLabel inputFilePathLabel = new JLabel("Input File Path:");
        JTextField filePathField = new JTextField(20);
        JButton fileChooserButton = new JButton("Choose File");

        // Label and text field for output directory
        JLabel outputDirectoryLabel = new JLabel("Output Directory:");
        JTextField outputDirField = new JTextField(20);
        JButton outputDirChooserButton = new JButton("Choose Output Directory");

        // Label and spinner for selecting the number of threads
        JLabel threadCountLabel = new JLabel("Number of Threads:");
        SpinnerModel threadCountModel = new SpinnerNumberModel(4, 1, 64, 1);
        JSpinner threadCountSpinner = new JSpinner(threadCountModel);

        // Start button to begin processing
        JButton startButton = new JButton("Start");

        // Label for displaying runtime
        JLabel runtimeLabel = new JLabel("Runtime: 0 ms");

        // Loading icon - set invisible by default
        ImageIcon loadingIcon = new ImageIcon(SAMProcessorGUI.class.getResource("/loading-symbol.gif"));
        JLabel loadingLabel = new JLabel(loadingIcon);
        loadingLabel.setVisible(false);




        // Adding components to the panel with constraints for layout
        // Adjusting grid positioning for the rest of the components
        constraints.gridx = 0; constraints.gridy = 1; panel.add(inputFilePathLabel, constraints);
        constraints.gridx = 1; constraints.gridy = 1; panel.add(filePathField, constraints);
        constraints.gridx = 2; constraints.gridy = 1; panel.add(fileChooserButton, constraints);

        constraints.gridx = 0; constraints.gridy = 2; panel.add(outputDirectoryLabel, constraints);
        constraints.gridx = 1; constraints.gridy = 2; panel.add(outputDirField, constraints);
        constraints.gridx = 2; constraints.gridy = 2; panel.add(outputDirChooserButton, constraints);

        constraints.gridx = 0; constraints.gridy = 3; panel.add(threadCountLabel, constraints);
        constraints.gridx = 1; constraints.gridy = 3; panel.add(threadCountSpinner, constraints);

        constraints.gridx = 0; constraints.gridy = 4; constraints.gridwidth = 3; panel.add(startButton, constraints);
        constraints.gridx = 0; constraints.gridy = 5; constraints.gridwidth = 3; panel.add(loadingLabel, constraints);
        constraints.gridx = 0; constraints.gridy = 6; constraints.gridwidth = 3; panel.add(runtimeLabel, constraints);





        // Action listeners for buttons
        // Opens a file chooser for selecting the input file
        fileChooserButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); // Only allow the user to choose a file
            int returnValue = fileChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        // Opens a file chooser for selecting the output directory
        outputDirChooserButton.addActionListener(e -> {
            JFileChooser directoryChooser = new JFileChooser();
            directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Don't allow the user to choose a file here
            int returnValue = directoryChooser.showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedDirectory = directoryChooser.getSelectedFile();
                outputDirField.setText(selectedDirectory.getAbsolutePath());
            }
        });

        // Action listener for the start button
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadingLabel.setVisible(true);
                String inputFilePath = filePathField.getText();
                String outputDirectory = outputDirField.getText();
                String outputFilePath = outputDirectory + File.separator + new File(inputFilePath).getName().replaceAll(".sam$", ".tsv");
                int threadCount = (Integer) threadCountSpinner.getValue();

                // Creating a new thread to process the file
                new Thread(() -> {
                    long startTime = System.currentTimeMillis();
                    SAMProcessor.processFile(inputFilePath, outputFilePath, threadCount, 1000);
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;

                    // Updating the runtime label on the Event Dispatch Thread
                    SwingUtilities.invokeLater(() -> {
                        runtimeLabel.setText("Runtime: " + duration + " ms");
                        loadingLabel.setVisible(false);
                    });
                }).start();
            }
        });

        // Adding the panel to the frame and making it visible
        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }
}
