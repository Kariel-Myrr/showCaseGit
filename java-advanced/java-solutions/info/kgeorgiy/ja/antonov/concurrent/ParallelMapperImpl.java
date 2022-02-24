package info.kgeorgiy.ja.antonov.concurrent;

import info.kgeorgiy.ja.antonov.concurrent.mapper.Mapper;
import info.kgeorgiy.ja.antonov.concurrent.mapper.MySemaphore;
import info.kgeorgiy.ja.antonov.concurrent.mapper.Task;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;

// java -cp . -p . -m info.kgeorgiy.java.advanced.mapper list info.kgeorgiy.ja.antonov.mapper.ParallelMapperImpl

public class ParallelMapperImpl implements ParallelMapper {

    private final List<Thread> threads;

    private final Deque<Task<?>> taskQueue;

    public ParallelMapperImpl(int threads) {

        this.taskQueue = new ArrayDeque<>();

        this.threads = new ArrayList<>();

        for (int i = 0; i < threads; i++) {

            Mapper mapper = new Mapper(taskQueue);
            this.threads.add(mapper);
            mapper.start();

        }
    }


    public <T, R> List<R> map(Function<? super T, ? extends R> function, List<? extends T> list) throws InterruptedException {

        List<R> results = new ArrayList<>(Collections.nCopies(list.size(), null));

        MySemaphore semaphore = new MySemaphore(list.size());

        synchronized (taskQueue) {
            for (int i = 0; i < list.size(); i++) {
                final int index = i;
                taskQueue.add(new Task<>(index, () -> function.apply(list.get(index)), results, semaphore));
            }
            taskQueue.notifyAll();
        }

        semaphore.acquire();

        synchronized (results) {
            return results;
        }
    }

    public void close() {

        for(Thread thread : threads){
            thread.interrupt();
        }

    }
}