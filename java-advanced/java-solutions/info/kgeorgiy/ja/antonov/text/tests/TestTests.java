package info.kgeorgiy.ja.antonov.text.tests;
import info.kgeorgiy.ja.antonov.text.TextStatCollector;
import info.kgeorgiy.ja.antonov.text.dataHelpers.TextDataHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TestTests {

    static final String path = "info/kgeorgiy/ja/antonov/text/tests/resource/parse/";

    static final Locale[] locales = new Locale[] {
            new Locale("ru"),
            new Locale("en"),
            new Locale("ru", "KZ"),
            new Locale("fr"),
            new Locale("fr", "MR"),
            new Locale("uk", "UA"),
            new Locale("ru", "RU"),
            new Locale("en", "US"),
            new Locale("zh", "HK")};

    @BeforeClass
    public static void prepare() {

        Date date = new Date(1235);
        Number number = 1233.123;

        for(Locale locale : locales) {
            try (BufferedWriter out = new BufferedWriter(new FileWriter("java-solutions/" + path + locale.toString() + "/correctValues.txt", StandardCharsets.UTF_8))) {

                for (int i = 0; i < 4; i++) {
                    out.write(DateFormat.getDateInstance(i, locale).format(date));
                    out.write("\n");
                }
                out.write(NumberFormat.getNumberInstance(locale).format(number));
                out.write("\n");
                out.write(NumberFormat.getCurrencyInstance(locale).format(number));
                out.write("\n");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("prepare");

    }

    @Before
    public void preTest() {

        System.out.println("run test");
    }

    @Test
    public void textTest(){

        for(Locale locale : locales) {
            if(Files.exists(Path.of("java-solutions/" + path + locale.toString() + "/text.txt")))
            try(BufferedReader reader = new BufferedReader(new FileReader("java-solutions/" + path + locale.toString() + "/text.txt"))) {
                char[] cbuf = new char[10000];
                StringBuilder builder = new StringBuilder();
                int indicator = reader.read(cbuf);
                while (indicator != -1){
                    builder.append(cbuf, 0, indicator);
                    indicator = reader.read(cbuf);
                }

                String text = builder.toString();

                TextStatCollector statCollector = new TextStatCollector(locale);

                TextDataHandler data = statCollector.collectData(text);

                Assert.assertEquals(6, data.words.getNumber());
                Assert.assertEquals(0, data.dates.getNumber());
                Assert.assertEquals(1, data.numbers.getNumber());
                Assert.assertEquals(0, data.currency.getNumber());

                System.out.println("Text test for " + locale.toString() + " passed");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void shouldBeParsedTest(){
        for(Locale locale : locales) {
            try(BufferedReader reader = new BufferedReader(new FileReader("java-solutions/" + path + locale.toString() + "/correctValues.txt"))) {

                char[] cbuf = new char[10000];
                StringBuilder builder = new StringBuilder();
                int indicator = reader.read(cbuf);
                while (indicator != -1){
                    builder.append(cbuf, 0, indicator);
                    indicator = reader.read(cbuf);
                }

                String text = builder.toString();

                TextStatCollector statCollector = new TextStatCollector(locale);

                TextDataHandler data = statCollector.collectData(text);

                Assert.assertEquals(4, data.dates.getNumber());
                Assert.assertEquals(1, data.numbers.getNumber());
                Assert.assertEquals(1, data.currency.getNumber());

                System.out.println("Correct parsing test for " + locale.toString() + " passed");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void shouldNotBeParseTest() throws IOException, URISyntaxException {

        System.out.println("ShouldNotBeParseTest starts");

        List<String> lines = Files.readAllLines(Paths.get(getClass().getClassLoader().getResource(path + "/en/incorrectInput.txt").toURI()));

        String text = String.join("\n", lines);

        TextStatCollector statCollector = new TextStatCollector(Locale.ENGLISH);

        TextDataHandler data = statCollector.collectData(text);

        Assert.assertEquals(0, data.dates.getNumber());
        Assert.assertEquals(5, data.numbers.getNumber());
        Assert.assertEquals(0, data.currency.getNumber());



        System.out.println("classicNewton test passed");

    }

}
