/*******************************************************************************
 * Copyright (c) 2007-2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.internal.theme;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;

/**
 * An instance of this class represents all the information provided by an RWT
 * theme, i.e. values from the theme properties file, derived values from the
 * default theme, and the name given to the theme.
 */
public final class Theme {

  private final String name;

  private final Map values;

  private final Map defaultValues;

  /**
   * Creates a new theme which has no default theme. In RWT theming, only the
   * predefined theme has no default theme.
   *
   * @param name the name of the theme, must not be <code>null</code>
   */
  public Theme( final String name ) {
    this( name, null );
  }

  /**
   * Creates a new theme with the given default theme. The default theme
   * specifies the possible keys and their default values. <strong>Important:</strong>
   * Modifying the default theme afterwards has no effect on this theme.
   *
   * @param name the name of the theme, must not be <code>null</code>
   * @param defaultTheme the default theme
   */
  public Theme( final String name, final Theme defaultTheme ) {
    checkName( name );
    if( name == null ) {
      throw new NullPointerException( "name is null" );
    }
    this.name = name;
    this.defaultValues = defaultTheme != null ? defaultTheme.values : null;
    values = new HashMap();
  }

  /**
   * Loads a theme from a <code>.properties</code> file.
   *
   * @param name the name for the theme to create, must not be <code>null</code>
   * @param inStr the input stream of the theme file to read
   * @param loader the loader for resources provided by the theme
   * @return the newly created theme
   */
  public static Theme loadFromFile( final String name,
                                    final Theme defaultTheme,
                                    final InputStream inStr,
                                    final ResourceLoader loader )
    throws IOException
  {
    if( inStr == null ) {
      throw new NullPointerException( "null argument" );
    }
    Theme newTheme = new Theme( name, defaultTheme );
    Properties properties = new Properties();
    properties.load( inStr );
    Iterator iterator = properties.keySet().iterator();
    while( iterator.hasNext() ) {
      String key = ( ( String )iterator.next() ).trim();
      String keyName;
      String keyVariant;
      int index = key.indexOf( '/' );
      if( index != -1 ) {
        keyVariant = key.substring( 0, index );
        keyName = key.substring( index + 1 );
      } else {
        keyName = key;
        keyVariant = null;
      }
      if( !defaultTheme.definesKey( keyName ) ) {
        String pattern = "Invalid key for themeing: ''{0}'' in ''{1}''";
        Object[] arguments = new Object[] { keyName, key };
        String message = MessageFormat.format( pattern, arguments );
        throw new IllegalArgumentException( message );
      }
      QxType defValue = defaultTheme.getValue( keyName, null );
      String value = ( ( String )properties.get( key ) ) .trim();
      if( value != null && value.trim().length() > 0 ) {
        QxType newValue;
        if( defValue instanceof QxBorder ) {
          newValue = QxBorder.valueOf( value );
        } else if( defValue instanceof QxBoolean ) {
          newValue = QxBoolean.valueOf( value );
        } else if( defValue instanceof QxBoxDimensions ) {
          newValue = QxBoxDimensions.valueOf( value );
        } else if( defValue instanceof QxFont ) {
          newValue = QxFont.valueOf( value );
        } else if( defValue instanceof QxColor ) {
          newValue = QxColor.valueOf( value );
        } else if( defValue instanceof QxDimension ) {
          newValue = QxDimension.valueOf( value );
        } else if( defValue instanceof QxImage ) {
          newValue = QxImage.valueOf( value, loader );
        } else {
          // TODO [rst] How to handle programming errors?
          throw new RuntimeException( "unknown type" );
        }
        newTheme.setValue( keyName, keyVariant, newValue );
      }
    }
    return newTheme;
  }

  public String getName() {
    return name;
  }

  /**
   * Indicates whether this theme has a value for the specified key, no matter
   * if the value is defined in the theme itself or derived from the default
   * theme.
   *
   * @return <code>true</code> if either the theme itself or its default theme
   *         has a value for key, <code>false</code> otherwise.
   */
  public boolean hasKey( final String key ) {
    boolean result = values.containsKey( key );
    if( defaultValues != null ) {
      result |= defaultValues.containsKey( key );
    }
    return result;
  }

  /**
   * Indicates whether this theme defines the specified key. If the key is only
   * defined in the default theme, <code>false</code> is returned.
   */
  public boolean definesKey( final String key ) {
    return values.containsKey( key );
  }

  /**
   * Returns all keys defined in this theme, including the keys of the default
   * theme. Variant keys will be omitted.
   *
   * @return an array of keys, never <code>null</code>
   */
  public String[] getKeys() {
    Set keySet;
    if( defaultValues != null ) {
      keySet = defaultValues.keySet();
    } else {
      keySet = values.keySet();
    }
    return ( String[] )keySet.toArray( new String[ keySet.size() ] );
  }

