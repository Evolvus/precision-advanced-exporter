package com.evolvus.precision;


import java.lang.Thread;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Connection;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.Locale;
import java.time.format.FormatStyle;
import java.time.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStream;



import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
Precision 100 Advanced Exporter
 */
public class AdvancedExporter {


    //Output
    private Boolean inclHeader = true;
    private String delimiter = ",";

    //File Details
    private String fileNamePrefix = "";
    private String fileNameSuffix = "_Export";
    private String fileLocation = "data";
    private String fileExtension = "txt";


    //JDBC
    private String dbProperties  = "db.properties";
    //Input
    private String containerLocation = "container";

    //Connection Details
    private HikariConfig cfg = null;
    private HikariDataSource ds = null;

    private DateTimeFormatter formatter =
    DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT, FormatStyle.MEDIUM )
                     .withLocale( Locale.UK )
                     .withZone( ZoneId.systemDefault() );


    public  AdvancedExporter(String config){
      try (InputStream input = new FileInputStream(config)) {

          Properties prop = new Properties();

          // load a properties file
          prop.load(input);

          // get the property value
          inclHeader = prop.getProperty("output.include_header").equals("YES")?true:false;
          delimiter = prop.getProperty("output.delimiter");
          fileNamePrefix = prop.getProperty("output.prefix");
          fileNameSuffix = prop.getProperty("output.suffix");
          fileLocation = prop.getProperty("output.location");
          fileExtension = prop.getProperty("output.extension");
          containerLocation = prop.getProperty("input.container.location");
          dbProperties = prop.getProperty("db.properties.file");

          cfg = new HikariConfig(dbProperties);
          ds = new HikariDataSource(cfg);




      } catch (IOException ex) {
          ex.printStackTrace();
      }
    }

    public void export(String table) {


        String csvFileName = getFileName(table);


        //Connection con = null;
        //PreparedStatement statement = null;
        //ResultSet result = null;

        //BufferedWriter fileWriter;


        try{

            Connection con = ds.getConnection();
            String sql = "SELECT * FROM ".concat(table);
            PreparedStatement statement = con.prepareStatement(sql);
            ResultSet result = statement.executeQuery();

            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(csvFileName));
            int columnCount = writeHeaderLine(result,fileWriter);

            while (result.next()) {
                String line = "";

                for (int i = 1; i <= columnCount; i++) {
                    Object valueObject = result.getObject(i);
                    String valueString = "";

                    if (valueObject != null) valueString = valueObject.toString();

                    if (valueObject instanceof String) {
                        valueString = escapeDoubleQuotes(valueString);
                    }

                    line = line.concat(valueString);

                    if (i != columnCount) {
                        line = line.concat(delimiter);
                    }
                }

                fileWriter.write(line);
                fileWriter.newLine();

            }

            fileWriter.close();
            result.close();
            statement.close();
            con.close();


        } catch (SQLException e) {
          Logger lgr = Logger.getLogger(AdvancedExporter.class.getName());
          lgr.log(Level.WARNING, e.getMessage(), e);
        } catch (IOException e) {
          Logger lgr = Logger.getLogger(AdvancedExporter.class.getName());
          lgr.log(Level.WARNING, e.getMessage(), e);
        }

    }

    private String getFileName(String baseName) {

        return  fileLocation + "/" + fileNamePrefix + baseName + fileNameSuffix+"."+fileExtension;
    }


    public void processContainerLocation() {
      processContainerLocation(containerLocation);
    }

    public void processContainerLocation(String loc){

      File file = new File(loc);
      File[] files = file.listFiles();
      System.out.println("Processing  Container Location "+file.getAbsolutePath());
      for(File f: files){
        System.out.println("Processing Container File "+f.getName());
        new Thread(() -> {
          processContainer(f.getAbsolutePath());
        }).start();


      }

    }

    public void processContainer(String container){
      try {
        File f = new File(container);
        Scanner myReader = new Scanner(f);
        while (myReader.hasNextLine()) {
          String data = myReader.nextLine();

          Instant start = Instant.now();
          System.out.format("Started extracting for table %s at %s%n",data,formatter.format( start ));

          export(data);

          Instant end = Instant.now();
          System.out.format("Finished extracting for table %s at %s%n",data,formatter.format( end ));
          System.out.format("Time taken to extract table %s --> %d seconds %n",data,Duration.between(start, end).toMillis()/1000);

        }
        myReader.close();

      } catch (FileNotFoundException e) {
        Logger lgr = Logger.getLogger(AdvancedExporter.class.getName());
        lgr.log(Level.WARNING, e.getMessage(), e);
      }

    }

    private int writeHeaderLine(ResultSet result,BufferedWriter fileWriter) throws SQLException, IOException {
        // write header line containing column names
        ResultSetMetaData metaData = result.getMetaData();
        int numberOfColumns = metaData.getColumnCount();
        String headerLine = "";

        // exclude the first column which is the ID field

        if(inclHeader){
          System.out.println("inclHeader");
          for (int i = 1; i <= numberOfColumns; i++) {
              String columnName = metaData.getColumnName(i);
              headerLine = headerLine.concat(columnName).concat(",");
          }
          fileWriter.write(headerLine.substring(0, headerLine.length() - 1));
          fileWriter.newLine();


        }

        return numberOfColumns;
    }

    private String escapeDoubleQuotes(String value) {
        return value.replaceAll("\"", "\"\"");
    }


}
