package com.evolvus.precision;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    @DisplayName("Parse Arguments testing for table")
    public void testParseArgument(){

        String[] args ={"-t=project_cr"};
        App.Handler oper = app.parseArgument(args);
        assertEquals(AdvancedExporter.Operation.TABLE, oper.getOper() ,
               "Parse Arguement for Table should work");
        assertEquals("project_cr", oper.getOperationName() ,
               "Parse Arguement for Table should work with name");

         args[0] ="-c=a.txt";
         oper = app.parseArgument(args);
         assertEquals(AdvancedExporter.Operation.CONTAINER, oper.getOper(),
                "Parse Arguement for Container should work");
         assertEquals("a.txt", oper.getOperationName() ,
                "Parse Arguement for Container should work with name");

        args[0] ="-f=container";
        oper = app.parseArgument(args);
        assertEquals(AdvancedExporter.Operation.CONTAINER_FOLDER, oper.getOper(),
               "Parse Arguement for Container Folder should work");
        assertEquals("container", oper.getOperationName() ,
               "Parse Arguement for Container Folder should work with name");

         args[0] ="junkvalues";
         oper = app.parseArgument(args);
         assertEquals(AdvancedExporter.Operation.NO_OPERATION, oper.getOper(),
                "Parse Arguement for Container Folder should work");


    }

    @Test
    @DisplayName("Parse Arguments testing for table")
    public void testMain(){
      String[] args ={"-t=project_cr"};
      app.main(args);
    }
}
