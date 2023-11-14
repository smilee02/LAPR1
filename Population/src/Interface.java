import org.la4j.Matrix;
import org.la4j.Vector;

import java.util.Scanner;

public class Interface {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.print("Enter the desired number of generations:");

        int generation = sc.nextInt();

        double[] vectorAux = new double [generation];

        population(vectorAux);

        double[][] matrixAux = new double [generation] [generation];

        fertility(generation, matrixAux);
        survival(generation, matrixAux);

        Matrix matrix = Matrix.from2DArray(matrixAux);
        Vector vector = Vector.fromArray(vectorAux);

    }

    public static double[] population (double[] arr){

        for (int i = 0; i < arr.length; i++){
            System.out.print("Enter the number of breeding individuals of the generation " + i + ":");
            arr [i] = sc.nextInt();

        }

        return arr;
    }

    public static double[][] survival (int num, double[][] array){

        int n = num - 1;

        for (int counter = 0; counter < n; counter++){

            System.out.print("Enter the survival rate of the generation " + counter + ":");

            array [counter + 1] [counter] = sc.nextDouble();
        }

        return array;
    }


    public static double[][] fertility (int n, double[][] matrix){

        for (int counter = 0; counter < n ;counter++){

            System.out.print("Enter the fertility rate of the generation " + counter + ":");
            matrix[0] [counter] = sc.nextDouble();

        }

        return matrix;

    }

}
