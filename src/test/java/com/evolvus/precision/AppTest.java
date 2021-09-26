package com.evolvus.precision;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;


/**
 * Unit test for simple App.
 */
public class AppTest
{

    App app;

    @BeforeEach
    void setUp() {
        app = new App();
    }

    @Test
    @DisplayName("Parse Arguments testing")
    public void testParseArgument()
    {
        String[] args ={"-t=project_cr"};
        App.Handler oper = app.parseArgument(args);
        if(oper.getOper() == AdvancedExporter.Operation.TABLE)
          assertTrue(true);
        else
          assertTrue(false);
    }
}
