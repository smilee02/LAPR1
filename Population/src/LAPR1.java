import org.la4j.Matrix;
import org.la4j.decomposition.EigenDecompositor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class LAPR1 {
    static Scanner in = new Scanner(System.in);
    public static void main(String[] args) throws IOException, InterruptedException {
        boolean interactive = true;

            if (args.length <= 2) //Interactive
        {
            interactive = true;
        }

        else if(args.length > 2) //Non-Interactive
        {
            interactive = false;
        }

        if (interactive && args.length==0)  //Interactive Interface
        {
            InteractiveInterface();
        }

        else if (interactive && args[0].compareTo("-n") == 0)   //Interactive with TXT
        {
            InteractiveTxt(args,interactive);
        }

        else if (!interactive) //Non-Interactive with TXT
        {
            NonInteractiveTxt(args,interactive);
        }
    }

    //------Escrita Manual-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static double[][] FillData(int dim) {
        // Initialize the Leslie matrix with dimensions dim x dim
        double[][] Leslie = new double[dim][dim];
        // Fill in fertility rates
        fertility(dim, Leslie);
        // Fill in survival rates
        survival(dim, Leslie);
        // Return the populated Leslie matrix
        return Leslie;
    }

    // Method to get user input for the dimension of the Leslie matrix
    public static int dimension() {
        System.out.print("\n \nEnter the number of age groups desired:\n---> ");
        int dim = in.nextInt();
        return dim;
    }

    // Method to get user input for the initial population vector
    public static double[] initialVector(int dim) {
        System.out.println();
        System.out.println();
        // Initialize the initial population vector with length dim
        double[] initialVector = new double[dim];
        // Loop through each age group to get the number of individuals
        for (int x = 0; x < dim; x++) {
            System.out.print("Enter the number of reproducing individuals in age group " +(x+1)+ ":\n---> ");
            initialVector[x] = in.nextDouble();
        }
        return initialVector;
    }

    // Method to get user input for fertility rates
    public static double[][] fertility(int dim, double[][] Leslie) {
        System.out.println();
        System.out.println();
        // Loop through each age group to get fertility rates
        for (int x= 0; x < dim; x++) {
            System.out.print("Enter the fertility rate for age group " +(x+1)+ ":\n---> ");
            Leslie[0][x] = in.nextDouble();
        }
        return Leslie;
    }

    // Method to get user input for survival rates
    public static double[][] survival(int dim, double[][] Leslie) {
        System.out.println();
        System.out.println();
        // Define the upper limit for the loop to dim - 1
        int n = dim - 1;
        // Loop through each age group to get survival rates
        for (int x = 0; x < n; x++) {
            System.out.print("Enter the survival rate for age group " +(x+1)+ ":\n---> ");
            Leslie[x + 1][x] = in.nextDouble();
        }
        return Leslie;
    }



    //------Read TXT File-----------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static double[] ReadIntoArrays(String s) {
        int i = 0, j=0;
        String[] arr = s.split(",");
        while(j<arr.length)
        {
            String a = arr[j];
            if(!a.contains("="))
            {
                System.out.println("The file format is not supported. Check the entries and restart the application.");
                DeleteTempFiles();
                System.exit(0);
            }
            j++;
        }
        double[] aux1 = new double[arr.length];
        int greater = -1;
        while (i < arr.length) {
            String o = arr[i];
            String[] aux = o.split("=");
            String helper = aux[0].substring(1);
            if(Integer.parseInt(helper)<greater)
            {
                System.out.println("The file format is incorrect. Please check the entries and restart the application.");
                DeleteTempFiles();
                System.exit(0);
            }
            greater = Integer.parseInt(helper);
            aux1[i] = Double.parseDouble(aux[1]);
            i++;
        }
        return aux1;
    }

    public static double[][] LeslieArray(double[] rates, double[] populationNumbers) {
        int j = 0;
        double[][] Leslie = new double[populationNumbers.length][populationNumbers.length];  //the matrix is nxn (square)
        for (int i = 0; i < populationNumbers.length; i++) {
            Leslie[0][i] = populationNumbers[i];    //fills the first row
        }
        while (j < rates.length) {
            Leslie[j + 1][j] = rates[j];  //survival rate on the diagonal
            j++;
        }
        return Leslie;
    }


