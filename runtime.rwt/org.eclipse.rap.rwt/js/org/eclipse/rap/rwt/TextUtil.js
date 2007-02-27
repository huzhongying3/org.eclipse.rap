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

qx.OO.defineClass( "org.eclipse.rap.rwt.TextUtil" );

///////////////////////////////////////////////////////////////
// Functions for ModifyEvents and maintenance of the text/value

/**
 * This function gets assigned to the 'keyup' event of a text widget.
 */
org.eclipse.rap.rwt.TextUtil.modifyText = function( evt ) {
  var text = evt.getTarget();
  if(    !org_eclipse_rap_rwt_EventUtil_suspend 
      && org.eclipse.rap.rwt.TextUtil._isModifyingKey( evt.getKeyIdentifier() ) )
  {
    // if not yet done, register an event listener that adds a request param
    // with the text widgets' content just before the request is sent
    if( !org.eclipse.rap.rwt.TextUtil._isModified( text ) ) {
      var req = org.eclipse.rap.rwt.Request.getInstance();
      req.addEventListener( "send", org.eclipse.rap.rwt.TextUtil._onSend, text );
      org.eclipse.rap.rwt.TextUtil._setModified( text, true );
    }
  }
  org.eclipse.rap.rwt.TextUtil.updateSelection( text );
}

/**
 * This function gets assigned to the 'keyup' event of a text widget if there
 * was a server-side ModifyListener registered.
 */
org.eclipse.rap.rwt.TextUtil.modifyTextAction = function( evt ) {
  var text = evt.getTarget();
  if(    !org_eclipse_rap_rwt_EventUtil_suspend 
      && !org.eclipse.rap.rwt.TextUtil._isModified( text ) 
      && org.eclipse.rap.rwt.TextUtil._isModifyingKey( evt.getKeyIdentifier() ) )
  {
    var req = org.eclipse.rap.rwt.Request.getInstance();
    // Register 'send'-listener that adds a request param with current text
    if( !org.eclipse.rap.rwt.TextUtil._isModified( text ) ) {
      req.addEventListener( "send", org.eclipse.rap.rwt.TextUtil._onSend, text );
      org.eclipse.rap.rwt.TextUtil._setModified( text, true );
    }
    // add modifyText-event with sender-id to request parameters
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( text );
    req.addEvent( "org.eclipse.rap.rwt.events.modifyText", id );
    // register listener that is notified when a request is sent
    qx.client.Timer.once( org.eclipse.rap.rwt.TextUtil._delayedModifyText, 
                          text, 
                          500 );
  }
  org.eclipse.rap.rwt.TextUtil.updateSelection( text );
};

/**
 * This function gets assigned to the 'blur' event of a text widget if there
 * was a server-side ModifyListener registered.
 */
org.eclipse.rap.rwt.TextUtil.modifyTextOnBlur = function( evt ) {
  if(    !org_eclipse_rap_rwt_EventUtil_suspend 
      && org.eclipse.rap.rwt.TextUtil._isModified( evt.getTarget() ) )
  {
    var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
    var id = widgetManager.findIdByWidget( evt.getTarget() );
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.addEvent( "org.eclipse.rap.rwt.events.modifyText", id );
    req.send();
  }
}

org.eclipse.rap.rwt.TextUtil._onSend = function( evt ) {
  // NOTE: 'this' references the text widget
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var id = widgetManager.findIdByWidget( this );
  var req = org.eclipse.rap.rwt.Request.getInstance();
  req.addParameter( id + ".text", this.getComputedValue() );
  // remove the _onSend listener and change the text widget state to 'unmodified'
  req.removeEventListener( "send", org.eclipse.rap.rwt.TextUtil._onSend, this );
  org.eclipse.rap.rwt.TextUtil._setModified( this, false );
  // Update the value property (which is qooxdoo-wise only updated on 
  // focus-lost) to be in sync with server-side
  if( this.getFocused() ) {
    this.setValue( this.getComputedValue() );
  }
}

org.eclipse.rap.rwt.TextUtil._delayedModifyText = function( evt ) {
  // NOTE: this references the text widget (see qx.client.Timer.once above)
  if( org.eclipse.rap.rwt.TextUtil._isModified( this ) ) {
    var req = org.eclipse.rap.rwt.Request.getInstance();
    req.send();
  }
}

org.eclipse.rap.rwt.TextUtil._isModified = function( widget ) {
  return widget.getUserData( "modified" ) == true;
}

org.eclipse.rap.rwt.TextUtil._setModified = function( widget, modified ) {
  return widget.setUserData( "modified", modified );
}

/**
 * Determines whether the given keyIdentifier potentially
 * modifies the content of a text widget.
 */
org.eclipse.rap.rwt.TextUtil._isModifyingKey = function( keyIdentifier ) {
  var result = false;
  switch( keyIdentifier ) {
    // Modifier keys
    case "Shift":
    case "Control":
    case "Alt":
    case "Meta":
    case "Win":
    // Navigation keys
    case "Up":
    case "Down":
    case "Left":
    case "Right":
    case "Home":
    case "End":
    case "PageUp":
    case "PageDown":
    case "Tab":
    // Context menu key
    case "Apps":
    //
    case "Escape":
    case "Insert":
    case "Enter":
    // 
    case "CapsLock":
    case "NumLock":
    case "Scroll":
    case "PrintScreen":
    // Function keys 1 - 12 
    case "F1":
    case "F2":
    case "F3":
    case "F4":
    case "F5":
    case "F6":
    case "F7":
    case "F8":
    case "F9":
    case "F10":
    case "F11":
    case "F12":
      break;

    default:
      result = true;
  }
  return result;
}

///////////////////////////////////////////////////////////////////
// Functions to maintain the selection-start and -length properties

org.eclipse.rap.rwt.TextUtil.onMouseUp = function( evt ) {
  if( !org_eclipse_rap_rwt_EventUtil_suspend ) {
    org.eclipse.rap.rwt.TextUtil.updateSelection( evt.getTarget() );
  }
}

org.eclipse.rap.rwt.TextUtil.updateSelection = function( text ) {
  // TODO [rh] executing the code below for a TextArea leads to Illegal Argument
  if( text.classname != "qx.ui.form.TextArea" ) { 
    var start = text.getSelectionStart();
    var length = text.getSelectionLength();
    if( text.getUserData( "selectionStart" ) != start ) {
      text.setUserData( "selectionStart", start ); 
      org.eclipse.rap.rwt.TextUtil._setPropertyParam( text, "selectionStart", start );
    }
    if( text.getUserData( "selectionLength" ) != length ) {
      text.setUserData( "selectionLength", length );
      org.eclipse.rap.rwt.TextUtil._setPropertyParam( text, "selectionCount", length );
    }
  }
}

org.eclipse.rap.rwt.TextUtil._setPropertyParam = function( widget, name, value ) {
  var widgetManager = org.eclipse.rap.rwt.WidgetManager.getInstance();
  var id = widgetManager.findIdByWidget( widget );
  var req = org.eclipse.rap.rwt.Request.getInstance();
  req.addParameter( id + "." + name, value );
}

org.eclipse.rap.rwt.TextUtil.setSelection = function( text, start, length ) {
  text.setUserData( "selectionStart", start );
  text.getSelectionStart( start );
  text.setUserData( "selectionLength", length );
  text.getSelectionLength( length );
}
