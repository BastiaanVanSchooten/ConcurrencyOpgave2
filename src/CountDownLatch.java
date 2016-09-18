import java.util.concurrent.Semaphore;

/**
 * Created by Bastiaan on 18-9-2016.
 */
public class CountDownLatch {

    private int nrOfEvents;
    private int nrOfThreadsWaiting = 0;
    private Semaphore latchOpen, mutex;

    public CountDownLatch(int nrOfEvents) {
        this.nrOfEvents = nrOfEvents;
        latchOpen = new Semaphore(0);
        mutex = new Semaphore(1);
    }

    public void countDown() {
        try {
            mutex.acquire();
            nrOfEvents--;

            if (nrOfEvents == 0) {
                mutex.release();
                latchOpen.release(nrOfThreadsWaiting);
            } else {
                mutex.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void await() throws InterruptedException {

        try {
            mutex.acquire();
            nrOfThreadsWaiting++;
            if (nrOfEvents > 0) {
                mutex.release();
                latchOpen.acquire();
            } else {
                mutex.release();
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }

    }
}