//------Population Distribution------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void populationDistribution(double[][] Leslie, double[] InitialVector) throws FileNotFoundException {
        boolean norm = true;
        System.out.print("\n\n\nEnter the number of generations to estimate!");
        System.out.print("\n\nExample: If you want to know the distribution of the initial generation, enter the value 0.\n---> ");
        int g = in.nextInt();
        System.out.println();
        while (g < 0) {
            System.out.print("\nError! Enter a number greater than or equal to 0");
            g = in.nextInt();
        }
        double[] auxInitialVector = InitialVector;

        double[][] populationDistribution= new double[g + 1][InitialVector.length];
        for (int y = 0; y < InitialVector.length; y++) {
            populationDistribution[0][y] = InitialVector[y];
        }
        for (int x = 1; x <= g; x++) {
            InitialVector = multiplyMatricesVectors(power(Leslie, (x - 1)), auxInitialVector);
            for (int y = 0; y < InitialVector.length; y++) {
                populationDistribution[x][y] = InitialVector[y];
            }
        }
        double[] population = population(Leslie, auxInitialVector, g);
        for (int n = 0; n <= g; n++) {
            System.out.print("\n\n\n----------------------------------------------------[" + n + "]----------------------------------------------------");
            System.out.print("\n\nData related to population distribution in generation: " + n + "\n");
            for (int k = 0; k < InitialVector.length; k++) {
                System.out.print("The population of the " + (k + 1) + "th age group is ");
                System.out.printf("%.2f",populationDistribution[n][k]);
                System.out.print(", which corresponds to a normalized distribution of ");
                System.out.printf("%.2f",(populationDistribution[n][k] * 100 / population[n]));
                System.out.println("%");
            }
        }
        TempToGraphsDis(populationDistribution,population,norm,InitialVector.length,g);
        norm = !norm;
        TempToGraphsDis(populationDistribution,population,norm,InitialVector.length,g);
    }

//------Population Size----------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void populationSize(double[][] Leslie, double[] InitialVector) throws FileNotFoundException {
        System.out.print("\n\n\nEnter a value, referring to a generation, for which you want to calculate the population size!");
        System.out.println("\n\nExample: If you want to know the size of the initial generation, enter the value 0.\n---> ");
        int t = in.nextInt();
        System.out.println();
        while (t < 0) {
            System.out.println("Enter a number greater than or equal to 0");
            t = in.nextInt();
        }
        double[] population = population(Leslie, InitialVector, t);
        System.out.println();
        for(int i=0;i<=t;i++)
        {
            System.out.print("The population size for t=" + i + " is ");
            System.out.printf("%.2f", population[i]);
            System.out.println();
        }
        System.out.println();
        System.out.println();


    }


    //------Population Growth Rate---------------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void populationGrowthRate(double[][] Leslie, double[] InitialVector) throws FileNotFoundException{
        boolean dim = true;
        System.out.println("\n\n\nEnter a value, referring to a generation, for which you want to calculate the population growth rate!");
        System.out.println("\n\nExample: If you want to know the growth rate of the initial generation, enter the value 0.\n---> ");
        int t = in.nextInt();
        System.out.println();
        while (t < 0) {
            System.out.println("Enter a number greater than or equal to 0");
            t = in.nextInt();
        }
        double[] population = population(Leslie, InitialVector, t);
        if(t>0) {
            for (int i = 0; i < t; i++) {
                System.out.print("The growth rate between t=" + i + " and t= " + (i+1) + " is ");
                System.out.printf("%.2f", (population[i + 1] / population[i]));
                System.out.println();
            }
        }
        else if (t==0)
        {
            System.out.println("t=0 is the initial moment, so there is no growth rate.");
        }
    }

