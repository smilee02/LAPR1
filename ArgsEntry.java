import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.la4j.Matrix;
import org.la4j.decomposition.EigenDecompositor;
import org.la4j.linear.LeastSquaresSolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ArgsEntry {
    static Scanner in = new Scanner(System.in);
    public static void main(String[] args) throws IOException, InterruptedException {
        boolean interactive = true;

        if (args.length <= 2) //Interativo
        {
            interactive = true;
        }

        else if(args.length > 2) //Nao Interativo
        {
            interactive = false;
        }

        if (interactive && args.length==0)  //Modo Interativo Interface
        {
            InterativoInterface();
        }

        else if (interactive && args[0].compareTo("-n") == 0)   //Modo Interativo TXT
        {
            InterativoTxt(args,interactive);
        }

        else if (!interactive) //Modo Nao Interativo TXT
        {
            NaoInterativoTxt(args,interactive);
        }
    }

    //------Escrita Manual-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static double[][] PreencherDados(int dim) {
        double[][] Leslie = new double[dim][dim];
        fertilidade(dim, Leslie);
        sobrevivencia(dim, Leslie);
        return Leslie;
    }

    public static int dimensao () {
        System.out.print("\n \nIntroduza o número de faixas etárias pretendidas:\n---> ");
        int dim = in.nextInt();
        return dim;
    }

    public static double[] vetorinicial (int dim) {
        System.out.println();
        System.out.println();
        double[] vetorinicial = new double[dim];
        for (int x = 0; x < dim; x++) {
            System.out.print("Introduzir o número de indivíduos reprodutores da faixa etária " + (x+1) + ":\n --> ");
            vetorinicial[x] = in.nextDouble();
        }
        return vetorinicial;
    }


    public static double[][] fertilidade(int dim, double[][] Leslie) {
        System.out.println();
        System.out.println();
        for (int x= 0; x < dim; x++) {
            System.out.print("Introduzir a taxa de fertilidade da faixa etária " + (x+1) + ":\n---> ");
            Leslie[0][x] = in.nextDouble();
        }
        return Leslie;

    }

    public static double[][] sobrevivencia(int dim, double[][] Leslie) {
        System.out.println();
        int n = dim - 1;
        for (int x = 0; x < n; x++) {
            System.out.print("Introduzir a taxa de sobrevivência da faixa etária " + (x+1) + ":\n---> ");
            Leslie[x + 1][x] = in.nextDouble();
        }

        return Leslie;
    }

    //------Leitura Ficheiro TXT-----------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static double[] LeituraEmArrays(String s) {
        int i = 0, j=0;
        String[] arr = s.split(",");
        while(j<arr.length)
        {
            String a = arr[j];
            if(!a.contains("="))
            {
                System.out.println("O formato do ficheiro nao é suportado. Verifique as entradas e reinicie a aplicacao");
                System.exit(0);
            }
            j++;
        }
        double[] aux1 = new double[arr.length];
        while (i < arr.length) {
            String o = arr[i];
            String[] aux = o.split("=");
            aux1[i] = Double.parseDouble(aux[1]);
            i++;
        }
        return aux1;
    }

    public static double[][] ArrayDeLeslie(double[] taxa, double[] nindiv) {
        int j = 0;
        double[][] Leslie = new double[nindiv.length][nindiv.length];  //a matriz é nxn (quadrada)
        for (int i = 0; i < nindiv.length; i++) {
            Leslie[0][i] = nindiv[i];    //preenche a primeira linha
        }
        while (j < taxa.length) {
            Leslie[j + 1][j] = taxa[j];  //taxa de sobrevivencia na diagonal
            j++;
        }
        return Leslie;
    }


    //------Distribuição da população------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void distpopulaçao(double[][] Leslie, double[] VectorInicial) throws FileNotFoundException {
        boolean norm = true;
        System.out.println();
        System.out.println();
        System.out.println("Introduza o número de geracoes a estimar!");
        System.out.println();
        System.out.println("Exemplo: Caso deseja saber a distribuicao da geracao inicial, deve introduzir o valor 0.");
        System.out.print("---> ");
        int g = in.nextInt();
        System.out.println();
        while (g < 0) {
            System.out.println("Erro! Introduza um número maior ou igual a 0");
            g = in.nextInt();
        }
        double[] VectorInicial1 = new double[VectorInicial.length];
        for (int l = 0; l < VectorInicial.length; l++) {
            VectorInicial1[l] = VectorInicial[l];
        }
        double[][] distpopulaçao= new double[g + 1][VectorInicial.length];
        for (int y = 0; y < VectorInicial.length; y++) {
            distpopulaçao[0][y] = VectorInicial[y];
        }
        for (int x = 1; x <= g; x++) {
            VectorInicial = multiplyMatricesVectors(potência(Leslie, (x - 1)), VectorInicial1);
            for (int y = 0; y < VectorInicial.length; y++) {
                distpopulaçao[x][y] = VectorInicial[y];
            }
        }
        double[] populaçao = populaçao(Leslie, VectorInicial1, g);
        for (int n = 0; n <= g; n++) {
            System.out.println();
            System.out.println();
            System.out.println("///////////////////////////////////////////////////////[" + n + "]////////////////////////////////////////////////////");
            System.out.println();
            System.out.println("Dados relativos à distribuicao da populacao na geracao: " + n);
            for (int k = 0; k < VectorInicial.length; k++) {
                System.out.print("A populacao da " + (k + 1) + "º faixa etária é ");
                System.out.printf("%.2f",distpopulaçao[n][k]);
                System.out.print(", o que corresponde a uma distribuicao normalizada de ");
                System.out.printf("%.2f",(distpopulaçao[n][k] * 100 / populaçao[n]));
                System.out.println("%");
            }
        }
        TempToGraphsDis(distpopulaçao,populaçao,norm,VectorInicial.length,g);
        norm = !norm;
        TempToGraphsDis(distpopulaçao,populaçao,norm,VectorInicial.length,g);
    }

    //------Dimensão da população----------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void dimpopulaçao(double[][] Leslie, double[] VectorInicial) throws FileNotFoundException {
        boolean dim = true;
        System.out.println();
        System.out.println();
        System.out.println("Introduza um valor, referente a uma geracao, para o qual deseja calcular a dimensao da populacao!");
        System.out.println();
        System.out.println("Exemplo: Caso deseja saber a dimensao da geracao inicial, deve introduzir o valor 0.");
        System.out.print("---> ");
        int t = in.nextInt();
        System.out.println();
        while (t < 0) {
            System.out.println("Introduza um número maior ou igual a 0");
            t = in.nextInt();
        }
        double[] populaçao = populaçao(Leslie, VectorInicial, t);
        System.out.println();
        for(int i=0;i<=t;i++)
        {
            System.out.print("A dimensao da populacao para t=" + i + " é ");
            System.out.printf("%.2f", populaçao[i]);
            System.out.println();
        }
            System.out.println();
            System.out.println();


    }

    //------Taxa de Variação---------------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void taxavarpop(double[][] Leslie, double[] VectorInicial) throws FileNotFoundException{
        boolean dim = true;
        System.out.println();
        System.out.println();
        System.out.println("Introduza um valor, referente a uma geracao, para o qual deseja calcular a taxa de variacao da populacao!");
        System.out.println();
        System.out.println("Exemplo: Caso deseja saber a dimensao da geracao inicial, deve introduzir o valor 0.");
        System.out.print("---> ");
        int t = in.nextInt();
        System.out.println();
        while (t < 0) {
            System.out.println("Introduza um número maior ou igual a 0");
            t = in.nextInt();
        }
        double[] populaçao = populaçao(Leslie, VectorInicial, t);
        if(t>0) {
            for (int i = 0; i < t; i++) {
                System.out.print("A taxa de variacao entre t=" + i + " e t= " + (i+1) + " é ");
                System.out.printf("%.2f", (populaçao[i + 1] / populaçao[i]));
                System.out.println();
            }
        }
        else if (t==0)
        {
            System.out.println("t=0 é o momento inicial logo nao existe taxa de variacao.");
        }
    }

    //------Auxiliares da população--------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static double[] populaçao(double[][] Leslie, double[] VectorInicial, int t) {
        double[] populaçao = new double[t + 1];
        double somaa = 0;
        for (int x = 0; x < VectorInicial.length; x++) {
            somaa = somaa + VectorInicial[x];
            populaçao[0] = somaa;
        }
        for (int a = 0; a < t; a++) {
            double[] aux = multiplyMatricesVectors(potência(Leslie, a), VectorInicial);
            double soma = 0;
            for (int k = 0; k < aux.length; k++) {
                soma = soma + aux[k];
                populaçao[a + 1] = soma;
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

    //------Gráficos-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------O
    /*public static void Gráficos(int t, String especie) throws IOException, InterruptedException {
        int contador=0;
        System.out.println("\n" + "\n");
        System.out.println("//////////////////////////////////////////////////[GRÁFICOS]////////////////////////////////////////////////////");
        System.out.println();
        while(contador<4)
        {
            Grafico(contador,t, especie);
            contador++;
        }
    }*/

    public static void Grafico(int contador, String especie, double[][] Leslie, double[] vetorinicial) throws IOException, InterruptedException{
        int type;
        boolean norm,dim;
        switch (contador) {
            case (0):
                System.out.println("-----------------------------------[Dimensao da Populacao ao Longo do Tempo]------------------------------------");
                System.out.print("\n");
                break;
            case(1):
                System.out.println("-------------------------------[Taxa de Variacao da Populacao ao Longo do Tempo]--------------------------------");
                System.out.print("\n");
                break;
            case(2):
                System.out.println("------------------------------------[Evolucao da Distribuicao da Populacao]-------------------------------------");
                System.out.print("\n");break;
            case(3):
                System.out.println("------------------------[Distribuicao Normalizada Pelo Total da Populacao Ao Longo do Tempo]--------------------");
                System.out.print("\n");break;


        }
        int geracao = geracoes();
        switch (contador){
            case 0:dim=true;populaçao(Leslie,vetorinicial,geracao);TempToGraphsDim(populaçao(Leslie,vetorinicial,geracao),dim,geracao);
                break;
            case 1:dim=false;populaçao(Leslie,vetorinicial,geracao);TempToGraphsDim(populaçao(Leslie,vetorinicial,geracao),dim,geracao);
                break;
            case 2:norm= false; distribuicaopop(Leslie,vetorinicial,geracao,norm);
                break;
            case 3:norm = true; distribuicaopop(Leslie,vetorinicial,geracao,norm);
                break;

        }
            type = Type();
            Mostra(contador,especie);
            Graphs(contador, type, especie);
            OutputGraphs(contador);
            Guardar(contador, type, especie);
            boolean a = (new File("script.sh").delete());

    }  //4 graficos

    public static int Type() {
        int formato;
        System.out.print("Introduza o número que corresponde ao formato desejado (1-PNG) (2-TXT) (3-EPS): ");
        do {
            formato = in.nextInt();
            System.out.print("\n");
            if (formato != 1 && formato != 2 && formato != 3) {
                System.out.print("Não é um formato de ficheiro válido, tente outro (1-PNG) (2-TXT) (3-EPS): ");
            }
        } while (formato != 1 && formato != 2 && formato != 3);
        System.out.print("\n");
        return formato;
    } //Formato desejado

    public static void Graphs(int contador, int formato, String especie) throws IOException, InterruptedException {    //cria um comando para executar | Recebe como parametro o grafico que precisa de fazer

        Thread.sleep(250);
        String[] var1 = Variáveis(contador, formato, especie);
        File script = Script(var1);
        String[] gnuplot = {"C:\\Program Files\\gnuplot\\bin\\gnuplot.exe","-p", String.valueOf(script)};
        GnuPlot(gnuplot);

    }  //cria um comando para executar | Recebe como parametro o grafico que precisa de fazer

    public static void Mostra(int contador, String especie) throws IOException, InterruptedException {
        String[] var = Variáveis(contador, 1, especie );
        var[0] = "png";
        var[1] = "show.png";
        File script = Script(var);
        String[] show = {"C:\\Program Files\\gnuplot\\bin\\gnuplot.exe","-p", String.valueOf(script)};
        GnuPlot(show);
        String[] cmd = {"cmd.exe","/c","show.png"};
        GnuPlot(cmd);
        Thread.sleep(250);
    } //cria um ficheiro png que mostra o grafico

    public static void GnuPlot(String[] gnuplot) throws IOException {

        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(gnuplot);  //executa o comando recebido como parametro


    } //Execucao do Programa

    public static File Script(String[] var) throws FileNotFoundException {   //criacao de script
        File script = new File("script.sh");
        PrintWriter write = new PrintWriter(script);
        File file = new File(var[5]);
        if(file.exists()) {
            Scanner sc = new Scanner(file);
            String generations = sc.nextLine();
            String[] geracoes = generations.split(" ");

            write.print("#!/bin/bash" + "\n");
            write.print("set terminal " + var[0] + "\n");
            write.print("set output " + "\"" + var[1] + "\"" + "\n");
            write.print("\n");
            write.print("set title " + "\"" + var[2] + "\"" + "\n");
            write.print("set xlabel " + "\"" + var[3] + "\"" + "\n");
            write.print("set ylabel " + "\"" + var[4] + "\"" + "\n");
            write.print("\n");
            write.print("plot ");
            if (var[5].compareTo("dimpopulacao.txt") == 0 || var[5].compareTo("taxavarpop.txt") == 0) {
                write.print("'" + var[5] + "' using 1:2 with lines t \"\" ");
            } else {
                for (int i = 0; i < geracoes.length; i++) {
                    write.print("'" + var[5] + "' using 1:" + (i + 2) + " t " + "\"" + (i + 1) + "º faixa etária  \" " + " with lines linestyle " + (i + 1));
                    if (i != (geracoes.length - 1)) {
                        write.print(", ");
                    }
                }
            }
            write.print("\n");
            write.print("exit");
        }

        write.close();
        return script;
    }  //script que cria o grafico, png, txt, eps

    public static String[] Variáveis(int contador, int formato, String especie)     //variaveis do script
    {
        String terminal = "", output = "", title = "", xlabel = "", ylabel = "", file = "";
        switch (contador) {
            case 0:
                title = "Dimensao da Populacao ao Longo do Tempo";
                xlabel = "Geracoes";
                ylabel = "Dimensão da Populacao";
                file = "dimpopulacao.txt";
                break;
            case 1:
                title = "Taxa de Variacao da Populacao ao Longo do Tempo";
                xlabel = "Geracoes";
                ylabel = "Taxa de Variacao da Populacao";
                file = "taxavarpop.txt";
                break;
            case 2:
                title = "Evolucao da Distribuicao da Populacao";
                xlabel = "Geracoes";
                ylabel = "Distribuicao da Populacao";
                file = "dispopulacao.txt";
                break;
            case 3:
                title = "Distribuicao Normalizada Pelo Total da Populacao Ao Longo do Tempo";
                xlabel = "Geracoes";
                ylabel = "Distribuicao Normalizada Pelo Total da Populacao";
                file = "dispopulacaonorm.txt";
                break;
            default:
                title = "ACABOU";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); //data usando a classe date e formatter para formatar a data em apenas dias meses e horas
        Date date = new Date();
        String data = formatter.format(date);
        switch (formato) {
            case 1:
                terminal = "pngcairo";
                output = title + " " + especie + " " + data + ".png";
                break;
            case 2:
                terminal = "dumb";
                output = title + " " + especie + " " + data + ".txt";
                break;
            case 3:
                terminal = "postscript";
                output = title + " " +especie + " " + data + ".eps";
                break;
            default:
                System.out.println("Valor não expectável: " + formato);
        }
        String[] var = new String[6];
        var[0] = terminal;
        var[1] = output;
        var[2] = title;
        var[3] = xlabel;
        var[4] = ylabel;
        var[5] = file;
        return var;
    }

    public static File Guardar(int contador, int type, String especie) throws IOException {
        //Show(contador,type);
        System.out.print("Deseja guardar uma cópia do gráfico? (s/n) : ");
        String s = in.nextLine();
        String guardar = in.nextLine();
        while (guardar.compareTo("n") != 0 && guardar.compareTo("s") != 0) {
            System.out.print("\"" + guardar + "\"" + " não é um formato válido. Por favor introduza um dos dois (s-sim/n-nao): ");
            guardar = in.nextLine();
        }
        File copia = new File(Variáveis(contador, type, especie)[1]);
        if (guardar.compareTo("n") == 0) {
            copia.delete();
        }
        System.out.print("\n");
        System.out.print("\n");
        return Script(Variáveis(contador, type, especie));
    }  //Metodo de guardar o ficheiro

    public static void DeleteTempFile()   //Modulo para apagar tds os ficheiros temporarios   (Necessita olhadela)
    {
        String[] files = {"dimpopulacao.txt","dispopulacao.txt","dispopulacaonorm.txt","taxavarpop.txt","script.sh","show.png"};
        for(int i=0;i<files.length;i++)
        {
            if(!new File(files[i]).delete())
            {
                System.out.println("ERRO : Apagar ficheiro " + files[i]);
            }
        }
    }

    //------Escrita Em/De Ficheiros---------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static File TempToGraphsDim(double[] arr, boolean dim, int t) throws FileNotFoundException {
        File file;
        if(dim)
        {
            file = new File("dimpopulacao.txt");
            PrintWriter write = new PrintWriter(file);
            for(int i=0;i<=t;i++)
            {
                write.print( i + " ");
                write.printf("%.2f",arr[i]);
                write.println();
            }
            write.close();
        }
        else {
            file = new File("taxavarpop.txt");
            PrintWriter write = new PrintWriter(file);
            for(int i=0;i<t;i++)
            {
                write.print( i + " ");
                write.printf("%.2f",(arr[i+1]/arr[i]));
                write.println();
            }
            write.close();
        }
        return file;
    }  //Ficheiros Temporarios Graficos Dimensao e Taxa

    public static File TempToGraphsDis(double[][] arr, double[] aux, boolean norm, int columns, int rows) throws FileNotFoundException {
        File file = new File ("hello.txt");
        if(!norm)
        {
            file = new File("dispopulacao.txt");
            PrintWriter write = new PrintWriter(file);
            for(int i=0;i<=rows;i++)
            {
                write.print(i);
                for(int j=0;j<columns;j++)
                {
                    write.print(" ");
                    write.printf("%.2f", arr[i][j]);
                }
                write.print("\n");
            }
            write.close();
        }
        if(norm)
        {
            file = new File("dispopulacaonorm.txt");
            PrintWriter write = new PrintWriter(file);
            for(int i=0;i<=rows;i++)
            {
                write.print(i);
                for(int j=0;j<columns;j++)
                {
                    write.print(" ");
                    write.printf("%.2f", (arr[i][j] * 100 / aux[i]));
                }
                write.print("\n");
            }
            write.close();
        }
        return file;
    }  //Ficheiros Temporarios Graficos Distribuicao Normalizada e Nao

    public static void OutputGraphs(int contador) throws FileNotFoundException {
        String ficheiro;
        switch(contador)
        {
            case(0):
                ficheiro = "dimpopulacao.txt";break;
            case(1):
                ficheiro = "taxavarpop.txt";break;
            case(2):
                ficheiro = "dispopulacao.txt";break;
            case(3):
                ficheiro = "dispopulacaonorm.txt";break;
            default:
                ficheiro = "";
        }
        File file = new File(ficheiro);
        Scanner ler = new Scanner(file);
        while(ler.hasNextLine())
        {
            System.out.println(ler.nextLine());
        }
        System.out.println("\n");
    }  //Dados dos ficheiros dos graficos na consola

    //------Auxiliar Distribuição--------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void distribuicaopop(double[][] Leslie, double[] VectorInicial, int g, boolean norm) throws FileNotFoundException {
        double[] VectorInicial1 = new double[VectorInicial.length];
        for (int l = 0; l < VectorInicial.length; l++) {
            VectorInicial1[l] = VectorInicial[l];
        }
        double[][] distpopulaçao = new double[g + 1][VectorInicial.length];
        for (int y = 0; y < VectorInicial.length; y++) {
            distpopulaçao[0][y] = VectorInicial[y];
        }
        for (int x = 1; x <= g; x++) {
            VectorInicial = multiplyMatricesVectors(potência(Leslie, (x - 1)), VectorInicial1);
            for (int y = 0; y < VectorInicial.length; y++) {
                distpopulaçao[x][y] = VectorInicial[y];
            }
        }
        double[] populaçao = populaçao(Leslie, VectorInicial1, g);
        TempToGraphsDis(distpopulaçao,populaçao,norm,VectorInicial.length,g);
    }  //distribuicao normalizada e nao normalizada para nao interativo

    //------Assintotico------------------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void Assimtotico(double[][] Leslie) {



            Matrix maatriz = Matrix.from2DArray(Leslie);

            valorProprio(maatriz);


        }

        public static void valorProprio(Matrix matrix) {

            int coluna = 0;
            double soma = 0;

            EigenDecompositor decomp = new EigenDecompositor(matrix);

            Matrix[] result = decomp.decompose();

            Matrix vetor = result[0];
            Matrix valor = result[1];

            double assimtotico = valor.max();

            System.out.println();
            System.out.println();
            System.out.print("A taxa de crescimento assimtótico é = ");
            System.out.printf("%.4f",assimtotico);
            System.out.println();

            /*if(assimtotico<1)
            {
                System.out.print("A populacao desta espécie tem tendência a desparecer. Após geracoes infinitas, a geracao seguinte tende a diminuir ");
                System.out.printf("%.2f",(100-(assimtotico*100)));
                System.out.println("% em relação à anterior.");
            }
            else if(assimtotico>1)
            {
                System.out.print("A populacao desta espécie tem tendência a aumentar. Após geracoes infinitas, a geracao seguinte tende a aumentar ");
                System.out.printf("%.2f",((assimtotico*100)-100));
                System.out.println("% em relação à anterior.");
            }
            if(assimtotico==1)
            {
                System.out.println("A populacao desta espécie tem tendência a estagnar. Após geracoes infinitas a geracao seguinte tende a ter populacao igual à anterior.");

            }*/
            System.out.println();

            double vetoresProprios[][] = vetor.toDenseMatrix().toArray();


            double valoresProprios[][] = valor.toDenseMatrix().toArray();


            for (int a = 0; a < valoresProprios.length; a++) {

                for (int b = 0; b < valoresProprios.length; b++) {

                    if (assimtotico == valoresProprios[a][b]) {

                        coluna = b;

                    }
                }

            }

            for (int j = 0; j < vetoresProprios.length; j++) {

                soma = vetoresProprios[j][coluna] + soma;
            }





            double[] divisoes = new double[vetoresProprios.length];

            for (int con = 0; con < divisoes.length; con++) {

                divisoes[con] = vetoresProprios[con][coluna] / soma;

            }
            System.out.print("Vetor Próprio = (");
            System.out.printf("%.2f",divisoes[0]*100);

            for(int i = 1; i < divisoes.length; i++)
            {
                System.out.print(", ");
                System.out.printf("%.2f",divisoes[i]*100);
            }
            System.out.println(")");


        }

    //------Modos------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void NaoInterativoTxt(String[] args, boolean interactive) throws IOException {
        boolean valorvetorprop = false, dimpopulacao = false, variacaodapop = false;  //("-e,-v,-r")
        int formatograph=1, numbergen=1;
        for(int i=0;i<args.length;i++)
        {
            if(args[i].compareTo("-t")==0)
            {
                try {
                    numbergen = Integer.parseInt(args[i + 1]);
                }catch(NumberFormatException e)
                {
                    System.out.println("\"" + args[i+1] + "\"" + " nao é um formato válido. Introduza um valor inteiro positivo");
                    System.exit(0);
                }

            }
            if(args[i].compareTo("-g")==0)
            {
                if(args[i+1].compareTo("1")==0 || args[i+1].compareTo("2")==0 || args[i+1].compareTo("3")==0)
                {
                    formatograph = Integer.parseInt(args[i + 1]);
                }
                else{
                    System.out.println("\"" + args[i+1] + "\"" + " nao é um formato válido. Introduza um valor inteiro positivo, entre 1 e 3");
                    System.exit(0);
                }
            }
            if(args[i].compareTo("-e")==0)
            {
                valorvetorprop = true;
            }
            if(args[i].compareTo("-v")==0)
            {
                dimpopulacao = true;
            }
            if(args[i].compareTo("-r")==0)
            {
                variacaodapop = true;
            }
        }
        String ficheiroentrada = args[args.length-2]; //penultima posicao
        String ficheirosaida = args[args.length-1];  //ultima posicao
        File saida = new File(ficheirosaida);  //criacao de ficheiro de saida
        PrintWriter write = new PrintWriter(saida);   //escrever no ficheiro de saida
        try{
            File evolucao = new File(ficheiroentrada);  //file.txt ---- nomeficheiro
            Scanner sc = new Scanner(evolucao);
            String vetores = "", sobrevivencia = "", fecundidade = "";
            while (sc.hasNextLine()) {
                String linha = sc.nextLine();
                if ((String.valueOf(linha.charAt(0))).compareTo("x") == 0) {
                    vetores = linha;
                } else if ((String.valueOf(linha.charAt(0))).compareTo("s") == 0) {
                    sobrevivencia = linha;
                } else if ((String.valueOf(linha.charAt(0))).compareTo("f") == 0) {
                    fecundidade = linha;
                }
            }
            double[] vetor = LeituraEmArrays(vetores);  //Vetor Inicial
            if(vetor.length>200)  //ERRO CASO TENHA MAIS QUE 200 CLASSES
            {
                System.out.println("Erro: O número de classes introduzidas é superior ao máximo definido. ");
            }
            else {
                int index = ficheirosaida.indexOf(".");
                boolean norm = false;
                String especie = ficheirosaida.substring(0,index);
                double[] taxadesobre = LeituraEmArrays(sobrevivencia);
                double[] fecundidades = LeituraEmArrays(fecundidade);
                double[][] Leslie = ArrayDeLeslie(taxadesobre, fecundidades);  //Matriz de Leslie
                distribuicaopop(Leslie, vetor, numbergen,norm);
                norm = true;
                distribuicaopop(Leslie, vetor, numbergen,norm);
                write.println("Número de Gerações = " + numbergen);
                write.print("\n" + "\n");
                write.println("Matriz de Leslie");
                write.println("----------------");
                for (int i = 0; i < fecundidades.length; i++) {
                    for (int j = 0; j < fecundidades.length; j++) {
                        write.printf("%.2f", Leslie[i][j]);
                        write.print(" ");
                    }
                    write.print("\n");
                }
                write.println();
                //------------------------------------------------Número Total De Indivíduos Reprodutores
                if (dimpopulacao) {
                    boolean dim = true;
                    double[] populacao = populaçao(Leslie, vetor, numbergen);

                    TempToGraphsDim(populacao, dim, numbergen);
                    Graphs(0, formatograph, especie);

                    write.print("------Número Total De Indivíduos Reprodutores------" + "\n");
                    write.print("\n");
                    write.print("( t, Nt)" + "\n");
                    for (int i = 0; i <= numbergen; i++) {
                        write.print("( " + i + ", ");
                        write.printf("%.2f", populacao[i]);
                        write.print(")" + "\n");
                    }
                    write.print("\n" + "\n" + "\n");
                }
                //------------------------------------------------Taxa de Variação De Indivíduos Reprodutores
                if (variacaodapop) {
                    boolean dim = false;
                    double[] populacao = populaçao(Leslie, vetor, numbergen);

                    TempToGraphsDim(populacao, dim, numbergen);

                    if (numbergen > 0) {
                        Graphs(1, formatograph, especie);
                    }

                    write.print("----Taxa de Variação De Indivíduos Reprodutores----" + "\n");
                    write.print("\n");
                    write.print("( t, Δt)" + "\n");

                    for (int i = 0; i < numbergen; i++) {
                        write.print("( " + i + ", ");
                        write.printf("%.2f", (populacao[i + 1] / populacao[i]));
                        write.print(")" + "\n");
                    }
                    write.print("\n" + "\n" + "\n");
                }

                //------------------------------------------------Distribuição Da Espécie Por Classes
                {
                    File dispop = new File("dispopulacao.txt");
                    Scanner in = new Scanner(dispop);

                    write.println("--------Distribuição Da Espécie Por Classes--------" + "\n");
                    write.print("( t");

                    String[] arr = (in.nextLine()).split(" ");  //Descobrir o numero de classes
                    for (int i = 0; i < arr.length; i++) {
                        write.print(", x" + i);
                    }
                    write.println(")");

                    write.print("( 0");
                    for (int k = 1; k < arr.length; k++) {
                        write.print(", " + arr[k]);
                    }
                    write.print(")" + "\n");

                    for (int j = 1; j <= numbergen; j++) {
                        write.print("( " + j);
                        String[] aux = (in.nextLine()).split(" ");
                        for (int z = 1; z < arr.length; z++) {
                            write.print(", ");
                            write.printf(aux[z]);
                        }
                        write.println(")");
                    }
                    write.print("\n" + "\n" + "\n");
                }

                //------------------------------------------------Distribuição Da Espécie Por Classes (Normalizado)
                {
                    File dispopnorm = new File("dispopulacaonorm.txt");
                    Scanner ler = new Scanner(dispopnorm);

                    write.println("-Distribuição Da Espécie Por Classes (Normalizado)-" + "\n");
                    write.print("( t");

                    String[] zzz = (ler.nextLine()).split(" ");  //Descobrir o numero de classes
                    for (int i = 0; i < zzz.length; i++) {
                        write.print(", x" + i);
                    }
                    write.println(")");

                    write.print("( 0");
                    for (int k = 1; k < zzz.length; k++) {
                        write.print(", " + zzz[k]);
                    }
                    write.print(")" + "\n");

                    for (int j = 1; j <= numbergen; j++) {
                        write.print("( " + j);
                        String[] aux1 = (ler.nextLine()).split(" ");
                        for (int z = 1; z < aux1.length; z++) {
                            write.print(", ");
                            write.printf(aux1[z]);
                        }
                        write.println(")");
                    }
                    write.print("\n" + "\n" + "\n");
                }

                //------------------------------------------------Assintotico
                if (valorvetorprop) {
                    int coluna = 0;
                    double soma = 0;
                    Matrix matriz = Matrix.from2DArray(Leslie);
                    EigenDecompositor decomp = new EigenDecompositor(matriz);

                    Matrix[] result = decomp.decompose();

                    Matrix vector = result[0];
                    Matrix valor = result[1];

                    double assimtotico = valor.max();

                    write.println();
                    write.println();
                    write.print("A taxa de crescimento assimtótico é = ");
                    write.printf("%.4f", assimtotico);
                    write.println();

                    /*if (assimtotico < 1) {
                        write.print("A população desta espécie tem tendência a desparecer. Após gerações infinitas, a geração seguinte tende a diminuir ");
                        write.printf("%.2f", (100 - (assimtotico * 100)));
                        write.println("% em relação à anterior.");
                    } else if (assimtotico > 1) {
                        write.print("A população desta espécie tem tendência a aumentar. Após gerações infinitas, a geração seguinte tende a aumentar ");
                        write.printf("%.2f", ((assimtotico * 100) - 100));
                        write.println("% em relação à anterior.");
                    }
                    if (assimtotico == 1) {
                        write.println("A população desta espécie tem tendência a estagnar. Após gerações infinitas a geração seguinte tende a ter população igual à anterior.");

                    }*/
                    write.println();

                    double vetoresProprios[][] = vector.toDenseMatrix().toArray();


                    double valoresProprios[][] = valor.toDenseMatrix().toArray();


                    for (int a = 0; a < valoresProprios.length; a++) {

                        for (int b = 0; b < valoresProprios.length; b++) {

                            if (assimtotico == valoresProprios[a][b]) {

                                coluna = b;

                            }
                        }
                    }

                    for (int j = 0; j < vetoresProprios.length; j++) {

                        soma = vetoresProprios[j][coluna] + soma;
                    }


                    double[] divisoes = new double[vetoresProprios.length];

                    for (int con = 0; con < divisoes.length; con++) {

                        divisoes[con] = vetoresProprios[con][coluna] / soma;

                    }
                    write.print("Vetor Próprio = (");
                    write.printf("%.2f", divisoes[0] * 100);

                    for (int i = 1; i < divisoes.length; i++) {
                        write.print(", ");
                        write.printf("%.2f", divisoes[i] * 100);
                    }
                    write.println(")");
                    write.println();
                }

                write.close();

                Graphs(2, formatograph, especie);
                Graphs(3, formatograph, especie);
            }
        }catch (FileNotFoundException | InterruptedException e)
        {
            System.out.println("O ficheiro introduzido nao existe. Verifique o nome e se este se encontra no mesmo local que a aplicacao.");
        }


    }

    public static void InterativoTxt(String[]args, boolean interactive) throws IOException, InterruptedException {
        try {
            File evolucao = new File(args[1]);  //file.txt ---- nomeficheiro
            Scanner sc = new Scanner(evolucao);
            String vetores = "", sobrevivencia = "", fecundidade = "";
            while (sc.hasNextLine()) {
                String linha = sc.nextLine();
                if ((String.valueOf(linha.charAt(0))).compareTo("x") == 0) {
                    vetores = linha;
                } else if ((String.valueOf(linha.charAt(0))).compareTo("s") == 0) {
                    sobrevivencia = linha;
                } else if ((String.valueOf(linha.charAt(0))).compareTo("f") == 0) {
                    fecundidade = linha;
                }
            }
            double[] vetor = LeituraEmArrays(vetores);  //Vetor Inicial
            double[] taxadesobre = LeituraEmArrays(sobrevivencia);
            double[] fecundidades = LeituraEmArrays(fecundidade);
            double[][] Leslie = ArrayDeLeslie(taxadesobre, fecundidades);  //Matriz de Leslie
            boolean dim=true;
            TempToGraphsDim(populaçao(Leslie,vetor,1),dim,1);
            Mostra(0,"f");
            abertura();
            String especie = especie();
            menu(Leslie,vetor,especie);
        }catch(FileNotFoundException e)
        {
            System.out.println("O ficheiro introduzido nao existe. Verifique o nome e se este se encontra no mesmo local que a aplicacao.");
        }
    }

    public static void InterativoInterface() throws IOException, InterruptedException {
        abertura();
        String especie = especie();
        System.out.println();
        System.out.println("A aplicaçao foi iniciada em Modo Interativo Insercao Manual, começe por inserir os dados.");
        int dim =dimensao();
        double[]vetorinicial = vetorinicial(dim);
        double[][]Leslie = PreencherDados(dim);
        boolean f=true;
        TempToGraphsDim(populaçao(Leslie,vetorinicial,1),f,1);
        Mostra(0,"f");
        System.out.print("\n Os dados foram preenchidos com sucesso. Ao selecionar uma funcionalidade que necessite de cálculo, serão usados estes dados.\n\n");
        menu (Leslie, vetorinicial, especie);
    }

    //------Menus------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void abertura() {
        System.out.print("\n\n\n----------------- Estudo do modelo de população formalizado por Leslie. -----------------\n");
        System.out.print("\nSempre que for necessário inserir números com parte decimal, utilize a vírgula.\n\n");
    }  //Titulo da app

    public static String especie()  //para saber o nome da especie
    {
        System.out.print("Introduza o nome da especie/populacao que vai ser estudada: ");
        String especie = in.nextLine();   //Nome da Especie
        return  especie;
    }

    public static int geracoes()   //quantos geracoes deseja calcular
    {
        System.out.println();
        System.out.println();
        System.out.println("Introduza o número de geracoes a estimar!");
        System.out.println();
        System.out.println("Exemplo: Caso deseja saber na geracao inicial, deve introduzir o valor 0.");
        System.out.print("---> ");
        int g = in.nextInt();
        System.out.println();
        return g;
    }

    public static int menu (double[][] Leslie, double[] vetorinicial, String especie) throws IOException, InterruptedException {
        System.out.println();
        System.out.println("[------------------------MENU PRINCIPAL------------------------]");
        System.out.println();
        System.out.println("0 ---> INTRODUZIR/MUDAR OS DADOS");
        System.out.println("1 ---> DISTRIBUICAO NORMALIZADA E NAO NORMALIZADA DA POPULAÇAO");
        System.out.println("2 ---> DIMENSÃO DA POPULACÃO");
        System.out.println("3 ---> TAXA DE VARIACAO");
        System.out.println("4 ---> COMPORTAMENTO ASSIMTÓTICO");
        System.out.println("5 ---> EXECUCAO DE TODAS AS FUNCIONALIDADES");
        System.out.println("6 ---> GRÁFICOS");
        System.out.println("7 ---> SAIR DA APLICACAO");
        System.out.print("\nSelecione uma das opções apresentadas no menu.\n---> ");
        int number = in.nextInt();
        while ((number<0)  || (number>7)){
            System.out.print("\nSelecione uma das opções apresentadas no menu.\n---> ");
            number = in.nextInt();
        }
        switch (number) {
            case 0:
                Clear();introducaoDados(Leslie, vetorinicial, especie);
                break;
            case 1:  Clear();distpopulaçao(Leslie, vetorinicial); submenu(1, Leslie, vetorinicial,especie);
                break;
            case 2:  Clear();dimpopulaçao(Leslie, vetorinicial); submenu(2, Leslie, vetorinicial,especie);
                break;
            case 3: Clear();taxavarpop(Leslie,vetorinicial); submenu(3, Leslie, vetorinicial,especie);
                break;
            case 4:  Clear();Assimtotico(Leslie);menu(Leslie, vetorinicial, especie);
                break;
            case 5:  Clear();distpopulaçao(Leslie, vetorinicial); dimpopulaçao(Leslie, vetorinicial); taxavarpop(Leslie,vetorinicial);Assimtotico(Leslie); submenu(5, Leslie, vetorinicial,especie);
                break;
            case 6: Clear(); menugráficos(Leslie, vetorinicial, especie);
                break;
            case 7:System.exit(0);
                break;
            default:



        }
        return number;
    }   //menu principal

    public static void submenu(int opcao, double[][] Leslie, double[] vetorinicial, String especie) throws IOException, InterruptedException {

        for (int x=0; x<4; x++) {
            System.out.println("");
        }
        System.out.println("[---------------SUBMENU---------------]");
        System.out.println();
        System.out.println("1 ---> REPETIR COM MUDANÇA DE GERACAO");
        System.out.println("2 ---> VOLTAR AO MENU INICIAL");
        System.out.println("3 ---> SAIR DA APLICACAO");
        System.out.print("\nSelecione uma das opções apresentadas no menu.\n---> ");
        int number = in.nextInt();
        while ((number<=0)  || (number>3)){
            System.out.print("\nSelecione uma das opções apresentadas no menu.\n---> ");
            number = in.nextInt();
        }
        switch (number) {
            case 1: switch (opcao) {
                case 1: Clear();distpopulaçao(Leslie, vetorinicial); submenu(1, Leslie, vetorinicial,especie);
                    break;
                case 2: Clear();dimpopulaçao(Leslie, vetorinicial); submenu(2, Leslie, vetorinicial,especie);
                    break;
                case 3: Clear();taxavarpop(Leslie,vetorinicial); submenu(3,Leslie,vetorinicial,especie);
                    break;
                case 4: Clear();Assimtotico(Leslie); submenu(4, Leslie, vetorinicial,especie);
                    break;
                case 5: Clear();distpopulaçao(Leslie, vetorinicial); dimpopulaçao(Leslie, vetorinicial); taxavarpop(Leslie,vetorinicial);Assimtotico(Leslie); submenu(5, Leslie, vetorinicial,especie);
                    break;
            }
                break;
            case 2: Clear();menu(Leslie, vetorinicial,especie); break;
            case 3: System.exit(0); break;
        }
    }   //submenu o que aparece depois de cada operacao

    public static void menugráficos (double[][] Leslie, double[] vetorinicial, String especie) throws IOException, InterruptedException {
        System.out.print("\n \nSelecione o gráfico que deseja visualizar:\n");
        System.out.println("1 ---> DISTRIBUIÇÃO NÃO NORMALIZADA DA POPULAÇÃO EM FUNÇAO DO TEMPO");
        System.out.println("2 ---> DISTRIBUIÇÃO NORMALIZADA DA POPULAÇÃO EM FUNÇAO DO TEMPO");
        System.out.println("3 ---> DIMENSÃO DA POPULAÇÃO EM FUNÇAO DO TEMPO");
        System.out.println("4 ---> TAXA DE VARIAÇÃO DA POPULAÇÃO EM FUNÇAO DO TEMPO");
        System.out.println("5 ---> VOLTAR AO MENU INICIAL");
        System.out.println("6 ---> SAIR DA APLICAÇÃO");
        System.out.println("");
        System.out.print("\nSelecione uma das opções apresentadas no menu.\n---> ");
        int number = in.nextInt();
        while ((number<=0)  || (number>6)){
            System.out.print("\nSelecione uma das opções apresentadas no menu.\n---> ");
            number = in.nextInt();
        }
        switch (number) {
            case 1: Clear();Grafico(2,especie,Leslie,vetorinicial);menugráficos(Leslie, vetorinicial, especie);
            case 2: Clear();Grafico(3,especie,Leslie,vetorinicial);menugráficos(Leslie, vetorinicial, especie);
            case 3: Clear();Grafico(0,especie,Leslie,vetorinicial);menugráficos(Leslie, vetorinicial, especie);
            case 4: Clear();Grafico(1,especie,Leslie,vetorinicial);menugráficos(Leslie, vetorinicial, especie);
            case 5: Clear();menu(Leslie, vetorinicial,especie);
            case 6:System.exit(0);
        }
    }   //menu para os graficos

    public static void introducaoDados (double[][] Leslie,double[] vetorinicial,String especie) throws IOException, InterruptedException {
        System.out.println("[-----------Introduzir Dados-----------]");
        System.out.println();
        System.out.println("1 ---> FICHEIRO DE TEXTO");
        System.out.println("2 ---> INTRODUZIR MANUAL");
        System.out.println("3 ---> VOLTAR AO MENU PRINCIPAL");
        System.out.print("\nSelecione uma das opções apresentadas no menu.\n---> ");
        int number = in.nextInt();
        while ((number<=0)  || (number>3)){
            System.out.print("\nSelecione uma das opções apresentadas no menu.\n---> ");
            number = in.nextInt();
        }
        switch (number) {
            case 1: Clear();
                in.nextLine();
                System.out.println("\n" + "\n");
                System.out.println("Introduza o nome do ficheiro que deseja carregar. Verifique que se encontra presente na mesma pasta que a aplicação");
                String ficheiroentrada = in.nextLine();
                try {
                    File evolucao = new File(ficheiroentrada);  //file.txt ---- nomeficheiro
                    Scanner sc = new Scanner(evolucao);
                    String vetores = "", sobrevivencia = "", fecundidade = "";
                    while (sc.hasNextLine()) {
                        String linha = sc.nextLine();
                        if ((String.valueOf(linha.charAt(0))).compareTo("x") == 0) {
                            vetores = linha;
                        } else if ((String.valueOf(linha.charAt(0))).compareTo("s") == 0) {
                            sobrevivencia = linha;
                        } else if ((String.valueOf(linha.charAt(0))).compareTo("f") == 0) {
                            fecundidade = linha;
                        }
                    }
                    double[]vetor = LeituraEmArrays(vetores);  //Vetor Inicial
                    double[] taxadesobre = LeituraEmArrays(sobrevivencia);
                    double[] fecundidades = LeituraEmArrays(fecundidade);
                    Leslie = ArrayDeLeslie(taxadesobre, fecundidades);  //Matriz de Leslie
                    especie = especie();
                    menu(Leslie,vetor,especie);
                } catch (FileNotFoundException e) {
                    System.out.println("O ficheiro introduzido nao existe. Verifique o nome e se este se encontra no mesmo local que a aplicacao.");
                }
                break;
            case 2: Clear();in.nextLine();
                especie = especie();
                int dim =dimensao();
                vetorinicial = vetorinicial(dim);
                Leslie = PreencherDados(dim);
                System.out.print("\n Os dados foram preenchidos com sucesso. Ao selecionar uma funcionalidade que necessite de cálculo, serão usados estes dados.\n\n");
                menu (Leslie, vetorinicial, especie);
                break;
            case 3: Clear();menu(Leslie, vetorinicial,especie); break;
        }
    }   //introduzir dados de um ficheiro ou manualmente

    //------Clear------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void Clear() throws IOException {
        for(int i=0;i<80;i++)
        {
            System.out.println();
        }
    }
}


