package info.kgeorgiy.ja.antonov.text.dataHelpers;

import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.*;

public class TextDataHandler {

    //TODO: public field - плохо
    public StatDataAggregator<String> words;
    public StatDataAggregator<String> sentences;
    public StatDataAggregator<Date> dates;
    public StatDataAggregator<Number> numbers;
    public StatDataAggregator<Number> currency;

    public TextDataHandler(){

        words = new StatDataAggregator<>();
        sentences = new StatDataAggregator<>();
        dates = new StatDataAggregator<>();
        numbers = new StatDataAggregator<>();
        currency = new StatDataAggregator<>();



    }

    @Override
    // :NOTE: \n
    public String toString() {

        return "TextDataHandler:\n" +
                "words:\n" + words.toString() +
                "\nsentences:\n" + sentences.toString() +
                "\ndates:\n" + dates.toString() +
                "\nnumbers:\n" + numbers.toString() +
                "\ncurrency:\n" + currency.toString();
    }

    public String toString(Collator collator, Comparator<Number> comparator) {


        return "TextDataHandler:\n" +
                "words:\n" + words.toString(collator::compare) +
                "\nsentences:\n" + sentences.toString(collator::compare) +
                "\ndates:\n" + dates.toString(Date::compareTo) +
                "\nnumbers:\n" + numbers.toString(comparator) +
                "\ncurrency:\n" + currency.toString(comparator);
    }


}
