package de.randomerror.fh.zoo;

import java.util.Random;

public class Neuron {
    private double[] weights;

    private double lastActivation;

    public Neuron(double[] weights) {
        setWeights(weights);
    }

    public Neuron(Random r1, int weightCount) {
        weights = new double[weightCount];
        for (int i = 0; i < weightCount; i++) {
            weights[i] = r1.nextDouble();
        }
        setWeights(weights);
    }

    public double getLastActivation() {
        return lastActivation;
    }

    public void setLastActivation(double lastActivation) {
        this.lastActivation = lastActivation;
    }

    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = Main.normalize(weights);
    }


    public double activation(double[] input) {
        double output = 0;
        for (int i = 0; i < input.length; i++) {
            output += input[i] * weights[i];
        }
        lastActivation = output;
        return output;
    }

    public void learn(double[] input, double learningRate) {
        for (int i = 0; i < input.length; i++) {
            weights[i] += learningRate * input[i];
        }
        setWeights(this.weights);
    }
}
