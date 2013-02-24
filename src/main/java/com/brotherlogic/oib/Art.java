package com.brotherlogic.oib;

import java.io.Serializable;

public class Art implements Serializable
{
   String address;
   String artist = "";
   double latitude;
   double longitude;
   String source = "";
   String title = "";
   String url = "";

   public Art()
   {

   }

   public String getAddress()
   {
      return address;
   }

   public String getArtist()
   {
      return artist;
   }

   public double getLatitude()
   {
      return latitude;
   }

   public double getLongitude()
   {
      return longitude;
   }

   public String getSource()
   {
      return source;
   }

   public String getTitle()
   {
      return title.replace("\"", "").replace("'", "");
   }

   public String getUrl()
   {
      return url;
   }

   public void setAddress(String address)
   {
      this.address = address;
   }

   public void setArtist(String artist)
   {
      this.artist = artist;
   }

   public void setLatitude(double latitude)
   {
      this.latitude = latitude;
   }

   public void setLongitude(double longitude)
   {
      this.longitude = longitude;
   }

   public void setSource(String source)
   {
      this.source = source;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public void setUrl(String url)
   {
      this.url = url;
   }
}
