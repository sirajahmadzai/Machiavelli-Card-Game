package client.views;

import client.ClientManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import utils.constants;

/**
 *
 */
public class StartOptionsView extends View {
    /**
     * FXML INJECTIONS
     */
    @FXML
    private TextField joinIp;
    @FXML
    private TextField joinPort;
    @FXML
    private TextField newGamePort;
    @FXML
    private TextField numOfPlayers;
    @FXML
    private TextField userName;
    @FXML
    private TextField adminUserName;
    @FXML
    private Text messageText;

    @FXML
    private ToggleButton modeReactive;
    @FXML
    private ToggleButton modeProactive;

    @FXML
    private TabPane loginTabs;

    private constants.GameMode serverMode;

    /************************
     **** PRIVATE STATICS ***
     ************************/
    private static StartOptionsView ourInstance = new StartOptionsView();


    /***********************
     ***** CONSTRUCTOR *****
     ***********************/
    private StartOptionsView() {
        super();
        fxml = "/fxml/StartOptions.fxml";

        try {
            loadFxml();

            ToggleGroup toggleGroup = modeReactive.getToggleGroup();
            setServerMode(toggleGroup.getSelectedToggle());
            toggleGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
                if (newVal == null) {
                    oldVal.setSelected(true);
                } else {
                    setServerMode(newVal);
                }
            });

            loginTabs.widthProperty().addListener((observable, oldValue, newValue) ->
            {
                double width=loginTabs.getWidth() / 2-25;
                loginTabs.setTabMinWidth(width);
                loginTabs.setTabMaxWidth(width);
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setServerMode(Toggle selectedMode) {
        if (selectedMode == modeReactive) {
            serverMode = constants.GameMode.REACTIVE;
        } else {
            serverMode = constants.GameMode.PROACTIVE;
        }
    }
    /**
     * GETTERS
     */
    /**
     * get the instance
     *
     * @return
     */
    public static StartOptionsView getInstance() {
        return ourInstance;
    }

    /**
     * handles new game action event
     *
     * @param ae
     */
    @FXML
    public void onNewGame(ActionEvent ae) {
        int numberOfPlayers = Integer.parseInt(numOfPlayers.getText());
        int port = Integer.parseInt(newGamePort.getText());

        try {
            ClientManager.getInstance().startServer(port, numberOfPlayers, adminUserName.getText(), serverMode);
            setMessageText("Started server at port " + port);
        } catch (Exception e) {
            setMessageText("Couldn't start server:" + e.getMessage());
        }
    }

    /**
     * handles join game action event
     *
     * @param ae
     */
    @FXML
    public void onJoinGame(ActionEvent ae) {
        int port = Integer.parseInt(joinPort.getText());
        String ip = joinIp.getText();

        String userNameText = userName.getText();

        ClientManager.getInstance().loginServer(ip, port, userNameText);
    }

    public void setMessageText(String message) {
        messageText.setText(message);
    }
}
