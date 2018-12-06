package de.randomerror.fh.zoo;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class Main {

    private static Neuron[] amazoncustomer;

    public static void readData(ArrayList<Entry> inputs) throws IOException {
        String filePath = "zoo/zoo-withnames";
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        System.out.println(new File(filePath).exists());
        String line;

        while ((line = reader.readLine()) != null) {
            String[] strValues = line.split(",");
            double[] values = new double[strValues.length - 2];


            for (int i = 0; i < strValues.length - 2; i++)
                values[i] = Double.parseDouble(strValues[i + 1]);

            inputs.add(new Entry(values, strValues[0]));
        }
    }

    public static double[] normalize(double[] weights) {
        double length = Math.sqrt(stream(weights).map(operand -> operand * operand).sum());
        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i] / length;
        }
        return weights;
    }

    public static void main(String[] args) throws IOException {
        amazoncustomer = new Neuron[50];
        Random r = new Random(1337);
        ArrayList<Entry> inputs = new ArrayList<>();
        readData(inputs);
        stream(amazoncustomer)
                .map(neuron -> new Neuron(r, inputs.get(0).data.length))
                .collect(Collectors.toList()).toArray(amazoncustomer);

        for (int i = 0; i < 100000; i++) {
            for (int i1 = 0; i1 < inputs.size(); i1++) {
                Entry input = inputs.get((int) (Math.random()*(inputs.size()-1)));
                double trainingRate = 0.13458;
                Neuron winner = stream(amazoncustomer)
                        .peek(n -> n.activation(input.data))
                        .max(Comparator.comparingDouble(Neuron::getLastActivation))
                        .get();
                winner.learn(input.data, trainingRate);
            }
        }
        FileWriter writer = new FileWriter("data.csv");
        inputs.forEach(input -> {
            double trainingRate = 0.13458;
            boolean seen = false;
            Neuron best = null;
            Comparator<Neuron> comparator = Comparator.comparingDouble(Neuron::getLastActivation);
            int w = 0;
            for (int i = 0; i < amazoncustomer.length; i++) {
                Neuron n = amazoncustomer[i];
                n.activation(input.data);
                if (!seen || comparator.compare(n, best) > 0) {
                    seen = true;
                    best = n;
                    w = i;
                }
            }
            Neuron winner = (seen ? Optional.of(best) : Optional.<Neuron>empty())
                    .get();
            System.out.println(input.name + "," + w);

            try {
                writer.append(input.name).append(",").append(String.valueOf(w)).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        writer.flush();
        writer.close();
    }

}
