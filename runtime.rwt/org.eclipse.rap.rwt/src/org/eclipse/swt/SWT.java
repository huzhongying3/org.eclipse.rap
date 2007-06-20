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
package org.eclipse.swt;



/**
 * This class provides access to a small number of SWT system-wide
 * methods, and in addition defines the public constants provided
 * by SWT.
 * <p>
 * By defining constants like UP and DOWN in a single class, SWT
 * can share common names and concepts at the same time minimizing
 * the number of classes, names and constants for the application
 * programmer.
 * </p><p>
 * Note that some of the constants provided by this class represent
 * optional, appearance related aspects of widgets which are available
 * either only on some window systems, or for a differing set of
 * widgets on each window system. These constants are marked
 * as <em>HINT</em>s. The set of widgets which support a particular
 * <em>HINT</em> may change from release to release, although we typically
 * will not withdraw support for a <em>HINT</em> once it is made available.
 * </p>
 */
// TODO [doc] Add javadoc for fields
public class SWT {
  
  public static final int NULL = 0;
  public static final int NONE = 0;

  /**
   * The move event type (value is 10).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Event
   * @see org.eclipse.swt.widgets.Control#addControlListener
   * @see org.eclipse.swt.widgets.TableColumn#addControlListener
   * @see org.eclipse.swt.widgets.Tracker#addControlListener
   * @see org.eclipse.swt.widgets.TreeColumn#addControlListener
   * @see org.eclipse.swt.events.ControlListener#controlMoved
   * @see org.eclipse.swt.events.ControlEvent
   */
  public static final int Move = 10;

  /**
   * The resize event type (value is 11).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Event
   * @see org.eclipse.swt.widgets.Control#addControlListener
   * @see org.eclipse.swt.widgets.TableColumn#addControlListener
   * @see org.eclipse.swt.widgets.Tracker#addControlListener
   * @see org.eclipse.swt.widgets.TreeColumn#addControlListener
   * @see org.eclipse.swt.events.ControlListener#controlResized
   * @see org.eclipse.swt.events.ControlEvent
   */
  public static final int Resize = 11;

  /**
   * The dispose event type (value is 12).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Display#addListener
   * @see org.eclipse.swt.widgets.Event
   * @see org.eclipse.swt.widgets.Widget#addDisposeListener
   * @see org.eclipse.swt.events.DisposeListener#widgetDisposed
   * @see org.eclipse.swt.events.DisposeEvent
   */
  public static final int Dispose = 12;
  
  /**
   * The selection event type (value is 13).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Event
   * @see org.eclipse.swt.widgets.Button#addSelectionListener
   * @see org.eclipse.swt.widgets.Combo#addSelectionListener
   * @see org.eclipse.swt.widgets.CoolItem#addSelectionListener
   * @see org.eclipse.swt.widgets.Link#addSelectionListener
   * @see org.eclipse.swt.widgets.List#addSelectionListener
   * @see org.eclipse.swt.widgets.MenuItem#addSelectionListener
   * @see org.eclipse.swt.widgets.Sash#addSelectionListener
   * @see org.eclipse.swt.widgets.Scale#addSelectionListener
   * @see org.eclipse.swt.widgets.ScrollBar#addSelectionListener
   * @see org.eclipse.swt.widgets.Slider#addSelectionListener
   * @see org.eclipse.swt.widgets.TabFolder#addSelectionListener
   * @see org.eclipse.swt.widgets.Table#addSelectionListener
   * @see org.eclipse.swt.widgets.TableColumn#addSelectionListener
   * @see org.eclipse.swt.widgets.ToolItem#addSelectionListener
   * @see org.eclipse.swt.widgets.TrayItem#addSelectionListener
   * @see org.eclipse.swt.widgets.Tree#addSelectionListener
   * @see org.eclipse.swt.widgets.TreeColumn#addSelectionListener
   * @see org.eclipse.swt.events.SelectionListener#widgetSelected
   * @see org.eclipse.swt.events.SelectionEvent
   */
  public static final int Selection = 13;

