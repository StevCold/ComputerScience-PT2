package ro.tuc.pt.model;

import javafx.application.Platform;
import javafx.concurrent.Task;
import ro.tuc.pt.controllers.Controller;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.Thread.sleep;

public class SimulationManager extends Task<Void> {
    private int maxNumberOfQueues;
    private int maxNumberOfClients;

    private int maxSimulationTime;

    private int minArrivalTime;
    private int maxArrivalTime;

    private int minServiceTime;
    private int maxServiceTime;

    private String fileName;

    private List<Client> clients;
    private Scheduler scheduler;
    private FileWriter fileWriter;

    private Controller controller;

    public SimulationManager(int maxNumberOfClients, int maxNumberOfQueues, int maxSimulationTime, int minArrivalTime, int maxArrivalTime, int minServiceTime, int maxServiceTime, String strategy, String logFileName, Controller controller) {
        this.controller = controller;
        this.maxNumberOfQueues = maxNumberOfQueues;
        this.maxNumberOfClients = maxNumberOfClients;
        this.maxSimulationTime = maxSimulationTime;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.minServiceTime = minServiceTime;
        this.maxServiceTime = maxServiceTime;
        this.fileName = logFileName;

        try {
            fileWriter = new FileWriter(logFileName, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientQueue.resetTotalQueues();
        clients = new ArrayList<Client>();
        scheduler = new Scheduler(maxNumberOfQueues);

        if (strategy.equals("Shortest queue"))
            scheduler.changeStrategy(SelectionPolicy.SHORTEST_QUEUE);
        else scheduler.changeStrategy(SelectionPolicy.SHORTEST_TIME);

        clearLogFile();

        generateRandomClients(maxNumberOfClients);

        /// Start all threads from the scheduler
        scheduler.startAllQueues();
    }

    private void generateRandomClients(int maxNoClients) {
        Random random = new Random();
        int arrivalTime;
        int serviceTime;

        for (int i = 1; i <= maxNoClients; i++) {
            arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime) + minArrivalTime;
            serviceTime = random.nextInt(maxServiceTime - minServiceTime) + minServiceTime;

            clients.add(new Client(i, arrivalTime, serviceTime));
        }

        Collections.sort(clients, new Comparator<Client>() {
            @Override
            public int compare(Client c1, Client c2) {
                return c1.getArrivalTime() - c2.getArrivalTime();
            }
        });
    }

    @Override
    protected Void call() throws Exception {
        Platform.runLater(() -> controller.disableSimulationButton(true));

        int currentTime = 0;
        ArrayList<Client> clientsToRemove;

        while (currentTime < maxSimulationTime && (scheduler.getClientsInQueues() > 0 || clients.size() > 0)) {
            int i = 0;
            boolean clientAddedInQueue = false;

            /// Check for clients to be added in a queue
            while (i < clients.size()) {
                if (clients.get(i).getArrivalTime() == currentTime) {
                    scheduler.dispatchClient(clients.get(i));
                    clientAddedInQueue = true;
                    clients.remove(clients.get(i));
                } else i++;
            }

            if (clientAddedInQueue)
                scheduler.checkPeakHour(currentTime);

            scheduler.checkServiceTime();
            writeLogFile(currentTime);

            currentTime++;
            sleep(1000);
        }

        scheduler.stopAllQueues();

        writeSimulationResults();
        fileWriter.close();

        return null;
    }

    private void clearLogFile() {
        String logText = "";
        try {
            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write(logText);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private void writeLogFile(int currentTime) throws IOException {
        String logText = "Time " + currentTime + "\n";

        if (clients.size() > 0) {
            logText = logText + "Waiting clients: ";
            for (Client client : clients) {
                logText += "(" + client.getId() + "," + client.getArrivalTime() + "," + client.getServiceTime() + "); ";
            }
            logText += "\n";
        }

        logText = logText + scheduler.getLogs();
        fileWriter.write(logText);

        /// Update the user interface
        String finalLogText = logText;
        Platform.runLater(() -> controller.updateQueueTextArea(finalLogText));
    }

    private void writeSimulationResults() throws IOException {
        String logText = "Average waiting time: ";

        int totalWaitingTime = scheduler.getTotalWaitingTime();

        logText = logText + ((float) totalWaitingTime / maxNumberOfClients) + "\n";
        logText = logText + "Total service time: " + scheduler.getTotalServiceTime() + "\n";
        logText = logText + "Peak hour: ";

        ArrayList<Integer> peakHours = scheduler.getPeakHour();
        for (Integer i : peakHours) {
            logText = logText + i + " ";
        }
        logText += "\n";

        fileWriter.write(logText);

        /// Update the user interface
        String finalLogText = logText;
        Platform.runLater(() -> controller.updateQueueTextArea(finalLogText));
    }
}
