package com.brotherlogic.oib.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brotherlogic.oib.Art;

public class Adder extends Handler
{

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
         IOException
   {
      Art newArt = new Art();
      newArt.setArtist(req.getParameter("blah"));
   }
}
