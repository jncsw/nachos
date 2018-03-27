package nachos.threads;

import nachos.machine.*;
import nachos.verify.TestAlarmStatic;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     * <p>
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
        Machine.timer().setInterruptHandler(new Runnable() {
            public void run() {
                timerInterrupt();
            }
        });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
        //task1-3
        if(!waitQueue.isEmpty()){
            while (true){
                waitThread wT = waitQueue.poll();
                if(wT == null )break;
                if(wT.wakeTime<=Machine.timer().getTime()){
                    wT.currentThread.ready();
                    //For Task 2 Test
                    if(TestAlarmStatic.isTesting())
                        System.out.println("Alarm: "+wT.currentThread.getName()+" ready at "+Machine.timer().getTime());
                }else{
                    waitQueue.add(wT);
                    break;
                }
            }

        }

        //end task 1-3



        KThread.currentThread().yield();
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     * <p>
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param    x    the minimum number of clock ticks to wait.
     * @see    nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
        //del for task 1-3
//        // for now, cheat just to get something working (busy waiting is bad)
//        long wakeTime = Machine.timer().getTime() + x;
//        while (wakeTime > Machine.timer().getTime())
//            KThread.yield();
        //end del for task 1-3
        //task1-3
        boolean intStatus = Machine.interrupt().disable();
        //For Task 2 Test
        if(TestAlarmStatic.isTesting())
            System.out.println("Alarm: Current time:"+Machine.timer().getTime());
        //For Task 2 Test
        long wakeTime = Machine.timer().getTime() + x;
        if(TestAlarmStatic.isTesting())
            System.out.println("Alarm: "+KThread.currentThread().getName()+" waitUntil "+wakeTime);
        waitThread wT = new waitThread(wakeTime,KThread.currentThread());
        waitQueue.add(wT);
        KThread.sleep();
        Machine.interrupt().restore(intStatus);//恢复当前进程的中断状态

        //end task1-3
    }
    class waitThread{
        long wakeTime = 0;
        KThread currentThread;
        waitThread(long w,KThread k){
            this.wakeTime = w;
            this.currentThread = k;
        }

    }

    PriorityQueue<waitThread> waitQueue = new PriorityQueue<waitThread>(new Comparator<waitThread>(){

        @Override
        public int compare(waitThread T1, waitThread T2) {
            // TODO Auto-generated method stub
            return (int)(T1.wakeTime-T2.wakeTime);
        }

    });

}