//------Auxiliary Population Methods--------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static double[] population(double[][] Leslie, double[] InitialVector, int t) {
        double[] population = new double[t + 1];
        double sum1 = 0;
        for (int x = 0; x < InitialVector.length; x++) {
            sum1 = sum1 + InitialVector[x];
            population[0] = sum1;
        }
        for (int a = 0; a < t; a++) {
            double[] aux = multiplyMatricesVectors(power(Leslie, a), InitialVector);
            double sum = 0;
            for (int k = 0; k < aux.length; k++) {
                sum = sum + aux[k];
                population[a + 1] = sum;
            }
        }
        return population;
    }

    public static double[] multiplyMatricesVectors(double[][] Matrix, double[] Vector) {
        int dim = Vector.length;
        double[] product = new double[dim];
        for (int x = 0; x < dim; x++) {
            for (int y = 0; y < dim; y++) {
                product[x] = product[x] + Matrix[x][y] * Vector[y];
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
        for (int x = 0; x < firstMatrix.length; x++) {
            for (int y = 0; y < firstMatrix.length; y++) {
                for (int p = 0; p < firstMatrix.length; p++) {
                    product[x][y] += firstMatrix[x][p] * secondMatrix[p][y];
                }
            }
        }
        return product;
    }


    //------Graphs-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void Graph(int counter, String species, double[][] Leslie, double[] initialVector) throws IOException, InterruptedException {
        int type;
        boolean norm, dim;
        switch (counter) {
            case (0):
                System.out.println("-----------------------------------[Population Size Over Time]------------------------------------");
                System.out.print("\n");
                break;
            case (1):
                System.out.println("-------------------------------[Population Growth Rate Over Time]--------------------------------");
                System.out.print("\n");
                break;
            case (2):
                System.out.println("------------------------------------[Population Distribution Evolution]-------------------------------------");
                System.out.print("\n");
                break;
            case (3):
                System.out.println("------------------------[Distribution Normalized by Total Population Over Time]--------------------");
                System.out.print("\n");
                break;
        }
        int generation = generations();
        switch (counter){
            case 0:
                dim=true;
                population(Leslie, initialVector, generation);
                TempToGraphsDim(population(Leslie, initialVector, generation), dim, generation);
                break;
            case 1:
                dim=false;
                population(Leslie, initialVector, generation);
                TempToGraphsDim(population(Leslie, initialVector, generation), dim, generation);
                break;
            case 2:
                norm= false;
                populationDistribution(Leslie, initialVector, generation, norm);
                break;
            case 3:
                norm = true;
                populationDistribution(Leslie, initialVector, generation, norm);
                break;
        }
        type = fileType();
        ShowGraph(counter, species);
        GraphProcessing(counter, type, species);
        OutputGraphs(counter);
        SaveGraph(counter, type, species);
        boolean deleted = (new File("script.sh").delete());
    }

    public static int fileType() {
        int format;
        System.out.print("\nEnter the number corresponding to the desired file format (1-PNG) (2-TXT) (3-EPS): ");
        do {
            format = in.nextInt();
            if (format != 1 && format != 2 && format != 3) {
                System.out.print("\nNot a valid file format, try another one (1-PNG) (2-TXT) (3-EPS): ");
            }
        } while (format != 1 && format != 2 && format != 3);
        System.out.print("\n");
        return format;
    }

    public static void GraphProcessing(int counter, int format, String species) throws IOException, InterruptedException {
        Thread.sleep(250);
        String[] variables = GraphVariables(counter, format, species);
        File script = GraphScript(variables);
        String[] gnuplotCommand = {"C:\\Program Files\\gnuplot\\bin\\gnuplot.exe","-p", String.valueOf(script)};
        ExecuteGnuPlot(gnuplotCommand);
    }

    public static void ShowGraph(int counter, String species) throws IOException, InterruptedException {
        String[] vars = GraphVariables(counter, 1, species);
        vars[0] = "png";
        vars[1] = "show.png";
        File script = GraphScript(vars);
        String[] showCommand = {"C:\\Program Files\\gnuplot\\bin\\gnuplot.exe","-p", String.valueOf(script)};
        ExecuteGnuPlot(showCommand);
        String[] cmd = {"cmd.exe","/c","show.png"};
        ExecuteGnuPlot(cmd);
        Thread.sleep(250);
    }

    public static void ExecuteGnuPlot(String[] gnuplotCommand) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec(gnuplotCommand);
    }

    public static File GraphScript(String[] variables) throws FileNotFoundException {
        File script = new File("script.sh");
        PrintWriter writer = new PrintWriter(script);
        File file = new File(variables[5]);
        if(file.exists()) {
            Scanner scanner = new Scanner(file);
            String generations = scanner.nextLine();
            String[] gen = generations.split(" ");
            writer.print("#!/bin/bash" + "\n");
            writer.print("set terminal " + variables[0] + "\n");
            writer.print("set output " + "\"" + variables[1] + "\"" + "\n");
            writer.print("\n");
            writer.print("set title " + "\"" + variables[2] + "\"" + "\n");
            writer.print("set xlabel " + "\"" + variables[3] + "\"" + "\n");
            writer.print("set ylabel " + "\"" + variables[4] + "\"" + "\n");
            writer.print("\n");
            writer.print("plot ");
            if (variables[5].compareTo("population.txt") == 0 || variables[5].compareTo("growthrate.txt") == 0) {
                writer.print("'" + variables[5] + "' using 1:2 with lines t \"\" ");
            } else {
                for (int i = 0; i < gen.length; i++) {
                    writer.print("'" + variables[5] + "' using 1:" + (i + 2) + " t " + "\"" + (i + 1) + "th age group\" " + " with lines linestyle " + (i + 1));
                    if (i != (gen.length - 1)) {
                        writer.print(", ");
                    }
                }
            }
            writer.print("\n");
            writer.print("exit");
        }
        writer.close();
        return script;
    }

    public static String[] GraphVariables(int counter, int format, String species) {
        String terminal = "", output = "", title = "", xlabel = "", ylabel = "", file = "";
        switch (counter) {
            case 0:
                title = "Population Size Over Time";
                xlabel = "Generations";
                ylabel = "Population Size";
                file = "population.txt";
                break;
            case 1:
                title = "Population Growth Rate Over Time";
                xlabel = "Generations";
                ylabel = "Population Growth Rate";
                file = "growthrate.txt";
                break;
            case 2:
                title = "Population Distribution Evolution";
                xlabel = "Generations";
                ylabel = "Population Distribution";
                file = "populationdistribution.txt";
                break;
            case 3:
                title = "Distribution Normalized by Total Population Over Time";
                xlabel = "Generations";
                ylabel = "Normalized Population Distribution";
                file = "normalizedpopulationdistribution.txt";
                break;
            default:
                title = "ENDED";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String dateFormatted = formatter.format(date);
        switch (format) {
            case 1:
                terminal = "pngcairo";
                output = title + " " + species + " " + dateFormatted + ".png";
                break;
            case 2:
                terminal = "dumb";
                output = title + " " + species + " " + dateFormatted + ".txt";
                break;
            case 3:
                terminal = "postscript";
                output = title + " " + species + " " + dateFormatted + ".eps";
                break;
            default:
                System.out.println("Unexpected value: " + format);
        }
        String[] vars = new String[6];
        vars[0] = terminal;
        vars[1] = output;
        vars[2] = title;
        vars[3] = xlabel;
        vars[4] = ylabel;
        vars[5] = file;
        return vars;
    }


    public static File SaveGraph(int counter, int format, String species) throws IOException {
        System.out.print("Do you want to save a copy of the graph? (y/n): ");
        String save = in.nextLine();
        while (!save.equalsIgnoreCase("n") && !save.equalsIgnoreCase("y")) {
            System.out.print("\"" + save + "\"" + " is not a valid format. Please enter either 'y' or 'n': ");
            save = in.nextLine();
        }
        File copy = new File(GraphVariables(counter, format, species)[1]);
        if (save.equalsIgnoreCase("n")) {
            copy.delete();
        }
        System.out.print("\n\n");
        return GraphScript(GraphVariables(counter, format, species));
    }

    public static void DeleteTempFiles() {
        String[] files = {"population.txt", "growthrate.txt", "populationdistribution.txt", "normalizedpopulationdistribution.txt", "script.sh", "show.png"};
        for (String file : files) {
            new File(file).delete();
        }
    }

//------File Input/Output---------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static File TempToGraphsDim(double[] arr, boolean dim, int t) throws FileNotFoundException {
        File file;
        if (dim) {
            file = new File("population.txt");
            PrintWriter write = new PrintWriter(file);
            for (int i = 0; i <= t; i++) {
                write.print(i + " ");
                write.printf("%.2f", arr[i]);
                write.println();
            }
            write.close();
        } else {
            file = new File("growthrate.txt");
            PrintWriter write = new PrintWriter(file);
            for (int i = 0; i < t; i++) {
                write.print(i + " ");
                write.printf("%.2f", (arr[i + 1] / arr[i]));
                write.println();
            }
            write.close();
        }
        return file;
    }

    public static File TempToGraphsDis(double[][] arr, double[] aux, boolean norm, int columns, int rows) throws FileNotFoundException {
        File file;
        if (!norm) {
            file = new File("populationdistribution.txt");
        } else {
            file = new File("normalizedpopulationdistribution.txt");
        }
        PrintWriter write = new PrintWriter(file);
        for (int i = 0; i <= rows; i++) {
            write.print(i);
            for (int j = 0; j < columns; j++) {
                write.print(" ");
                if (norm) {
                    write.printf("%.2f", (arr[i][j] * 100 / aux[i]));
                } else {
                    write.printf("%.2f", arr[i][j]);
                }
            }
            write.print("\n");
        }
        write.close();
        return file;
    }

    public static void OutputGraphs(int counter) throws FileNotFoundException {
        String fileName;
        switch (counter) {
            case (0):
                fileName = "population.txt";
                break;
            case (1):
                fileName = "growthrate.txt";
                break;
            case (2):
                fileName = "populationdistribution.txt";
                break;
            case (3):
                fileName = "normalizedpopulationdistribution.txt";
                break;
            default:
                fileName = "";
        }
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            System.out.println(scanner.nextLine());
        }
        System.out.println("\n");
    }

