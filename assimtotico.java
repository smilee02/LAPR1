import org.la4j.Matrix;
import org.la4j.decomposition.EigenDecompositor;

public class assimtotico {

    public static void main(String[] args) {

        double[][] matriz = new double[][]{{2, 7, 9}, {3, 6, 1}, {1, 2, 3}};

        Matrix maatriz = Matrix.from2DArray(matriz);

        valorProprio(maatriz);


    }

    public static void valorProprio(Matrix matrix) {

        int coluna = 0;
        double soma = 0;

        EigenDecompositor decomp = new EigenDecompositor(matrix);

        Matrix[] result = decomp.decompose();

        Matrix um = result[0];
        Matrix dois = result[1];

        double assimtotico = dois.max();

        System.out.println("A taxa de comportamento assimptótico é = " + assimtotico);

        double vetoresProprios[][] = um.toDenseMatrix().toArray();

        for (int i = 0; i < vetoresProprios.length; i++) {
            for (int j = 0; j < vetoresProprios.length; j++) {

                System.out.print(vetoresProprios[i][j] + " ");

            }

            System.out.println();
        }

        double valoresProprios[][] = dois.toDenseMatrix().toArray();

        for (int i = 0; i < valoresProprios.length; i++) {
            for (int j = 0; j < valoresProprios.length; j++) {
                System.out.print(valoresProprios[i][j] + " ");
            }

            System.out.println();
        }

        System.out.println();
        System.out.println();


        for (int a = 0; a < valoresProprios.length; a++) {

            for (int b = 0; b < valoresProprios.length; b++) {

                if (assimtotico == valoresProprios[a][b]) {

                    coluna = b;

                }
            }

        }

        for (int j = 0; j < 3; j++) {

            System.out.print(vetoresProprios[j][coluna] + " ");

            System.out.println();


            soma = vetoresProprios[j][coluna] + soma;
        }


        System.out.println(soma);
        System.out.println();


        double[] divisoes = new double[vetoresProprios.length];

        for (int con = 0; con < divisoes.length; con++) {

            divisoes[con] = vetoresProprios[con][coluna] / soma;

            System.out.print(divisoes[con] + " ");
        }


    }

}
