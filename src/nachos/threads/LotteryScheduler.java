package nachos.threads;

import nachos.machine.*;

import java.util.Random;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A scheduler that chooses threads using a lottery.
 * <p>
 * <p>
 * A lottery scheduler associates a number of tickets with each thread. When a
 * thread needs to be dequeued, a random lottery is held, among all the tickets
 * of all the threads waiting to be dequeued. The thread that holds the winning
 * ticket is chosen.
 * <p>
 * <p>
 * Note that a lottery scheduler must be able to handle a lot of tickets
 * (sometimes billions), so it is not acceptable to maintain state for every
 * ticket.
 * <p>
 * <p>
 * A lottery scheduler must partially solve the priority inversion problem; in
 * particular, tickets must be transferred through locks, and through joins.
 * Unlike a priority scheduler, these tickets add (as opposed to just taking
 * the maximum).
 */
public class LotteryScheduler extends PriorityScheduler {
    /**
     * Allocate a new lottery scheduler.
     */
    public LotteryScheduler() {
    }

    /**
     * Allocate a new lottery thread queue.
     *
     * @param    transferPriority    <tt>true</tt> if this queue should
     * transfer tickets from waiting threads
     * to the owning thread.
     * @return a new lottery thread queue.
     */
    public ThreadQueue newThreadQueue(boolean transferPriority) {
        // implement me
        //Task 2-4
        return new LotteryQueue(transferPriority);

        //return null;
    }
    //Task 2-4

    public void setPriority(KThread thread, int priority) {
        Lib.assertTrue(Machine.interrupt().disabled());

        Lib.assertTrue(priority >= 0);

        getThreadState(thread).setPriority(priority);
    }
    protected ThreadState getThreadState(KThread thread) {
        if (thread.schedulingState == null)
            thread.schedulingState = new LotteryStatus(thread);

        return (LotteryStatus) thread.schedulingState;
    }
    public boolean increasePriority() {
        boolean intStatus = Machine.interrupt().disable();

        KThread thread = KThread.currentThread();

        int priority = getPriority(thread);


        setPriority(thread, priority + 1);

        Machine.interrupt().restore(intStatus);
        return true;
    }

    public boolean decreasePriority() {
        boolean intStatus = Machine.interrupt().disable();

        KThread thread = KThread.currentThread();

        int priority = getPriority(thread);

        setPriority(thread, priority - 1);

        Machine.interrupt().restore(intStatus);
        return true;
    }


    protected class LotteryQueue extends PriorityQueue{
        LotteryQueue(boolean transferPriority){
            super(transferPriority);
//            this.transferPriority = transferPriority;
            rand = new Random();
        }

        protected ThreadState pickNextThread() {
            // implement me
            //task5
            if(waitQueue.isEmpty())return null;
            ThreadState tmp=null;
            int pri = 0;
            for(KThread t:waitQueue){
                tmp = getThreadState(t);
                pri += tmp.getEffectivePriority();
            }
            int randLottery = rand.nextInt(pri);
            pri = 0;
            for(KThread t:waitQueue){
                tmp = getThreadState(t);
                pri += tmp.getEffectivePriority();
                if(pri>=randLottery){
                    return tmp;
                }
            }

            return tmp;
            //return  getThreadState(waitQueue.poll());
            //end task5
        }




        Random rand;
//        boolean transferPriority;




    }
    protected class LotteryStatus extends ThreadState{


        public LotteryStatus(KThread thread) {
            super(thread);



        }
        public int getEffectivePriority(){
            return effective_priority;
        }
        public void setPriority(int priority) {
            if (this.priority == priority) {
                return;
            }
            // implement me

            this.effective_priority += priority - this.priority;
            this.priority = priority;

        }
        public void waitForAccess(PriorityQueue waitQueue) {//将当期进程加入等待队列进行调度
            // implement me
            waitQueue.waitQueue.add(this.thread);
            start_time = Machine.timer().getTime();
            if(waitQueue.transferPriority && waitQueue.donateThread!=null){
                getThreadState(waitQueue.donateThread).effective_priority += this.getEffectivePriority();
            }
        }

    }


}
