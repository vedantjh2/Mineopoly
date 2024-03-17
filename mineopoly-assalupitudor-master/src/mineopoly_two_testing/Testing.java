package mineopoly_two_testing;
import mineopoly_two.action.TurnAction;
import mineopoly_two.game.GameEngine;
import mineopoly_two.strategy.MinePlayerStrategy;
import mineopoly_two.strategy.MyStrategy;
import mineopoly_two.strategy.RandomStrategy;
import org.junit.Before;
import org.junit.Test;

import static mineopoly_two.strategy.MyStrategy.getToTile;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Testing {
    private GameEngine gameEngine;
    private MinePlayerStrategy myStrategy;
    private MinePlayerStrategy randomStrategy;
    @Before
    public void setUp() throws IOException {
        myStrategy = new MyStrategy();
        randomStrategy = new RandomStrategy();
        gameEngine = new GameEngine(20, myStrategy, randomStrategy);
    }

    @Test
    //should move right
    public void testGetToTileFunctionRight() {
        assertEquals(TurnAction.MOVE_RIGHT, getToTile(3, 2));
    }

    @Test
    //should move left
    public void testGetToTileFunctionLeft() {
        assertEquals(TurnAction.MOVE_LEFT, getToTile(-4, 2));
    }

    @Test
    //should move up
    public void testGetToTileFunctionUp() {
        assertEquals(TurnAction.MOVE_UP, getToTile(0, 5));
    }

    @Test
    //should move down
    public void testGetToTileFunctionDown() {
        assertEquals(TurnAction.MOVE_DOWN, getToTile(0,-7));
    }

}

