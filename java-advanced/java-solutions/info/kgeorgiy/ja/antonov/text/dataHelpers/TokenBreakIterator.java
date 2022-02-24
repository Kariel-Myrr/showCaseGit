package info.kgeorgiy.ja.antonov.text.dataHelpers;

import java.text.BreakIterator;
import java.util.Locale;


/**
 * Iterator based on Break iterator.
 * But it iterates on by tokens, not on breaks indexes.
 *
 * |token1|token2|token3|....|
 *
 */
public class TokenBreakIterator {

    public static final int LINE = 1;
    public static final int SENTENCE = 2;
    public static final int WORD = 3;
    public static final int CHARACTER = 4;

    private final BreakIterator iterator;

    private String text;
    private int start;
    private int end;


    public TokenBreakIterator(Locale locale, int mode){
        switch (mode) {
            case 1 -> iterator = BreakIterator.getLineInstance(locale);
            case 2 -> iterator = BreakIterator.getSentenceInstance(locale);
            case 3 -> iterator = BreakIterator.getWordInstance(locale);
            case 4 -> iterator = BreakIterator.getCharacterInstance(locale);
            default -> throw new IllegalArgumentException("Incorrect mode state. For mode: " + mode);
        }

        this.text = null;
    }

    public TokenBreakIterator(Locale locale, int mode, String text){
        switch (mode) {
            case 1 -> iterator = BreakIterator.getLineInstance(locale);
            case 2 -> iterator = BreakIterator.getSentenceInstance(locale);
            case 3 -> iterator = BreakIterator.getWordInstance(locale);
            case 4 -> iterator = BreakIterator.getCharacterInstance(locale);
            default -> throw new IllegalArgumentException("Incorrect mode state. For mode: " + mode);
        }

        setText(text);
    }

    public void setText(String text){
        this.text = text;
        iterator.setText(this.text);

        start = iterator.first();
        end = iterator.first();
    }


    //можно объединить getCurToken и next
    /**
     *
     * @return current token or null if out of bounds
     */
    public String getCurToken(){
        if(end == -1){
            return null;
        }
        return text.substring(start, end);
    }

    /**
     *
     * @return -1 if next token is out of bounds.
     */
    public int next(){
        start = end;
        end = iterator.next();

        //System.out.println(start + ":" + end);

        return end;
    }

}
