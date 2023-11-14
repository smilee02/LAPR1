import org.la4j.Matrix;
import org.la4j.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Read{
    public static void main(String[] args) throws FileNotFoundException {
        File evolution = new File("file.txt");
        Scanner sc = new Scanner(evolution);
        String vectors = sc.nextLine();
        String survival = sc.nextLine();
        String nindividuals = sc.nextLine();
        sc.close();
        double[] vector = ReadingInArrays(vectors);
        double[] taxOf = ReadingInArrays(survival);
        double[] numberOfIndividuals = ReadingInArrays(nindividuals);
        Matrix Leslie = Matrix.from2DArray(ArrayDeLeslie(taxOf,numberOfIndividuals)); //Matrix Leslie
        Vector InitialVector = Vector.fromArray(vector); //InitialVector
        /*for(int i=0;i<numerodeindiv.length;i++)
        {
            for(int j=0;j<numerodeindiv.length;j++)
            { //print array
                System.out.print(Leslie[i][j]+"|");
            }
            System.out.print("\n");
        }*/
    }
    public static double[] ReadingInArrays(String s)
    {
        int i=0;
        String[] arr = s.split(",");
        double[] aux1 = new double[arr.length];
        while(i< arr.length)
        {
            String o = arr[i];
            String[] aux = o.split("=");
            aux1[i] = Double.parseDouble(aux[1]);
            i++;
        }
        return aux1;
    }
    public static double[][] ArrayDeLeslie(double[] tax, double[] nIndiv)
    {
        int j=0;
        double[][] Leslie = new double[nIndiv.length][nIndiv.length];  //Matrix NxN
        for(int i=0;i<nIndiv.length;i++)
        {
            Leslie[0][i]=nIndiv[i];    //First Line
        }
        while(j<tax.length)
        {
            Leslie[j+1][j]=tax[j];  //Surival Rate in Diagonal
            j++;
        }
        return Leslie;
    }
}
