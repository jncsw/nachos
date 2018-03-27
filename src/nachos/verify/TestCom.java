package nachos.verify;

import nachos.threads.Communicator;
import nachos.threads.KThread;

public class TestCom implements Runnable {
    private Communicator com2;

    public TestCom(Communicator communicator){
        this.com2 = communicator;
    }
    public void run() {
        for(int i=0;i<cnt;i++){
            int tmp = (int)(Math.random()*100);
            System.out.println("Listener listened: " + com2.listen());
            if(!mul){
                System.out.println("Speaker speaks: " + tmp);
                com2.speak(tmp);
            }
        }

    }
    public static void TestCom(){
        System.out.println("***TestCom***");
        Communicator com = new Communicator();

//        new KThread(new TestCom(com) {
//            public void run() {
//                for(int i=0;i<5;i++){
//
//                    int tmp = (int)(Math.random()*100);
//                    System.out.println("Speaker speaks: "+tmp);
//                        com.speak(tmp);
//        //                    tmp = 1001;
//                    System.out.println("Listener listened: "+com.listen());
//                }
//            }
//        }).setName("TestCom").fork();

        new KThread(new TestCom(com)).setName("TestCom").fork();
        for(int i = 0;i<cnt;i++){
            int tmp = (int)(Math.random()*100);
            System.out.println("Speaker speaks: " + tmp);
            com.speak(tmp);
            if(!mul)
                System.out.println("Listener listened: " + com.listen());
        }
        System.out.println("***end of Testcom***");
    }

    public static boolean isMul() {
        return mul;
    }

    public static void setMul(boolean mul) {
        TestCom.mul = mul;
    }

    final static int cnt = 5;
    static boolean mul = true;

}
