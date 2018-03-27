package nachos.verify;

import nachos.threads.KThread;


public class TestJoinCascade {
    TestJoinCascade(int id) {
        this.id = id;
    }

//    public void run() {
//        for (int i = 0; i < 5; i++) {
//            System.out.println("thread " + id + " is running "+i+" times!");
//        }
//    }

    private int id;
    public static void TestJoinCascade() {
        System.out.println("***starting test Join Cascade method***");
//        KThread thread2 = new KThread(new TestJoin(2));
//        thread2.setName("testJoin").fork();
//        System.out.println("Call join");
//        thread2.join();
//        new TestJoin(1).run();

        KThread ThreadA = new KThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread A is starting");
                System.out.println("Thread A is running");
                System.out.println("Thread A is finished");
            }
        });
        ThreadA.setName("A");
        KThread ThreadB = new KThread(new Runnable() {
            @Override
            public void run() {
//                KThread.yield();
                System.out.println("Thread B is starting");
                System.out.println("Thread B is running");
                System.out.println("Thread B is calling join thread A");
                ThreadA.join();
                System.out.println("Thread B is finished");
            }
        });
        ThreadB.setName("B");
        KThread ThreadC = new KThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Thread C is starting");
                System.out.println("Thread C is running");
                System.out.println("Thread C is calling join thread B");
                ThreadB.join();
                System.out.println("Thread C is finished");
            }
        });
        ThreadC.setName("C");

        ThreadC.fork();
        ThreadB.fork();
        ThreadA.fork();
        KThread.yield();
        System.out.println("***end test Join Cascade method***");
    }


}


