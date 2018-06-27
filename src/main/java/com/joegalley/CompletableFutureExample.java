package com.joegalley;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;

public class CompletableFutureExample {

    public static void main(String[] args) throws Exception {
        example1();
        example2();
        example3();
    }

    /**
     * Basic CompletableFuture example. The static runAsync method
     * schedules a CompletableFuture to be run and returns it.
     * We wait for the CompletableFuture to complete by calling .join().
     * If we don't do this, the program may exit before the CF is done executing.
     */
    public static void example1() throws Exception {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("done");
            }
        };

        CompletableFuture completableFuture = CompletableFuture.runAsync(runnable);
        completableFuture.join();  // wait for completableFuture to finish
        // prints "done"
    }

    /**
     * Same as example1 but with lambdas
     */
    public static void example2() throws Exception {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("done");
        }).join();
        // prints "done"
    }

    /**
     * Return a value from a CompletableFuture using supplyAsync() instead of runAsync().
     *
     */
    public static void example3() throws Exception {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "done");

        String result = completableFuture.join();

        assertEquals(result, "done");
    }
}
