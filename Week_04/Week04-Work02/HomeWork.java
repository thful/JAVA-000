import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class HomeWork {
    final static int fibonum = 36;

    // 斐波那契数
    private static int fibo(int N) {
        return Stream.iterate(new int[]{0, 1}, a -> new int[]{a[1], a[0] + a[1]}).skip(N).findFirst().get()[0];
    }

    // 入口
    public static void main(String[] args) throws Exception {
        System.out.println(test1());
        System.out.println(test2());
        System.out.println(test3());
        System.out.println(test4());
        System.out.println(test5());
        System.out.println(test6());
        System.out.println(test7());
        System.out.println(test8());
        System.out.println(test9());
        System.out.println(test10());
        System.out.println(test11());
        System.out.println(test12());
        System.out.println(test13());
    }

    // 1 通过Runnable Join子线程 等待结果之时得到结果
    public static AtomicInteger test1() throws InterruptedException {
        AtomicInteger sum = new AtomicInteger();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                sum.set(fibo(fibonum));
            }
        };
        Thread t = new Thread(run);
        t.start();
        t.join();
        return sum;
    }

    // 2 通过Thread Join子线程 等待结果之时得到结果
    public static AtomicInteger test2() throws InterruptedException {
        AtomicInteger sum = new AtomicInteger();
        Thread t = new Thread(() -> {
            sum.set(fibo(fibonum));
        });
        t.start();
        t.join();
        return sum;
    }

    // 3 通过sleep和同步块控制子线程获取锁 子线程结束后 主线程获取锁的结果
    public static AtomicInteger test3() throws InterruptedException {
        Object obj = new Object();
        AtomicInteger sum = new AtomicInteger();
        Thread t = new Thread(() -> {
            synchronized (obj) {
                sum.set(fibo(fibonum));
            }
        });
        t.start();
        TimeUnit.SECONDS.sleep(1);
        synchronized (obj) {
            return sum;
        }
    }

    // 4 通过同步块与wait控制主线程等待子线程计算出结果
    public static AtomicInteger test4() throws InterruptedException {
        Object obj = new Object();
        AtomicInteger sum = new AtomicInteger();
        Thread t = new Thread(() -> {
            synchronized (obj) {
                sum.set(fibo(fibonum));
                obj.notify();
            }
        });
        t.start();
        synchronized (obj) {
            sum.wait();
            return sum;
        }
    }

    // 5 通过ReentrantLock和Condition控制主线程等待子线程计算出结果
    public static AtomicInteger test5() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        AtomicInteger sum = new AtomicInteger();
        Thread t = new Thread(() -> {
            lock.lock();
            try {
                sum.set(fibo(fibonum));
            } catch (Exception e) {
            } finally {
                condition.signal();
                lock.unlock();
            }
        });
        t.start();
        lock.lock();
        condition.wait();
        lock.unlock();
        return sum;
    }

    // 6 通过CountDownLatch控制主线程等待子线程计算出结果
    public static AtomicInteger test6() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicInteger sum = new AtomicInteger();
        Thread t = new Thread(() -> {
            try {
                sum.set(fibo(fibonum));
            } catch (Exception e) {
            } finally {
                countDownLatch.countDown();
            }
        });
        t.start();
        countDownLatch.await();
        return sum;
    }

    // 7 子线程计算完成主线程互相等待 主线程获取结果后退出
    public static AtomicInteger test7() throws BrokenBarrierException, InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
        AtomicInteger sum = new AtomicInteger();
        Thread t = new Thread(() -> {
            sum.set(fibo(fibonum));
            try {
                cyclicBarrier.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        cyclicBarrier.await();
        return sum;
    }

    // 8 主线程先park 通过CyclicBarrier的barrierAction唤醒主线程 然后获取结果
    public static AtomicInteger test8() {
        Thread current = Thread.currentThread();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(1, () -> {
            LockSupport.unpark(current);
        });

        AtomicInteger sum = new AtomicInteger();
        Thread t = new Thread(() -> {
            sum.set(fibo(fibonum));
            try {
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        LockSupport.park();
        return sum;
    }


    // 9 设置一个volatile变量 主线程遍历标志 CyclicBarrier的barrierAction改变计算状态 主线程可以获取结果了
    static volatile boolean over = false;

    public static AtomicInteger test9() {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(1, () -> {
            over = true;
        });
        AtomicInteger sum = new AtomicInteger();
        Thread t = new Thread(() -> {
            sum.set(fibo(fibonum));
            try {
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
        while (true) {
            if (!over) continue;
            return sum;
        }
    }

    // 10 初始化semaphore 主线程获取阻塞
    // 子线程计算结束后 释放一个资源 此时主线程可以获得许可然后获取计算结果
    public static AtomicInteger test10() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        AtomicInteger sum = new AtomicInteger();
        Thread t = new Thread(() -> {
            sum.set(fibo(fibonum));
            semaphore.release();
        });
        t.start();
        semaphore.acquire();
        return sum;
    }

    /**
     * 11
     * 利用阻塞队列的特性 计算结果存在队列中
     * 主线程使用take出队 子线程offer入队
     * 如果队列为空则take会阻塞 会等待子线程把结果offer进队列 主线程才会获取到结果
     */
    public static Integer test11() throws InterruptedException {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(1);
        Thread t = new Thread(() -> {
            int sum = fibo(fibonum);
            queue.offer(sum);
        });
        t.start();
        Integer sum = queue.take();
        return sum;
    }

    // 12 利用FutureTask把执行体包装成有返回值的任务 子线程启动后 主线程中get获取结果 会阻塞等待到获取到结果
    public static Integer test12() throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return fibo(fibonum);
            }
        });
        new Thread(futureTask).start();
        Integer sum = futureTask.get();
        return sum;
    }

    // 13 线程池
    public static Integer test13() throws ExecutionException, InterruptedException {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return fibo(fibonum);
            }
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Integer> submit = executorService.submit(callable);
        Integer sum = submit.get();
        executorService.shutdown();
        return sum;
    }
}

