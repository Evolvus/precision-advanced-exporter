package com.evolvus.precision;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;





public class App
{
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final String EXTRACT_CONST = "extract";


    private static class Handler{
      private AdvancedExporter.Operation  oper;
      private String operationName;

      public Handler(AdvancedExporter.Operation  oper,String operationName){
        this.oper = oper;
        this.operationName = operationName;
      }

      public void setOper(AdvancedExporter.Operation  oper){
        this.oper = oper;
      }

      public void setOperationName(String operationName){
        this.operationName = operationName;
      }

      public AdvancedExporter.Operation getOper(){
        return this.oper;
      }
      public String getOperationName(){
        return this.operationName;
      }
    }


    public static void main( String[] args ){

      LOGGER.info("Welcome to Advanced Extractor");
      Handler oper = parseArgument(args);
      if( oper.getOper() == AdvancedExporter.Operation.NO_OPERATION){
        System.exit(1);
      }

      AdvancedExporter exporter = new AdvancedExporter.Builder(".properties")
                        .withOperation(oper.getOper())
                        .withOperationName(oper.getOperationName())
                        .build();

      exporter.execute();


    }

    public static Handler parseArgument(String[] args) {
      Options options = new Options();

      try{

        setOptions(options);
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if(cmd.getArgs().length != 0){
          printDefault("Unrecognized Parameter",options);
        } else if (cmd.hasOption("h")){
          printDefault("Help Requested",options);
        } else if(cmd.getOptions().length == 0){
          LOGGER.info("No argument supplied. Container from properties file will be used");
          return new Handler(AdvancedExporter.Operation.CONTAINER_FOLDER , "");

        } else if (cmd.hasOption("f")) {

            if (cmd.hasOption("c") || cmd.hasOption("t")) {
                LOGGER.info("Container / table option is ignored because container folder is defined");
            }
            return new Handler(AdvancedExporter.Operation.CONTAINER_FOLDER , cmd.getOptionValue("f"));

        } else if (cmd.hasOption("c")) {
            if (cmd.hasOption("t")) {
                LOGGER.info("Table option is omitted because container is defined");
            }
            return new Handler(AdvancedExporter.Operation.CONTAINER , cmd.getOptionValue("c"));

        } else if (cmd.hasOption("t")) {
          return new Handler(AdvancedExporter.Operation.TABLE , cmd.getOptionValue("t"));

        } else {
          printDefault("Invalid Option",options);
        }

      } catch (ParseException pe) {
        printDefault("Invalid Option",options);
      }
      return new Handler(AdvancedExporter.Operation.NO_OPERATION , "");
    }



    private static void setOptions(Options options){
      options.addOption(Option.builder("h")
        .longOpt("help")
        .hasArg(false)
        .desc("Help on this tool usage")
        .required(false)
        .build());

      options.addOption(Option.builder("f")
        .longOpt("containerFolder")
        .hasArg(true)
        .desc("Container Folder containing containers")
        .required(false)
        .build());

        options.addOption(Option.builder("c")
          .longOpt("container")
          .hasArg(true)
          .desc("Container containing all the tables to be extracted")
          .required(false)
          .build());

        options.addOption(Option.builder("t")
          .longOpt("table")
          .hasArg(true)
          .desc("Table to be extracted")
          .required(false)
          .build());

    }

    private static void printDefault(String message,Options options){
      LOGGER.info(message);
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp( EXTRACT_CONST, options );
    }
}