  /**
   * The default selection event type (value is 14).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Event
   * @see org.eclipse.swt.widgets.Combo#addSelectionListener
   * @see org.eclipse.swt.widgets.List#addSelectionListener
   * @see org.eclipse.swt.widgets.Spinner#addSelectionListener
   * @see org.eclipse.swt.widgets.Table#addSelectionListener
   * @see org.eclipse.swt.widgets.Text#addSelectionListener
   * @see org.eclipse.swt.widgets.TrayItem#addSelectionListener
   * @see org.eclipse.swt.widgets.Tree#addSelectionListener
   * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected
   * @see org.eclipse.swt.events.SelectionEvent
   */
  public static final int DefaultSelection = 14;
  
  /**
   * The focus in event type (value is 15).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Event
   * @see org.eclipse.swt.widgets.Control#addFocusListener
   * @see org.eclipse.swt.events.FocusListener#focusGained
   * @see org.eclipse.swt.events.FocusEvent
   */
  public static final int FocusIn = 15;
  
  /**
   * The focus out event type (value is 16).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Event
   * @see org.eclipse.swt.widgets.Control#addFocusListener
   * @see org.eclipse.swt.events.FocusListener#focusLost
   * @see org.eclipse.swt.events.FocusEvent
   */
  public static final int FocusOut = 16;
  
  /**
   * The expand event type (value is 17).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Event
   * @see org.eclipse.swt.widgets.Tree#addTreeListener
   * @see org.eclipse.swt.events.TreeListener#treeExpanded
   * @see org.eclipse.swt.events.TreeEvent
   */
  public static final int Expand = 17;
  
  /**
   * The collapse event type (value is 18).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Event
   * @see org.eclipse.swt.widgets.Tree#addTreeListener
   * @see org.eclipse.swt.events.TreeListener#treeCollapsed
   * @see org.eclipse.swt.events.TreeEvent
   */
  public static final int Collapse = 18;

  /**
   * The close event type (value is 21).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Display#addListener
   * @see org.eclipse.swt.widgets.Event
   * @see org.eclipse.swt.widgets.Shell#addShellListener
   * @see org.eclipse.swt.events.ShellListener#shellClosed
   * @see org.eclipse.swt.events.ShellEvent
   */
  public static final int Close = 21;
  
  /**
   * The show event type (value is 22).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Event
   * @see org.eclipse.swt.widgets.Menu#addMenuListener
   * @see org.eclipse.swt.events.MenuListener#menuShown
   * @see org.eclipse.swt.events.MenuEvent
   */
  public static final int Show = 22;
  
  /**
   * The hide event type (value is 23).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Event
   * @see org.eclipse.swt.widgets.Menu#addMenuListener
   * @see org.eclipse.swt.events.MenuListener#menuHidden
   * @see org.eclipse.swt.events.MenuEvent
   */
  public static final int Hide = 23;

  /**
   * The modify event type (value is 24).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Event
   * @see org.eclipse.swt.widgets.Combo#addModifyListener
   * @see org.eclipse.swt.widgets.Spinner#addModifyListener
   * @see org.eclipse.swt.widgets.Text#addModifyListener
   * @see org.eclipse.swt.events.ModifyListener#modifyText
   * @see org.eclipse.swt.events.ModifyEvent
   */
  public static final int Modify = 24;

  /**
   * The activate event type (value is 26).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Event
   * @see org.eclipse.swt.widgets.Shell#addShellListener
   * @see org.eclipse.swt.events.ShellListener#shellActivated
   * @see org.eclipse.swt.events.ShellEvent
   */
  public static final int Activate = 26;
 
  /**
   * The deactivate event type (value is 27).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Event
   * @see org.eclipse.swt.widgets.Shell#addShellListener
   * @see org.eclipse.swt.events.ShellListener#shellDeactivated
   * @see org.eclipse.swt.events.ShellEvent
   */
  public static final int Deactivate = 27;
  
