package de.randomerror.fh.zoo;

public class Entry {
    public double[] data;


    public String name;

    public Entry(double[] data, String name) {
        setData(data);
        this.name = name;
    }

    public double[] getData() {
        return data;
    }

    public void setData(double[] data) {
        this.data = Main.normalize(data);
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

}