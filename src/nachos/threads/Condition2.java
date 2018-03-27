package nachos.threads;

import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 * <p>
 * <p>
 * You must implement this.
 *
 * @see nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     *
     * @param conditionLock the lock associated with this condition
     *                      variable. The current thread must hold this
     *                      lock whenever it uses <tt>sleep()</tt>,
     *                      <tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
        this.conditionLock = conditionLock;
        waitQueue = ThreadedKernel.scheduler.newThreadQueue(false);
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt>  returns.
     */
    public void sleep() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());
        //Condition
        boolean intStatus = Machine.interrupt().disable();
        waitQueue.waitForAccess(KThread.currentThread());
        //end Condition
        conditionLock.release();
        //Condition
        KThread.sleep();
        //end Condition
        conditionLock.acquire();
        //Condition
        Machine.interrupt().restore(intStatus);
        //end Condition
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());
        //Condition
        boolean intStatus = Machine.interrupt().disable();
        KThread nextThread = waitQueue.nextThread();
        if(nextThread!=null){
            nextThread.ready();//放入等待队列
        }

        Machine.interrupt().restore(intStatus);
        //end Condition
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());
        //Condition
        boolean intStatus = Machine.interrupt().disable();
        KThread nextThread = null;
        while (true){
            nextThread = waitQueue.nextThread();
            if(nextThread==null) break;
            nextThread.ready();
        }
        Machine.interrupt().restore(intStatus);
        //end Condition

    }

    private Lock conditionLock;

    //Condition
    private ThreadQueue waitQueue = null;
    //ENd Condition

}
