package info.kgeorgiy.ja.antonov.concurrent.mapper;

import java.util.Deque;

public class Mapper extends Thread{

    private final Deque<Task<?>> taskQueue;

    public Mapper(Deque<Task<?>> taskQueue) {
        this.taskQueue = taskQueue;
    }


    public void run(){

        while(!isInterrupted()){

            Task<?> task;

            synchronized (taskQueue) {
                while (taskQueue.isEmpty()) {
                    try {
                        taskQueue.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                task = taskQueue.pollFirst();
            }
            task.setResult();
            task.semaphore.inc();
        }
    }
}
