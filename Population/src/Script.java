import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Script {           //Interactive
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        String terminal = "", output = "", title = "", file = "", save = "", s="";
        int flag=0;
        System.out.println(" /////////Graphics/////////");
        System.out.println("--------[Population Size Over Time]--------");
        int counter=0;
        System.out.print("\n");
        Save(counter);

        System.out.println("-----[Rate of Population Change Over Time]-----");
        counter++;
        System.out.print("\n");
        Save(counter);

        System.out.println("--------[Evolution of Population Distribution]--------");
        counter++;
        System.out.print("\n");
        Save(counter);

        System.out.println("-[Distribution Normalised by the Total Population Over Time]-");
        counter++;
        System.out.print("\n");
        File script = Save(counter);
        script.delete();

    }

    public static int Graphs(int counter) throws FileNotFoundException { //creates a command to execute | Receives as parameter the graph it needs to make
        int format;
        System.out.print("Enter the number that corresponds to the desired format (1-PNG) (2-TXT) (3-EPS): ");
        do {
            format = sc.nextInt();
            System.out.print("\n");
            if(format!=1 && format!=2 && format!=3)
            {
                System.out.print("Not a valid file format, try another: ");
            }
        }while(format!=1 && format!=2 && format!=3);
        System.out.print("\n");
        String[] var = Variables(counter,format);
        String[] gnuplot = {"C:\\Program Files\\gnuplot\\bin\\gnuplot.exe", String.valueOf(Script(var))}; //creating a command that opens the gnuplot application and executes the script
        GnuPlot(gnuplot);
        return format;
    }

    public static void GnuPlot(String[] gnuplot) {
        try{
            Process p = Runtime.getRuntime().exec(gnuplot);  //executes the command received
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public static File Script(String[] var) throws FileNotFoundException {   //script creation
        File script = new File("script.sh");
        PrintWriter write = new PrintWriter(script);

        write.print("#!/bin/bash"+"\n");
        write.print("set terminal "+var[0]+"\n");
        write.print("set output "+"\""+var[1]+"\""+"\n");
        write.print("\n");
        write.print("set title "+"\""+var[2]+"\""+"\n");
        write.print("set xlabel "+"\""+var[3]+"\""+"\n");
        write.print("set ylabel "+"\""+var[4]+"\""+"\n");
        write.print("\n");
        write.print("plot sin(x) title 'sin(x)' with lines linestyle 1"+"\n");
        write.print("exit");

        write.close();
        return script;
    }

    public static String[] Variables(int counter, int format) // script variables
    {
        String terminal = "", output = "", title = "", xlabel = "", ylabel = "";
        int flag=1;
        switch(counter){
            case 0:
                title = "Population Size Over Time";
                xlabel = "Years";
                ylabel = "Population Size";
                break;
            case 1:
                title = "Rate of Population Change Over Time";
                xlabel = "Years";
                ylabel = "Population Change Rate";
                break;
            case 2:
                title = "Evolution of Population Distribution";
                xlabel = "Years";
                ylabel = "Population Distribution";
                break;
            case 3:
                title = "Distribution Normalised by Total Population Over Time";
                xlabel = "Years";
                ylabel = "Distribution Normalised by Total Population";
                break;
            default:
                title = "FINISHED";
        }

        switch (format) {
            case 1:
                terminal = "pngcairo";
                output = title + ".png";
                flag=0;
                break;
            case 2:
                terminal = "dumb";
                flag=0;
                output = title + ".txt";
                break;
            case 3:
                terminal = "postscript";
                flag=0;
                output = title + ".eps";
                break;
            default:
                System.out.println("Unexpected value: " + format);
                flag=1;
        }
        String[] var = new String[5];
        var[0] = terminal;
        var[1] = output;
        var[2] = title;
        var[3] = xlabel;
        var[4] = ylabel;
        return var;
    }

    public static File Save(int counter) throws FileNotFoundException {
        int format = Graphs(counter);
        System.out.print("Do you want to save a copy of the graph? (s/n) : ");
        String s = sc.nextLine();
        String save = sc.nextLine();
        File copy = new File(Variables(counter,format)[1]);
        if(save.compareTo("n")==0)
        {
            copy.delete();
        }
        System.out.print("\n");
        System.out.print("\n");
        return Script(Variables(counter,format));
    }


}
