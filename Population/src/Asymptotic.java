import org.la4j.Matrix;
import org.la4j.decomposition.EigenDecompositor;

public class Asymptotic {

    public static void ownValue(Matrix matrix) {

        int collumn = 0;
        double sum = 0;

        EigenDecompositor decomp = new EigenDecompositor(matrix);

        Matrix[] result = decomp.decompose();

        Matrix one = result[0];
        Matrix two = result[1];

        double asymptotic = two.max();

        System.out.println("The rate of asymptotic behaviour is =" + asymptotic);

        double ownVectors[][] = one.toDenseMatrix().toArray();

        for (int i = 0; i < ownVectors.length; i++) {
            for (int j = 0; j < ownVectors.length; j++) {

                System.out.print(ownVectors[i][j] + " ");

            }

            System.out.println();
        }

        double ownValues[][] = two.toDenseMatrix().toArray();

        for (int i = 0; i < ownValues.length; i++) {
            for (int j = 0; j < ownValues.length; j++) {
                System.out.print(ownValues[i][j] + " ");
            }

            System.out.println();
        }

        System.out.println();
        System.out.println();


        for (int a = 0; a < ownValues.length; a++) {

            for (int b = 0; b < ownValues.length; b++) {

                if (asymptotic == ownValues[a][b]) {

                    collumn = b;

                }
            }

        }

        for (int j = 0; j < 3; j++) {

            System.out.print(ownVectors[j][collumn] + " ");

            System.out.println();


            sum = ownVectors[j][collumn] + sum;
        }


        System.out.println(sum);
        System.out.println();


        double[] divisions = new double[ownVectors.length];

        for (int con = 0; con < divisions.length; con++) {

            divisions[con] = ownVectors[con][collumn] / sum;

            System.out.print(divisions[con] + " ");
        }


    }

}
