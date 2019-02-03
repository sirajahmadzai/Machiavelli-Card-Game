package client;

import client.views.GameView;
import client.views.StartOptionsView;
import client.views.View;
import client.views.components.CardSetView;
import client.views.components.CardView;
import commands.server.PassTurn;
import commands.server.PlayerMove;
import interfaces.clientManagerInterface;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import server.Server;
import server.models.CardSet;
import server.models.cards.Card;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static utils.constants.PORT_ERROR_MESSAGE;

public class ClientManager implements clientManagerInterface {
    /**
     * PRIVATE STATIC FINALS
     */
    private static final int MINIMUM_SET_SIZE = 3;

    /**
     * PRIVATE FINALS
     */
    private final GameView gameView;
    private final StartOptionsView startOptionsView;


    /**
     * PRIVATE STATICS
     */
    private static ClientManager ourInstance = new ClientManager();
    /**
     * CLASS VARIABLES
     */
    private Server server;
    private boolean serverStarted = false;

    private StackPane root;
    private App app;
    private SelectionManager selectionManager = new SelectionManager();
    private Client client;
    private int currentTurn;
    private CardView lastCardDrawn;

    /**
     * PUBLICS
     */
    public BufferedReader in;
    public PrintWriter out;

    /**
     * gets the instance
     *
     * @return
     */
    public static ClientManager getInstance() {
        return ourInstance;
    }

    /**
     * CONSTRUCTOR
     */
    private ClientManager() {
        this.gameView = GameView.getInstance();
        this.startOptionsView = StartOptionsView.getInstance();
    }

