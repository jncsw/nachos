package nachos.verify;

import nachos.machine.Machine;
import nachos.threads.KThread;
import nachos.threads.Lock;
import nachos.threads.ThreadedKernel;

public class TestPriorityScheduler {
    public static void TestJoin(){
        boolean intStatus = Machine.interrupt().disable();
        System.out.println("*** Test priority -  Testjoin method***");
        intStatus = Machine.interrupt().disable();
        KThread ThreadA = new KThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                System.out.println("Thread A is starting");
//                lock.acquire(); // join方法，注释
                System.out.println("Thread A is running");
//                System.out.println(ThreadedKernel.scheduler.getEffectivePriority(ThreadA));
//                lock.release();
                System.out.println("Thread A is finishing");
            }
        });
        ThreadA.setName("A").fork();
        ThreadedKernel.scheduler.setPriority(ThreadA, 1);

        KThread ThreadB = new KThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                System.out.println("Thread B is starting");
//                lock.acquire();
                System.out.println("Thread B is running");
//                lock.release();
                System.out.println("Thread B is finishing");
            }
        });
        ThreadB.setName("B").fork();
        ThreadedKernel.scheduler.setPriority(ThreadB, 2);

        KThread ThreadC = new KThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                System.out.println("Thread C is starting");
                System.out.println("Thread C call A.join");
                ThreadA.join();
                System.out.println("Thread C is finishing");
            }
        });
        ThreadC.setName("C").fork();
        ThreadedKernel.scheduler.setPriority(ThreadC, 3);
        Machine.interrupt().restore(intStatus);
        KThread.yield();
        System.out.println("*** end of TestPriority-join-Scheduler method ***");

    }


    public static void TestPriorityScheduler() {
        System.out.println("***Test priority - testPriorityScheduler***");
        boolean intStatus = Machine.interrupt().disable();
        Lock lock = new Lock();

        KThread ThreadA = new KThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                System.out.println("Thread A is starting");
                System.out.println("Thread A is sleeping");
                KThread.yield();
                lock.acquire();
                System.out.println("Thread A is running");
                lock.release();
                System.out.println("Thread A is finished");
            }
        });
        ThreadA.setName("A").fork();
        ThreadedKernel.scheduler.setPriority(ThreadA, 1);
        KThread.yield();//先让A运行 获得锁
        KThread ThreadB = new KThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                System.out.println("Thread B is starting");
                lock.acquire();
                System.out.println("Thread B is running");
                lock.release();
                System.out.println("Thread B is finished");
            }
        });

        ThreadB.setName("B").fork();
        ThreadedKernel.scheduler.setPriority(ThreadB, 2);
        KThread ThreadC = new KThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                System.out.println("Thread C is starting");
                lock.acquire();
                System.out.println("Thread C is running");
                lock.release();
                System.out.println("Thread C is finishing");
            }
        });
//        ThreadA.setName("A").fork();
//        ThreadB.setName("B").fork();
        ThreadC.setName("C").fork();
        ThreadedKernel.scheduler.setPriority(ThreadC, 3);

        Machine.interrupt().restore(intStatus);
        KThread.yield();//此时让出CPU，C优先级最高




        System.out.println("*** end of TestPriorityScheduler method ***");
    }

}
