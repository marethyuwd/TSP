package de.randomerror.fh.k_means;

import java.util.Arrays;

public class Entry {
    public double[] data;
    public String name;
    public int clazz;

    public double dist = 0;

    public double getDist() {
        return dist;
    }

    public Entry(double[] data, String name) {
        this.data = data;
        this.name = name;
    }

    public Entry(Entry e) {
        this.data = Arrays.copyOf(e.data, e.data.length);
        this.name = e.name;
        this.clazz = e.clazz;
        this.dist = e.dist;
    }
}