    /**
     * @param port
     * @param numberOfPlayers
     * @param adminName
     * @throws Exception
     */
    public void startServer(int port, int numberOfPlayers, String adminName) throws Exception {
        if (serverStarted) {
            throw new UnsupportedOperationException(PORT_ERROR_MESSAGE + server.getPort());
        }
        try {
            //This is the admin of the game. The admin starts the server and waits for other players to join.
            server = new Server(port, numberOfPlayers);

            Thread serverThread = new Thread(server);
            serverThread.setName("Server thread");
            serverThread.start();
            serverStarted = true;

            //Admin joins the game first.
            loginServer(port, adminName);

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * sets the app
     *
     * @param app
     */
    public void setApp(App app) {
        this.app = app;
    }

    /**
     * sets the stage
     *
     * @param stage
     */
    public void setStage(Stage stage) {
        this.root = (StackPane) stage.getScene().getRoot();
    }

    /**
     * @param view
     */
    public synchronized void showView(View view) {
        root.getChildren().clear();
        root.getChildren().add(view.getRoot());
    }

    /**
     * @param view
     */
    public synchronized void pushView(View view) {
        root.getChildren().add(view.getRoot());
        view.setMainApp(this.app);
    }

    /**
     * @param client
     */
    private void loginServer(Client client) {
        Thread clientThread = new Thread(client);
        this.client = client;
        clientThread.start();
    }

    /**
     * @param numberOfPlayers
     */
    public void startGame(int numberOfPlayers) {
        gameView.setPlayerCount(numberOfPlayers);
        showView(gameView);
    }

    /**
     * @param port
     * @param name
     */
    public void loginServer(int port, String name) {
        try {
            loginServer(new Client(this, port, name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param ip
     * @param port
     * @param name
     */
    public void loginServer(String ip, int port, String name) {
        try {
            loginServer(new Client(this, ip, port, name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param seatNumber
     * @param hand
     */
    public void dealHand(int seatNumber, CardSet hand) {
        for (Card card : hand.getCards()) {
            // 1 open card to the owner
            gameView.addCardToHand(seatNumber, card);

            // 1 hidden card to each opponent.
            gameView.dealHands();
        }
        startTurn();
    }

    /**
     * @param playerName
     * @param playerId
     * @param seatNumber
     * @param owner
     */
    public void introducePlayer(String playerName, int playerId, int seatNumber, boolean owner) {

        if (owner) {
            gameView.setOwnerPlayer(playerId, seatNumber);
        } else {

        }
        gameView.fillSeat(playerName, playerId, seatNumber);


    }

    public void connectionLost() {
        removePlayer(gameView.getOwnerSeat());
    }
    /**
     * @param seatNumber
     */
    public void removePlayer(int seatNumber) {
        if (seatNumber == gameView.getOwnerSeat()) {
            showView(startOptionsView);
            startOptionsView.setMessageText("You've disconnected from the game server.");
        }
        gameView.setMessage("One of the players is disconnected. Waiting for someone to join and game will restart!");
        gameView.removePlayer(seatNumber);
        resetGame();


    }

    private void resetGame() {
        gameView.resetView();
        selectionManager.deselectAll();
        selectionManager.clearSelections();
        lastCardDrawn=null;
    }


    /**
     * When user clicks a card target, we move the card from old set to the target set.
     *
     * @param targetSet
     */
    public void droppedToTarget(CardSetView targetSet) {
        if (!isOwnerTurn()) {
            return;
        }
        gameView.takeSnapshot();

        if (!selectionManager.isEmpty()) {
            // Move all selected cards to the new set.
            for (CardView cardView : selectionManager.getSelectedCards()) {
                targetSet.addCard(cardView.getCard());
                cardView.removeFromParentSet();
                gameView.setPlayAreaActive(false);
            }
            // Clear selections.
            selectionManager.deselectAll();
        }
        gameView.clearMessage();
    }

    //    Keep track of any card selected inside the views.
    //    Activate the play area so that the selected card can be moved to it.
    public void cardSelected(CardView selectedCard) {
        if (!isOwnerTurn()) {
            return;
        }
        selectionManager.addCard(selectedCard);

        if (!selectionManager.isEmpty()) {
            gameView.setPlayAreaActive(selectionManager.getSelectedCardsSet());
        } else {
            gameView.setPlayAreaActive(false);
        }
    }

    private boolean isOwnerTurn() {
        return currentTurn == gameView.getOwnerSeat();
    }

    private void startTurn() {
        gameView.init_snapstate();
        gameView.takeSnapshot();
    }

    public boolean endTurn(MouseEvent event) {
        if (!isOwnerTurn()) {
            return false;
        }
        lastCardDrawn = null;
        selectionManager.deselectAll();

        CardSet prevHand = gameView.getHand().getLastSnapshot();
        CardSet lastHand = gameView.getHand().getCardSet();

//      No card played. Just pass the turn.
        if (prevHand.equals(lastHand)) {
            client.sendCommandToServer(new PassTurn());
            return true;
        }

//      Can't make the move, invalid sets on table.
        if (!gameView.getPlayArea().isValid(MINIMUM_SET_SIZE)) {
            gameView.setMessage("Not a valid play!");

            return false;
        }
        List<CardSet> table = gameView.getPlayArea().takeSnapshot();

        CardSet playedCards = prevHand.diff(lastHand);
        PlayerMove move = new PlayerMove(gameView.getOwnerSeat(), playedCards, table);


        client.sendCommandToServer(move);

        return true;
    }

    public void setWinner(int winnerSeatNumber) {
        if (gameView.getOwnerSeat() == winnerSeatNumber) {
            gameView.setMessage("Hurray!!! You are the winner.");
        } else {
            gameView.setMessage("You've lost the game, better luck next time :)");
        }
    }

    public void switchTurn(int seatNumber) {
        startTurn();
        currentTurn = seatNumber;
        gameView.switchTurn(seatNumber);

        if (lastCardDrawn != null) {
            lastCardDrawn.setNewcomer(true);
        }
    }

    public void drawCard(int seatNumber, Card card) {
        CardView drawnCard = gameView.addCardToHand(seatNumber, card);
        if (!drawnCard.getCard().isHidden()) {
            lastCardDrawn = drawnCard;
        }
        startTurn();
    }

    public void playMove(PlayerMove move) {
        gameView.getPlayArea().setAllSets(move.getTable());
        gameView.removeCardsFrom(move.getSeatNumber(), move.getPlayedCards());
        startTurn();
    }


}
