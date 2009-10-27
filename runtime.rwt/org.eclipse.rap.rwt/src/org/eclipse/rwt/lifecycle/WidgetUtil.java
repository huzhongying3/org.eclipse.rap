/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.lifecycle;

import java.text.MessageFormat;

import org.eclipse.rwt.internal.lifecycle.UITestUtil;
import org.eclipse.swt.internal.widgets.WidgetAdapter;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor;
import org.eclipse.swt.internal.widgets.WidgetTreeVisitor.AllWidgetTreeVisitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;


/**
 * This is a helper class to obtain different aspects for a widget
 * related to the handling of widgets in RAP.
 *
 * @since 1.0
 */
public final class WidgetUtil {

  /**
   * Used to mark a widget as belonging to a custom variant using
   * <code>Widget.setData</code>. For more information on custom widget
   * variants, see the RAP help on theming.
   *
   * @see Widget#setData(String,Object)
   * @since 1.1
   */
  public static final String CUSTOM_VARIANT = "org.eclipse.rwt.themeVariant";

  /**
   * <p><strong>Note:</strong> This constant is provisional and subject to
   * change without further notice.</p>
   *
   * By default, the widget-id returned by {@link IWidgetAdapter#getId()} is
   * an automatically generated value that is session-wide unique.
   * A custom id can be assigned by using the <code>Widget#setData(String,
   * Object)</code> method and using this constant for the <code>key</code>
   * argument and a string that denotes the new id as the <code>data</code>
   * argument.
   *
   * <p>The <code>id</code> must only contain characters that are valid according
   * to the <a href="http://www.w3.org/TR/html401/types.html#type-cdata">W3C
   * recommendation for id and name attributes</a>.</p>
   *
   * <p>It is the clients' responsibility to choose a unique id. Assigning an
   * id that is used by another widget will lead to indeterministic behavior.
   * </p>
   *
   * <p>The following code would assign the id 'myId' to the widget:
   * <pre>
   *   Widget widget = new ...
   *   widget.setData( WidgetUtil.CUSTOM_WIDGET_ID, "myId" );
   * </pre></p>
   *
   * @see Widget#setData(String,Object)
   * @see #getId(Widget)
   *
   * @since 1.1
   */
  public static final String CUSTOM_WIDGET_ID
    = "org.eclipse.rwt.UITests#customId";

  /**
   * <p><strong>Note:</strong> This constant is provisional and subject to
   * change without further notice.</p>
   *
   * If a system property with this name is set to <code>true</code>, the
   * UI testing support is activated. For all widgets that are rendered to
   * the client, the HTML id attribute is set.
   *
   * <p>In conjunction with <code>CUSTOM_WIDGET_ID</code>, each widget can
   * be assigned a custom, more human-readable, identifier that is independant
   * if the order in which widgets are created.</p>
   *
   * @see #CUSTOM_WIDGET_ID
   *
   * @since 1.1
   */
  public static final String ENABLE_UI_TESTS
    = "org.eclipse.rwt.enableUITests";

  private WidgetUtil() {
    // prevent instantiation
  }

  /**
   * Returns the according {@link IWidgetAdapter} for a specified
   * widget.
   *
   * @param widget the widget
   * @return the {@link IWidgetAdapter} instance
   */
  public static IWidgetAdapter getAdapter( final Widget widget ) {
    IWidgetAdapter result;
    result = ( IWidgetAdapter )widget.getAdapter( IWidgetAdapter.class );
    if( result == null ) {
      throwAdapterException( IWidgetAdapter.class );
    }
    return result;
  }

  /**
   * Returns the id of the given <code>widget</code> that is used to identify
   * the widget on the client.
   *
   * @param widget the widget to obtain the id for, must not be
   *          <code>null</code>
   * @return the id for the given <code>widget</code>
   */
  public static String getId( final Widget widget ) {
    // TODO [rh] consider overriding the id when Widget#setData is called
    //      - safer 1: in case someone tries to obtain id directly from addapter
    //      - safer 2: changing the id after widget was initialized could be
    //        detected and prevented
    //      - less memory: new 'data' array created per widget to hold the id
    //      - illegal id's could be rejected immediately (close to error source)
    //      - faster (?): only "return getAdapter( widget ).getId();" in here
    String result = null;
    if( UITestUtil.isEnabled() ) {
      result = ( String )widget.getData( CUSTOM_WIDGET_ID );
    }
    if( result == null ) {
      result = getAdapter( widget ).getId();
    }
    return result;
  }

