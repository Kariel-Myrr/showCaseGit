package info.kgeorgiy.ja.antonov.text;

import info.kgeorgiy.ja.antonov.text.dataHelpers.TextDataHandler;
import info.kgeorgiy.ja.antonov.text.dataHelpers.TokenBreakIterator;

import java.text.*;
import java.util.*;

public class TextStatCollector {

    Locale locale;

    public TextStatCollector(Locale locale) {
        this.locale = locale;
    }


    public TextDataHandler collectData(String text) {

        TextDataHandler textData = new TextDataHandler();

        List<DateFormat> dateFormats = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            dateFormats.add(DateFormat.getDateInstance(i, locale));
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);

        TokenBreakIterator sentenceIterator = new TokenBreakIterator(locale, TokenBreakIterator.SENTENCE, text);

        BreakIterator wordIterator = BreakIterator.getWordInstance(locale);


        while (sentenceIterator.next() != -1) {
            String sentence = sentenceIterator.getCurToken();

            //System.out.println(sentence + "\nStart sentence parsing: \"" + sentence + "\"");

            wordIterator.setText(sentence);

            int wordRightBorder = wordIterator.first();
            int wordLeftBorder = wordIterator.next();
            while (wordLeftBorder != -1) {

                ParsePosition pos = new ParsePosition(wordRightBorder);

                //пытаемся распарсить дату
                Date date = null;
                for (DateFormat format : dateFormats) {
                    date = format.parse(sentence, pos);
                    if (posIsNotValid(pos, wordLeftBorder, wordIterator)) {
                        pos.setIndex(wordRightBorder);
                    } else {
                        break;
                    }
                }

                if (posIsNotValid(pos, wordLeftBorder, wordIterator)) {
                    pos.setIndex(wordRightBorder);

                    //пытаемся распарсить валюту
                    Number currency = currencyFormat.parse(sentence, pos);

                    if (posIsNotValid(pos, wordLeftBorder, wordIterator)) {
                        pos.setIndex(wordRightBorder);

                        //пытаемся распарсить число
                        Number number = numberFormat.parse(sentence, pos);

                        if (posIsNotValid(pos, wordLeftBorder, wordIterator)) {

                            //ничего не распарсили, значит скорее всего слово
                            //we assume that word starts from letter or digit

                            if (Character.isLetterOrDigit(sentence.charAt(wordRightBorder))) {

                                String word = sentence.substring(wordRightBorder, wordLeftBorder);
                                pos.setIndex(wordLeftBorder);

                                textData.words.add(word);
                            }

                        } else {
                            textData.numbers.add(number);
                        }
                    } else {
                        textData.currency.add(currency);
                    }
                } else {
                    textData.dates.add(date);
                }

                wordRightBorder = wordLeftBorder;
                while (wordRightBorder < pos.getIndex() && wordRightBorder != -1) {

                    wordRightBorder = wordIterator.next();
                }
                wordLeftBorder = wordIterator.next();

            }

            textData.sentences.add(sentence);

        }


        return textData;

    }


    private boolean posIsNotValid(ParsePosition pos, int left, BreakIterator iterator) {

        int counter = 0;

        if (pos.getIndex() < left) {
            return true;
        }

        while (left != -1 && left < pos.getIndex()) {
            left = iterator.next();
            counter++;
        }

        boolean flag = (left != pos.getIndex());

        while (counter > 0) {
            counter--;
            iterator.previous();
        }

        return flag;

    }


}
