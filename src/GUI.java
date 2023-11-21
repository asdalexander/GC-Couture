import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUI extends JFrame {
    private JTextField directoryField;
    private JTextField windowSizeField;
    private JButton analyzeButton;
    private JCheckBox outputWiggleFileCheckBox;
    private JCheckBox outputSummaryFileCheckBox;

    public GUI() {
        setTitle("GC Couture");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        directoryField = new JTextField(20);
        windowSizeField = new JTextField(5);
        analyzeButton = new JButton("Analyze");
        outputWiggleFileCheckBox = new JCheckBox("Output Wiggle File");
        outputSummaryFileCheckBox = new JCheckBox("Output Summary File");

        add(new JLabel("Directory:"));
        add(directoryField);
        add(new JLabel("Window Size:"));
        add(windowSizeField);
        add(outputWiggleFileCheckBox);
        add(outputSummaryFileCheckBox);
        add(analyzeButton);

        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }


    public static void main(String[] args) {
        new GUI().setVisible(true);
    }
}
