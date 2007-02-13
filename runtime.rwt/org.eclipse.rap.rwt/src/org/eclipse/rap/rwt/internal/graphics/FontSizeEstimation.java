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

package org.eclipse.rap.rwt.internal.graphics;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Font;
import org.eclipse.rap.rwt.graphics.Point;

public class FontSizeEstimation {

  public static Point stringExtent( String string, Font font ) {    
    if ( string == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    int width = getLineWidth( string, font );
    int height = getCharHeight( font ) + 2;
    return new Point( width, height );
  }
  
  /**
   * respects line breaks and wrap
   */
  public static Point textExtent( final String string,
                                  final int wrapWidth,
                                  final Font font )
  {
    if ( string == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    int lineCount = 0;
    int maxWidth = 0;
    String[] lines = string.split( "\n" );
    for( int i = 0; i < lines.length; i++ ) {
      String line = lines[ i ];
      lineCount++;
      int width = getLineWidth( line, font );
      if( wrapWidth > 0 ) {
        boolean done = false;
        while( !done ) {
          int index = getLongestMatch( line, wrapWidth, font );
          if( index == 0 || index == line.length() ) {
            // line fits or cannot be wrapped
            done = true;
          } else {
            // wrap line
            String substr = line.substring( 0, index );
            width = getLineWidth( substr, font );
            maxWidth = Math.max( maxWidth, width );
            line = line.substring( index, line.length() );
            lineCount++;
          }
        }
      }
      maxWidth = Math.max( maxWidth, width );
    }
    int height = Math.round( getCharHeight( font ) * 1.25f * lineCount );
    return new Point( maxWidth, height );
  }
  
  public static int getCharHeight( final Font font ) {
    // at 72 dpi, 1 pt == 1 px
    return font.getSize();
  }
  
  public static float getAvgCharWidth( final Font font ) {
    float width = font.getSize() * 0.48f;
    if( ( font.getStyle() & RWT.BOLD ) != 0 ) {
      width *= 1.45;
    }
    return width;
  }
  
  /**
   * @return The length of the longest substring, whose width is smaller or
   *         equal to wrapWidth. If there is no such substring, zero is
   *         returned. The result is never negative.
   */
  private static int getLongestMatch( final String string,
                                      final int wrapWidth,
                                      final Font font )
  {
    int result = 0;
    int index = 0;
    int width = getLineWidth( string, font );
    if( width < wrapWidth ) {
      result = string.length();
    } else {
      while( ( index = string.indexOf( ' ', index ) ) != -1 ) {
        String subStr = string.substring( 0, index );
        width = getLineWidth( subStr, font );
        index++;
        if( width <= wrapWidth ) {
          result = index;
        } else {
          break; // I know, you don't like it, but anything else would complicate the algorithm
        }
      }
    }
    return result;
  }
  
  private static int getLineWidth( final String string, final Font font ) {
    return Math.round( getAvgCharWidth( font ) * string.length() );
  }
}
