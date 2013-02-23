package com.brotherlogic.oib;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
      {
         currArt.setSource("Oakland");
         readArts.add(currArt);
      }
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

      addFlickrImages();
      addInstagramImages();
   }

   private void addFlickrImages()
   {
      String url = "http://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=2e468dba0f8ac39ed9c15b8a20501554&tags=oaklandart&has_geo=1&extras=url_s%2Cgeo%2Cowner_name&per_page=500&format=rest";
      try
      {
         SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
         FlickrArtHandler handler = new FlickrArtHandler();
         parser.parse(new URL(url).openStream(), handler);
         allArt.addAll(handler.getArts());
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private void addInstagramImages()
   {
      String url = "https://api.instagram.com/v1/tags/oaklandart/media/recent?client_id=31281b4e487041cc8f68e18f5a38f619&count=500";
      String json = "";
      try
      {
         BufferedReader reader = new BufferedReader(
               new InputStreamReader(new URL(url).openStream()));
         for (String line = reader.readLine(); line != null; line = reader.readLine())
            json += line + "\n";
         reader.close();

         JsonObject obj = new JsonParser().parse(json).getAsJsonObject();

         JsonArray arr = obj.getAsJsonArray("data");
         for (int i = 0; i < arr.size(); i++)
         {
            JsonObject instImg = arr.get(i).getAsJsonObject();
            Art art = new Art();
            art.setTitle(instImg.get("caption").getAsJsonObject().get("text").getAsString());
            art.setArtist(instImg.get("user").getAsJsonObject().get("username").getAsString());
            art.setUrl(instImg.get("images").getAsJsonObject().get("standard_resolution")
                  .getAsJsonObject().get("url").getAsString());
            art.setSource("Instagram");
            allArt.add(art);
         }
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

   public int getSize()
   {
      return allArt.size();
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

class FlickrArtHandler extends DefaultHandler
{
   List<Art> arts = new LinkedList<Art>();

   public List<Art> getArts()
   {
      return arts;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
         throws SAXException
   {
      String fName = localName + qName;
      if (fName.equals("photo"))
      {
         Art art = new Art();
         art.setTitle(attributes.getValue("title"));
         art.setArtist(attributes.getValue("ownername"));
         art.setLatitude(Double.parseDouble(attributes.getValue("latitude")));
         art.setLongitude(Double.parseDouble(attributes.getValue("longitude")));
         art.setUrl(attributes.getValue("url_s"));
         art.setSource("Flickr");

         arts.add(art);
      }
   }

}
