package ro.tuc.pt.model;

import java.util.List;

public interface Strategy {
    public void addClient(List<ClientQueue> queues, Client client);
}
