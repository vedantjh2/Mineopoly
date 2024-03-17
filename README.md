The Game


The game is a turn based mining competition played on an NxN square set of tiles which is considered the Game Board. There are 4 types of tiles on the board:

Empty tiles: These tiles contain nothing
Resource tiles: These tiles contain a type of ore you can mine
Market tiles: Stepping on the market tile for your robot's color will sell all gathered resources
Recharge tiles: Standing on a recharge tile will give your robot more energy, as your robot's charge depletes when you move
There are two players per game, one with a red robot and one with a blue robot. Both players start in the center of the board on their lower market tile. Each turn both players will receive information about the board state. This information contains where all the resources are, where your opponent is, prices for each resource, and other such information. Each turn both players will be required to return a TurnAction. This TurnAction is an enum value which indicates what your strategy wants your player to do for the current turn. TurnActions are one of the following:

MOVE_UP, MOVE_DOWN, MOVE_LEFT, or MOVE_RIGHT all indicate that your player would like to move in that direction on this turn. Movement is relative to the bottom left tile, so MOVE_UP will move your player towards the top of your screen and MOVE_RIGHT will move your player towards the right of your screen. If your robot is out of charge (indicated by the battery icon in the GUI), then your robot will be in low-power mode. When in low-power mode, your robot only has a 25% chance to successfully move to another tile in a single turn.

MINE indicates that you would like to mine your player's current tile. How many turns it takes to finish mining a resource depends on the resource type. Diamond takes 3 turns to mine, emerald takes 2 turns to mine, and ruby only takes 1 turn to mine. When you have mined a resource enough, it will drop a gem on the tile your player is standing on.

PICK_UP indicates you would like to add an item to your inventory. If your player is standing on a tile that has a mined gem, it will add the gem to your inventory. The maximum inventory size for your player will always be 5. Attempting to pick up an item when your inventory is full will leave the item on the ground.

null indicates that you would like to do nothing on your turn. You will need to return null for multiple turns in a row to become fully charged while standing on a recharge tile.

As previously mentioned, market tiles are where you can sell your gathered gems for points. Stepping on one of these tiles will immediately sell every gem in your inventory at the current price for each type of gem. The price for each gem will then decrease depending on the amount of each type sold. Prices for gems will steadily increase over time again until reaching the max value for the gem type.

Finally, since players execute turns "at the same time", potential conflicts such as moving into the same tile will be handled by the concept of red turns and blue turns. On a red turn, the red player will have priority for these types of moves. On a blue turn, the blue player will have priority. Red turns and blue turns will alternate throughout the game. The first turn will be a red turn. So if two players are trying to move into the same tile, and it is currently a red turn, the red player has priority and will successfully move into that tile. The blue player will be blocked from moving and therefore do nothing.

The Interface

We have given you an interface that your player strategy should implement. You need to implement this interface without modifications, otherwise your strategy will not run in the provided game engine or the competition. This interface is called MinePlayerStrategy. It has the following methods:

initialize() - This function will be called at the start of every round, passing in the size of the board, the maximum items that a player can carry in their inventory, the maximum charge that your robot will start with the score to reach to win a single round, an initial view of the GameBoard, the starting tile location for the player, whether the player is a red player, and finally a Random object for the player to use for generating random numbers if their strategy needs that.

getTurnAction() - This function is the primary purpose of your strategy. It is called every turn to get the action your player should execute. The parameters are a PlayerBoardView object which represents the state of the board, an Economy object with resource price information, the current charge your robot has, and a boolean to say if the current turn is a red priority turn or not. This function returns a TurnAction for the action the player wants to execute. Returning null from this function will cause the player to do nothing.

onReceiveItem() - This function is called whenever the player successfully executed a PICK_UP TurnAction on their last turn. The only parameter for this function is a reference to the InventoryItem picked up. This function is not called when the player inventory is full because the item is not picked up in that case.

onSoldInventory() - This function is called whenever the player steps on a market tile of their color with at least one gem in their inventory. The only parameter for this function is an int for the total number of points received by selling everything in the inventory.

getName() - This function is super simple, just return the name that you want to give your player on the GUI and in the competition. Can be as simple as a string constant like "ExampleName". Note that if you somehow manage to throw an exception in this function, your player's name will be the exception that you threw. That's super embarrassing for everyone involved.

endRound() - This function is called at the end of every round. This passes in the number of points that your player scored and the number of points that the opposing player scored. You should also use this method to reset the state of your strategy class. Every semester we get complaints from people that their strategy acts buggy in the competition, and it turns out their strategy is still using variables set in the previous round.

Assignment Requirements

To fully complete this assignment, you must write at least one MinePlayerStrategy in the mineopoly_two.strategy package that meets the following requirements:

Your strategy must not crash. For instance, it should not throw an exception when all the rubies have been mined and it tries to find a ruby.
When playing your strategy against the RandomStrategy, you must achieve a minimum score of 30 * (boardSize * boardSize) 99% of the time for at least board sizes of 14, 20, 26, and 32.
The provided implementation of RandomStrategy cannot pick up gems, so it cannot score any points. It will therefore only make things easier for you to score more points.

In addition to those requirements on the strategy:

You must prove your strategy reaches the minimum score 99% of the time by completing the getStrategyWinPercent() method in MineopolyMain.java.
You are entirely allowed to write more than one strategy. In fact it probably makes sense to have a strategy that maximizes your points against a RandomStrategy for the assignment, and then another strategy that does more complicated actions against a smarter human-programmed opponent for the competition. If you write a separate strategy for the competition, it will not be graded as part of this assignment.

