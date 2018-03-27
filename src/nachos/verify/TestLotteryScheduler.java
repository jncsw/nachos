package nachos.verify;

import nachos.machine.Machine;
import nachos.threads.KThread;
import nachos.threads.Lock;
import nachos.threads.ThreadedKernel;

public class TestLotteryScheduler {
    public static void TestLotteryScheduler() {
        System.out.println("***TestLotteryScheduler***");
        boolean intStatus = Machine.interrupt().disable();

        KThread ThreadA = new KThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
//                System.out.println("Thread A is starting");
//                lock.acquire();
//                KThread.yield();
                for(int i=0;i<100;i++){

                    System.out.println("Thread A is running");
                    KThread.yield();
                }
//                lock.release();
//                System.out.println("Thread A is finished");
            }
        });

        ThreadedKernel.scheduler.setPriority(ThreadA, 10);
//        System.out.println("Thread A priority = "+ThreadedKernel.scheduler.getEffectivePriority(ThreadA));

        KThread ThreadB = new KThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
//                System.out.println("Thread B is starting");
//                lock.acquire();
//                KThread.yield();
                for(int i=0;i<100;i++){

                    System.out.println("Thread B is running");
                    KThread.yield();
                }
//                lock.release();
//                System.out.println("Thread B is finished");
            }
        });

        ThreadedKernel.scheduler.setPriority(ThreadB, 100);
//        System.out.println("Thread B priority = "+ThreadedKernel.scheduler.getEffectivePriority(ThreadB));

        KThread ThreadC = new KThread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
//                System.out.println("Thread C is starting");
//                lock.acquire();
//                KThread.yield();
                for(int i=0;i<100;i++){

                    System.out.println("Thread C is running");
                    KThread.yield();
                }
//
//                lock.release();
//                System.out.println("Thread C is finishing");
            }
        });
        ThreadedKernel.scheduler.setPriority(ThreadC, 1000);
//        System.out.println("Thread C priority = "+ThreadedKernel.scheduler.getEffectivePriority(ThreadC));

        ThreadA.setName("A").fork();
        ThreadB.setName("B").fork();
        ThreadC.setName("C").fork();

        Machine.interrupt().restore(intStatus);
        while (ThreadA.getStatus()!=4 || ThreadB.getStatus()!=4 || ThreadC.getStatus()!=4)//KThread 加getState
            KThread.yield();//此时让出CPU，C优先级最高


        System.out.println("*** end of TestLotteryScheduler method ***");
    }

}
