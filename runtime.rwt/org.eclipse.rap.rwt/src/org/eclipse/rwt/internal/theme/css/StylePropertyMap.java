/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme.css;

import java.util.*;

import org.eclipse.rwt.internal.theme.*;
import org.w3c.css.sac.LexicalUnit;


public class StylePropertyMap implements IStylePropertyMap {

  private final Map properties;

  private final ResourceLoader loader;

  public StylePropertyMap( final ResourceLoader loader ) {
    this.loader = loader;
    properties = new HashMap();
  }

  public void setProperty( final String key, final LexicalUnit value ) {
    if( key == null || value == null ) {
      throw new NullPointerException( "null argument" );
    }
    properties.put( key, value );
  }

  public String[] getProperties() {
    Set keySet = properties.keySet();
    String[] result = new String[ keySet.size() ];
    keySet.toArray( result );
    return result;
  }

  public QxType getValue( final String property ) {
    QxType result = null;
    if( "color".equals( property ) ) {
      result = getColor( "color" );
    } else if( "background-color".equals( property ) ) {
      result = getColor( "background-color" );
    } else if( "background-image".equals( property ) ) {
      result = getImage( "background-image" );
    } else if( "border".equals( property ) ) {
      result = getBorder( "border" );
    } else if( "padding".equals( property ) ) {
      result = getBoxDimensions( "padding" );
    } else if( "margin".equals( property ) ) {
      result = getBoxDimensions( "margin" );
    } else if( "spacing".equals( property ) ) {
      result = getDimension( "spacing" );
    } else if( "height".equals( property ) ) {
      result = getDimension( "height" );
    } else if( "width".equals( property ) ) {
      result = getDimension( "width" );
    } else if( "font".equals( property ) ) {
      result = getFont( "font" );
    } else if( property.startsWith( "rwt" )
               && property.endsWith( "color" ) )
    {
      result = getColor( property );
    } else if( "background-gradient-color".equals( property ) ) {
      result = getColor( "background-color" );
    } else {
      // TODO [rst] Logging instead of sysout
      System.err.println( "WARNING: unsupported css property: " + property );
    }
    return result;
  }

  public QxType getValue( final String propertyName, final Class type ) {
    QxType result = null;
    if( QxBorder.class == type ) {
      result = getBorder( propertyName );
//    } else if( QxBoolean.class == type ) {
//      result = getBoolean( propertyName );
    } else if( QxBoxDimensions.class == type ) {
      result = getBoxDimensions( propertyName );
    } else if( QxColor.class == type ) {
      result = getColor( propertyName );
    } else if( QxDimension.class == type ) {
      result = getDimension( propertyName );
    } else if( QxFont.class == type ) {
      result = getFont( propertyName );
    } else if( QxImage.class == type ) {
      result = getImage( propertyName );
    } else {
      // TODO [rst] Logging instead of sysout
      System.err.println( "WARNING: unsupported type: " + type.getName() );
    }
    return result;
  }

  public QxFont getFont( final String propertyName ) {
    QxFont result = null;
    LexicalUnit lexicalUnit = getPropertyValue( propertyName );
    if( lexicalUnit != null ) {
      result = PropertyResolver.readFont( lexicalUnit );
    }
    return result;
  }

  public QxBorder getBorder( final String propertyName ) {
    QxBorder result = null;
    LexicalUnit lexicalUnit = getPropertyValue( propertyName );
    if( lexicalUnit != null ) {
      result = PropertyResolver.readBorder( lexicalUnit );
    }
    return result;
  }

  public QxBoxDimensions getBoxDimensions( final String propertyName ) {
    QxBoxDimensions result = null;
    LexicalUnit lexicalUnit = getPropertyValue( propertyName );
    if( lexicalUnit != null ) {
      result = PropertyResolver.readBoxDimensions( lexicalUnit );
    }
    return result;
  }

  public QxDimension getDimension( final String propertyName ) {
    QxDimension result = null;
    LexicalUnit lexicalUnit = getPropertyValue( propertyName );
    if( lexicalUnit != null ) {
      result = PropertyResolver.readDimension( lexicalUnit );
    }
    return result;
  }

  public QxColor getColor( final String propertyName ) {
    QxColor result = null;
    LexicalUnit lexicalUnit = getPropertyValue( propertyName );
    if( lexicalUnit != null ) {
      result = PropertyResolver.readColor( lexicalUnit );
    }
    return result;
  }

  public QxImage getImage( final String propertyName ) {
    QxImage result = null;
    LexicalUnit lexicalUnit = getPropertyValue( propertyName );
    if( lexicalUnit != null ) {
      result = PropertyResolver.readBackgroundImage( lexicalUnit, loader );
    }
    return result;
  }

  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append( "{\n" );
    String[] properties = getProperties();
    for( int i = 0; i < properties.length; i++ ) {
      String property = properties[ i ];
      QxType value = getValue( property );
      result.append( "  " );
      result.append( property );
      result.append( ": " );
      result.append( value.toDefaultString() );
      result.append( ";\n" );
    }
    result.append( "}" );
    return result.toString();
  }

  private LexicalUnit getPropertyValue( final String key ) {
    return ( LexicalUnit )properties.get( key );
  }

  public boolean equals( Object obj ) {
    boolean result = false;
    if( obj == this ) {
      result = true;
    } else if( obj.getClass() == this.getClass() ) {
      StylePropertyMap other = ( StylePropertyMap )obj;
      result = properties.equals( other.properties );
    }
    return result;
  }

  public int hashCode() {
    return properties.hashCode();
  }
}
