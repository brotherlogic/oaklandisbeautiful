package com.brotherlogic.oib.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brotherlogic.oib.Art;
import com.brotherlogic.oib.Database;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Main web handler
 * 
 * @author simon
 * 
 */
public class Handler extends HttpServlet
{
   private String buildPage(String templateFile, Map<String, Object> producer) throws IOException,
         TemplateException
   {
      Configuration cfg = new freemarker.template.Configuration();
      cfg.setObjectWrapper(new DefaultObjectWrapper());

      BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass()
            .getResourceAsStream(templateFile)));
      Template temp = new Template("temp", reader, cfg);

      StringWriter sw = new StringWriter();
      temp.process(producer, sw);
      sw.flush();

      String out = sw.toString();
      sw.close();
      return out;
   }

   private void deliverPage(String template, Map<String, Object> mapper, HttpServletResponse resp)
         throws IOException
   {
      try
      {
         String page = buildPage(template, mapper);
         PrintStream ps = new PrintStream(resp.getOutputStream());
         ps.print(page);
         ps.close();
      }
      catch (TemplateException e)
      {
         throw new IOException(e);
      }
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
         IOException
   {
      if (req.getParameter("action") == null)
      {
         // Deliver the index page
         HashMap<String, Object> sMap = new HashMap<String, Object>();
         sMap.put("amount", Database.getInstance().getSize());
         deliverPage("index.html", sMap, resp);
      }
      else if (req.getParameter("action").equals("mapper"))
      {
         deliverPage("mapper.html", new HashMap<String, Object>(), resp);
      }
      else if (req.getParameter("action").equals("map"))
      {
         double lat = Double.parseDouble(req.getParameter("lat"));
         double lon = Double.parseDouble(req.getParameter("lon"));
         HashMap<String, Object> mapper = new HashMap<String, Object>();
         mapper.put("arts", Database.getInstance().getArts());
         mapper.put("lat", lat);
         mapper.put("lon", lon);
         deliverPage("map.html", mapper, resp);
      }
      else if (req.getParameter("action").equals("newart"))
      {
         deliverPage("addlocate.html", new HashMap<String, Object>(), resp);
      }
      else if (req.getParameter("action").equals("doadd"))
      {
         double lat = Double.parseDouble(req.getParameter("lat"));
         double lon = Double.parseDouble(req.getParameter("lon"));
         Map<String, Object> addMap = new HashMap<String, Object>();
         addMap.put("lat", lat);
         addMap.put("lon", lon);
         deliverPage("addart.html", addMap, resp);
      }
      else if (req.getParameter("action").equals("findme"))
      {
         deliverPage("locator.html", new HashMap<String, Object>(), resp);
      }
      else if (req.getParameter("action").equals("randme"))
      {
         deliverPage("random.html", new HashMap<String, Object>(), resp);
      }
      else if (req.getParameter("action").equals("findart"))
      {
         double lat = Double.parseDouble(req.getParameter("lat"));
         double lon = Double.parseDouble(req.getParameter("lon"));

         Art closest = Database.getInstance().getClosestArt(lat, lon);
         Map<String, Object> artMap = new HashMap<String, Object>();
         artMap.put("title", closest.getTitle());
         artMap.put("artist", closest.getArtist());
         artMap.put("directions",
               getDirections(lat, lon, closest.getLatitude(), closest.getLongitude()));
         artMap.put("url", closest.getUrl());
         artMap.put("source", closest.getSource());
         deliverPage("artpage.html", artMap, resp);
      }
      else if (req.getParameter("action").equals("findart"))
      {
         double lat = Double.parseDouble(req.getParameter("lat"));
         double lon = Double.parseDouble(req.getParameter("lon"));

         Art closest = Database.getInstance().getClosestArt(lat, lon);
         Map<String, Object> artMap = new HashMap<String, Object>();
         artMap.put("title", closest.getTitle());
         artMap.put("artist", closest.getArtist());
         artMap.put("directions",
               getDirections(lat, lon, closest.getLatitude(), closest.getLongitude()));
         artMap.put("url", closest.getUrl());
         artMap.put("source", closest.getSource());
         deliverPage("artpage.html", artMap, resp);
      }
      else if (req.getParameter("action").equals("showart"))
      {
         double lat = Double.parseDouble(req.getParameter("lat"));
         double lon = Double.parseDouble(req.getParameter("lon"));

         Art closest = Database.getInstance().getArt(req.getParameter("title"));
         Map<String, Object> artMap = new HashMap<String, Object>();
         artMap.put("title", closest.getTitle());
         artMap.put("artist", closest.getArtist());
         artMap.put("directions",
               getDirections(lat, lon, closest.getLatitude(), closest.getLongitude()));
         artMap.put("url", closest.getUrl());
         artMap.put("source", closest.getSource());
         deliverPage("artpage.html", artMap, resp);
      }
   }

   private String getDirections(double latStart, double lonStart, double latEnd, double lonEnd)
   {
      String res2 = "https://maps.google.com/maps?q=" + latStart + "," + lonStart + "+to+" + latEnd
            + "," + lonEnd + "&dirflg=w";
      return res2;
   }
}
