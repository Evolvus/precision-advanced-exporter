package com.evolvus.precision;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
      System.out.println("Welcome to Advanced Extractor");
      AdvancedExporter exporter = new AdvancedExporter(".properties");
      exporter.processContainerLocation();
    }
}
