package com.evolvus.precision;


import java.util.Arrays;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class App
{
    public static void main( String[] args )
    {
      Options options = new Options();
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



        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        System.out.println("Welcome to Advanced Extractor");

        try {
            AdvancedExporter exporter = new AdvancedExporter(".properties");
            cmd = parser.parse(options, args);
            if(cmd.getArgs().length != 0) {
              System.out.println("Invalid Parameters");
              HelpFormatter formatter = new HelpFormatter();
              formatter.printHelp( "extract", options );

            } else if(cmd.getOptions().length == 0){
              System.out.println("No arguement supplied. Container from properties file will be used");
              exporter.processContainerLocation();

            } else if (cmd.hasOption("h")) {
              HelpFormatter formatter = new HelpFormatter();
              formatter.printHelp( "extract", options );
            } else if (cmd.hasOption("f")) {
                String containerFolder = cmd.getOptionValue("f");
                if (cmd.hasOption("c") || cmd.hasOption("t")) {
                    System.out.println("Container / table option is ignored because container folder is defined");
                }
                exporter.processContainerLocation(containerFolder);


            } else if (cmd.hasOption("c")) {
              String container = cmd.getOptionValue("c");
                if (cmd.hasOption("t")) {
                    System.out.println("Table option is omitted because container is defined");
                }
                exporter.processContainer(container);

              } else if (cmd.hasOption("t")) {
                String table = cmd.getOptionValue("t");
                exporter.export(table);
              } else {
                System.out.println("Invalid Option");

                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "extract", options );
            }

          } catch (ParseException pe) {
            System.out.println("Invalid command-line arguments!");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "extract", options );
            System.exit(1);
          }


    }
}