  /**
   * The set data event type (value is 36).
   * 
   * @see org.eclipse.swt.widgets.Widget#addListener
   * @see org.eclipse.swt.widgets.Event
   * 
   * @see org.eclipse.swt.widgets.Table
   * <!-- @see org.eclipse.swt.widgets.Tree -->
   */
  public static final int SetData = 36;

  
  public static final int DEFAULT = -1;
  public static final int HORIZONTAL = 1 << 8;
  public static final int VERTICAL = 1 << 9;
  public static final int CENTER = 1 << 24;
  public static final int UP = 1 << 7;
  public static final int TOP = UP;
  public static final int DOWN = 1 << 10;
  public static final int BOTTOM = DOWN;
  public static final int LEAD = 1 << 14;
  public static final int LEFT = LEAD;
  public static final int TRAIL = 1 << 17;
  public static final int RIGHT = TRAIL;
  public static final int BEGINNING = 1;
  public static final int FILL = 4;
  public static final int KEYCODE_BIT = ( 1 << 24 );
  public static final int END = KEYCODE_BIT + 8;
  public static final int SEPARATOR = 1 << 1;
  public static final int PUSH = 1 << 3;
  public static final int RADIO = 1 << 4;
  public static final int CHECK = 1 << 5;
  public static final int ARROW = 1 << 2;
  public static final int TOGGLE = 1 << 1;
  public static final int BORDER = 1 << 11;
  public static final int FLAT = 1 << 23;
  public static final int NO_FOCUS = 1 << 19;
  public static final int H_SCROLL = 1 << 8;
  public static final int V_SCROLL = 1 << 9;
  public static final int READ_ONLY = 1 << 3;
  
  // Combo flags
  public static final int SIMPLE = 1 << 6;

  // menu flags
  public static final int BAR = 1 << 1;
  public static final int POP_UP = 1 << 3;
  public static final int DROP_DOWN = 1 << 2;
  public static final int CASCADE = 1 << 6;
  
  // text flags
  public static final int SINGLE = 1 << 2;
  public static final int MULTI = 1 << 1;
  public static final int WRAP = 1 << 6;
  public static final int PASSWORD = 1 << 22;
  

  // Shells
  public static final int NO_TRIM = 1 << 3;
  public static final int RESIZE = 1 << 4;
  public static final int TITLE = 1 << 5;
  public static final int CLOSE = 1 << 6;
  public static final int MIN = 1 << 7;
  public static final int MAX = 1 << 10;
  public static final int ON_TOP = 1 << 14;
  public static final int SHELL_TRIM = CLOSE | TITLE | MIN | MAX | RESIZE;
  public static final int DIALOG_TRIM = TITLE | CLOSE | BORDER;
  public static final int APPLICATION_MODAL = 1 << 16;

  public static final int SHADOW_IN = 1 << 2;
  public static final int SHADOW_OUT = 1 << 3;
  public static final int SHADOW_NONE = 1 << 5;
  
  /**
   * Style constant for progress bar behavior (value is 1&lt;&lt;1).
   * <p><b>Used By:</b><ul>
   * <li><code>ProgressBar</code></li>
   * </ul></p>
   */
  public static final int INDETERMINATE = 1 << 1;

  /**
   * Style constant to allow virtual data (value is 1&lt;&lt;28).
   * <p><b>Used By:</b><ul>
   * <li><code>Table</code></li>
   * <!-- <li><code>Tree</code></li> -->
   * </ul></p>
   */
  // TODO [rh] uncomment when Tree has support for VIRTUAL
  public static final int VIRTUAL = 1 << 28;

  // Dialog Icons

  /**
   * The <code>MessageBox</code> style constant for error icon
   * behavior (value is 1).
   */
  public static final int ICON_ERROR = 1;

  /**
   * The <code>MessageBox</code> style constant for information icon
   * behavior (value is 1&lt;&lt;1).
   */
  public static final int ICON_INFORMATION = 1 << 1;

  /**
   * The <code>MessageBox</code> style constant for question icon
   * behavior (value is 1&lt;&lt;2).
   */
  public static final int ICON_QUESTION = 1 << 2;

  /**
   * The <code>MessageBox</code> style constant for warning icon
   * behavior (value is 1&lt;&lt;3).
   */
  public static final int ICON_WARNING = 1 << 3;

