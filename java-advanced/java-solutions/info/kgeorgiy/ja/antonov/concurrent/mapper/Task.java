package info.kgeorgiy.ja.antonov.concurrent.mapper;

import java.util.List;
import java.util.function.Supplier;

public class Task<R> {


    public final int index;
    public final Supplier<R> convert;
    public final List<R> results;
    public final MySemaphore semaphore;


    public Task(int index, Supplier<R> convert, List<R> results, MySemaphore semaphore) {
        this.convert = convert;
        this.index = index;
        this.semaphore = semaphore;
        this.results = results;
    }

    public void setResult(){

        results.set(index, convert.get());

    }


}
