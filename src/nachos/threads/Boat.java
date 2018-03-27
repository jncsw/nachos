package nachos.threads;

import nachos.ag.BoatGrader;
import nachos.machine.Machine;

public class Boat {
    static BoatGrader bg;

    public static void selfTest() {
        BoatGrader b = new BoatGrader();

        System.out.println("\n ***Testing Boats with only 2 children***");
        begin(0, 2, b);

        System.out.println("\n ***Testing Boats with 2 children, 1 adult***");
        begin(1, 2, b);

        System.out.println("\n ***Testing Boats with 3 children, 3 adults***");
        begin(3, 3, b);
    }

    public static void begin(int adults, int children, BoatGrader b) {
        // Store the externally generated autograder in a class
        // variable to be accessible by children.
        bg = b;

        // Instantiate global variables here
        ChildWaitAtOahu = new Condition2(lock);
        ChildWaitAtMolokai = new Condition2(lock);
        AdultWaitAtOahu = new Condition2(lock);
        BoatPosition = BoatAtOahu;
        BoatLoad = NoPeopleAtBoat;
        ChildAtMolokai = AdultAtMolokai = 0;
        ChildAtOahu = children;
        AdultAtOahu = adults;
        // Create threads here. See section 3.4 of the Nachos for Java
        // Walkthrough linked from the projects page.

//        Runnable r = new Runnable() {
//            public void run() {
//                SampleItinerary();
//            }
//        };
//        KThread t = new KThread(r);
//        t.setName("Sample Boat Thread");
//        t.fork();


        boolean intStatus = Machine.interrupt().disable();


        for(int i = 0;i < adults;i++){
            KThread adultThread = new KThread(new Runnable(){
                public void run() {
                    AdultItinerary();
                }
            });
            adultThread.setName("Adult "+i).fork();
        }

        for(int i = 0;i < children;i++){
            KThread childThread = new KThread(new Runnable(){
                public void run() {
                    ChildItinerary();
                }
            });
            childThread.setName("Child "+i).fork();
        }
        Machine.interrupt().restore(intStatus);

        KThread.yield();

        //分不同情况讨论
        lock.acquire();
        while (AdultAtOahu>0 || ChildAtOahu>0){
            if(BoatPosition == BoatAtOahu){
                if(BoatLoad == NoPeopleAtBoat && ChildAtMolokai>0 && AdultAtOahu>0)AdultWaitAtOahu.wake();
                else ChildWaitAtOahu.wake();
            }else{
                ChildWaitAtMolokai.wake();
            }
            lock.release();
            KThread.yield();
            lock.acquire();
        }
        KThread.yield();
        lock.release();

    }

    static void AdultItinerary() {
        bg.initializeAdult(); //Required for autograder interface. Must be the first thing called.
        //DO NOT PUT ANYTHING ABOVE THIS LINE.

	/* This is where you should put your solutions. Make calls
       to the BoatGrader to show that it is synchronized. For
	   example:
	       bg.AdultRowToMolokai();
	   indicates that an adult has rowed the boat across to Molokai
	*/


        lock.acquire();
        AdultWaitAtOahu.sleep();
        while (BoatPosition == BoatAtMolokai || BoatLoad!=NoPeopleAtBoat || ChildAtMolokai ==0)AdultWaitAtOahu.sleep();
        bg.AdultRowToMolokai();
        --AdultAtOahu;
        BoatPosition = BoatAtMolokai;
        ++AdultAtMolokai;
        BoatLoad = NoPeopleAtBoat;
        lock.release();
    }

    static void ChildItinerary() {
        bg.initializeChild(); //Required for autograder interface. Must be the first thing called.
        //DO NOT PUT ANYTHING ABOVE THIS LINE.

        lock.acquire();
        ChildWaitAtOahu.sleep();
        while (ChildAtOahu>0 || AdultAtOahu>0){
            if(BoatPosition == BoatAtOahu){
                if(AdultAtOahu==0 || ChildAtMolokai ==0){//send children
                    if(BoatLoad == NoPeopleAtBoat){
//                        bg.ChildRowToMolokai();
//                        if(ChildAtOahu>=2){
//                            ChildAtOahu-=2;
//                            BoatLoad = TwoChildrenAtBoat;
//                            bg.ChildRideToMolokai();
//                            ChildAtMolokai+=2;
//                            BoatPosition = BoatAtMolokai;
//                            BoatLoad = NoPeopleAtBoat;
//
//                        }else{
//                            --ChildAtOahu;
//                            BoatLoad = OneChildAtBoat;
//                            ++ChildAtMolokai;
//                            BoatPosition = BoatAtMolokai;
//                            BoatLoad = NoPeopleAtBoat;
//                        }
//                        ChildWaitAtMolokai.sleep();

                        bg.ChildRowToMolokai();
                        --ChildAtOahu;
                        if(ChildAtOahu ==0){//在Oahu没有小孩，则直接开船
                            ChildAtMolokai++;
                            BoatLoad = NoPeopleAtBoat;
                            BoatPosition = BoatAtMolokai;
                        }else{//一个小孩上船
                            BoatLoad = OneChildAtBoat;
                        }
                        ChildWaitAtMolokai.sleep();

                    }else if(BoatLoad == OneChildAtBoat){
                        if(ChildAtOahu>0){
                            --ChildAtOahu;
                            BoatLoad = TwoChildrenAtBoat;

                            BoatPosition = BoatAtMolokai;
                            BoatLoad = NoPeopleAtBoat;
                            ChildAtMolokai+=2;
                            bg.ChildRideToMolokai();
                        }else{
                            BoatPosition = BoatAtMolokai;
                            BoatLoad = NoPeopleAtBoat;
                            ChildAtMolokai++;
                        }


                        ChildWaitAtMolokai.sleep();
                    }else{
                        ChildWaitAtOahu.sleep();
                    }
                }else ChildWaitAtOahu.sleep();
            }else{//At M
                if(BoatLoad == NoPeopleAtBoat){
                    --ChildAtMolokai;
                    ++ChildAtOahu;
                    BoatLoad = OneChildAtBoat;
                    bg.ChildRowToOahu();
                    BoatPosition = BoatAtOahu;
                    BoatLoad = NoPeopleAtBoat;
                    ChildWaitAtOahu.sleep();
                }else{
                    ChildWaitAtMolokai.sleep();
                }
            }
        }
        lock.release();


    }

    static void SampleItinerary() {
        // Please note that this isn't a valid solution (you can't fit
        // all of them on the boat). Please also note that you may not
        // have a single thread calculate a solution and then just play
        // it back at the autograder -- you will be caught.
        System.out.println("\n ***Everyone piles on the boat and goes to Molokai***");
        bg.AdultRowToMolokai();
        bg.ChildRideToMolokai();
        bg.AdultRideToMolokai();
        bg.ChildRideToMolokai();
    }


    //task 1-6
    private static int BoatPosition;
    private final static int BoatAtOahu = 1;
    private final static int BoatAtMolokai = 2;

    private static int BoatLoad;
    private final static int OneChildAtBoat = 1;
    private final static int TwoChildrenAtBoat = 2;
    private final static int OneAdultAtBoat = 3;
    private final static int NoPeopleAtBoat = 4;

    private static int ChildAtOahu = 0;
    private static int ChildAtMolokai = 0;
    private static int AdultAtOahu = 0;
    private static int AdultAtMolokai = 0;


    private static Lock lock = new Lock();
    private static Condition2 ChildWaitAtOahu,AdultWaitAtOahu,ChildWaitAtMolokai;
    //end task 1-6
}
