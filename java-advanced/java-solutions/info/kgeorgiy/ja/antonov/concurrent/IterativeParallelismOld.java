package info.kgeorgiy.ja.antonov.concurrent;

import info.kgeorgiy.java.advanced.concurrent.ListIP;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class IterativeParallelismOld implements ListIP {


    //сливаем все трэды в мэйн
    private void joinAllThreads(List<Thread> threads) throws InterruptedException {

        InterruptedException interruptedException = new InterruptedException("Some threads were interrupted");

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                interruptedException.addSuppressed(e);
            }
        }

        if (interruptedException.getSuppressed().length != 0) {
            throw interruptedException;
        }

    }

    //разделяем на i тредов, последний может быть меньше остальных
    private <T> List<List<T>> separateOnSubLists(int n, List<T> list) {

        // :NOTE:done: last thread processes less values?

        int overFlow = list.size() % n;
        int step = (list.size() - overFlow) / n;

        int iterations = Math.min(list.size(), n);

        List<List<T>> separator = new ArrayList<>();

        int start = 0;
        int end = step;

        for(int i = 0; i < iterations; i++) {
            if(overFlow > 0){
                end++;
                overFlow--;
            }
            separator.add(list.subList(start, end));
            start = end;
            end = start + step;
        }

        //separator.add(list.subList(step * (i - 1), list.size()));

        return separator;
    }

    /**
     * T - тип того что лежит в листе
     *
     * @param assocOp        - применяет операцию ко все эл-там, получает вердикт по подмножествам
     * @param endResFunction - из вердиктов дает окончательный вердикт по мн-ву
     * @return U - вердикт по мн-ву по list
     */
    private <T, U> U parallelVerdictWalker(
            int i, List<T> list, Function<Stream<T>, U> assocOp, Function<Stream<U>, U> endResFunction
    ) throws InterruptedException {

        if (list.size() == 0) {
            throw new NoSuchElementException("Лист пуст, нету элементов для применения операций");
        }

        List<Thread> threads = new ArrayList<>();

        List<List<T>> subLists = separateOnSubLists(i, list);

        List<U> threadResults = new ArrayList<>(Collections.nCopies(subLists.size(), null));

        int curPos = 0;
        for (List<T> subList : subLists) {
            int finalCurPos = curPos;
            Thread tread = new Thread(() -> threadResults.set(finalCurPos, assocOp.apply(subList.stream())));

            tread.start();
            threads.add(tread);

            curPos++;
        }

        joinAllThreads(threads);

        return endResFunction.apply(threadResults.stream());
    }

    private <T> T monoidWalker(
            int i, List<T> list, Function<Stream<T>, T> operation
    ) throws InterruptedException {
        return parallelVerdictWalker(i, list, operation, operation);
    }

    @Override
    public <T> T maximum(
            int i, List<? extends T> list, Comparator<? super T> comparator
    ) throws InterruptedException {
        // :NOTE:Done: orElseThrow
        return monoidWalker(i, list, s -> s.max(comparator).orElseThrow());
    }

    @Override
    public <T> T minimum(
            int i, List<? extends T> list, Comparator<? super T> comparator
    ) throws InterruptedException {
        return monoidWalker(i, list, s -> s.min(comparator).orElseThrow());
    }

    @Override
    public <T> boolean all(
            int i, List<? extends T> list, Predicate<? super T> predicate
    ) throws InterruptedException {
        return parallelVerdictWalker(i, list, (s) -> s.allMatch(predicate), (s) -> s.allMatch(Predicate.isEqual(true)));
    }

    @Override
    public <T> boolean any(
            int i, List<? extends T> list, Predicate<? super T> predicate
    ) throws InterruptedException {
        return !all(i, list, predicate.negate());
    }

    @Override
    public String join(int i, List<?> list) throws InterruptedException {
        // :NOTE:done: duplicate
        return parallelVerdictWalker(i, list,
                s -> s.map(Object::toString).collect(Collectors.joining()),
                s -> s.collect(Collectors.joining()));
    }

    @Override
    public <T> List<T> filter(
            int i, List<? extends T> list, Predicate<? super T> predicate
    ) throws InterruptedException {
        return parallelVerdictWalker(i, list,
                s -> s.filter(predicate).collect(Collectors.toList()),
                s -> s.flatMap(List::stream).collect(Collectors.toList()));
    }

    @Override
    public <T, U> List<U> map(
            int i, List<? extends T> list, Function<? super T, ? extends U> function
    ) throws InterruptedException {
        return parallelVerdictWalker(i, list,
                s -> s.map(function).collect(Collectors.toList()),
                s -> s.flatMap(List::stream).collect(Collectors.toList()));
    }
}
