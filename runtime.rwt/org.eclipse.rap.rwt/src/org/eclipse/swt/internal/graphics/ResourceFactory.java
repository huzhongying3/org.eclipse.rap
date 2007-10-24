/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.graphics;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.*;

import javax.imageio.ImageIO;

import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;


public final class ResourceFactory {

  private final static Map colors = new HashMap();
  private static final Map fonts = new HashMap();
  private static final Map images = new HashMap();

  
  /////////
  // COLORS

  public static Color getColor( final RGB rgb ) {
    if( rgb == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return getColor( rgb.red, rgb.green, rgb.blue );
  }

  public static Color getColor( final int red,
                                final int green,
                                final int blue )
  {
    if(    red > 255
        || red < 0
        || green > 255
        || green < 0
        || blue > 255
        || blue < 0 )
    {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    int colorNr = red | ( green << 8 ) | ( blue << 16 );
    return getColor( colorNr );
  }

  public static synchronized Color getColor( final int color ) {
    Color result;
    Integer key = new Integer( color );
    if( colors.containsKey( key ) ) {
      result = ( Color )colors.get( key );
    } else {
      result = createColorInstance( color );
      colors.put( key, result );
    }
    return result;
  }
  

  ////////
  // FONTS

  public static Font getFont( final FontData data ) {
    return getFont( data.getName(), data.getHeight(), data.getStyle() );
  }

  public static Font getFont( final String name,
                              final int height,
                              final int style )
  {
    validateFontParams( name, height );
    int checkedStyle = checkFontStyle( style );
    Font result;
    Integer key = new Integer( name.hashCode() ^ height ^ style );
    synchronized( Font.class ) {
      result = ( Font )fonts.get( key );
      if( result == null ) {
        FontData fontData = new FontData( name, height, checkedStyle );
        result = createFontInstance( fontData );
        fonts.put( key, result );
      }
    }
    return result;
  }

  
  /////////
  // IMAGES

  public static synchronized Image findImage( final String path ) {
    IResourceManager manager = ResourceManager.getInstance();
    return findImage( path, manager.getContextLoader() );
  }

  public static synchronized Image findImage( final String path,
                                              final ClassLoader imageLoader )
  {
    if( path == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( "".equals( path ) ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    Image result;
    if( images.containsKey( path ) ) {
      result = ( Image )images.get( path );
    } else {
      result = createImage( path, imageLoader );
    }
    return result;
  }

  public static synchronized Image findImage( final String path,
                                              final InputStream inputStream )
  {
    if( path == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( "".equals( path ) ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
    Image result;
    if( images.containsKey( path ) ) {
      result = ( Image )images.get( path );
    } else {
      result = createImage( path, inputStream );
    }
    return result;
  }

  public static synchronized String getImagePath( final Image image ) {
    String result = null;
    Iterator it = images.entrySet().iterator();
    boolean next = true;
    while( next && it.hasNext() ) {
      Map.Entry entry = ( Map.Entry )it.next();
      if( entry.getValue().equals( image ) ) {
        result = ( String )entry.getKey();
        next = false;
      }
    }
    return result;
  }

  
  ///////////////
  // Test helpers

  public static void clear() {
    colors.clear();
    fonts.clear();
    images.clear();
  }

  static int colorsCount() {
    return colors.size();
  }
  
  static int fontsCount() {
    return fonts.size();
  }
  
  static int imagesCount() {
    return images.size();
  }

  
  //////////////////
  // Helping methods

  private static Image createImage( final String path,
                                    final ClassLoader imageLoader )
  {
    Image result;
    IResourceManager manager = ResourceManager.getInstance();
    ClassLoader loaderBuffer = manager.getContextLoader();
    if( imageLoader != null ) {
      manager.setContextLoader( imageLoader );
    }
    try {
      InputStream inputStream = manager.getResourceAsStream( path );
      result = createImage( path, inputStream );
    } finally {
      manager.setContextLoader( loaderBuffer );
    }
    return result;
  }

  private static Image createImage( final String path,
                                    final InputStream inputStream )
  {
    if( inputStream == null ) {
      String txt = "Image ''{0}'' cannot be found.";
      String msg = MessageFormat.format( txt, new Object[] { path } );
      SWT.error( SWT.ERROR_INVALID_ARGUMENT,
                 new IllegalArgumentException( msg ),
                 msg );
    }
    Image result;

    ////////////////////////////////////////////////////////////////////////////
    // TODO: [fappel] Image size calculation and resource registration both
    //                read the input stream. Because of this I use a workaround
    //                with a BufferedInputStream. Resetting it after reading the
    //                image size enables the ResourceManager to reuse it for
    //                registration. Note that the order is crucial here, since
    //                the ResourceManager seems to close the stream (shrug).
    //                It would be nice to find a solution without reading the
    //                stream twice.

    IResourceManager manager = ResourceManager.getInstance();
    BufferedInputStream bis = new BufferedInputStream( inputStream );
    bis.mark( Integer.MAX_VALUE );
    Point size = readImageSize( bis );
    if( size != null ) {
      result = createImageInstance( size.x, size.y );
    } else {
      result = createImageInstance( -1, -1 );
    }
    try {
      bis.reset();
    } catch( final IOException shouldNotHappen ) {
      String txt = "Could not reset input stream while reading image ''{0}''.";
      String msg = MessageFormat.format( txt, new Object[] { path } );
      throw new RuntimeException( msg, shouldNotHappen );
    }
    manager.register( path, bis );

    ////////////////////////////////////////////////////////////////////////////

    images.put( path, result );
    return result;
  }

  /**
   * @return an array whose first element is the image <em>width</em> and
   *         second is the <em>height</em>, <code>null</code> if the bounds
   *         could not be read.
   */
  private static Point readImageSize( final InputStream input ) {
    Point result = null;
    boolean cacheBuffer = ImageIO.getUseCache();
    try {
      // [fappel]: We don't use caching since it sometimes causes problems
      //           if the application is deployed at a servlet container. This
      //           does not have any memories or performance impacts, since
      //           a image is a value object that is loaded only once in
      //           an application.
      ImageIO.setUseCache( false );
      // TODO [fappel]: To use BufferedImage on Mac Os the following
      //                system property has to be set: java.awt.headless=true.
      //                Put this info in a general documentation
      BufferedImage image = ImageIO.read( input );
      if( image != null ) {
        int width = image.getWidth();
        int height = image.getHeight();
        result = new Point( width, height );
      }
    } catch( final Exception e ) {
      // ImageReader throws IllegalArgumentExceptions for some files
      // TODO [rst] log exception
      e.printStackTrace();
    } finally {
      ImageIO.setUseCache( cacheBuffer );
    }
    return result;
  }

  //////////////////
  // Helping methods

  private static void validateFontParams( final String name, final int height ) 
  {
    if( name == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( height < 0 ) {
      SWT.error( SWT.ERROR_INVALID_ARGUMENT );
    }
  }

  private static int checkFontStyle( final int style ) {
    int result = SWT.NORMAL;
    if( ( style & SWT.BOLD ) != 0 ) {
      result |= SWT.BOLD;
    }
    if( ( style & SWT.ITALIC ) != 0 ) {
      result |= SWT.ITALIC;
    }
    return result;
  }

  ////////////////////
  // Instance creation

  private static Color createColorInstance( final int colorNr ) {
    Color result = null;
    try {
      Class colorClass = Color.class;
      Class[] classes = colorClass.getDeclaredClasses();
      Class colorExtClass = classes[ 0 ];
      Class[] paramList = new Class[] { int.class };
      Constructor constr = colorExtClass.getDeclaredConstructor( paramList );
      constr.setAccessible( true );
      Object[] args = new Object[] { new Integer( colorNr ) };
      result = ( Color )constr.newInstance( args );
    } catch( final Exception e ) {
      throw new RuntimeException( "Failed to instantiate Color", e );
    }
    return result;
  }

  private static Font createFontInstance( final FontData fontData ) {
    Font result = null;
    try {
      Class fontClass = Font.class;
      Class[] paramList = new Class[] { FontData.class };
      Constructor constr = fontClass.getDeclaredConstructor( paramList );
      constr.setAccessible( true );
      result = ( Font )constr.newInstance( new Object[] { fontData } );
    } catch( final Exception e ) {
      throw new RuntimeException( "Failed to instantiate Font", e );
    }
    return result;
  }

  private static Image createImageInstance( final int width, final int height )
  {
    Image result = null;
    try {
      Class fontClass = Image.class;
      Class[] paramList = new Class[] { int.class, int.class };
      Constructor constr = fontClass.getDeclaredConstructor( paramList );
      constr.setAccessible( true );
      result = ( Image )constr.newInstance( new Object[]{
        new Integer( width ), new Integer( height )
      } );
    } catch( final Exception e ) {
      throw new RuntimeException( "Failed to instantiate Image", e );
    }
    return result;
  }
  
  private ResourceFactory() {
    // prevent instantiation
  }
}
