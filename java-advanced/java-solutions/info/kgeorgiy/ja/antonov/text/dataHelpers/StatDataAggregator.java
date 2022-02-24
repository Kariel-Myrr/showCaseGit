package info.kgeorgiy.ja.antonov.text.dataHelpers;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class StatDataAggregator<T> {

    Map<T, Integer> map;


    public StatDataAggregator(){

        map = new HashMap<>();
    }


    public void add(T element){

        if(element == null){
            System.out.println("null element");
        }

        map.put(element, map.getOrDefault(element, 0) + 1);

    }

    public T getMax(Comparator<T> comp) {
        return map.keySet().stream().max(comp).orElse(null);
    }

    public T getMin(Comparator<T> comp) {
       return map.keySet().stream().min(comp).orElse(null);
    }

    public T getAverage(Function<Collection<Map.Entry<T, Integer>>, T> function){
        return function.apply(map.entrySet());
    }

    public int getNumberOfUnique(){
        return map.size();
    }

    public int getNumber(){
        return map.values().stream().reduce(Integer::sum).orElse(0);
    }

    @Override
    public String toString() {
        // :NOTE: copy paste
        StringBuilder builder = new StringBuilder();

        int numberOfElements = 0;

        for (Map.Entry<T, Integer> entry : map.entrySet()) {
            builder.append(entry.getKey().toString()).append(" : ").append(entry.getValue()).append("\n");
            numberOfElements += entry.getValue();
        }

        builder.append("\nNumber of elements: ").append(numberOfElements)
                .append("\nNumber of unique elements: ").append(getNumberOfUnique());

        return builder.toString();

    }



    public String toString(Comparator<T> comp) {
        StringBuilder builder = new StringBuilder();

        int numberOfElements = 0;

        for (Map.Entry<T, Integer> entry : map.entrySet()) {
            builder.append(entry.getKey().toString()).append(" : ").append(entry.getValue()).append("\n");
            numberOfElements += entry.getValue();
        }

        builder.append("\nNumber of elements: ").append(numberOfElements)
                .append("\nNumber of unique elements: ").append(getNumberOfUnique())
                .append("\nMin element: \"").append(getMin(comp))
                .append("\" \nMax element: \"").append(getMax(comp)).append("\"\n");

        return builder.toString();

    }
}
