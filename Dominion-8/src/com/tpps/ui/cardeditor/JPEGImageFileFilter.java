package com.tpps.ui.cardeditor;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * 
 * @author Maximilian Hauk -mhauk
 *
 */


/**
 * This class implements a filter for the image upload, so that it allows the listing and and
 * selection of .jpg/.jpeg. files
 * 
 */


public class JPEGImageFileFilter extends FileFilter implements java.io.FileFilter
 {
 public boolean accept(File f)
   {
   if (f.getName().toLowerCase().endsWith(".jpeg")) return true;
   if (f.getName().toLowerCase().endsWith(".jpg")) return true;
   if(f.isDirectory())return true;
   return false;
  }
 public String getDescription()
   {
   return "JPEG files";
   }

} 