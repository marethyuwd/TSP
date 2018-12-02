package de.randomerror.fh.nearest_neighbor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Reader {

    private static String[] irisClasses = new String[]{"Iris-setosa", "Iris-versicolor", "Iris-virginica"};
    private static String[] wineClasses = new String[]{"1", "2", "3"};
    private static String[] sonarClasses = new String[]{"R", "M"};

    private static String[] consideredClasses = irisClasses;

    public static void main( String[] args ) throws IOException {
        ArrayList<double[]> inputValues = new ArrayList<>();
        ArrayList<Double> targetOutputs = new ArrayList<>();

        readData( "data/iris.all-data", inputValues, targetOutputs );

        for ( int i = 0; i < inputValues.size(); i++ )
            System.out.println( targetOutputs.get( i ) + " : " + Arrays.toString( inputValues.get( i ) ) );

        NearestNeighbor.run(inputValues, targetOutputs);
    }

    public static void readData( String filePath, ArrayList<double[]> inputs, ArrayList<Double> targetOutputs ) throws IOException {
        BufferedReader reader = new BufferedReader( new FileReader( filePath ) );

        System.out.println(new File(filePath).exists());
        String line;

        while ( ( line = reader.readLine() ) != null ) {
            System.out.println("parsing line");
            String[] strValues = line.split( "," );
            double[] values = new double[strValues.length];

            values[0] = 1;

            for ( int i = 0; i < strValues.length - 1; i++ )
                values[i + 1] = Double.parseDouble( strValues[i] );

            inputs.add( values );
            for ( int i = 0; i < consideredClasses.length; i++) {
                if (strValues[strValues.length-1].equals(consideredClasses[i])) {
                    targetOutputs.add((double)i);
                    break;
                }
            }
        }
    }
}
