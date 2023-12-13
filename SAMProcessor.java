import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

// Class for processing coordinate-sorted SAM files
public class SAMProcessor {

    // The main method is empty because this class is meant to be used by the SAMProcessorGUI class
    public static void main(String[] args) {
        // No implementation required
    }

    public static void processFile(String inputFilePath, String outputFilePath, int threadCount, int chunkSize) {
        // ExecutorService is used to manage a fixed number of threads
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<List<ProcessedLine>>> futures = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
             BufferedWriter wigWriter = new BufferedWriter(new FileWriter(outputFilePath + ".wig"))) {

            // Writing a header for a Wiggle format file
            wigWriter.write("track type=wiggle_0\n");

            List<String> lines = new ArrayList<>(chunkSize);
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("@")) {
                    lines.add(line);
                    if (lines.size() == chunkSize) {
                        // Submit a chunk of lines for processing
                        futures.add(executor.submit(new ChunkProcessor(new ArrayList<>(lines))));
                        lines.clear();
                    }
                }
            }
            // Handle any remaining lines
            if (!lines.isEmpty()) {
                futures.add(executor.submit(new ChunkProcessor(lines)));
            }

            // Waiting for all tasks to complete and writing output
            for (Future<List<ProcessedLine>> future : futures) {
                List<ProcessedLine> results = future.get();
                for (ProcessedLine result : results) {
                    writer.write(result.toString() + "\n");
                    int endPosition = Integer.parseInt(result.position) + result.sequence.length();
                    wigWriter.write(result.chromosome + " " + result.position + " " + endPosition + " " + result.gcContent + "\n");
                }
            }

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // Shut down the executor service
        executor.shutdown();
    }

    // Inner class for processing chunks of lines
    static class ChunkProcessor implements Callable<List<ProcessedLine>> {
        private final List<String> lines;

        ChunkProcessor(List<String> lines) {
            this.lines = lines;
        }

        // Processes the lines in the chunk
        @Override
        public List<ProcessedLine> call() throws Exception {
            List<ProcessedLine> processedLines = new ArrayList<>();
            for (String line : lines) {
                // Splitting the line into its components
                String[] fields = line.split("\t");
                String chromosome = fields[2];
                String position = fields[3];
                String sequence = fields[9];
                double gcContent = calculateGCContent(sequence);

                processedLines.add(new ProcessedLine(chromosome, position, sequence, gcContent));
            }
            return processedLines;
        }

        // Method to calculate GC content in a DNA sequence
        private double calculateGCContent(String sequence) {
            int gcCount = 0;
            for (char nucleotide : sequence.toCharArray()) {
                if (nucleotide == 'G' || nucleotide == 'C') {
                    gcCount++;
                }
            }
            return sequence.length() > 0 ? (double) gcCount / sequence.length() : 0;
        }
    }

    // Inner class representing a processed line of data
    static class ProcessedLine {
        String chromosome;
        String position;
        String sequence;
        double gcContent;

        ProcessedLine(String chromosome, String position, String sequence, double gcContent) {
            this.chromosome = chromosome;
            this.position = position;
            this.sequence = sequence;
            this.gcContent = gcContent;
        }

        // Overriding toString for easier output formatting
        @Override
        public String toString() {
            return chromosome + "\t" + position + "\t" + gcContent;
        }
    }
}
