package com.brotherlogic.oib;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

class ArtHandler extends DefaultHandler
{
   Art currArt;
   List<Art> readArts = new LinkedList<Art>();
   String text = "";

   @Override
   public void characters(char[] ch, int start, int length) throws SAXException
   {
      text += new String(ch, start, length);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException
   {
      String fName = localName + qName;
      if (fName.equals("row") && currArt != null)
         readArts.add(currArt);

      if (fName.equals("artist_name"))
         currArt.setArtist(text);
      else if (fName.equals("project_name"))
         currArt.setTitle(text);

   }

   public List<Art> getArts()
   {
      return readArts;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
         throws SAXException
   {
      text = "";
      String fName = localName + qName;
      if (fName.equals("row"))
         currArt = new Art();
      if (fName.equals("location_district_neighborhood"))
      {
         currArt.setLatitude(Double.parseDouble(attributes.getValue("latitude")));
         currArt.setLongitude(Double.parseDouble(attributes.getValue("longitude")));
      }
   }

}

/**
 * Hello world!
 * 
 */
public class Database
{
   static Database singleton;

   List<Art> allArt = new LinkedList<Art>();

   private Database()
   {
      try
      {
         SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
         ArtHandler handler = new ArtHandler();
         parser.parse(this.getClass().getResourceAsStream("data.xml"), handler);
         allArt.addAll(handler.getArts());
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private double computeDist(Art a1, Art a2)
   {
      return Math.sqrt(Math.pow(a1.getLatitude() - a2.getLatitude(), 2)
            + Math.pow(a1.getLongitude() - a2.getLongitude(), 2));
   }

   public Art getClosestArt(double lat, double lon)
   {
      Art best = allArt.get(0);
      double bestDist = Double.MAX_VALUE;
      for (Art art : allArt.subList(1, allArt.size()))
      {
         double dist = computeDist(art, best);
         if (dist < bestDist)
         {
            bestDist = dist;
            best = art;
         }
      }

      return best;
   }

   public Art getRandomArt()
   {
      Collections.shuffle(allArt);
      return allArt.get(0);
   }

   public void storeArt(Art art)
   {
      allArt.add(art);
   }

   public static Database getInstance()
   {
      if (singleton == null)
         singleton = new Database();

      return singleton;
   }

   public static void main(String[] args)
   {
      Database d = new Database();
      System.out.println(d.getClosestArt(37.269174, -119.306607));
   }
}
