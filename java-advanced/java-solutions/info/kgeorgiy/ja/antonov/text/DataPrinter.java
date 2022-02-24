package info.kgeorgiy.ja.antonov.text;

import info.kgeorgiy.ja.antonov.text.dataHelpers.StatDataAggregator;
import info.kgeorgiy.ja.antonov.text.dataHelpers.TextDataHandler;

import java.io.IOException;
import java.io.Writer;
import java.text.Collator;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class DataPrinter {


    Writer out;
    ResourceBundle bundle;
    Locale locale;

    public DataPrinter(Writer out, Locale locale) {

        this.bundle = ResourceBundle.getBundle("info.kgeorgiy.ja.antonov.text.bundle.UsageRecourseBundle", locale);
        this.out = out;
        this.locale = locale;
    }

    void printData(TextDataHandler textData, int collatorSetting) throws IOException {
        Comparator<Object> lenComp = Comparator.comparingInt(a -> a.toString().length());

        Collator collator = Collator.getInstance(locale);
        collator.setDecomposition(collatorSetting);

        Comparator<Number> comparator = Comparator.comparingDouble(Number::doubleValue);


        printDataAboutKey("words", textData.words, collator::compare, lenComp::compare);
        printDataAboutKey("sentences", textData.sentences, collator::compare, lenComp::compare);

        printDataAboutKey("dates", textData.dates, Date::compareTo, null);

        printDataAboutKey("numbers", textData.numbers, comparator, null);
        printDataAboutKey("currency", textData.currency, comparator, null);
    }


    <T> void printDataAboutKey(String key, StatDataAggregator<T> aggregator, Comparator<T> comparator, Comparator<T> lenComparator) throws IOException {

        println("\n" + get("general.information") + get(key));

        printMessage("general." + key, aggregator.getNumber());
        printMessage("general.unique", aggregator.getNumberOfUnique());//TODO согласовать по роду
        if (comparator != null) {
            printMessage(key + ".min", aggregator.getMin(comparator));
            printMessage(key + ".max", aggregator.getMax(comparator));
        }
        if (lenComparator != null) {
            printMessage(key + ".len.min", aggregator.getMin(lenComparator).toString().length());
            printMessage(key + ".len.max", aggregator.getMax(lenComparator).toString().length());
        }
        out.flush();

    }

    void printMessage(String nameKey, Object data) throws IOException {
        if (data == null) {
            println("  " + get(nameKey) + get("separator") + get("none"));
        } else {
            println("  " + get(nameKey) + get("separator") + data);
        }
    }

    String get(String key) {
        return bundle.getString(key);
    }

    void println(String str) throws IOException {
        out.write(str + "\n");
    }


}
