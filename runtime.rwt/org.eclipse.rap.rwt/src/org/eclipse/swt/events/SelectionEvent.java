/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.events;

import org.eclipse.rwt.Adaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.widgets.EventUtil;
import org.eclipse.swt.widgets.*;



/**
 * Instances of this class are sent as a result of
 * widgets being selected.
 * <p>
 * Note: The fields that are filled in depend on the widget.
 * </p>
 *
 * <p><strong>IMPORTANT:</strong> All <code>public static</code> members of 
 * this class are <em>not</em> part of the RWT public API. They are marked 
 * public only so that they can be shared within the packages provided by RWT. 
 * They should never be accessed from application code.
 * </p>
 * 
 * @see SelectionListener
 */
public class SelectionEvent extends TypedEvent {

  private static final long serialVersionUID = 1L;

  public static final int WIDGET_SELECTED = SWT.Selection;
  public static final int WIDGET_DEFAULT_SELECTED = SWT.DefaultSelection;
  
  private static final Class LISTENER = SelectionListener.class;
  
  /**
   * The x location of the selected area.
   */
  public int x;

  /**
   * The y location of selected area.
   */
  public int y;

  /**
   * The width of selected area.
   */
  public int width;

  /**
   * The height of selected area.
   */
  public int height;

  /**
   * The text of the hyperlink that was selected.
   * This will be either the text of the hyperlink or the value of its HREF,
   * if one was specified.
   * 
   * @see org.eclipse.swt.widgets.Link#setText(String)
   */
  public String text;
  
	/**
	 * A flag indicating whether the operation should be allowed.
	 * Setting this field to <code>false</code> will cancel the
	 * operation, depending on the widget.
	 */
  public boolean doit;
  
  /**
   * The item that was selected.
   */
  public Widget item;

  /**
   * Extra detail information about the selection, depending on the widget.
   * <!--
   * <p><b>Sash</b><ul>
   * <li>{@link org.eclipse.swt.SWT#DRAG}</li>
   * </ul></p><p><b>ScrollBar and Slider</b><ul>
   * <li>{@link org.eclipse.swt.SWT#DRAG}</li>
   * <li>{@link org.eclipse.swt.SWT#HOME}</li>
   * <li>{@link org.eclipse.swt.SWT#END}</li>
   * <li>{@link org.eclipse.swt.SWT#ARROW_DOWN}</li>
   * <li>{@link org.eclipse.swt.SWT#ARROW_UP}</li>
   * <li>{@link org.eclipse.swt.SWT#PAGE_DOWN}</li>
   * <li>{@link org.eclipse.swt.SWT#PAGE_UP}</li>
   * -->
   * </ul></p><p><b>Table and Tree</b><ul>
   * <li>{@link org.eclipse.swt.SWT#CHECK}</li>
   * <!--
   * </ul></p><p><b>Text</b><ul>
   * <li>{@link org.eclipse.swt.SWT#CANCEL}</li>
   * -->
   * </ul></p><p><b>CoolItem and ToolItem</b><ul>
   * <li>{@link org.eclipse.swt.SWT#ARROW}</li>
   * </ul></p>
   */
  public int detail;
  
  /**
   * Constructs a new instance of this class based on the
   * information in the given untyped event.
   *
   * @param e the untyped event containing the information
   */
  public SelectionEvent( final Event e ) {
    this( e.widget,
          e.item,
          e.type,
          new Rectangle( e.x, e.y, e.width, e.height ),
          e.text,
          e.doit,
          e.detail );
  }
  
  /**
   * Constructs a new instance of this class. 
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed 
   * from application code.
   * </p>
   */
  public SelectionEvent( final Widget widget,
                         final Widget item,
                         final int id )
  {
    this( widget, item, id, new Rectangle( 0, 0, 0, 0 ), null, true, SWT.NONE );
  }

  /**
   * Constructs a new instance of this class. 
   * <p><strong>IMPORTANT:</strong> This method is <em>not</em> part of the RWT
   * public API. It is marked public only so that it can be shared
   * within the packages provided by RWT. It should never be accessed 
   * from application code.
   * </p>
   */
  public SelectionEvent( final Widget widget,
                         final Widget item,
                         final int id,
                         final Rectangle bounds,
                         final String text,
                         final boolean doit,
                         final int detail )
  {
    super( widget, id );
    this.widget = widget;
    this.x = bounds.x;
    this.y = bounds.y;
    this.width = bounds.width;
    this.height = bounds.height;
    this.text = text;
    this.doit = doit;
    this.item = item;
    this.detail = detail;
  }

  protected void dispatchToObserver( final Object listener ) {
    switch( getID() ) {
      case WIDGET_SELECTED:
        ( ( SelectionListener )listener ).widgetSelected( this );
      break;
      case WIDGET_DEFAULT_SELECTED:
        ( ( SelectionListener )listener ).widgetDefaultSelected( this );        
        break;
      default:
        throw new IllegalStateException( "Invalid event handler type." );
    }
  }

  protected Class getListenerType() {
    return LISTENER;
  }
  
  protected boolean allowProcessing() {
    return EventUtil.isAccessible( widget );
  }

  public static boolean hasListener( final Adaptable adaptable ) {
    return hasListener( adaptable, LISTENER );
  }
  
  public static void addListener( final Adaptable adaptable,
                                  final SelectionListener listener )
  {
    addListener( adaptable, LISTENER, listener );
  }

  public static void removeListener( final Adaptable adaptable,
                                     final SelectionListener listener )
  {
    removeListener( adaptable, LISTENER, listener );
  }

  public static Object[] getListeners( final Adaptable adaptable ) {
    return getListener( adaptable, LISTENER );
  }
  
  public String toString() {
    String string = super.toString ();
    return   string.substring( 0, string.length() - 1 ) // remove trailing '}'
           + " item="
           + item
           + " detail="
           + detail
           + " x="
           + x
           + " y="
           + y
           + " width="
           + width
           + " height="
           + height
           // + " stateMask=" + stateMask
           + " text=" + text
           + " doit="
           + doit
           + "}";
  }
}