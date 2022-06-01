package ro.tuc.pt.model;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

public class ClientQueue implements Runnable {
    private static int totalQueues;
    private int queueNumber;
    private LinkedBlockingQueue<Client> clients;
    private AtomicInteger waitingPeriod;
    private AtomicInteger totalWaitingTime;
    private boolean isRunning = true;

    public ClientQueue() {
        totalQueues++;
        queueNumber = totalQueues;
        clients = new LinkedBlockingQueue<>();

        waitingPeriod = new AtomicInteger(0);
        totalWaitingTime = new AtomicInteger(0);
    }

    public static void resetTotalQueues() {
        totalQueues = 0;
    }

    public void addClient(Client client) {
        try {
            clients.put(client);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        waitingPeriod.addAndGet(client.getServiceTime());
    }

    public void stopThread() {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Client c = clients.peek();
                if (c != null) {
                    sleep(c.getServiceTime() * 1000L);
                    clients.poll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getWaitingPeriod() {
        return waitingPeriod.get();
    }

    public int getTotalWaitingTime() {
        return totalWaitingTime.get();
    }

    public int getQueueSize() {
        return clients.size();
    }

    public String getLog() {
        String s = "Queue " + queueNumber + ": ";

        if (clients.size() == 0)
            s = s + "closed";
        else {
            int cont = 0;
            for (Client client : clients) {
                int clientServiceTime = client.getServiceTime();
                if (clientServiceTime > 0)
                    s = s + "(" + client.getId() + "," + client.getArrivalTime() + "," + clientServiceTime + "); ";
                else cont++;
            }
            if (cont == clients.size())
                s = s + "closed";
        }
        s = s + "\n";

        if (clients.size() != 0) {
            totalWaitingTime.addAndGet(clients.size());
            Client c = clients.peek();
            c.setServiceTime(c.getServiceTime() - 1);

            waitingPeriod.decrementAndGet();
        }

        return s;
    }
}
