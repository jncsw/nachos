package nachos.userprog;

import nachos.machine.*;
import nachos.threads.*;
import nachos.userprog.*;

/**
 * A kernel that can support multiple user processes.
 */
public class UserKernel extends ThreadedKernel {
    /**
     * Allocate a new user kernel.
     */
    public UserKernel() {
        super();
    }

    /**
     * Initialize this kernel. Creates a synchronized console and sets the
     * processor's exception handler.
     */
    public void initialize(String[] args) {
        super.initialize(args);
        //Task 2-2
        FreePage = Machine.processor().getNumPhysPages();
        FreePages = new boolean[FreePage];
        for(int i=0;i<FreePage;i++)FreePages[i]=true;
        pageLock = new Lock();

        //end
        console = new SynchConsole(Machine.console());
        Machine.processor().setExceptionHandler(new Runnable() {
            public void run() {
                exceptionHandler();
            }
        });
    }

    /**
     * Test the console device.
     */
    public void selfTest() {
        super.selfTest();

//        System.out.println("Testing the console device. Typed characters");
//        System.out.println("will be echoed until q is typed.");
//
//        char c;
//
//        do {
//            c = (char) console.readByte(true);
//            console.writeByte(c);
//        }
//        while (c != 'q');
//
//        System.out.println("");
    }

    /**
     * Returns the current process.
     *
     * @return the current process, or <tt>null</tt> if no process is current.
     */
    public static UserProcess currentProcess() {
        if (!(KThread.currentThread() instanceof UThread))
            return null;

        return ((UThread) KThread.currentThread()).process;
    }

    /**<p>
     * The exception handler. This handler is called by the processor whenever
     * a user instruction causes a processor exception.
     *
     *
     * When the exception handler is invoked, interrupts are enabled, and the
     * processor's cause register contains an integer identifying the cause of
     * the exception (see the <tt>exceptionZZZ</tt> constants in the
     * <tt>Processor</tt> class). If the exception involves a bad virtual
     * address (e.g. page fault, TLB miss, read-only, bus error, or address
     * error), the processor's BadVAddr register identifies the virtual address
     * that caused the exception.
     * </p>
     */
    public void exceptionHandler() {
        Lib.assertTrue(KThread.currentThread() instanceof UThread);

        UserProcess process = ((UThread) KThread.currentThread()).process;
        int cause = Machine.processor().readRegister(Processor.regCause);
        process.handleException(cause);

    }

    /**
     * Start running user programs, by creating a process and running a shell
     * program in it. The name of the shell program it must run is returned by
     * <tt>Machine.getShellProgramName()</tt>.
     *
     * @see    nachos.machine.Machine#getShellProgramName
     */
    public void run() {
        super.run();

        UserProcess process = UserProcess.newUserProcess();

        String shellProgram = Machine.getShellProgramName();

        Lib.assertTrue(process.execute(shellProgram, new String[]{}));

        KThread.currentThread().finish();
    }

    /**
     * Terminate this kernel. Never returns.
     */
    public void terminate() {
        super.terminate();
    }

    /**
     * Globally accessible reference to the synchronized console.
     */
    public static SynchConsole console;

    // dummy variables to make javac smarter
    private static Coff dummy1 = null;

    //Task 2-2

    public static TranslationEntry[] AssignPage(int cnt){
        TranslationEntry[] pages = new TranslationEntry[cnt];
        int assigned = 0;
        for(int i=0;i<FreePages.length && assigned<cnt;i++){
            if(FreePages[i]){
                TranslationEntry ent = new TranslationEntry();
                ent.ppn = i;
                ent.vpn = assigned;
                ent.readOnly = false;
                ent.valid = true;
                pages[assigned++] = ent;
                FreePages[i]=false;
            }
        }
        FreePage-=cnt;
        return pages;
    }

    public static void releasePage(TranslationEntry[] pages){
        int cnt = 0;
        for(int i=0;i<pages.length;i++){
            if(FreePages[pages[i].ppn]==false){
                cnt++;
                FreePages[pages[i].ppn]=true;
            }
        }
        FreePage+=cnt;

    }




    public static int FreePage;//页数
    private static boolean FreePages[]; //判断是否被分配
    public static Lock pageLock;//保证原子性
    //end


}
