package info.kgeorgiy.ja.antonov.text;

import info.kgeorgiy.ja.antonov.text.dataHelpers.TextDataHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.Locale;

public class TextStatistics {

    /*TODO:
    -tests for different languages
    -степень сравнения слов
    -склонения через Choice format
     */

    public static void main(String[] args) {


        if (args.length != 4) {
            System.out.println("Incorrect arguments. Try: input_local output_local input_file output_file");
            return;
        }


        Locale inLocal = localeParser(args[0]);
        Locale outLocal = localeParser(args[1]);

        File inFile;
        File outFile;


        inFile = new File(args[2]);

        if (!inFile.exists()) {
            System.out.println("Input file isn't exists");
            return;
        }

        outFile = new File(args[3]);

        if (!outFile.exists()) {
            System.out.println("Output file isn't exists");
            return;
        }

        TextDataHandler textData;
        String text;

        try (BufferedReader reader = new BufferedReader(new FileReader(inFile, StandardCharsets.UTF_8))) {
            // :NOTE: move to a const value
            char[] cbuf = new char[10000];
            StringBuilder builder = new StringBuilder();
            int indicator = reader.read(cbuf);
            while (indicator != -1) {
                builder.append(cbuf, 0, indicator);
                indicator = reader.read(cbuf);
            }

            text = builder.toString();

        } catch (IOException e) {
            // :NOTE: e.getMessage, System.error
            System.out.println("Can't open inFile: " + inFile);
            e.printStackTrace();
            return;
        }

            TextStatCollector statCollector = new TextStatCollector(inLocal);

            textData = statCollector.collectData(text);

            //System.out.println(textData);


        try (BufferedWriter out = new BufferedWriter(new FileWriter(outFile))) {

            DataPrinter printer = new DataPrinter(out, outLocal);

            printer.printData(textData, Collator.PRIMARY);

        } catch (IOException e) {
            System.out.println("Can't open outFile: " + outFile);
            e.printStackTrace();
        }


    }

    private static Locale localeParser(String str) {
        String[] parsed = str.split("_");
        return switch (parsed.length) {
            case 0 -> Locale.getDefault();
            case 2 -> new Locale(parsed[0], parsed[1]);
            case 1 -> new Locale(parsed[0]);
            default -> new Locale(parsed[0], parsed[1], parsed[2]);
        };
    }

}
