package nachos.verify;

import nachos.threads.Condition2;
import nachos.threads.KThread;
import nachos.threads.Lock;

public class TestCondition implements Runnable{
    TestCondition(){

    }

    public void run() {
        System.out.println("TestCondition is acquiring the lock");
        lock.acquire();
        System.out.println("TestCondition is sleeping");
        condition2.sleep();
        System.out.println("TestCondition is running");
        System.out.println("TestCondition wake up ThreadA ");
        condition2.wake();
        lock.release();
        System.out.println("TestCondition released the lock and finished");
        ThreadA.yield();//让A执行完
    }

    public static void TestCondition() {
        System.out.println("***Test Condition2***");
        ThreadA = new KThread(new Runnable() {
            public void run() {
                System.out.println("ThreadA is acquiring the lock");
                lock.acquire();
                System.out.println("ThreadA is waking up the main thread");
                condition2.wake();
                System.out.println("ThreadA is running");
                System.out.println("ThreadA is sleeping");
                condition2.sleep();
                lock.release();
                System.out.println("ThreadA released the lock and finished");
            }
        });
        ThreadA.setName("ThreadA").fork();
        new TestCondition().run();
        System.out.println("*** end of TestCondition ***");
    }
    private static Lock lock = new Lock();
    private static Condition2 condition2 = new Condition2(lock);
    private static KThread ThreadA = null;

}