  /**
   * The <code>MessageBox</code> style constant for "working" icon
   * behavior (value is 1&lt;&lt;4).
   */
  public static final int ICON_WORKING = 1 << 4;

  // Font style constants
  /**
   * The font style constant indicating a normal weight, non-italic font
   * (value is 0).
   */
  public static final int NORMAL = 0;
  
  /**
   * The font style constant indicating a bold weight font
   * (value is 1&lt;&lt;0).
   */
  public static final int BOLD = 1 << 0;
  
  /**
   * The font style constant indicating an italic font
   * (value is 1&lt;&lt;1).
   */
  public static final int ITALIC = 1 << 1;

  // Color constants
  
  /**
   * Default color white (value is 1).
   */
  public static final int COLOR_WHITE = 1;

  /**
   * Default color black (value is 2).
   */
  public static final int COLOR_BLACK = 2;

  /**
   * Default color red (value is 3).
   */
  public static final int COLOR_RED = 3;

  /**
   * Default color dark red (value is 4).
   */
  public static final int COLOR_DARK_RED = 4;

  /**
   * Default color green (value is 5).
   */
  public static final int COLOR_GREEN = 5;

  /**
   * Default color dark green (value is 6).
   */
  public static final int COLOR_DARK_GREEN = 6;

  /**
   * Default color yellow (value is 7).
   */
  public static final int COLOR_YELLOW = 7;

  /**
   * Default color dark yellow (value is 8).
   */
  public static final int COLOR_DARK_YELLOW = 8;

  /**
   * Default color blue (value is 9).
   */
  public static final int COLOR_BLUE = 9;

  /**
   * Default color dark blue (value is 10).
   */
  public static final int COLOR_DARK_BLUE = 10;

  /**
   * Default color magenta (value is 11).
   */
  public static final int COLOR_MAGENTA = 11;

  /**
   * Default color dark magenta (value is 12).
   */
  public static final int COLOR_DARK_MAGENTA = 12;

  /**
   * Default color cyan (value is 13).
   */
  public static final int COLOR_CYAN = 13;

  /**
   * Default color dark cyan (value is 14).
   */
  public static final int COLOR_DARK_CYAN = 14;

  /**
   * Default color gray (value is 15).
   */
  public static final int COLOR_GRAY = 15;

  /**
   * Default color dark gray (value is 16).
   */
  public static final int COLOR_DARK_GRAY = 16;
  
  // OS color constants
  
  /**
   * System color used to paint dark shadow areas (value is 17).
   */
  public static final int COLOR_WIDGET_DARK_SHADOW = 17;

  /**
   * System color used to paint normal shadow areas (value is 18).
   */
  public static final int COLOR_WIDGET_NORMAL_SHADOW = 18;

  /**
   * System color used to paint light shadow areas (value is 19).
   */
  public static final int COLOR_WIDGET_LIGHT_SHADOW = 19;

  /**
   * System color used to paint highlight shadow areas (value is 20).
   */
  public static final int COLOR_WIDGET_HIGHLIGHT_SHADOW = 20;

  /**
   * System color used to paint foreground areas (value is 21).
   */
  public static final int COLOR_WIDGET_FOREGROUND = 21;

  /**
   * System color used to paint background areas (value is 22).
   */
  public static final int COLOR_WIDGET_BACKGROUND = 22;

  /**
   * System color used to paint border areas (value is 23).
   */
  public static final int COLOR_WIDGET_BORDER = 23;

  /**
   * System color used to paint list foreground areas (value is 24).
   */
  public static final int COLOR_LIST_FOREGROUND = 24;

  /**
   * System color used to paint list background areas (value is 25).
   */
  public static final int COLOR_LIST_BACKGROUND = 25;

  /**
   * System color used to paint list selection background areas (value is 26).
   */
  public static final int COLOR_LIST_SELECTION = 26;

  /**
   * System color used to paint list selected text (value is 27).
   */
  public static final int COLOR_LIST_SELECTION_TEXT = 27;

  /**
   * System color used to paint tooltip text (value is 28).
   */
  public static final int COLOR_INFO_FOREGROUND = 28;

