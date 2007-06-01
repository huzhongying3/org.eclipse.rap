/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.theme;

import java.util.HashMap;
import java.util.Map;

public class QxColor implements QxType {

  private static final Map NAMED_COLORS = new HashMap();
  
  static {
    NAMED_COLORS.put( "black", new int[] { 0, 0, 0 } );
    NAMED_COLORS.put( "white", new int[] { 255, 255, 255 } );
    NAMED_COLORS.put( "gray", new int[] { 128, 128, 128 } );
    NAMED_COLORS.put( "red", new int[] { 255, 0, 0 } );
    NAMED_COLORS.put( "green", new int[] { 0, 255, 0 } );
    NAMED_COLORS.put( "blue", new int[] { 0, 0, 255 } );
    // TODO Add most frequently used colors from qx ColorUtil
  }
  
  public final String name;
  public final int red;
  public final int green;
  public final int blue;
  
  public QxColor( final String color ) {
    if( color == null ) {
      throw new NullPointerException( "null argument" );
    }
    if( color.startsWith( "#" ) ) {
      try {
        if( color.length() == 7 ) {
          red = Integer.parseInt( color.substring( 1, 3 ), 16 );
          green = Integer.parseInt( color.substring( 3, 5 ), 16 );
          blue = Integer.parseInt( color.substring( 5, 7 ), 16 );
          name = color;
        } else if( color.length() == 4 ) {
          red = Integer.parseInt( color.substring( 1, 2 ), 16 ) * 17;
          green = Integer.parseInt( color.substring( 2, 3 ), 16 ) * 17;
          blue = Integer.parseInt( color.substring( 3, 4 ), 16 ) * 17;
          name = color;
        } else {
          throw new IllegalArgumentException( "Illegal number of characters in color definition: "
                                              + color );
        }
      } catch( NumberFormatException e ) {
        throw new IllegalArgumentException( "Illegal number format in color definition: "
                                            + color );
      }
    } else if( NAMED_COLORS.containsKey( color.toLowerCase() ) ) {
      int[] values = ( int[] )NAMED_COLORS.get( color.toLowerCase() );
      red = values[ 0 ];
      green = values[ 1 ];
      blue = values[ 2 ];
      name = color.toLowerCase();
    } else {
      String[] parts = color.split( "\\s*,\\s*" );
      if( parts.length == 3 ) {
        try {
          red = Integer.parseInt( parts[ 0 ] );
          green = Integer.parseInt( parts[ 1 ] );
          blue = Integer.parseInt( parts[ 2 ] );
          name = toHtmlStr( red, green, blue );
        } catch( NumberFormatException e ) {
          throw new IllegalArgumentException( "Illegal number format in color definition: "
                                              + color );
        }
      } else {
        throw new IllegalArgumentException( "Invalid color name: " + color );
      }
    }
  }

  private static String toHtmlStr( final int red, final int green, final int blue )
  {
    StringBuffer sb = new StringBuffer();
    sb.append( "#" );
    sb.append( getHexStr( red ) );
    sb.append( getHexStr( green ) );
    sb.append( getHexStr( blue ) );
    return sb.toString();
  }
  
  private static String getHexStr( final int value ) {
    String hex = Integer.toHexString( value );
    return hex.length() == 1 ? "0" + hex : hex;
  }

  public boolean equals( final Object obj ) {
    boolean result = false;
    if( obj == this ) {
      result = true;
    } else if( obj instanceof QxColor ) {
      QxColor other = ( QxColor )obj;
      result =  other.red == red
             && other.green == green
             && other.blue == blue; 
    }
    return result;
  }
  
  public int hashCode() {
    return red ^ green ^ blue ;
  }
  
  public String toString() {
    return "QxColor {"
           + red
           + ", "
           + green
           + ", "
           + blue
           + "}";
  }

}
