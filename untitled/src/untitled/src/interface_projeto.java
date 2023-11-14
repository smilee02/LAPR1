import org.la4j.Matrix;
import org.la4j.Vector;

import java.util.Scanner;

public class interface_projeto {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.print("Introduzir o número de gerações pretendidas:");

        int geracao = sc.nextInt();

        double[] vetor = new double [geracao];

        populacao (vetor);

        double[][] matriz = new double [geracao] [geracao];

        fertilidade (geracao, matriz);
        sobrevivencia (geracao, matriz);

       Matrix Matriz = Matrix.from2DArray (matriz);
       Vector Vetor = Vector.fromArray (vetor);

    }

    public static double[] populacao (double[] arr){

        for (int i = 0; i < arr.length; i++){
            System.out.print("Introduzir o número de indivíduos reprodutores da geração " + i + ":");
            arr [i] = sc.nextInt();

        }

        return arr;
    }

    public static double[][] sobrevivencia (int num, double[][] array){

        int n = num - 1;

        for (int contador = 0; contador < n; contador++){

            System.out.print("Introduzir a taxa de sobrevivência da geração " + contador + ":");

            array [contador + 1] [contador] = sc.nextDouble();
        }

        return array;
    }


    public static double[][] fertilidade (int n, double[][] matriz){

        for (int contador = 0; contador < n ;contador++){

            System.out.print("Introduzir a taxa de fertilidade da geração " + contador + ":");
            matriz [0] [contador] = sc.nextDouble();

        }

        return matriz;

    }

}
