package de.randomerror.fh.nearest_neighbor;

public class Entry {
    public double[] data;

    public Double getDataClass() {
        return dataClass;
    }

    public Double dataClass;

    public double dist = 0;

    public double getDist() {
        return dist;
    }

    public Entry(double[] data, Double dataClass) {
        this.data = data;
        this.dataClass = dataClass;
    }
}
