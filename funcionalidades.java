
    // Distribuição da população
    public static void distpopulaçao(double[][] Leslie, double[] VectorInicial) {
        System.out.println();
        System.out.println();
        System.out.println("Introduza o número de gerações a estimar!");
        System.out.println();
        System.out.println("Exemplo: Caso deseja saber a distribuição da geração inicial, deve introduzir o valor 1.");
        int g = sc.nextInt();
        System.out.println();
        while (g <= 0) {
            System.out.println("Introduza um inteiro positivo");
            g = sc.nextInt();
        }
        double distpopulaçao[][] = new double[g][VectorInicial.length];
        for (int y = 0; y < VectorInicial.length; y++) {
            distpopulaçao[0][y] = 2 * VectorInicial[y];
        }
        for (int x = 1; x < g; x++) {
            VectorInicial = multiplyMatricesVectors(potência(Leslie, x), VectorInicial);
            for (int y = 0; y < VectorInicial.length; y++) {
                distpopulaçao[x][y] = 2 * VectorInicial[y];
            }
        }
        double[] populaçao = populaçao(Leslie, VectorInicial, g);
        for (int n = 0; n < g; n++) {
            System.out.println("Dados realtivos à distribuição da população na " + (n + 1) + "º geração:");
            for (int k = 0; k < VectorInicial.length; k++) {
                System.out.println("A população da " + (k + 1) + "º faixa etária é " + distpopulaçao[n][k] + ", o que corresponde a uma distribuição normalizada de " + (distpopulaçao[n][k] * 100 / populaçao[n]) + "%.");
            }
        }

    }


    // Dimensão da população
    public static void dimpopulaçao(double[][] Leslie, double[] VectorInicial) {
        System.out.println();
        System.out.println();
        System.out.println("Introduza um valor, referente a uma geração, para o qual deseja calcular a dimensão da população!");
        System.out.println();
        System.out.println("Exemplo: Caso deseja saber a dimensão da geração inicial, deve introduzir o valor 1.");
        int t = sc.nextInt();
        System.out.println();
        while (t <= 0) {
            System.out.println("Introduza um inteiro positivo");
            t = sc.nextInt();
        }
        double[] populaçao = populaçao(Leslie, VectorInicial, t);
        System.out.println();
        System.out.println("A dimensão da população para t=" + t + " é " + populaçao[t - 1]);
        System.out.println();
        for (int i = 1; i < t; i++) {
            System.out.println("A taxa de variação entre t=" + i + " e t=" + (i + 1) + " é " + (populaçao[i] - populaçao[i - 1]));
        }

    }

    public static double[] populaçao(double[][] Leslie, double[] VectorInicial, int t) {
        double[] populaçao = new double[t];
        double somaa = 0;
        for (int x = 0; x < VectorInicial.length; x++) {
            somaa = somaa + VectorInicial[x];
            populaçao[0] = 2 * somaa;
        }
        for (int a = 1; a < t; a++) {
            double[] aux = multiplyMatricesVectors(potência(Leslie, a), VectorInicial);
            double soma = 0;
            for (int k = 0; k < aux.length; k++) {
                soma = soma + aux[k];
                populaçao[a] = 2 * soma;
            }
        }
        return populaçao;
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

    public static double[][] potência(double[][] Matrix, int n) {

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

}