//------Auxiliary Population Distribution--------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void populationDistribution(double[][] Leslie, double[] initialVector, int generations, boolean norm) throws FileNotFoundException {
        double[] auxInitialVector = new double[initialVector.length];
        System.arraycopy(initialVector, 0, auxInitialVector, 0, initialVector.length);
        double[][] distribution = new double[generations + 1][initialVector.length];
        for (int y = 0; y < initialVector.length; y++) {
            distribution[0][y] = initialVector[y];
        }
        for (int x = 1; x <= generations; x++) {
            initialVector = multiplyMatricesVectors(power(Leslie, (x - 1)), auxInitialVector);
            for (int y = 0; y < initialVector.length; y++) {
                distribution[x][y] = initialVector[y];
            }
        }
        double[] population = population(Leslie, auxInitialVector, generations);
        TempToGraphsDis(distribution, population, norm, initialVector.length, generations);
    }


    //------Asymptotic------------------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void Asymptotic(double[][] Leslie) {
        Matrix matrix = Matrix.from2DArray(Leslie);
        EigenDecompositor decomp = new EigenDecompositor(matrix);
        Matrix[] result = decomp.decompose();
        Matrix vector = result[0];
        Matrix value = result[1];
        double asymptoticRate = value.max();
        System.out.print("\n\nThe asymptotic growth rate is = ");
        System.out.printf("%.4f", asymptoticRate);
        System.out.println();

    /*if (asymptoticRate < 1) {
        System.out.print("The population of this species tends to disappear. After infinite generations, the next generation tends to decrease ");
        System.out.printf("%.2f", (100 - (asymptoticRate * 100)));
        System.out.println("% compared to the previous one.");
    } else if (asymptoticRate > 1) {
        System.out.print("The population of this species tends to increase. After infinite generations, the next generation tends to increase ");
        System.out.printf("%.2f", ((asymptoticRate * 100) - 100));
        System.out.println("% compared to the previous one.");
    }
    if (asymptoticRate == 1) {
        System.out.println("The population of this species tends to stagnate. After infinite generations, the next generation tends to have the same population as the previous one.");
    }*/
        System.out.println();

        double[][] eigenvectors = vector.toDenseMatrix().toArray();
        double[][] eigenvalues = value.toDenseMatrix().toArray();
        int column = 0;
        double sum = 0;
        for (int a = 0; a < eigenvalues.length; a++) {
            for (int b = 0; b < eigenvalues.length; b++) {
                if (asymptoticRate == eigenvalues[a][b]) {
                    column = b;
                }
            }
        }
        for (int j = 0; j < eigenvectors.length; j++) {
            sum += eigenvectors[j][column];
        }
        double[] divisions = new double[eigenvectors.length];
        for (int con = 0; con < divisions.length; con++) {
            divisions[con] = eigenvectors[con][column] / sum;
        }
        System.out.print("\nEigenvector = (");
        System.out.printf("%.2f", divisions[0] * 100);
        for (int i = 1; i < divisions.length; i++) {
            System.out.print(", ");
            System.out.printf("%.2f", divisions[i] * 100);
        }
        System.out.println(")");
    }

