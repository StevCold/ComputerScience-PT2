package ro.tuc.pt.model;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<ClientQueue> queues;
    private Strategy strategy;

    private ArrayList<Integer> peakHours;
    private int maxClientsPeakHour = 0;
    private int totalServiceTime = 0;

    public Scheduler(int maxNoQueues) {
        strategy = new ConcreteStrategyQueue();
        queues = new ArrayList<>();
        peakHours = new ArrayList<>();

        for (int i = 0; i < maxNoQueues; i++) {
            queues.add(new ClientQueue());
        }
    }

    public void changeStrategy(SelectionPolicy policy) {
        if (policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ConcreteStrategyQueue();
        }

        if (policy == SelectionPolicy.SHORTEST_TIME) {
            strategy = new ConcreteStrategyTime();
        }
    }

    public void dispatchClient(Client client) {
        strategy.addClient(queues, client);
    }

    public List<ClientQueue> getQueues() {
        return queues;
    }

    public void startAllQueues() {
        for (ClientQueue clientQueue : queues) {
            Thread thread = new Thread(clientQueue);
            thread.setDaemon(true);
            thread.start();
        }
    }

    public void stopAllQueues() {
        for (ClientQueue clientQueue : queues) {
            clientQueue.stopThread();
        }
    }

    public int getTotalWaitingTime() {
        int totalWaitingTime = 0;
        for (ClientQueue q : queues) {
            totalWaitingTime += q.getTotalWaitingTime();
        }

        return totalWaitingTime;
    }

    public void checkServiceTime() {
        for (ClientQueue q : queues) {
            if (q.getQueueSize() > 0) {
                totalServiceTime++;
                break;
            }
        }
    }

    public int getTotalServiceTime() {
        return totalServiceTime;
    }

    public int getClientsInQueues() {
        int clientsInQueues = 0;
        for (ClientQueue q : queues) {
            clientsInQueues += q.getQueueSize();
        }

        return clientsInQueues;
    }

    public void checkPeakHour(int currentHour) {
        int numberOfClients = 0;
        for (ClientQueue q : queues) {
            numberOfClients += q.getQueueSize();
        }

        if (numberOfClients == maxClientsPeakHour) {
            peakHours.add(currentHour);
        } else if (numberOfClients > maxClientsPeakHour) {
            peakHours.clear();
            peakHours.add(currentHour);
            maxClientsPeakHour = numberOfClients;
        }
    }

    public ArrayList<Integer> getPeakHour() {
        return peakHours;
    }

    public String getLogs() {
        String queuesLog = "";
        for (ClientQueue q : queues) {
            queuesLog = queuesLog + q.getLog();
        }

        queuesLog = queuesLog + "\n";
        return queuesLog;
    }
}
