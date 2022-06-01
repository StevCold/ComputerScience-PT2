package ro.tuc.pt.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ro.tuc.pt.model.SimulationManager;

public class Controller {
    private ObservableList<String> strategiesList = FXCollections.observableArrayList("Shortest queue", "Shortest time");

    @FXML
    private TextField numberOfClientsTextField;

    @FXML
    private TextField numberOfQueuesTextField;

    @FXML
    private TextField simulationTimeTextField;

    @FXML
    private TextField minArrivalTimeTextField;

    @FXML
    private TextField maxArrivalTimeTextField;

    @FXML
    private TextField minServiceTimeTextField;

    @FXML
    private TextField maxServiceTimeTextField;

    @FXML
    private TextArea currentQueuesStatusTextArea;

    @FXML
    private Button simulationButton;

    @FXML
    private Label simulationStatusLabel;

    @FXML
    private ChoiceBox strategyChoiceBox;

    @FXML
    protected void initialize() {
        strategyChoiceBox.setValue("Shortest queue");
        strategyChoiceBox.setItems(strategiesList);
    }

    @FXML
    protected void simulate(ActionEvent event) {
        int numberOfClients = Integer.parseInt(numberOfClientsTextField.getText());
        int maxNumberOfQueues = Integer.parseInt(numberOfQueuesTextField.getText());
        int maxSimulationTime = Integer.parseInt(simulationTimeTextField.getText());
        int minArrivalTime = Integer.parseInt(minArrivalTimeTextField.getText());
        int maxArrivalTime = Integer.parseInt(maxArrivalTimeTextField.getText());
        int minServiceTime = Integer.parseInt(minServiceTimeTextField.getText());
        int maxServiceTime = Integer.parseInt(maxServiceTimeTextField.getText());

        if (minArrivalTime >= maxArrivalTime || minServiceTime >= maxServiceTime)
            return;

        String strategy = strategyChoiceBox.getValue().toString();
        System.out.println(strategy);

        SimulationManager manager = new SimulationManager(numberOfClients, maxNumberOfQueues, maxSimulationTime, minArrivalTime, maxArrivalTime, minServiceTime, maxServiceTime, strategy, "log.txt", this);

        manager.setOnSucceeded(event2 -> {
            updateSimulationStatusLabel("Simulation done!");
            disableSimulationButton(false);
        });

        updateSimulationStatusLabel("Simulation running");
        Thread t = new Thread(manager);
        t.setDaemon(true);
        t.start();
    }

    public void updateSimulationStatusLabel(String message) {
        simulationStatusLabel.setText(message);
    }

    public void updateQueueTextArea(String text) {
        currentQueuesStatusTextArea.setText(text);
    }

    public void disableSimulationButton(boolean disable) {
        simulationButton.setDisable(disable);
    }
}
