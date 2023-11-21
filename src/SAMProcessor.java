import java.io.*;

public class SAMProcessor {
    public static void main(String[] args) {
        String inputFilePath = "/home/adam/ExcludeFromSyncthing/GC_Couture/A004.sorted.sam"; // Replace with your SAM file path
        String outputFilePath = "/home/adam/ExcludeFromSyncthing/GC_Couture/A004.txt"; // Replace with your desired output file path

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("@")) { // Skip header lines
                    String[] fields = line.split("\t");
                    String chromosome = fields[2];
                    String position = fields[3];
                    String sequence = fields[9];
                    double gcContent = calculateGCContent(sequence);

                    writer.write(chromosome + "\t" + position + "\t" + gcContent + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double calculateGCContent(String sequence) {
        int gcCount = 0;
        for (char nucleotide : sequence.toCharArray()) {
            if (nucleotide == 'G' || nucleotide == 'C') {
                gcCount++;
            }
        }
        return (double) gcCount / sequence.length();
    }
}

