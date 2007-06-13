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

package org.eclipse.swt.custom;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.graphics.FontSizeEstimation;
import org.eclipse.swt.internal.widgets.IWidgetFontAdapter;
import org.eclipse.swt.widgets.*;

/**
 * Instances of this class represent a selectable user interface object
 * that represent a page in a notebook widget.
 * 
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>SWT.CLOSE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>(none)</dd>
 * </dl>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 */
public class CTabItem extends Item {

  static final int TOP_MARGIN = 2;
  static final int BOTTOM_MARGIN = 2;
  static final int LEFT_MARGIN = 4;
  static final int RIGHT_MARGIN = 4;
  static final int INTERNAL_SPACING = 4;
  
  static final String ELLIPSIS = "..."; 
  
  private final CTabFolder parent;
  private final IWidgetFontAdapter widgetFontAdapter;
  private Control control;
  private String toolTipText;
  private Font font;
  // TODO [rh] shortenedText is not yet calculated
  String shortenedText;
  int shortenedTextWidth;
  
  boolean showing = false;
  int x;
  int y;
  
  int width;
  int height;
  
  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>CTabFolder</code>) and a style value
   * describing its behavior and appearance. The item is added
   * to the end of the items maintained by its parent.
   * <p>
   * The style value is either one of the style constants defined in
   * class <code>SWT</code> which is applicable to instances of this
   * class, or must be built by <em>bitwise OR</em>'ing together 
   * (that is, using the <code>int</code> "|" operator) two or more
   * of those <code>SWT</code> style constants. The class description
   * lists the style constants that are applicable to the class.
   * Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a CTabFolder which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   * </ul>
   *
   * @see SWT
   * @see Widget#getStyle()
   */
  public CTabItem( final CTabFolder parent, final int style ) {
    this( parent, style, checkNull( parent ).getItemCount() );
  }

  /**
   * Constructs a new instance of this class given its parent
   * (which must be a <code>CTabFolder</code>), a style value
   * describing its behavior and appearance, and the index
   * at which to place it in the items maintained by its parent.
   * <p>
   * The style value is either one of the style constants defined in
   * class <code>SWT</code> which is applicable to instances of this
   * class, or must be built by <em>bitwise OR</em>'ing together 
   * (that is, using the <code>int</code> "|" operator) two or more
   * of those <code>SWT</code> style constants. The class description
   * lists the style constants that are applicable to the class.
   * Style bits are also inherited from superclasses.
   * </p>
   *
   * @param parent a CTabFolder which will be the parent of the new instance (cannot be null)
   * @param style the style of control to construct
   * @param index the zero-relative index to store the receiver in its parent
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
   *    <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements in the parent (inclusive)</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the parent</li>
   * </ul>
   *
   * @see SWT
   * @see Widget#getStyle()
   */
  public CTabItem( final CTabFolder parent, final int style, final int index ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    parent.createItem( this, index );
    widgetFontAdapter = new IWidgetFontAdapter() {
      public Font getUserFont() {
        return font;
      }
    };
  }

  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == IWidgetFontAdapter.class ) {
      result = widgetFontAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }

  public Display getDisplay() {
    return parent.getDisplay();
  }
  
  /**
   * Returns the receiver's parent, which must be a <code>CTabFolder</code>.
   *
   * @return the receiver's parent
   * 
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public CTabFolder getParent() {
    checkWidget();
    return parent;
  }
  
  /////////////////////////////////////////
  // Getter/setter for principal properties
  
  public void setText( final String text ) {
    checkWidget();
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( !text.equals( getText() ) ) {
      super.setText( text );
      shortenedText = null;
      shortenedTextWidth = 0;
      parent.updateItems();
    }
  }
  
  public void setImage( final Image image ) {
    checkWidget();
    if( image != getImage() ) {
      super.setImage( image );
      parent.updateItems();
    }
  }

  /**
   * Sets the font that the receiver will use to paint textual information
   * for this item to the font specified by the argument, or to the default font
   * for that kind of control if the argument is null.
   * 
   * <p>Changing font works, but tab size is not adjusted accordingly.</p>
   *
   * @param font the new font (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the argument has been disposed</li> 
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   * @since 1.0
   */
  public void setFont( final Font font ) {
    checkWidget();
    if( font != this.font ) {
      this.font = font;
      if( !parent.updateTabHeight( false ) ) {
        parent.updateItems();
      }
    }
  }

  /**
   * Returns the font that the receiver will use to paint textual information.
   *
   * @return the receiver's font
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
   *  @since 1.0
   */
  public Font getFont() {
    checkWidget();
    Font result = font;
    if( font == null ) {
      result = parent.getFont();
    }
    return result;
  }

  /**
  * Gets the control that is displayed in the content area of the tab item.
  *
  * @return the control
  *
  * @exception SWTException <ul>
  *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
  *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
  * </ul>
  */
  public Control getControl() {
    checkWidget();
    return control;
  }

  /**
   * Sets the control that is used to fill the client area of
   * the tab folder when the user selects the tab item.
   *
   * @param control the new control (or null)
   *
   * @exception IllegalArgumentException <ul>
   *    <li>ERROR_INVALID_ARGUMENT - if the control has been disposed</li> 
   *    <li>ERROR_INVALID_PARENT - if the control is not in the same widget tree</li>
   * </ul>
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setControl( final Control control ) {
    checkWidget();
    if( control != null ) {
      if( control.isDisposed() ) {
        SWT.error( SWT.ERROR_INVALID_ARGUMENT );
      }
      if( control.getParent() != parent ) {
        SWT.error( SWT.ERROR_INVALID_PARENT );
      }
    }
    if( this.control != null && !this.control.isDisposed() ) {
      this.control.setVisible( false );
    }
    this.control = control;
    if( this.control != null ) {
      int index = parent.indexOf( this );
      if( index == parent.getSelectionIndex() ) {
        this.control.setBounds( parent.getClientArea() );
        this.control.setVisible( true );
      } else {
        this.control.setVisible( false );
      }
    }
  }
  
  /**
   * Sets the receiver's tool tip text to the argument, which
   * may be null indicating that no tool tip text should be shown.
   *
   * @param string the new tool tip text (or null)
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public void setToolTipText( final String toolTipText ) {
    checkWidget();
    this.toolTipText = toolTipText;
  }
  
  /**
   * Returns the receiver's tool tip text, or null if it has
   * not been set.
   *
   * @return the receiver's tool tip text
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public String getToolTipText () {
    checkWidget();
    String result = toolTipText;
    if( result == null && shortenedText != null ) {
      String text = getText();
      if( !shortenedText.equals( text ) ) {
        result = text;
      }
    }
    return result;
  }
  
  ////////////////////////
  // Bounds and visibility
  
  /**
  * Returns <code>true</code> if the item will be rendered in the visible area of the CTabFolder. Returns false otherwise.
  * 
  *  @return <code>true</code> if the item will be rendered in the visible area of the CTabFolder. Returns false otherwise.
  * 
  *  @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   * 
  * @since 1.0
  */
  public boolean isShowing() {
    checkWidget();
    return showing;
  }
  
  /**
   * Returns a rectangle describing the receiver's size and location
   * relative to its parent.
   *
   * @return the receiver's bounding column rectangle
   *
   * @exception SWTException <ul>
   *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
   *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
   * </ul>
   */
  public Rectangle getBounds() {
    checkWidget();
    return new Rectangle( x, y, width, height );
  }
  
  ///////////////////
  // Widget overrides

  protected void releaseChildren() {
    // do nothing
  }

  protected void releaseParent() {
    parent.destroyItem( this );
  }

  protected void releaseWidget() {
    // do nothing
  }
  
  ///////////////////////////////////////////////////////////////////////
  // Helping methods used by CTabFolder to control item size and location

  int preferredHeight() {
    return 20;
  }
  
  int preferredWidth( final boolean isSelected, final boolean minimum ) {
    int result = 0;
    if( !isDisposed() ) {
      int width = 0;
      Image image = getImage();
      if( image != null ) {
        // TODO [rh] determine bounds for image
//        width += image.getBounds().width;
        width += 20;
      }
      String text = null;
      if( minimum ) {
        int minChars = parent.getMinimumCharacters();
        text = minChars == 0 ? null : getText();
        if( text != null && text.length() > minChars ) {
          int end = minChars < ELLIPSIS.length() + 1 
                  ? minChars 
                  : minChars - ELLIPSIS.length();
          text = text.substring( 0, end );
          if( minChars > ELLIPSIS.length() + 1 ) {
            text += ELLIPSIS;
          }
        }
      } else {
        text = getText();
      }
      if( text != null ) {
        if( width > 0 ) {
          width += INTERNAL_SPACING;
        }
        width += FontSizeEstimation.stringExtent( text, getFont() ).x;
      }
      if( canClose() && ( isSelected || parent.getUnselectedCloseVisible() ) ) {
        if( result > 0 ) {
          result += INTERNAL_SPACING;
        }
        result += CTabFolder.BUTTON_SIZE;
      }
      result = width + LEFT_MARGIN + RIGHT_MARGIN;
    }
    return result;
  }

  boolean canClose() {
    return   ( parent.getStyle() & SWT.CLOSE ) != 0 
          || ( getStyle() & SWT.CLOSE ) != 0;
  }

  //////////////////
  // Helping methods
  
  private static CTabFolder checkNull( final CTabFolder parent ) {
    if( parent == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return parent;
  }

  private static int checkStyle( final int style ) {
    int result = SWT.NONE;
    if( ( style & SWT.CLOSE ) != 0 ) {
      result = SWT.CLOSE;
    }
    return result;
  }
}