  /**
   * Returns the widget variant defined for the given widget using
   * <code>Widget.setData()</code>.
   *
   * @param widget the widget whose variant is requested
   * @return the variant or <code>null</code> if no variant has been specified
   *         for the given widget
   */
  public static String getVariant( final Widget widget ) {
    String result = null;
    WidgetAdapter widgetAdapter = ( WidgetAdapter )getAdapter( widget );
    Object data = widget.getData( WidgetUtil.CUSTOM_VARIANT );
    if( data instanceof String ) {
      result = ( String )data;
      if( !result.equals( widgetAdapter.getCachedVariant() ) ) {
        if( validateVariantString( result ) ) {
          widgetAdapter.setCachedVariant( result );
        } else {
          String pattern = "Illegal character in widget variant ''{0}''";
          Object[] arguments = new Object[] { result };
          String message = MessageFormat.format( pattern, arguments );
          throw new IllegalArgumentException( message );
        }
      }
    }
    return result;
  }

  /**
   * Returns the {@link AbstractWidgetLCA} instance for this widget.
   *
   * @param widget the widget to obtain the life cycle adapter from
   * @return the life cycle adapter for the given <code>widget</code>
   */
  // TODO [bm] why do we return AbstractWidgetLCA instead of pulling the interesting
  // methods up to IWidgetLifeCycleAdapter and using this to talk to the outside
  // world
  public static AbstractWidgetLCA getLCA( final Widget widget ) {
    Class clazz = ILifeCycleAdapter.class;
    AbstractWidgetLCA result = ( AbstractWidgetLCA )widget.getAdapter( clazz );
    if( result == null ) {
      throwAdapterException( clazz );
    }
    return result;
  }

  /**
   * This method searches for a widget with the given <code>id</code> within
   * the widget hierachy starting at <code>root</code>.
   *
   * @param root the root widget where to start the search
   * @param id the id of the widget to search for
   * @return the widget or <code>null</code> if there was no widget found with
   * the given <code>id</code> within the widget hierarchy
   */
  public static Widget find( final Composite root, final String id ) {
    final Widget[] result = { null };
    if( id != null ) {
      WidgetTreeVisitor.accept( root, new AllWidgetTreeVisitor() {
        public boolean doVisit( final Widget widget ) {
          if( getId( widget ).equals( id ) ) {
            result[ 0 ] = widget;
          }
          return result[ 0 ] == null;
        }
      } );
    }
    return result[ 0 ];
  }

  private static void throwAdapterException( final Class clazz ) {
    String text =   "Could not retrieve an instance of ''{0}''. Probably the "
                  + "AdapterFactory was not properly registered.";
    Object[] param = new Object[]{ clazz.getName() };
    String msg = MessageFormat.format( text, param );
    throw new IllegalStateException( msg );
  }

  private static boolean validateVariantString( final String variant ) {
    boolean result = false;
    String name = variant;
    if( name.startsWith( "-" ) ) {
      name = name.substring( 1 );
    }
    int length = name.length();
    if( length > 0 ) {
      result = isValidStart( name.charAt( 0 ) );
      for( int i = 1; i < length && result; i++ ) {
        result &= isValidPart( name.charAt( i ) );
      }
    }
    return result;
  }

  private static boolean isValidStart( final char ch ) {
    return ch == '_'
      || ( ch >= 'a' && ch <= 'z' )
      || ( ch >= 'A' && ch <= 'Z' )
      || ( ch >= 128 && ch <= 255 );
  }

  private static boolean isValidPart( final char ch ) {
    return isValidStart( ch )
      || ( ch >= '0' && ch <= '9' )
      || ch == '-';
  }
}
