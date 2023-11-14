import java.util.Scanner;

public class Functionalities {

    static Scanner sc = new Scanner(System.in);
    // Population distribution
    public static void distpopulation(double[][] Leslie, double[] InitialVector) {
        System.out.println();
        System.out.println();
        System.out.println("Enter the number of generations to estimate!");
        System.out.println();
        System.out.println("Example: If you want to know the distribution of the initial generation, enter the value 1.");
        int g = sc.nextInt();
        System.out.println();
        while (g <= 0) {
            System.out.println("Enter a positive integer");
            g = sc.nextInt();
        }
        double distpopulation[][] = new double[g][InitialVector.length];
        for (int y = 0; y < InitialVector.length; y++) {
            distpopulation[0][y] = 2 * InitialVector[y];
        }
        for (int x = 1; x < g; x++) {
            InitialVector = multiplyMatricesVectors(power(Leslie, x), InitialVector);
            for (int y = 0; y < InitialVector.length; y++) {
                distpopulation[x][y] = 2 * InitialVector[y];
            }
        }
        double[] population = population(Leslie, InitialVector, g);

        for (int n = 0; n < g; n++) {
            System.out.println("Data regarding the distribution of the population in the " + (n + 1) + "º generation:");
            for (int k = 0; k < InitialVector.length; k++) {
                System.out.println("The population in the " + (k + 1) + "º age group is " + distpopulation[n][k] + ", which corresponds to a normalised distribution of " + (distpopulation[n][k] * 100 / population[n]) + "%.");
            }
        }


    }


    // Population size
    public static void dimpopulation(double[][] Leslie, double[] InitialVector) {
        System.out.println();
        System.out.println();
        System.out.println("Enter a value for a generation for which you want to calculate the population size!");
        System.out.println();
        System.out.println("Example: If you want to know the size of the initial generation, enter the value 1.");
        int t = sc.nextInt();
        System.out.println();
        while (t <= 0) {
            System.out.println("Enter a positive integer");
            t = sc.nextInt();
        }
        double[] population = population(Leslie, InitialVector, t);
        System.out.println();
        System.out.println("The size of the population for t=" + t + " is " + population[t - 1]);
        System.out.println();
        for (int i = 1; i < t; i++) {
            System.out.println("The rate of change between t=" + i + " and t=" + (i + 1) + " is " + (population[i] - population[i - 1]));
        }


    }

    public static double[] population(double[][] Leslie, double[] InitialVector, int t) {
        double[] population = new double[t];
        double sum = 0;
        for (int x = 0; x < InitialVector.length; x++) {
            sum = sum + InitialVector[x];
            population[0] = 2 * sum;
        }
        for (int a = 1; a < t; a++) {
            double[] aux = multiplyMatricesVectors(power(Leslie, a), InitialVector);
            double summ = 0;
            for (int k = 0; k < aux.length; k++) {
                summ = summ + aux[k];
                population[a] = 2 * summ;
            }
        }
        return population;
    }

    public static double[] multiplyMatricesVectors(double[][] Matrix, double[] Vector) {
        int dim = Vector.length;
        double[] product = new double[dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                product[i] = product[i] + Matrix[i][j] * Vector[j];
            }
        }
        return product;
    }

    public static double[][] power(double[][] Matrix, int n) {

        double[][] Matrix1 = new double[Matrix.length][Matrix.length];
        for (int x = 0; x < Matrix.length; x++) {
            for (int y = 0; y < Matrix.length; y++) {
                Matrix1[x][y] = Matrix[x][y];
            }
        }
        for (int i = 0; i < n; i++) {
            Matrix = multiplyMatrices(Matrix, Matrix1);
        }
        return Matrix;
    }

    public static double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
        double[][] product = new double[firstMatrix.length][firstMatrix.length];
        for (int i = 0; i < firstMatrix.length; i++) {
            for (int j = 0; j < firstMatrix.length; j++) {
                for (int p = 0; p < firstMatrix.length; p++) {
                    product[i][j] += firstMatrix[i][p] * secondMatrix[p][j];
                }
            }
        }
        return product;
    }
}


