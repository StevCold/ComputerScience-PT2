package ro.tuc.pt.model;

import java.util.List;

public class ConcreteStrategyQueue implements Strategy {
    @Override
    public void addClient(List<ClientQueue> queues, Client client) {
        int numberOfQueues = queues.size();
        int minSize = queues.get(0).getQueueSize();
        int minSizeQueueIndex = 0;
        for (int i = 1; i < numberOfQueues; i++) {
            int currentQueueSize = queues.get(i).getQueueSize();
            if (currentQueueSize < minSize) {
                minSize = currentQueueSize;
                minSizeQueueIndex = i;
            }
        }

        queues.get(minSizeQueueIndex).addClient(client);
    }
}
