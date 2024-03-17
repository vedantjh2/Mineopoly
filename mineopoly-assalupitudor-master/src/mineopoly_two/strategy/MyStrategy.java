package mineopoly_two.strategy;

import mineopoly_two.action.TurnAction;
import mineopoly_two.game.Economy;
import mineopoly_two.item.InventoryItem;
import mineopoly_two.tiles.TileType;
import java.awt.*;
import java.util.*;
import java.util.List;

import static mineopoly_two.util.DistanceUtil.getManhattanDistance;

public class MyStrategy implements MinePlayerStrategy {

    private int boardSize;
    private int maxCharge;
    private int maxInventorySize;
    private List<InventoryItem> myInventory;
    //'n' means not to charge and 'c' means need to charge
    private char state;
    private Point marketPlace;


    /**
     * Called at the start of every round
     *
     * @param boardSize         The length and width of the square game board
     * @param maxInventorySize  The maximum number of items that your player can carry at one time
     * @param maxCharge         The amount of charge your robot starts with (number of tile moves before needing to recharge)
     * @param winningScore      The first player to reach this score wins the round
     * @param startingBoard     A view of the GameBoard at the start of the game. You can use this to pre-compute fixed
     *                          information, like the locations of market or recharge tiles
     * @param startTileLocation A Point representing your starting location in (x, y) coordinates
     *                          (0, 0) is the bottom left and (boardSize - 1, boardSize - 1) is the top right
     * @param isRedPlayer       True if this strategy is the red player, false otherwise
     * @param random            A random number generator, if your strategy needs random numbers you should use this.
     */
    @Override
    public void initialize(int boardSize, int maxInventorySize, int maxCharge, int winningScore,
                                           PlayerBoardView startingBoard, Point startTileLocation, boolean isRedPlayer,
                           Random random) {
        this.boardSize = boardSize;
        this.maxCharge = maxCharge;
        this.maxInventorySize = maxInventorySize;
        this.myInventory = new ArrayList<>();
        this.state = 'n';

        //gets location of market place depending on the color of the player
        this.marketPlace = null;
        for (int x = 0; x < boardSize - 1; x++) {
            for (int y = 0; y < boardSize - 1; y++) {
                startingBoard.getTileTypeAtLocation(x, y);
                if (isRedPlayer) {
                    if (startingBoard.getTileTypeAtLocation(x, y).equals(TileType.RED_MARKET)) {
                        marketPlace = new Point(x, y);
                        break;
                    }
                } else {
                    if (startingBoard.getTileTypeAtLocation(x, y).equals(TileType.BLUE_MARKET)) {
                        marketPlace = new Point(x, y);
                        break;
                    }
                }

            }
        }


    }