  /**
   * System color used to paint tooltip background areas (value is 29).
   */
  public static final int COLOR_INFO_BACKGROUND = 29;
  
  /**
   * System color used to paint title text (value is 30).
   */
  public static final int COLOR_TITLE_FOREGROUND = 30;

  /**
   * System color used to paint title background areas (value is 31).
   */
  public static final int COLOR_TITLE_BACKGROUND = 31;

  /**
   * System color used to paint title background gradient (value is 32).
   */
  public static final int COLOR_TITLE_BACKGROUND_GRADIENT = 32;
  
  /**
   * System color used to paint inactive title text (value is 33).
   */
  public static final int COLOR_TITLE_INACTIVE_FOREGROUND = 33;

  /**
   * System color used to paint inactive title background areas (value is 34).
   */
  public static final int COLOR_TITLE_INACTIVE_BACKGROUND = 34;

  /**
   * System color used to paint inactive title background gradient (value is 35).
   */
  public static final int COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT = 35;
  
  // Error codes
  public static final int ERROR_UNSPECIFIED = 1;
  public static final int ERROR_NO_HANDLES = 2;
  public static final int ERROR_NO_MORE_CALLBACKS = 3;
  public static final int ERROR_NULL_ARGUMENT = 4;
  public static final int ERROR_INVALID_ARGUMENT = 5;
  public static final int ERROR_INVALID_RANGE = 6;
  public static final int ERROR_CANNOT_BE_ZERO = 7;
  public static final int ERROR_CANNOT_GET_ITEM = 8;
  public static final int ERROR_CANNOT_GET_SELECTION = 9;
  public static final int ERROR_CANNOT_INVERT_MATRIX = 10;
  public static final int ERROR_CANNOT_GET_ITEM_HEIGHT = 11;
  public static final int ERROR_CANNOT_GET_TEXT = 12;
  public static final int ERROR_CANNOT_SET_TEXT = 13;
  public static final int ERROR_ITEM_NOT_ADDED = 14;
  public static final int ERROR_ITEM_NOT_REMOVED = 15;
  public static final int ERROR_NO_GRAPHICS_LIBRARY = 16;
  public static final int ERROR_NOT_IMPLEMENTED = 20;
  public static final int ERROR_MENU_NOT_DROP_DOWN = 21;
  public static final int ERROR_THREAD_INVALID_ACCESS = 22;
  public static final int ERROR_WIDGET_DISPOSED = 24;
  public static final int ERROR_MENUITEM_NOT_CASCADE = 27;
  public static final int ERROR_CANNOT_SET_SELECTION = 28;
  public static final int ERROR_CANNOT_SET_MENU = 29;
  public static final int ERROR_CANNOT_SET_ENABLED = 30;
  public static final int ERROR_CANNOT_GET_ENABLED = 31;
  public static final int ERROR_INVALID_PARENT = 32;
  public static final int ERROR_MENU_NOT_BAR = 33;
  public static final int ERROR_CANNOT_GET_COUNT = 36;
  public static final int ERROR_MENU_NOT_POP_UP = 37;
  public static final int ERROR_UNSUPPORTED_DEPTH = 38;
  public static final int ERROR_IO = 39;
  public static final int ERROR_INVALID_IMAGE = 40;
  public static final int ERROR_UNSUPPORTED_FORMAT = 42;
  public static final int ERROR_INVALID_SUBCLASS = 43;
  public static final int ERROR_GRAPHIC_DISPOSED = 44;
  public static final int ERROR_DEVICE_DISPOSED = 45;
  public static final int ERROR_FAILED_EXEC = 46;
  public static final int ERROR_FAILED_LOAD_LIBRARY = 47;
  public static final int ERROR_INVALID_FONT = 48;
  public static final int ALT = 1 << 16;

  public static final int SHIFT = 1 << 17;
  public static final int CTRL = 1 << 18;
  public static final int CONTROL = CTRL;
  public static final int COMMAND = 1 << 22;

  public static void error( final int code ) {
    error( code, null );
  }
  
