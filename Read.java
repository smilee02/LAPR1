import org.la4j.Matrix;
import org.la4j.Vector;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Read{
    public static void main(String[] args) throws FileNotFoundException {
        File evolucao = new File("file.txt");
        Scanner sc = new Scanner(evolucao);
        String vetores = sc.nextLine();
        String sobrevivencia = sc.nextLine();
        String nindividuos = sc.nextLine();
        sc.close();
        double[] vetor = LeituraEmArrays(vetores);
        double[] taxadesobre = LeituraEmArrays(sobrevivencia);
        double[] numerodeindiv = LeituraEmArrays(nindividuos);
        Matrix Leslie = Matrix.from2DArray(ArrayDeLeslie(taxadesobre,numerodeindiv));  //Matriz de Leslie
        Vector VetorInicial = Vector.fromArray(vetor);  //Vetor Inicial
        /*for(int i=0;i<numerodeindiv.length;i++)
        {
            for(int j=0;j<numerodeindiv.length;j++)
            {                                               //print do array
                System.out.print(Leslie[i][j]+"|");
            }
            System.out.print("\n");
        }*/
    }
    public static double[] LeituraEmArrays(String s)
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
    public static double[][] ArrayDeLeslie(double[] taxa, double[] nindiv)
    {
        int j=0;
        double[][] Leslie = new double[nindiv.length][nindiv.length];  //a matriz é nxn (quadrada)
        for(int i=0;i<nindiv.length;i++)
        {
            Leslie[0][i]=nindiv[i];    //preenche a primeira linha
        }
        while(j<taxa.length)
        {
            Leslie[j+1][j]=taxa[j];  //taxa de sobrevivencia na diagonal
            j++;
        }
        return Leslie;
    }
}