    /**
     * The main part of your strategy, this method returns what action your player should do on this turn
     *
     * @param boardView     A PlayerBoardView object representing all the information about the board and the other player
     *                      that your strategy is allowed to access
     * @param economy       The GameEngine's economy object which holds current prices for resources
     * @param currentCharge The amount of charge your robot has (number of tile moves before needing to recharge)
     * @param isRedTurn     For use when two players attempt to move to the same spot on the same turn
     *                      If true: The red player will move to the spot, and the blue player will do nothing
     *                      If false: The blue player will move to the spot, and the red player will do nothing
     * @return The TurnAction enum for the action that this strategy wants to perform on this game turn
     */
    @Override
    public TurnAction getTurnAction(PlayerBoardView boardView, Economy economy, int currentCharge, boolean isRedTurn) {
        //get coordinates of the charging tiles
        Point chargingTile1 =  new Point((boardSize/2)-1, (boardSize/2)-1); // bottom left tile
        Point chargingTile2 =  new Point((boardSize/2)-1, (boardSize/2)); // upper left tile
        Point chargingTile3 =  new Point((boardSize/2), (boardSize/2)); //upper right tile
        Point chargingTile4 =  new Point((boardSize/2), (boardSize/2)-1); // lower right tile
        //add the points to an array with charging point 1 being first always;
        List<Point> chargingTileList = new ArrayList<>();
        chargingTileList.add(chargingTile1);
        chargingTileList.add(chargingTile2);
        chargingTileList.add(chargingTile3);
        chargingTileList.add(chargingTile4);

        Point currentPoint = boardView.getYourLocation();
        Map<Point, InventoryItem> itemsOnGround = boardView.getItemsOnGround();
        InventoryItem itemAtCurrentPoint= itemsOnGround.get(currentPoint);


        //charging the bot
        Point nearestChargingTile = chargingTileList.get(0);
        for (Point p: chargingTileList) {
            if (getManhattanDistance(currentPoint, p) < getManhattanDistance(currentPoint, nearestChargingTile)) {
                nearestChargingTile = p;
            }
        }
        int xDistanceFromChargingTile = nearestChargingTile.x - currentPoint.x;
        int yDistanceFromChargingTile = nearestChargingTile.y - currentPoint.y;
        if (currentCharge < 0.2*maxCharge) {
            this.state = 'c';
        }
        if (currentCharge >= 0.9*maxCharge) {
            this.state = 'n';
        }
        if (state == 'c') {
            //add code to charge at least till 80%
            return getToTile(xDistanceFromChargingTile, yDistanceFromChargingTile);

        }


        //going to the market tile;
        int xDistanceFromMarket = marketPlace.x - currentPoint.x;
        int yDistanceFromMarket = marketPlace.y - currentPoint.y;
        if (currentPoint.equals(marketPlace)) {
            myInventory.clear();
        }

        if (myInventory.size() == maxInventorySize - 1) {
            return getToTile(xDistanceFromMarket, yDistanceFromMarket);
        }

        if (itemAtCurrentPoint != null) {
            myInventory.add(itemAtCurrentPoint);
            return TurnAction.PICK_UP;

        }
        if (boardView.getTileTypeAtLocation(currentPoint).equals(TileType.RESOURCE_DIAMOND)
                || boardView.getTileTypeAtLocation(currentPoint).equals(TileType.RESOURCE_RUBY)
                || boardView.getTileTypeAtLocation(currentPoint).equals(TileType.RESOURCE_EMERALD)) {
            return TurnAction.MINE;
        }


        //main game logic
        List<Point> resourceTiles = new ArrayList<>();
        //traverses through all the blocks in the game and finds the x and y coordinates of a resource tile
        for (int x = 0; x < boardSize - 1; x++) {
            for (int y = 0; y < boardSize - 1; y++) {
                if (boardView.getTileTypeAtLocation(x, y).equals(TileType.RESOURCE_DIAMOND)
                        || boardView.getTileTypeAtLocation(x, y).equals(TileType.RESOURCE_RUBY)
                        || boardView.getTileTypeAtLocation(x, y).equals(TileType.RESOURCE_EMERALD)) {
                    //adds tile to the list of resource tiles
                    resourceTiles.add(new Point(x, y));

                }

            }
        }


        Point resourceTile = resourceTiles.get(0);
        int xDistanceFromResourceTile = resourceTile.x - currentPoint.x;
        int yDistanceFromResourceTile = resourceTile.y - currentPoint.y;
        return getToTile(xDistanceFromResourceTile, yDistanceFromResourceTile);
    }

    /**
     * Called when the player receives an item from performing a TurnAction that gives an item.
     * At the moment this is only from using PICK_UP on top of a mined resource
     *
     * @param itemReceived The item received from the player's TurnAction on their last turn
     */
    @Override
    public void onReceiveItem(InventoryItem itemReceived) {


    }

    /**
     * Called when the player steps on a market tile with items to sell. Tells your strategy how much all
     * of the items sold for.
     *
     * @param totalSellPrice The combined sell price for all items in your strategy's inventory
     */
    @Override
    public void onSoldInventory(int totalSellPrice) {

    }

    /**
     * Gets the name of this strategy. The amount of characters that can actually be displayed on a screen varies,
     * although by default at screen size 750 it's about 16-20 characters depending on character size
     *
     * @return The name of your strategy for use in the competition and rendering the scoreboard on the GUI
     */
    @Override
    public String getName() {
        return "Smart_Strategy";
    }

    /**
     * Called at the end of every round to let players reset, and tell them how they did if the strategy does not
     * track that for itself
     *
     * @param pointsScored         The total number of points this strategy scored
     * @param opponentPointsScored The total number of points the opponent's strategy scored
     */
    @Override
    public void endRound(int pointsScored, int opponentPointsScored) {

    }

    /**
     * method to get to the specific tile using standard coordinate system considering the current location as zero
     * @param xDistanceFromChargingTile number of tiles in the x coordinate to the Tile
     * @param yDistanceFromChargingTile number of tiles in the y coordinate to the Tile
     * @return TurnAction depending on position
     */
    public static TurnAction getToTile(int xDistanceFromChargingTile, int yDistanceFromChargingTile) {
        //movement in x direction
        if (xDistanceFromChargingTile > 0) {
            return TurnAction.MOVE_RIGHT;
        } else if (xDistanceFromChargingTile < 0) {
            return TurnAction.MOVE_LEFT;
        }
        //movement in y direction
        if (yDistanceFromChargingTile > 0) {
            return TurnAction.MOVE_UP;
        } else if (yDistanceFromChargingTile < 0) {
            return TurnAction.MOVE_DOWN;
        }
        return null;
    }




}