  /**
   * Returns all keys defined in this theme, including the keys of the default
   * theme and variant keys.
   *
   * @return An array of keys, never <code>null</code>
   */
  public String[] getKeysWithVariants() {
    Set keySet = new HashSet( values.keySet() );
    if( defaultValues != null ) {
      keySet.addAll( defaultValues.keySet() );
    }
    return ( String[] )keySet.toArray( new String[ keySet.size() ] );
  }

  public QxBorder getBorder( final String key, final String variant ) {
    QxType value = getValue( key, variant );
    checkType( key, value, QxBorder.class );
    return ( QxBorder )value;
  }

  public QxBoxDimensions getBoxDimensions( final String key, final String variant ) {
    QxType value = getValue( key, variant );
    checkType( key, value, QxBoxDimensions.class );
    return ( QxBoxDimensions )value;
  }

  public QxFont getFont( final String key, final String variant ) {
    QxType value = getValue( key, variant );
    checkType( key, value, QxFont.class );
    return ( QxFont )value;
  }

  public QxColor getColor( final String key, final String variant ) {
    QxType value = getValue( key, variant );
    checkType( key, value, QxColor.class );
    return ( QxColor )value;
  }

  public QxImage getImage( final String key, final String variant ) {
    QxType value = getValue( key, variant );
    checkType( key, value, QxImage.class );
    return ( QxImage )value;
  }

  public QxDimension getDimension( final String key, final String variant ) {
    QxType value = getValue( key, variant );
    checkType( key, value, QxDimension.class );
    return ( QxDimension )value;
  }

  /**
   * Returns the value for the given key. If the theme does not define a value
   * for the given key, the default value is returned.
   *
   * @param key the key to get the value for, may include variant
   * @return the value for the given key, never <code>null</code>
   */
  public QxType getValue( final String key ) {
    return getValue( key, null );
  }

  /**
   * Returns the value for the given key and variant. If the variant is
   * <code>null</code> or there is no such variant defined for the given key,
   * the default variant is assumed. If the theme does not define a value for
   * the given key, the default value is returned.
   *
   * @param key the key to get the value for
   * @param variant the variant to get the value for, or <code>null</code> for
   *            the default variant
   * @return the value for the given key, never <code>null</code>
   */
  public QxType getValue( final String key, final String variant ) {
    QxType result;
    if( variant != null && values.containsKey( variant + "/" + key ) ) {
      result = ( QxType )values.get( variant + "/" + key );
    } else if( values.containsKey( key ) ) {
      result = ( QxType )values.get( key );
    } else if( defaultValues != null && defaultValues.containsKey( key ) ) {
      result = ( QxType )defaultValues.get( key );
    } else {
      String pattern = "Undefined key: ''{0}'' in theme ''{1}''";
      Object[] arguments = new Object[] { key, name };
      String message = MessageFormat.format( pattern, arguments );
      throw new IllegalArgumentException( message );
    }
    return result;
  }

  public void setValue( final String key, final QxType value ) {
    setValue( key, null, value );
  }

  public void setValue( final String key,
                        final String variant,
                        final QxType value )
  {
    if( key == null ) {
      throw new NullPointerException( "null argument" );
    }
    if( variant != null && defaultValues == null ) {
      throw new IllegalArgumentException( "Variants not allowed in default theme" );
    }
    if( variant == null && values.containsKey( key ) ) {
      String msg = "Tried to redefine key: " + key;
      throw new IllegalArgumentException( msg );
    }
    if( defaultValues != null ) {
      if( defaultValues.containsKey( key ) ) {
        if( !defaultValues.get( key ).getClass().isInstance( value ) ) {
          String msg = "Tried to define key with wrong type: " + key;
          throw new IllegalArgumentException( msg );
        }
      } else {
        String msg = "Key does not exist in default theme: " + key;
        throw new IllegalArgumentException( msg );
      }
    }
    values.put( variant == null ? key : variant + "/" + key, value );
  }

  public boolean equals( final Object obj ) {
    boolean result;
    if( obj == this ) {
      result = true;
    } else if( obj instanceof Theme ) {
      Theme other = ( Theme )obj;
      result = name.equals( other.name );
      if( defaultValues == null ) {
        result &= other.defaultValues == null;
      } else {
        result &= defaultValues.equals( other.defaultValues );
      }
      result &= values.equals( other.values );
    } else {
      result = false;
    }
    return result;
  }

  public int hashCode() {
    int result = 17;
    result = 37 * result + name.hashCode();
    if( defaultValues != null ) {
      result = 37 * defaultValues.hashCode();
    }
    result = 37 * result + values.hashCode();
    return result;
  }

  private void checkType( final String key, final QxType value, final Class type )
  {
    if( !value.getClass().isAssignableFrom( type ) ) {
      String pattern = "Requested key ''{{0}}'' has a different type";
      Object[] arguments = new Object[] { key };
      String message = MessageFormat.format( pattern, arguments );
      throw new IllegalArgumentException( message );
    }
  }

  private void checkName( final String name ) {
    if( name == null ) {
      throw new NullPointerException( "name" );
    }
    if( name.length() == 0 ) {
      throw new IllegalArgumentException( "empty argument" );
    }
  }
}