  public static void error( final int code, final Throwable throwable ) {
    error( code, throwable, null );
  }

  public static void error( final int code, 
                            final Throwable throwable, 
                            final String detail ) 
  {
    /*
    * This code prevents the creation of "chains" of SWTErrors and
    * SWTExceptions which in turn contain other SWTErrors and 
    * SWTExceptions as their throwable. This can occur when low level
    * code throws an exception past a point where a higher layer is
    * being "safe" and catching all exceptions. (Note that, this is
    * _a_bad_thing_ which we always try to avoid.)
    *
    * On the theory that the low level code is closest to the
    * original problem, we simply re-throw the original exception here.
    */
    if( throwable instanceof SWTError ) {
      throw ( SWTError )throwable;
    }
    if( throwable instanceof SWTException ) {
      throw ( SWTException )throwable;
    }
    String message = findErrorText( code );
    if( detail != null ) {
      message += detail;
    }
    switch( code ) {
      /* Null Arguments (non-fatal) */
      case ERROR_NULL_ARGUMENT:
        throw new NullPointerException( message );
      /* Illegal Arguments (non-fatal) */
      case ERROR_CANNOT_BE_ZERO:
      case ERROR_INVALID_ARGUMENT:
      case ERROR_MENU_NOT_BAR:
      case ERROR_MENU_NOT_DROP_DOWN:
      case ERROR_MENU_NOT_POP_UP:
      case ERROR_MENUITEM_NOT_CASCADE:
      case ERROR_INVALID_PARENT:
      case ERROR_INVALID_RANGE:
        throw new IllegalArgumentException( message );
        /* SWT Exceptions (non-fatal) */
      case ERROR_INVALID_SUBCLASS:
      case ERROR_THREAD_INVALID_ACCESS:
      case ERROR_WIDGET_DISPOSED:
      case ERROR_GRAPHIC_DISPOSED:
      case ERROR_DEVICE_DISPOSED:
      case ERROR_INVALID_IMAGE:
      case ERROR_UNSUPPORTED_DEPTH:
      case ERROR_UNSUPPORTED_FORMAT:
      case ERROR_FAILED_EXEC:
      case ERROR_CANNOT_INVERT_MATRIX:
      case ERROR_NO_GRAPHICS_LIBRARY:
      case ERROR_IO:
        SWTException exception = new SWTException( code, message );
        exception.throwable = throwable;
        throw exception;
      /* Operation System Errors (fatal, may occur only on some platforms) */
      case ERROR_CANNOT_GET_COUNT:
      case ERROR_CANNOT_GET_ENABLED:
      case ERROR_CANNOT_GET_ITEM:
      case ERROR_CANNOT_GET_ITEM_HEIGHT:
      case ERROR_CANNOT_GET_SELECTION:
      case ERROR_CANNOT_GET_TEXT:
      case ERROR_CANNOT_SET_ENABLED:
      case ERROR_CANNOT_SET_MENU:
      case ERROR_CANNOT_SET_SELECTION:
      case ERROR_CANNOT_SET_TEXT:
      case ERROR_ITEM_NOT_ADDED:
      case ERROR_ITEM_NOT_REMOVED:
      case ERROR_NO_HANDLES:
      // FALL THROUGH
      /* SWT Errors (fatal, may occur only on some platforms) */
      case ERROR_FAILED_LOAD_LIBRARY:
      case ERROR_NO_MORE_CALLBACKS:
      case ERROR_NOT_IMPLEMENTED:
      case ERROR_UNSPECIFIED: {
        SWTError error = new SWTError( code, message );
        error.throwable = throwable;
        throw error;
      }
    }
    /* Unknown/Undefined Error */
    SWTError error = new SWTError( code, message );
    error.throwable = throwable;
    throw error;
  }