//------Modes------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void NonInteractiveTxt(String[] args, boolean interactive) throws IOException {
        boolean eigenVector = false, populationDimension = false, populationVariation = false;  //("-e,-v,-r")
        int graphFormat = 1, generationsNumber = 1;
        for (int i = 0; i < args.length; i++) {
            if (args[i].compareTo("-t") == 0) {
                try {
                    generationsNumber = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.out.println("\"" + args[i + 1] + "\"" + " is not a valid format. Please enter a positive integer value.");
                    System.exit(0);
                }
            }
            if (args[i].compareTo("-g") == 0) {
                if (args[i + 1].compareTo("1") == 0 || args[i + 1].compareTo("2") == 0 || args[i + 1].compareTo("3") == 0) {
                    graphFormat = Integer.parseInt(args[i + 1]);
                } else {
                    System.out.println("\"" + args[i + 1] + "\"" + " is not a valid format. Please enter a positive integer value between 1 and 3.");
                    System.exit(0);
                }
            }
            if (args[i].compareTo("-e") == 0) {
                eigenVector = true;
            }
            if (args[i].compareTo("-v") == 0) {
                populationDimension = true;
            }
            if (args[i].compareTo("-r") == 0) {
                populationVariation = true;
            }
        }
        String inputFile = args[args.length - 2]; //penultimate position
        String outputFile = args[args.length - 1];  //last position
        File output = new File(outputFile);  //output file creation
        PrintWriter write = new PrintWriter(output);   //writing to the output file
        try {
            File evolution = new File(inputFile);  //file.txt ---- filename
            Scanner scanner = new Scanner(evolution);
            String vectors = "", survival = "", fecundity = "";
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if ((String.valueOf(line.charAt(0))).compareTo("x") == 0) {
                    vectors = line;
                } else if ((String.valueOf(line.charAt(0))).compareTo("s") == 0) {
                    survival = line;
                } else if ((String.valueOf(line.charAt(0))).compareTo("f") == 0) {
                    fecundity = line;
                }
            }
            double[] vector = ReadIntoArrays(vectors);  //Initial Vector
            if (vector.length > 200) { //ERROR IF MORE THAN 200 CLASSES
                System.out.print("\nError: The number of classes entered exceeds the maximum defined.");
                System.exit(0);
            } else {
                int index = inputFile.indexOf(".");
                boolean normalized = false;
                String species = inputFile.substring(0, index);
                double[] survivalRates = ReadIntoArrays(survival);
                double[] fecundityRates = ReadIntoArrays(fecundity);
                double[][] Leslie = LeslieArray(survivalRates, fecundityRates);  //Leslie Matrix
                populationDistribution(Leslie, vector, generationsNumber, normalized);
                normalized = true;
                populationDistribution(Leslie, vector, generationsNumber, normalized);
                write.println("Number of Generations = " + generationsNumber);
                write.print("\n" + "\n");
                write.println("Leslie Matrix");
                write.println("--------------");
                for (int i = 0; i < fecundityRates.length; i++) {
                    for (int j = 0; j < fecundityRates.length; j++) {
                        write.printf("%.2f", Leslie[i][j]);
                        write.print(" ");
                    }
                    write.print("\n");
                }
                write.println();
                //------------------------------------------------Total Number of Breeding Individuals
                if (populationDimension) {
                    boolean dim = true;
                    double[] population = population(Leslie, vector, generationsNumber);
                    TempToGraphsDim(population, dim, generationsNumber);
                    GraphProcessing(0, graphFormat, species);
                    write.print("------Total Number of Breeding Individuals------" + "\n");
                    write.print("\n");
                    write.print("( t, Nt)" + "\n");
                    for (int i = 0; i <= generationsNumber; i++) {
                        write.print("( " + i + ", ");
                        write.printf("%.2f", population[i]);
                        write.print(")" + "\n");
                    }
                    write.print("\n" + "\n" + "\n");
                }
                //------------------------------------------------Population Growth Rate
                if (populationVariation) {
                    boolean dim = false;
                    double[] population = population(Leslie, vector, generationsNumber);
                    TempToGraphsDim(population, dim, generationsNumber);
                    if (generationsNumber > 0) {
                        GraphProcessing(1, graphFormat, species);
                    }
                    write.print("------Population Growth Rate------" + "\n");
                    write.print("\n");
                    write.print("( t, Delta t)" + "\n");
                    for (int i = 0; i < generationsNumber; i++) {
                        write.print("( " + i + ", ");
                        write.printf("%.2f", (population[i + 1] / population[i]));
                        write.print(")" + "\n");
                    }
                    write.print("\n" + "\n" + "\n");
                }

                //------------------------------------------------Species Distribution by Age Groups
                {
                    File dispop = new File("populationdistribution.txt");
                    Scanner in = new Scanner(dispop);
                    write.println("--------Species Distribution by Age Groups--------" + "\n");
                    write.print("( t");
                    String[] arr = (in.nextLine()).split(" ");  //Discover the number of Age Groups
                    for (int i = 0; i < arr.length; i++) {
                        write.print(", x" + i);
                    }
                    write.println(")");
                    write.print("( 0");
                    for (int k = 1; k < arr.length; k++) {
                        write.print(", " + arr[k]);
                    }
                    write.print(")" + "\n");
                    for (int j = 1; j <= generationsNumber; j++) {
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

                //------------------------------------------------Species Distribution by Age Groups (Normalized)
                {
                    File dispopnorm = new File("normalizedpopulationdistribution.txt");
                    Scanner ler = new Scanner(dispopnorm);
                    write.println("--------Species Distribution by Age Groups (Normalized)--------" + "\n");
                    write.print("( t");
                    String[] zzz = (ler.nextLine()).split(" ");  //Discover the number of classes
                    for (int i = 0; i < zzz.length; i++) {
                        write.print(", x" + i);
                    }
                    write.println(")");
                    write.print("( 0");
                    for (int k = 1; k < zzz.length; k++) {
                        write.print(", " + zzz[k]);
                    }
                    write.print(")" + "\n");
                    for (int j = 1; j <= generationsNumber; j++) {
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

                //------------------------------------------------Asymptotic
                if (eigenVector) {
                    Asymptotic(Leslie);
                }
                write.close();
                GraphProcessing(2, graphFormat, species);
                GraphProcessing(3, graphFormat, species);
            }
        } catch (FileNotFoundException | InterruptedException e) {
            System.out.println("The entered file does not exist. Please check the name and make sure it is located in the same location as the application.");
        }
    }


    public static void InteractiveTxt(String[] args, boolean interactive) throws IOException, InterruptedException {
        try {
            File evolution = new File(args[1]); // file.txt ---- filename
            Scanner scanner = new Scanner(evolution);
            String vectors = "", survival = "", fecundity = "";
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (String.valueOf(line.charAt(0)).compareTo("x") == 0) {
                    vectors = line;
                } else if (String.valueOf(line.charAt(0)).compareTo("s") == 0) {
                    survival = line;
                } else if (String.valueOf(line.charAt(0)).compareTo("f") == 0) {
                    fecundity = line;
                }
            }
            double[] vector = ReadIntoArrays(vectors); // Initial Vector
            double[] survivalRates = ReadIntoArrays(survival);
            double[] fecundityRates = ReadIntoArrays(fecundity);
            double[][] Leslie = LeslieArray(survivalRates, fecundityRates); // Leslie Matrix
            boolean dim = true;
            TempToGraphsDim(population(Leslie, vector, 1), dim, 1);
            ShowGraph(0, "f");
            opening();
            String species = species();
            menu(Leslie, vector, species);
        } catch (FileNotFoundException e) {
            System.out.println("The entered file does not exist. Please check the name and make sure it is located in the same location as the application.");
        }
    }

    public static void InteractiveInterface() throws IOException, InterruptedException {
        opening();
        String species = species();
        System.out.println();
        System.out.println("The application has started in Interactive Mode with Manual Input. Begin by entering the data.");
        int dim = dimension();
        double[] initialVector = initialVector(dim);
        double[][] Leslie = FillData(dim);
        boolean f = true;
        TempToGraphsDim(population(Leslie, initialVector, 1), f, 1);
        ShowGraph(0, "f");
        System.out.print("\n The data has been successfully entered. When selecting a functionality that requires calculation, this data will be used.\n\n");
        menu(Leslie, initialVector, species);
    }

//------Menus------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void opening() {
        System.out.print("\n\n\n----------------- Study of the population model formalized by Leslie. -----------------\n");
        System.out.print("\nWhenever you need to enter numbers with a decimal part, use a comma.\n\n");
    } // Application Title

    public static String species() { // to know the name of the species
        System.out.print("Enter the name of the species/population to be studied: ");
        String species = in.nextLine(); // Species Name
        return species;
    }

    public static int generations() { // how many generations do you want to calculate
        System.out.println();
        System.out.println();
        System.out.println("Enter the number of generations to estimate!");
        System.out.println();
        System.out.println("Example: If you want to know in the initial generation, you must enter the value 0.");
        System.out.print("---> ");
        int g = in.nextInt();
        System.out.println();
        return g;
    }

    public static int menu(double[][] Leslie, double[] initialVector, String species) throws IOException, InterruptedException {
        System.out.println();
        System.out.println("[------------------------Main Menu------------------------]");
        System.out.println();
        System.out.println("0 ---> Enter/Change Data");
        System.out.println("1 ---> Normalized and Non-Normalized Population Distribution");
        System.out.println("2 ---> Population Dimension");
        System.out.println("3 ---> Population Variation Rate");
        System.out.println("4 ---> Asymptotic Behavior");
        System.out.println("5 ---> Execute All Functionalities");
        System.out.println("6 ---> Graphs");
        System.out.println("7 ---> Exit Application");
        System.out.print("\nSelect one of the options presented in the menu.\n---> ");
        int number = in.nextInt();
        while ((number < 0) || (number > 7)) {
            System.out.print("\nSelect one of the options presented in the menu.\n---> ");
            number = in.nextInt();
        }
        switch (number) {
            case 0:
                Clear();
                inputData(Leslie, initialVector, species);
                break;
            case 1:
                Clear();
                populationDistribution(Leslie, initialVector);
                submenu(1, Leslie, initialVector, species);
                break;
            case 2:
                Clear();
                populationSize(Leslie, initialVector);
                submenu(2, Leslie, initialVector, species);
                break;
            case 3:
                Clear();
                populationGrowthRate(Leslie, initialVector);
                submenu(3, Leslie, initialVector, species);
                break;
            case 4:
                Clear();
                Asymptotic(Leslie);
                menu(Leslie, initialVector, species);
                break;
            case 5:
                Clear();
                populationDistribution(Leslie, initialVector);
                populationSize(Leslie, initialVector);
                populationGrowthRate(Leslie, initialVector);
                Asymptotic(Leslie);
                submenu(5, Leslie, initialVector, species);
                break;
            case 6:
                Clear();
                menuGraphs(Leslie, initialVector, species);
                break;
            case 7:
                DeleteTempFiles();
                System.exit(0);
                break;
            default:
        }
        return number;
    } // main menu

    public static void submenu(int option, double[][] Leslie, double[] initialVector, String species) throws IOException, InterruptedException {
        for (int x = 0; x < 4; x++) {
            System.out.println("");
        }
        System.out.println("[---------------Submenu---------------]");
        System.out.println();
        System.out.println("1 ---> Repeat With Generation Change");
        System.out.println("2 ---> Return To Main Menu");
        System.out.println("3 ---> Exit Application");
        System.out.print("\nSelect one of the options presented in the menu.\n---> ");
        int number = in.nextInt();
        while ((number <= 0) || (number > 3)) {
            System.out.print("\nSelect one of the options presented in the menu.\n---> ");
            number = in.nextInt();
        }
        switch (number) {
            case 1:
                switch (option) {
                    case 1:
                        Clear();
                        populationDistribution(Leslie, initialVector);
                        submenu(1, Leslie, initialVector, species);
                        break;
                    case 2:
                        Clear();
                        populationSize(Leslie, initialVector);
                        submenu(2, Leslie, initialVector, species);
                        break;
                    case 3:
                        Clear();
                        populationGrowthRate(Leslie, initialVector);
                        submenu(3, Leslie, initialVector, species);
                        break;
                    case 4:
                        Clear();
                        Asymptotic(Leslie);
                        submenu(4, Leslie, initialVector, species);
                        break;
                    case 5:
                        Clear();
                        populationDistribution(Leslie, initialVector);
                        populationSize(Leslie, initialVector);
                        populationGrowthRate(Leslie, initialVector);
                        Asymptotic(Leslie);
                        submenu(5, Leslie, initialVector, species);
                        break;
                }
                break;
            case 2:
                Clear();
                menu(Leslie, initialVector, species);
                break;
            case 3:
                DeleteTempFiles();
                System.exit(0);
                break;
        }
    } // submenu what appears after each operation

    public static void menuGraphs(double[][] Leslie, double[] initialVector, String species) throws IOException, InterruptedException {
        System.out.println("\n" + "[---------------Graphs---------------]" + "\n");
        System.out.print("\n \nSelect the graph you want to view:\n");
        System.out.println("1 ---> Non-Normalized Population Distribution Over Time");
        System.out.println("2 ---> Normalized Population Distribution Over Time");
        System.out.println("3 ---> Population Dimension Over Time");
        System.out.println("4 ---> Population Variation Rate Over Time");
        System.out.println("5 ---> Return To Main Menu");
        System.out.println("6 ---> Exit Application");
        System.out.println("");
        System.out.print("\nSelect one of the options presented in the menu.\n---> ");
        int number = in.nextInt();
        while ((number <= 0) || (number > 6)) {
            System.out.print("\nSelect one of the options presented in the menu.\n---> ");
            number = in.nextInt();
        }
        switch (number) {
            case 1:
                Clear();
                Graph(2, species, Leslie, initialVector);
                menuGraphs(Leslie, initialVector, species);
            case 2:
                Clear();
                Graph(3, species, Leslie, initialVector);
                menuGraphs(Leslie, initialVector, species);
            case 3:
                Clear();
                Graph(0, species, Leslie, initialVector);
                menuGraphs(Leslie, initialVector, species);
            case 4:
                Clear();
                Graph(1, species, Leslie, initialVector);
                menuGraphs(Leslie, initialVector, species);
            case 5:
                Clear();
                menu(Leslie, initialVector, species);
            case 6:
                DeleteTempFiles();
                System.exit(0);
        }
    } // menu for graphs

    public static void inputData(double[][] Leslie, double[] initialVector, String species) throws IOException, InterruptedException {
        System.out.println("[-----------Enter Data-----------]");
        System.out.println();
        System.out.println("1 ---> Text File");
        System.out.println("2 ---> Manual Entry");
        System.out.println("3 ---> Return to Main Menu");
        System.out.print("\nSelect one of the options presented in the menu.\n---> ");
        int number = in.nextInt();
        while ((number <= 0) || (number > 3)) {
            System.out.print("\nSelect one of the options presented in the menu.\n---> ");
            number = in.nextInt();
        }
        switch (number) {
            case 1:
                Clear();
                in.nextLine();
                System.out.print("\n\n" + "\n");
                System.out.println("Enter the name of the file you want to load. Make sure it is present in the same folder as the application");
                System.out.print("--->");
                String inputFile = in.nextLine();
                System.out.println();
                try {
                    File evolution = new File(inputFile); // file.txt ---- filename
                    Scanner scanner = new Scanner(evolution);
                    String vectors = "", survival = "", fecundity = "";
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (String.valueOf(line.charAt(0)).compareTo("x") == 0) {
                            vectors = line;
                        } else if (String.valueOf(line.charAt(0)).compareTo("s") == 0) {
                            survival = line;
                        } else if (String.valueOf(line.charAt(0)).compareTo("f") == 0) {
                            fecundity = line;
                        }
                    }
                    double[] vector = ReadIntoArrays(vectors); // Initial Vector
                    double[] survivalRates = ReadIntoArrays(survival);
                    double[] fecundityRates = ReadIntoArrays(fecundity);
                    Leslie = LeslieArray(survivalRates, fecundityRates); // Leslie Matrix
                    species = species();
                    menu(Leslie, vector, species);
                } catch (FileNotFoundException e) {
                    System.out.println("The entered file does not exist. Please check the name and make sure it is located in the same location as the application.");
                }
                break;
            case 2:
                Clear();
                in.nextLine();
                species = species();
                int dim = dimension();
                initialVector = initialVector(dim);
                Leslie = FillData(dim);
                System.out.print("\n The data has been successfully entered. When selecting a functionality that requires calculation, this data will be used.\n\n");
                menu(Leslie, initialVector, species);
                break;
            case 3:
                Clear();
                menu(Leslie, initialVector, species);
                break;
        }
    } // enter data from a file or manually

//------Clear------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------O

    public static void Clear() throws IOException {
        for (int i = 0; i < 80; i++) {
            System.out.println();
        }
    }

}
