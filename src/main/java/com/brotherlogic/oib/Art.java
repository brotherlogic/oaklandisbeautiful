package com.brotherlogic.oib;

import java.io.Serializable;

public class Art implements Serializable
{
   String address;
   String artist;
   double latitude;
   double longitude;
   String title;

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

   public String getTitle()
   {
      return title;
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

   public void setTitle(String title)
   {
      this.title = title;
   }
}
