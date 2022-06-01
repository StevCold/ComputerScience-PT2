package ro.tuc.pt.model;

import java.util.List;

public class ConcreteStrategyTime implements Strategy {
    @Override
    public void addClient(List<ClientQueue> queues, Client client) {
        int numberOfQueues = queues.size();
        int minWaitingPeriod = queues.get(0).getWaitingPeriod();
        int minTimeQueueIndex = 0;
        for (int i = 1; i < numberOfQueues; i++) {
            int currentQueueTime = queues.get(i).getWaitingPeriod();
            if (currentQueueTime < minWaitingPeriod) {
                minWaitingPeriod = currentQueueTime;
                minTimeQueueIndex = i;
            }
        }

        queues.get(minTimeQueueIndex).addClient(client);
    }
}
