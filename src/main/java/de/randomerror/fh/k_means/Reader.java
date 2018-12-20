package de.randomerror.fh.k_means;

import de.randomerror.fh.nearest_neighbor.NearestNeighbor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Reader {

    public static void main( String[] args ) throws IOException {
        ArrayList<Entry> inputValues = new ArrayList<>();
        ArrayList<Double> targetOutputs = new ArrayList<>();

        readData( "zoo/zoo-withnames", inputValues, targetOutputs );

        KMeans.run(inputValues);
    }

    public static void readData( String filePath, ArrayList<Entry> inputs, ArrayList<Double> targetOutputs ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader( filePath ) );

        System.out.println(new File(filePath).exists());
        String line;

        while ( ( line = reader.readLine() ) != null ) {
            System.out.println("parsing line");
            String[] strValues = line.split( "," );
            double[] values = new double[strValues.length-2];

            for ( int i = 0; i < strValues.length - 2; i++ )
                values[i] = Double.parseDouble( strValues[i+1] );

            inputs.add( new Entry(values, strValues[0]) );
        }
    }
}
