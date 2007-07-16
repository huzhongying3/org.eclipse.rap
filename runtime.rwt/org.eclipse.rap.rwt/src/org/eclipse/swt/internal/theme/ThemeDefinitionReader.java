/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/

package org.eclipse.swt.internal.theme;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

public class ThemeDefinitionReader {

  private static final String ATTR_DEFAULT = "default";
  private static final String ATTR_INHERIT = "inherit";
  private static final String ATTR_DESCRIPTION = "description";
  private static final String ATTR_NAME = "name";

  public class ThemeDef {
    public final String name;
    public final String inherit;
    public final QxType defValue;
    public final String description;
    public ThemeDef( final String name,
                     final String inherit,
                     final QxType defValue,
                     final String description )
    {
      this.name = name;
      this.inherit = inherit;
      this.defValue = defValue;
      this.description = description;
    }
  }

  public interface ThemeDefHandler {
    public abstract void readThemeDef( ThemeDef def );
  }

  private static final String NODE_ROOT = "theme";
  private static final String TYPE_FONT = "font";
  private static final String TYPE_COLOR = "color";
  private static final String TYPE_BOOLEAN = "boolean";
  private static final String TYPE_BORDER = "border";
  private static final String TYPE_BOXDIMENSION = "boxdim";
  private static final String TYPE_DIMENDSION = "dimension";
  private static final String TYPE_IMAGE = "image";

  private static final String THEME_DEF_SCHEMA = "themedef.xsd";
  private static final String JAXP_SCHEMA_LANGUAGE
    = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  private static final String W3C_XML_SCHEMA
    = "http://www.w3.org/2001/XMLSchema";

  private InputStream inputStream;

  /**
   * An instance of this class reads theme definitions from an XML resource.
   * @param inputStream input stream from a theme definition XML
   */
  public ThemeDefinitionReader( final InputStream inputStream )
  {
    if( inputStream == null ) {
      throw new NullPointerException( "null argument" );
    }
    this.inputStream = inputStream;
  }

  /**
   * Reads a theme definition from the stream. The stream is kept open after
   * reading.
   */
  public void read( final ThemeDefHandler callback )
    throws IOException, FactoryConfigurationError,
    ParserConfigurationException, SAXException
  {
    Document document = parseThemeDefinition( inputStream );
    Node root = document.getElementsByTagName( NODE_ROOT ).item( 0 );
    NodeList childNodes = root.getChildNodes();
    for( int i = 0; i < childNodes.getLength(); i++ ) {
      Node node = childNodes.item( i );
      if( node.getNodeType() == Node.ELEMENT_NODE ) {
        ThemeDef def = readElement( node );
        callback.readThemeDef( def );
      }
    }
  }

  private ThemeDef readElement( final Node node ) {
    String type = node.getNodeName();
    String name = getAttributeValue( node, ATTR_NAME );
    String description = getAttributeValue( node, ATTR_DESCRIPTION );
    String inherit = getAttributeValue( node, ATTR_INHERIT );
    String defaultStr = getAttributeValue( node, ATTR_DEFAULT );
    QxType value;
    if( TYPE_FONT.equals( type ) ) {
      value = new QxFont( defaultStr );
    } else if( TYPE_COLOR.equals( type ) ) {
      value = new QxColor( defaultStr );
    } else if( TYPE_BOOLEAN.equals( type ) ) {
      value = new QxBoolean( defaultStr );
    } else if( TYPE_BORDER.equals( type ) ) {
      value = new QxBorder( defaultStr );
    } else if( TYPE_BOXDIMENSION.equals( type ) ) {
      value = new QxBoxDimensions( defaultStr );
    } else if( TYPE_DIMENDSION.equals( type ) ) {
      value = new QxDimension( defaultStr );
    } else if( TYPE_IMAGE.equals( type ) ) {
      value = new QxImage( defaultStr );
    } else {
      // TODO [rst] Remove when XML validation is active
      throw new IllegalArgumentException( "Illegal type: " + type );
    }
    return new ThemeDef( name, inherit, value, description );
  }

  private static String getAttributeValue( final Node node, final String name ) {
    String result = null;
    NamedNodeMap attributes = node.getAttributes();
    if( attributes != null ) {
      Node namedItem = attributes.getNamedItem( name );
      if( namedItem != null ) {
        result = namedItem.getNodeValue();
      }
    }
    return result;
  }

  private Document parseThemeDefinition( final InputStream is )
    throws FactoryConfigurationError, ParserConfigurationException,
    SAXException, IOException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware( true );
    ClassLoader loader = ThemeDefinitionReader.class.getClassLoader();
    final URL schema = loader.getResource( THEME_DEF_SCHEMA );
    factory.setValidating( schema != null );
    try {
      factory.setAttribute( JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA );
    } catch( final IllegalArgumentException iae ) {
      // XML-Processing does not support JAXP 1.2 or greater
      factory.setNamespaceAware( false );
      factory.setValidating( false );
    }
    DocumentBuilder builder = factory.newDocumentBuilder();
//    builder.setEntityResolver( new EntityResolver() {
//      public InputSource resolveEntity( final String publicID,
//                                        final String systemID )
//        throws IOException, SAXException
//      {
//        InputSource result = null;
//        if( schema != null && systemID.endsWith( THEME_DEF_SCHEMA ) ) {
//          URLConnection connection = schema.openConnection();
//          connection.setUseCaches( false );
//          result = new InputSource( connection.getInputStream() );
//        }
//        return result;
//      }
//    } );
    builder.setErrorHandler( getErrorHandler() );
    return builder.parse( is );
  }

  private ErrorHandler getErrorHandler() {
    // TODO: decent error handling
    return new ErrorHandler() {

      public void error( final SAXParseException spe ) throws SAXException {
        System.out.println( "Error parsing theme definition:" );
        System.out.println( spe.getMessage() );
      }

      public void fatalError( final SAXParseException spe )
        throws SAXException
      {
        String msg = "Fatal error parsing theme definition:";
        System.out.println( msg );
        System.out.println( spe.getMessage() );
      }

      public void warning( final SAXParseException spe )
        throws SAXException
      {
        System.out.println( "Warning parsing theme definition:" );
        System.out.println( spe.getMessage() );
      }
    };
  }
}
