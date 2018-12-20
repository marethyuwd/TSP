package de.randomerror.fh.nearest_neighbor;

import java.util.*;
import java.util.stream.Collectors;

public class NearestNeighbor {

    private static List<Entry> learningMaterial;
    private static List<Entry> testMaterial;

    private static final double RATIO = 0.2;
    private static int K = 1;

    public static void run(ArrayList<double[]> inputValues, ArrayList<Double> targetOutputs) {
        List<Entry> entries = new LinkedList<>();

        for(int i = 0; i < inputValues.size(); i++) {
            entries.add(new Entry(inputValues.get(i), targetOutputs.get(i)));
        }

        Random r1 = new Random(1337);
        Collections.shuffle(entries, r1);

        learningMaterial = entries.subList(0, (int)(entries.size()*RATIO));
        testMaterial = entries.subList((int)(entries.size()*RATIO)+1, entries.size());

        System.out.println(String.format("using %d as Learning %d as Test", learningMaterial.size(), testMaterial.size()));

        for (int i = 1; i < 10; i++) {
            K = i;
            int correctlyClassified = testMaterial.stream()
                    .mapToInt(e -> classify(e.data).equals(e.dataClass) ? 1 : 0)
                    .sum();

            System.out.println(String.format("testMaterial.size: %d; correct: %d; Accuracy: %.2f; K: %d", testMaterial.size(), correctlyClassified, ((double) correctlyClassified / testMaterial.size())*100.0, K));
        }
    }

    public static Double classify(double[] input) {
        return learningMaterial.stream()
                .peek(e -> e.dist = distance(input, e.data))
                .sorted(Comparator.comparingDouble(Entry::getDist))
                .limit(K)
                .collect(Collectors.groupingBy(Entry::getDataClass))
                .values().stream()
                .max(Comparator.comparing(List::size))
                .map(l -> l.get(0).dataClass)
                .get();
    }

    public static double distance(double[] i1, double[] i2) {
        double sum = 0;
        for (int i = 0; i < i1.length; i++) {
            sum += (i2[i]-i1[i])*(i2[i]-i1[i]);
        }
        return Math.sqrt(sum);
    }
}
