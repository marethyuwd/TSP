package de.randomerror.fh.k_means;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;

public class KMeans {
    static final int K = 50;

    public static void run(ArrayList<Entry> inputValues) throws IOException {
        List<Entry> cluster = new LinkedList<>();
        Random r = new Random(1357);

        IntStream.generate(() -> r.nextInt(inputValues.size()))
                .distinct()
                .limit(K)
                .forEach(rand -> {
                    Entry e = inputValues.get(r.nextInt(inputValues.size()));
                    Entry clusterShoppingCenter = new Entry(e);
                    clusterShoppingCenter.clazz = cluster.size();
                    cluster.add(clusterShoppingCenter);
                });

        while (true) {
            List<double[]> clustertruck = cluster.stream()
                    .map(entry -> entry.data)
                    .collect(Collectors.toList());

            for (Map.Entry<Integer, List<Entry>> entry : inputValues.stream().peek(i -> {
                i.clazz = classify(cluster, i.data);
            }).collect(groupingBy(i -> i.clazz)).entrySet()) {
                Integer key = entry.getKey();
                List<Entry> value = entry.getValue();
                Entry shoppingCenter = cluster.get(key);
                double[] data = value.stream().map(l -> l.data).reduce(((l, l2) -> {
                    double[] sum = new double[l.length];
                    for (int i = 0; i < l.length; i++) {
                        sum[i] = l[i] + l2[i];
                    }
                    return sum;
                })).get();
                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i] / (double) value.size();
                }
                shoppingCenter.data = data;
            }
            if (IntStream.range(0, cluster.size())
                    .allMatch(value -> Arrays.equals(cluster.get(value).data, clustertruck.get(value)))) {
                break;
            }

        }

        write(inputValues);
    }

    private static void write(ArrayList<Entry> inputValues) throws IOException {
        FileWriter writer = new FileWriter("data.csv");
        inputValues.forEach(input -> {
            System.out.println(input.name + "," + input.clazz);

            try {
                writer.append(input.name).append(",").append(String.valueOf(input.clazz)).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        writer.flush();
        writer.close();
    }

    public static int classify(List<Entry> learningMaterial, double[] input) {
        return learningMaterial.stream()
                .peek(e -> e.dist = distance(input, e.data))
                .sorted(Comparator.comparingDouble(Entry::getDist))
                .findFirst()
                .get()
                .clazz;
    }

    public static double distance(double[] input1, double[] input2) {
        return manhatten(input1, input2);
    }

    public static double manhatten(double[] input1, double[] input2) {
        int sum = 0;
        for (int i = 0; i < input1.length; i++) {
            sum += Math.abs(input1[i] - input2[i]);
        }
        return sum;
    }

    public static double minkowski(double[] input1, double[] input2, int exponent) {
        int sum = 0;
        for (int i = 0; i < input1.length; i++) {
            sum += Math.pow(input1[i] - input2[i], exponent);
        }
        return Math.pow(sum, 1 / (double) exponent);
    }
}
