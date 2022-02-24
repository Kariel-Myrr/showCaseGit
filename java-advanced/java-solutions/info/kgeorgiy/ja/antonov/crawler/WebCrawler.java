package info.kgeorgiy.ja.antonov.crawler;

//java -cp . -p . -m info.kgeorgiy.java.advanced.crawler easy info.kgeorgiy.ja.antonov.crawler.WebCrawler

import info.kgeorgiy.java.advanced.crawler.Crawler;
import info.kgeorgiy.java.advanced.crawler.Document;
import info.kgeorgiy.java.advanced.crawler.Downloader;
import info.kgeorgiy.java.advanced.crawler.Result;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class WebCrawler implements Crawler {

    final Downloader downloader;
    final int perHost;

    final ExecutorService downloaders;
    final ExecutorService extractors;


    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloaders = Executors.newFixedThreadPool(downloaders);
        this.extractors = Executors.newFixedThreadPool(extractors);
        this.perHost = perHost;
    }


    @Override
    public Result download(String url, int depth) {
        return (new RecursiveDownloader()).downloadStart(url, depth);
    }

    @Override
    public void close() {
        downloaders.shutdown();
        extractors.shutdown();
        try {
            downloaders.awaitTermination(0, TimeUnit.MILLISECONDS);
            extractors.awaitTermination(0, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            System.err.println("Can't terminate executor pools: " + e.getMessage());
        }
    }


    //=============================================================================================================================
    public class RecursiveDownloader {

        final ConcurrentMap<String, IOException> errors = new ConcurrentHashMap<>();
        final Set<String> done = ConcurrentHashMap.newKeySet();
        final Set<String> visited = ConcurrentHashMap.newKeySet();

        public Result downloadStart(String url, int depth){
            visited.add(url);
            return downloadRec(List.of(url), depth);
        }

        public Result downloadRec(List<String> urls, int depth) {

            if (depth == 0) {
                return new Result(new ArrayList<>(done), errors);
            }

            Set<String> toGo = ConcurrentHashMap.newKeySet();
            LinkedList<Future<Future<List<String>>>> futureList = new LinkedList<>();

            for (String i : urls) {
                futureList.add(downloaders.submit(urlDownloadTaskMapper(i, toGo)));
            }

            for (int i = 0; i < futureList.size(); i++) {
                //TODO: что делать, если нам заинтераптили поток
                try {
                    Future<List<String>> future = futureList.get(i).get();
                    if (future != null) {
                        future.get();
                    }
                } catch (ExecutionException e) {
                    System.err.println("Can't correctly get result from Future due to Execution error for url: " + urls.get(i));
                    System.err.println(e.getMessage());
                    System.err.println(e.getCause().getMessage());
                    throw new RuntimeException("end2");
                } catch (InterruptedException e) {
                    System.err.println("Can't correctly get result from Future for url: " + urls.get(i));
                    System.err.println(e.getMessage());
                    throw new RuntimeException("end1");
                }
            }

            return downloadRec(new ArrayList<>(toGo), depth - 1);
        }

        public Callable<Future<List<String>>> urlDownloadTaskMapper(String url, Set<String> toGo) {
            return () -> {
                try {
                    Document document = downloader.download(url);
                    return extractors.submit(() -> {
                        try {
                            List<String> res = document.extractLinks();
                            for (String link : res) {
                                if (!visited.contains(link)) {
                                    visited.add(link);
                                    toGo.add(link);
                                }
                            }
                            done.add(url);
                            return res;
                        } catch (IOException e) {
                            errors.put(url, e);
                            return null;
                        }
                    });
                } catch (IOException e) {
                    errors.put(url, e);
                    return null;
                }
            };
        }


    }
}
