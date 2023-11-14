import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Script {           //modo interativo
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        String terminal = "", output = "", title = "", file = "", guardar = "", s="";
        int flag=0;
        System.out.println("               /////////Gráficos/////////");
        System.out.println("--------[Dimensão da População ao Longo do Tempo]--------");
        int contador=0;
        System.out.print("\n");
        Guardar(contador);

        System.out.println("-----[Taxa de Variação da População ao Longo do Tempo]-----");
        contador++;
        System.out.print("\n");
        Guardar(contador);

        System.out.println("--------[Evolução da Distribuição da População]--------");
        contador++;
        System.out.print("\n");
        Guardar(contador);

        System.out.println("-[Distribuição Normalizada Pelo Total da População Ao Longo do Tempo]-");
        contador++;
        System.out.print("\n");
        File script = Guardar(contador);
        script.delete();

    }

    public static int Graphs(int contador) throws FileNotFoundException {    //cria um comando para executar | Recebe como parametro o grafico que precisa de fazer
        int formato;
        System.out.print("Introduza o número que corresponde ao formato desejado (1-PNG) (2-TXT) (3-EPS): ");
        do {
            formato = sc.nextInt();
            System.out.print("\n");
            if(formato!=1 && formato!=2 && formato!=3)
            {
                System.out.print("Não é um formato de ficheiro válido, tente outro: ");
            }
        }while(formato!=1 && formato!=2 && formato!=3);
        System.out.print("\n");
        String[] var = Variáveis(contador,formato);
        String[] gnuplot = {"C:\\Program Files\\gnuplot\\bin\\gnuplot.exe", String.valueOf(Script(var))};    //criaca de comando q abre a aplicacao gnuplot e executa o script
        GnuPlot(gnuplot);
        return formato;
    }

    public static void GnuPlot(String[] gnuplot) {
        try{
            Process p = Runtime.getRuntime().exec(gnuplot);  //executa o comando recebido como parametro
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public static File Script(String[] var) throws FileNotFoundException {   //criacao de script
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

    public static String[] Variáveis(int contador, int formato)     //variaveis do script
    {
        String terminal = "", output = "", title = "", xlabel = "", ylabel = "";
        int flag=1;
        switch(contador){
            case 0:
                title = "Dimensao da Populacao ao Longo do Tempo";
                xlabel = "Anos";
                ylabel = "Dimensão da Populacao";
                break;
            case 1:
                title = "Taxa de Variacao da Populacao ao Longo do Tempo";
                xlabel = "Anos";
                ylabel = "Taxa de Variacao da Populacao";
                break;
            case 2:
                title = "Evolucao da Distribuicao da Populacao";
                xlabel = "Anos";
                ylabel = "Distribuicao da Populacao";
                break;
            case 3:
                title = "Distribuicao Normalizada Pelo Total da Populacao Ao Longo do Tempo";
                xlabel = "Anos";
                ylabel = "Distribuicao Normalizada Pelo Total da Populacao";
                break;
            default:
                title = "ACABOU";
        }
            switch (formato) {
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
                    System.out.println("Unexpected value: " + formato);
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

    public static File Guardar(int contador) throws FileNotFoundException {
        int formato = Graphs(contador);
        System.out.print("Deseja guardar uma cópia do gráfico? (s/n) : ");
        String s = sc.nextLine();
        String guardar = sc.nextLine();
        File copia = new File(Variáveis(contador,formato)[1]);
        if(guardar.compareTo("n")==0)
        {
            copia.delete();
        }
        System.out.print("\n");
        System.out.print("\n");
        return Script(Variáveis(contador,formato));
    }


}