  static String findErrorText( final int code ) {
    String result;
    switch( code ) {
      case ERROR_UNSPECIFIED:
        result = "Unspecified error"; //$NON-NLS-1$
      break;
      case ERROR_NO_HANDLES:
        result = "No more handles"; //$NON-NLS-1$
      break;
      case ERROR_NO_MORE_CALLBACKS:
        result = "No more callbacks"; //$NON-NLS-1$
      break;
      case ERROR_NULL_ARGUMENT:
        result = "Argument cannot be null"; //$NON-NLS-1$
      break;
      case ERROR_INVALID_ARGUMENT:
        result = "Argument not valid"; //$NON-NLS-1$
      break;
      case ERROR_INVALID_RANGE:
        result = "Index out of bounds"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_BE_ZERO:
        result = "Argument cannot be zero"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_GET_ITEM:
        result = "Cannot get item"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_GET_SELECTION:
        result = "Cannot get selection"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_GET_ITEM_HEIGHT:
        result = "Cannot get item height"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_GET_TEXT:
        result = "Cannot get text"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_SET_TEXT:
        result = "Cannot set text"; //$NON-NLS-1$
      break;
      case ERROR_ITEM_NOT_ADDED:
        result = "Item not added"; //$NON-NLS-1$
      break;
      case ERROR_ITEM_NOT_REMOVED:
        result = "Item not removed"; //$NON-NLS-1$
      break;
      case ERROR_NOT_IMPLEMENTED:
        result = "Not implemented"; //$NON-NLS-1$
      break;
      case ERROR_MENU_NOT_DROP_DOWN:
        result = "Menu must be a drop down"; //$NON-NLS-1$
      break;
      case ERROR_THREAD_INVALID_ACCESS:
        result = "Invalid thread access"; //$NON-NLS-1$
      break;
      case ERROR_WIDGET_DISPOSED:
        result = "Widget is disposed"; //$NON-NLS-1$
      break;
      case ERROR_MENUITEM_NOT_CASCADE:
        result = "Menu item is not a CASCADE"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_SET_SELECTION:
        result = "Cannot set selection"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_SET_MENU:
        result = "Cannot set menu"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_SET_ENABLED:
        result = "Cannot set the enabled state"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_GET_ENABLED:
        result = "Cannot get the enabled state"; //$NON-NLS-1$
      break;
      case ERROR_INVALID_PARENT:
        result = "Widget has the wrong parent"; //$NON-NLS-1$
      break;
      case ERROR_MENU_NOT_BAR:
        result = "Menu is not a BAR"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_GET_COUNT:
        result = "Cannot get count"; //$NON-NLS-1$
      break;
      case ERROR_MENU_NOT_POP_UP:
        result = "Menu is not a POP_UP"; //$NON-NLS-1$
      break;
      case ERROR_UNSUPPORTED_DEPTH:
        result = "Unsupported color depth"; //$NON-NLS-1$
      break;
      case ERROR_IO:
        result = "i/o error"; //$NON-NLS-1$
      break;
      case ERROR_INVALID_IMAGE:
        result = "Invalid image"; //$NON-NLS-1$
      break;
      case ERROR_UNSUPPORTED_FORMAT:
        result = "Unsupported or unrecognized format"; //$NON-NLS-1$
      break;
      case ERROR_INVALID_SUBCLASS:
        result = "Subclassing not allowed"; //$NON-NLS-1$
      break;
      case ERROR_GRAPHIC_DISPOSED:
        result = "Graphic is disposed"; //$NON-NLS-1$
      break;
      case ERROR_DEVICE_DISPOSED:
        result = "Device is disposed"; //$NON-NLS-1$
      break;
      case ERROR_FAILED_EXEC:
        result = "Failed to execute runnable"; //$NON-NLS-1$
      break;
      case ERROR_FAILED_LOAD_LIBRARY:
        result = "Unable to load library"; //$NON-NLS-1$
      break;
      case ERROR_CANNOT_INVERT_MATRIX:
        result = "Cannot invert matrix"; //$NON-NLS-1$
      break;
      case ERROR_NO_GRAPHICS_LIBRARY:
        result = "Unable to load graphics library"; //$NON-NLS-1$
      break;
      case ERROR_INVALID_FONT:
        result = "Font not valid"; //$NON-NLS-1$
      break;
      default:
        result = "Unknown error"; //$NON-NLS-1$
    }
    return result;
  }  
}
