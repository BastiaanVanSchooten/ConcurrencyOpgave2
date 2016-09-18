import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by Bastiaan on 18-9-2016.
 */
public class Company {

    private static final int NR_OF_CUSTOMERS = 10;
    private static final int NR_OF_DEVS = 6;
    private int customersWithProblem;
    private Semaphore freeDev, freeJaap, invitationToCompany, invitationForMeeting, counterMutex, listMutex;
    private Developer[] devs;
    private Customer[] customers;
    private Jaap jaap;
    private ArrayList<Customer> waitingCustomers;
    private CountDownLatch readyForMeeting, startMeeting, waitForEndOFMeeting, endMeeting;

    public void run() {

        init();

        jaap.start();

        for (Customer customer : customers) {
            customer.start();
        }

        for (Developer developer : devs) {
            developer.start();
        }

    }

    private void init() {

        freeDev = new Semaphore(0, true);
        freeJaap = new Semaphore(0, true);
        invitationToCompany = new Semaphore(0, true);
        counterMutex = new Semaphore(1);
        listMutex = new Semaphore(1);
        invitationForMeeting = new Semaphore(0, true);

        jaap = new Jaap();

        waitingCustomers = new ArrayList<>();
        customersWithProblem = 0;

        devs = new Developer[NR_OF_DEVS];
        for (int i = 0; i < NR_OF_DEVS; i++) {
            devs[i] = new Developer();
        }

        customers = new Customer[NR_OF_CUSTOMERS];
        for (int i = 0; i < NR_OF_CUSTOMERS; i++) {
            customers[i] = new Customer();
        }

    }

    private void attendMeeting() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class Developer extends Thread {

        @Override
        public void run() {

            while (true) {

                // Dev meldt 'regelmatig' dat hij besschikbaar is voor overleg
                // Als Jaap in overleg is gaat de dev weer aan het werk
                // Als Jaap niet in overleg is, gaat hij wachten op uitnodiging voor overleg
                // Als Jaap gaat overleggen, nodigt hij genoeg devs uit, als een dev wel aan het wachten was voor overleg maar wordt niet uitgenodigd, gaat deze weer aan het werk
                // Wordt hij wel uitgenodigd --> Wacht tot iedereen zit, wacht tot overleg klaar is, ga weer aan het werk

            }

        }
    }

    public class Customer extends Thread {

        @Override
        public void run() {

            while (true) {

                // Customer meld zich aan met een probleem
                // Customer wacht op uitnodiging
                // Customer meldt dat hij er is
                // Customer wacht tot overleg begint
                // Customer wacht tot iedereen zit
                // Customer wacht tot overleg is afgelopen
                // Customer gaat verder met leven, tot nieuw probleem (?)

                // Bepaal of er een probleem is
                double problemOrNo = Math.random();

                // Als er een probleem is (3% kans per cycle):
                if (problemOrNo <= 0.03) {
                    try {
                        // Verhoog aantal wachtende klanten met 1, mutex voor kritieke actie
                        counterMutex.acquire();
                        customersWithProblem++;
                        System.out.println(customersWithProblem);
                        counterMutex.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        // Klant wacht op uitnodiging van Jaap, en reist naar bedrijf
                        invitationToCompany.acquire();
                        travelToCompany();

                        // Klant voegt zichzelf toe aan lijst met op locatie wachtende klanten, mutex voor kritieke actie
                        listMutex.acquire();
                        waitingCustomers.add((Customer) Thread.currentThread());
                        listMutex.release();

                        // Wachtende klant wacht op sein dat het overleg gaat beginnen
                        invitationForMeeting.acquire();

                        // Meld dat hij klaar is om te beginnen
                        readyForMeeting.countDown();
                        // Gaat wachten op begin meeting
                        startMeeting.await();
                        // Woon meeting bij
                        attendMeeting();
                        // Meld klaar te zijn voor afsluiting van de meeting
                        waitForEndOFMeeting.countDown();
                        // Wacht op einde van de meeting
                        endMeeting.await();


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

        }

        private void travelToCompany() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public class Jaap extends Thread {

        @Override
        public void run() {

            while (true) {

                // Jaap probeert een dev te aquiren voor overleg {dus sleep wanneer geen beschikbaar}
                // if(waitingCustomers.size() > 0){
                //      freeDev.aquire();
                //      overleg met klanten
                // } else {
                //      freeDev.aquire(3);
                //      overleg met devs
            }

        }

    }

    public static void main(String[] args) {
        new Company().run();
    }

}
