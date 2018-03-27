package nachos.verify;

import nachos.machine.Machine;
import nachos.threads.KThread;
import nachos.threads.ThreadedKernel;


public class TestAlarm {
    public static void TestAlarm(){
        System.out.println("***Test Alarm***");
        TestAlarmStatic.setTesting(true);

        for(int i=0;i<5;i++){
            ThreadedKernel.alarm.waitUntil(10000);
            System.out.println("Alarm: Current time:"+ Machine.timer().getTime());
        }
        TestAlarmStatic.setTesting(false);
        System.out.println("***End Test Alarm***");
    }
}
