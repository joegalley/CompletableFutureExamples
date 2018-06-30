package com.joegalley;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class CompletableFutureExample {

    public static void main(String[] args) throws Exception {
        example11();
    }

    /**
     * This is a dummy method to simulate a long running task, such as retrieving
     * a value from a remote server. It will be used in several of the examples below.
     */
    private static String longRunningTask() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "done";
    }

    /**
     * Basic CompletableFuture example. The static runAsync method
     * schedules a CompletableFuture to be run and returns it.
     * We wait for the CompletableFuture to complete by calling .get().
     * If we don't do this, the program may exit before the CF is done executing.
     */
    public static void example1() throws Exception {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                String result = longRunningTask();
                System.out.println(result);
            }
        };

        CompletableFuture completableFuture = CompletableFuture.runAsync(runnable);
        completableFuture.get();  // wait for completableFuture to finish
        // prints "done"
    }

    /**
     * Same as example1 but with lambdas
     */
    public static void example2() throws Exception {
        CompletableFuture.runAsync(() -> {
            String result = longRunningTask();
            System.out.println(result);
        }).get();
        // prints "done"
    }

    /**
     * To return a value from a CompletableFuture, use supplyAsync() instead of runAsync().
     * supplyAsync() takes a Supplier (functional interface with a "get" method) as its only argument
     */
    public static void example3() throws Exception {
        Supplier<String> supplier = new Supplier<String>() {
            @Override
            public String get() {
                return "done";
            }
        };

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(supplier);

        String result = completableFuture.get();

        assertEquals("done", result);
    }

    /**
     * Same as example3 but with lambdas
     */
    public static void example4() throws Exception {
        String result = CompletableFuture.supplyAsync(() -> longRunningTask()).get();

        assertEquals("done", result);
    }

    /**
     * thenAccept() takes a Supplier functional interface as its parameter. This has the accept() method which returns void.
     * Chaining thenAccept() to the result of a future calls the supplier
     */
    public static void example5() throws Exception {
        Consumer<String> consumer = new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        };

        CompletableFuture
                .supplyAsync(() -> longRunningTask())
                .thenAccept(consumer)
                .get();
        //prints "done"
    }

    /**
     * Similar to example5, but shows how you can pass the result of a long running task as an argument to thenAccept().
     * Also creates a Consumer using a lambda
     */
    public static void example6() throws Exception {
        Consumer<String> consumer = System.out::println;

        CompletableFuture
                .supplyAsync(() -> longRunningTask())
                .thenAccept((String s) -> consumer.accept((s + " - and accepted"))) //"s" is the result of longRunningTask()
                .get(); //prints "done - and accepted"
    }


    /**
     * Similar to example6, but uses thenRun() instead of thenAccept().
     * The difference is that thenRun() does NOT have access to the CompletableFuture's result
     */
    public static void example7() throws Exception {
        /*
         * This will NOT work because thenRun has 0 parameters (so it cannot have String s as a parameter)
         *
         * CompletableFuture.supplyAsync(() -> longRunningTask())
         *        .thenRun((String s) -> System.out.println(s));
         *
         */

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("runnable done");
            }
        };

        CompletableFuture
                .supplyAsync(() -> longRunningTask())
                .thenRun(runnable)
                .get(); //prints "runnable done"
    }

    /**
     * thenApply() is similar to thenAccept(), but it returns a value. This allows you to transform the
     * result of a CompletableFuture.
     * You can think of this as analogous to .map() from the Stream API
     */
    public static void example8() throws Exception {

        Function<String, String> appendA = (String s) -> {
            return s + "A";
        };

        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(() -> longRunningTask())
                .thenApply(appendA);

        assertEquals("doneA", completableFuture.get());
    }

    /**
     * Similar to example8 but shows how you can chain any number of Functions together using thenApply()
     */
    public static void example9() throws Exception {
        Function<String, String> appendA = (String s) -> {
            // s = "done" at this point
            return s + "A";
        };

        Function<String, String> appendB = (String s) -> {
            // s = "doneA" at this point
            return s + "B";
        };

        Function<String, String> appendC = (String s) -> {
            // s = "doneAB" at this point
            return s + "C";
        };

        Function<String, String> appendD = (String s) -> {
            // s = "doneABC" at this point
            return s + "D";
        };

        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(() -> longRunningTask())
                .thenApply(appendA)
                .thenApply(appendB)
                .thenApply(appendC)
                .thenApply(appendD);

        assertEquals("doneABCD", completableFuture.get());
    }

    /**
     * Similar to example9 but with inline lambdas
     */
    public static void example10() throws Exception {
        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(() -> longRunningTask())
                .thenApply(s -> s + "A")
                .thenApply(s -> s + "B")
                .thenApply(s -> s + "C")
                .thenApply(s -> s + "D");

        assertEquals("doneABCD", completableFuture.get());
    }

    /**
     * Similar to example10 but with inline lambdas
     */
    public static void example11() throws Exception {
        CompletableFuture<String> completableFuture = CompletableFuture
                .supplyAsync(() -> longRunningTask())
                .thenApplyAsync(s -> s + "A")
                .thenApplyAsync(s -> s + "B")
                .thenApplyAsync(s -> s + "C")
                .thenApplyAsync(s -> s + "D");

        assertEquals("doneABCD", completableFuture.get());
    }
}
