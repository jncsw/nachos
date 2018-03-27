package nachos.threads;

import nachos.machine.*;

import java.util.LinkedList;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
        lock = new Lock();
        hasSpeaker = false;
        speaker = new Condition(lock);//条件变量
        listener = new Condition(lock);//条件变量
        speakercnt = listenercnt = 0;
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     * <p>
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param    word    the integer to transfer.
     */
    public void speak(int word) {
        lock.acquire();
        speakercnt++;
        if(speakercnt>1){
            speaker.sleep();
        }
//        System.out.println("Speaker is speaking : "+word);
        this.word = word;
        hasSpeaker = true;
        listener.wake();
        speaker.sleep();
//        System.out.println("Speaker done");
        speakercnt--;
        speaker.wake();
        lock.release();
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return the integer transferred.
     */
    public int listen() {
        lock.acquire();
        listenercnt++;
        if(!hasSpeaker || listenercnt>1){
            listener.sleep();
        }
//        System.out.println("listener received : "+word);
//        System.out.println("waking up a speaker");

        hasSpeaker = false;

        speaker.wake();
        listenercnt--;
        lock.release();
        return word;
    }
    //task1-4
    private Lock lock;
    private int word;
    private Condition speaker,listener;
    private int speakercnt,listenercnt;
    private boolean hasSpeaker;
    //end task1-4
}
