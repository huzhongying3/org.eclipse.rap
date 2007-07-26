//<!--
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

  // the event handler prototype
  function EventHandler() {
    
    /* **************************************************** *
     * declare member variables of the EventHandler object. *
     * **************************************************** */
    
    /* a value representing a not-set value on a form element;
     * we cannot use null or empty string because of browser
     * incompatibilities (Opera). */
    this.W4T_NULL = 'W4T_NULL';
    this.requestDelay = 50;


    /* the actual position of the horizontal 
     * scrollbar in the browser window. */
    this.scrollX = 0;
    /* the actual position of the vertical
     * scrollbar in the browser window. */
    this.scrollY = 0;
    
    // needed for itemStateChanged; must check, whether value
    // of the element which had focus lastly has changed 
    // (no submit, if not)
    this.focusElementValue = this.W4T_NULL;
    /* Flag: tells whether the focus listener is disabled.
     * This is only needed on loading the page, when some element
     * gets the focus and must be prevented from submitting a
     * focusGained event (see enableFocus below). */
    this.focusDisabledFlag = false;    
    // TODO: comment
    this.hasAlreadyFocus = false;

    // TODO: comment
    this.submitFlag = false;
    // TODO: comment
    this.suspendSubmitFlag = false;

    /* Indicates whether an AJAX request is in process or not */
    this.isRequestRunning = false;
    
    /* stores a reference to a checkbox, if one had 
     * a mouseDown event (and no mouseUp event yet). */
    this.clickedCheckBox = null;

    /* stores the keycode of a pressed key. */
    this.downKey = 0;
    /* stores a temporary reference to a checkbox. */
    this.checkedElement = null;

    /* **************************************************** *
     * declare member functions of the EventHandler object. *
     * **************************************************** */

    /* event trigger for webActionPerformed. */
    this.webActionPerformed = webActionPerformed;   
    
    /* event trigger for webItemStateChanged. */
    this.webItemStateChanged = webItemStateChanged; 
    
    /* event trigger for webFocusGained. */
    this.webFocusGained = webFocusGained;

    /* event trigger for webTreeNodeExpanded. */
    this.webTreeNodeExpanded = ev_webTreeNodeExpanded;
    
    /* event trigger for webTreeNodeCollapseded. */
    this.webTreeNodeCollapsed = ev_webTreeNodeCollapsed;
    
    
    /* resets all form elements on the displayed
     * html page. */
    this.resetForm = resetForm;
    
    /* called when clicked on link of an image object 
     * to insert or update. */
    this.changeImagePath = changeImagePath;
    
    /* sets the scrollbar position variables to the actual values
     * of the scrollbar positions in the browser window. */
    this.storeScrollbarPositions = storeScrollbarPositions;

    /* writes the component ID of the element which got focus 
     * into the hidden field 'focusElement'. */
    this.setFocusID = setFocusID;
    /* disables the focus listener.
     * This is only needed on loading the page, when some element
     * gets the focus and must be prevented from submitting a
     * focusGained event (see enableFocus below). */
    this.disableFocusEvent = disableFocusEvent;
    /* enables the focus listener.
     * This is only needed on loading the page, when some element
     * gets the focus and must be prevented from submitting a
     * focusGained event (see disableFocus above). */
    this.enableFocusEvent  = enableFocusEvent;

    //  TODO: comment
    this.suspendSubmit = suspendSubmit;
    //  TODO: comment
    this.resumeSubmit = resumeSubmit;
    //  TODO: comment
    this.submitDocument = submitDocument;
    // notifies the user about an error
    this.reportError = reportError;

    // see comment on function
    this.submitFullDocument = submitFullDocument;

    // ajax-related functions
    this.isAjaxEnabled = isAjaxEnabled;    
    this.getAjaxResponseElement = getAjaxResponseElement;
    this.postAjaxRequest = postAjaxRequest;
    this.applyAjaxResponse = applyAjaxResponse;
    this.updatePage = updatePage;
    this.updateNode = updateNode;
    this.isScriptNode = isScriptNode;
    this.getXmlHttpRequest = getXmlHttpRequest;
    
    /* changes the documents' cursor to an hourglass-cursor */
    this.waitCursor = waitCursor;
    /* resets the documents' cursor from whatever cursor back to 'normal'*/
    this.resetCursor = resetCursor;

    /** Focus the element specified by given id */
    this.restoreFocus = restoreFocus;
    
    
    /* stores a reference to the passed object (a checkbox on which
     * a mouseDown event occured). */
    this.setClickedCheckBox = setClickedCheckBox;
    /* checks, whether the passed obj is a formerly clicked checbox,
     * if so, a complete click event has occured, and itemStateChanged
     *  must be triggered. */
    this.triggerClickedCheckBox = triggerClickedCheckBox;
    /* called when a focused checkbox got a keystroke. Sends a 
     * webItemStateChanged, if the key was the spacebar. */
    this.keyOnCheckBox = keyOnCheckBox;
    this.help = help;

    this.minRequestDuration = 0;
    this.requestStart = new Date();

    /* **************************************************** *
     *                   inits.                             *
     * **************************************************** */
    
    document.onkeydown = onKeyDownHandler;
  }

   
  /*********************************************************/ 
  /* the section with the standard event handler functions */
  /*********************************************************/ 
         
  // webActionPerformed 
  function webActionPerformed( obj ) {
    if ( this.submitFlag == false && this.suspendSubmitFlag == false ) {
      this.submitFlag = true;
      componentID = String( obj );
      if ( componentID.charAt( 0 ) == 'p' ) {
        document.W4TForm.webActionEvent.value = componentID;
      } 
      else {    
        document.W4TForm.webActionEvent.value = obj.name;
      }  
      setTimeout( 'eventHandler.submitDocument()', eventHandler.requestDelay );
    }
  }
  
  // webItemStateChanged
  function webItemStateChanged( obj ) {
    if( this.focusDisabledFlag == false ) { 
      document.W4TForm.webItemEvent.value = obj.name;
      if (    this.submitFlag == false
           && this.focusElementValue != obj.value )
      {
        if( this.suspendSubmitFlag == false ) {
          setTimeout( 'eventHandler.submitDocument()', eventHandler.requestDelay );
        }
        this.submitFlag = true;
      } 
    }
  } 
  
  // webFocusGained
  function webFocusGained( obj ) {
    if( this.focusDisabledFlag == false ) {     
      componentID = String( obj );

      if ( componentID.charAt( 0 ) == 'p' ) {
        document.W4TForm.webFocusGainedEvent.value = componentID;
        this.hasAlreadyFocus = false;
      } 
      else {    
        document.W4TForm.webFocusGainedEvent.value = obj.name;
      }  
      
      if( this.hasAlreadyFocus == false ) {
        if ( this.submitFlag == false ) {
          if ( this.suspendSubmitFlag == false ) {
            setTimeout( 'eventHandler.submitDocument()', 150 );
          }
          this.submitFlag = true;
        }
      }    
    }
  }

  /* standard event trigger for webTreeNodeExpanded. */
  function ev_webTreeNodeExpanded( compID ) {
    // register the component which has caused the event
    document.W4TForm.webTreeNodeExpandedEvent.value = compID;
    // trigger the submit mechanism
    if ( this.submitFlag == false ) {
      if ( this.suspendSubmitFlag == false ) {
        setTimeout( 'eventHandler.submitDocument()', eventHandler.requestDelay );
      }
      this.submitFlag = true;
    }  
  }

  /* standard event trigger for webTreeNodeCollapseded. */
  function ev_webTreeNodeCollapsed( compID ) {
    // register the component which has caused the event
    document.W4TForm.webTreeNodeCollapsedEvent.value = compID;
    // trigger the submit mechanism
    if ( this.submitFlag == false ) {
      if ( this.suspendSubmitFlag == false ) {
        setTimeout( 'eventHandler.submitDocument()', eventHandler.requestDelay );
      }
      this.submitFlag = true;
    }  
  }

  /* resets all form elements on the displayed html page. */
  function resetForm() {
    document.W4TForm.reset();
  }
  
  /* called when clicked on link of an image object 
   * to insert or update. */
  function changeImagePath( webComponentID ) {
    document.W4TForm.changeImage.value = webComponentID;
    submitDocument();    
  }
  
  /* writes the component ID of the element which got focus 
   * into the hidden field 'focusElement'. */
  function setFocusID( obj ) {
    componentID = String( obj );
    if( componentID.charAt( 0 ) == 'p') {
      document.W4TForm.focusElement.value = componentID;
      this.focusElementValue = this.W4T_NULL;
      this.hasAlreadyFocus = false;
    }

    // the focus was set to a form element, we store its value
    else if( document.W4TForm.focusElement.value != obj.name ) {
      document.W4TForm.focusElement.value = obj.name;  
      if( obj.value != null ) {
        this.focusElementValue = obj.value;
      }
      this.hasAlreadyFocus = false;
    }
    else {
      this.hasAlreadyFocus = true;  
    }
  } 


  /* determines the scrollbar positions in the browser window 
   * (using the standard way) */
  function storeScrollbarPositions() {
    this.scrollX = window.pageXOffset;
    this.scrollY = window.pageYOffset;
    document.getElementById( 'scrollX' ).value = this.scrollX;
    document.getElementById( 'scrollY' ).value = this.scrollY;
  }


  /* disables the focus listener.
   * This is only needed on loading the page, when some element
   * gets the focus and must be prevented from submitting a
   * focusGained event (see enableFocus below). */
  function disableFocusEvent() {
    this.focusDisabledFlag = true;     
  }
  
  /* enables the focus listener.
   * This is only needed on loading the page, when some element
   * gets the focus and must be prevented from submitting a
   * focusGained event (see disableFocus above). */
  function enableFocusEvent() {
    this.focusDisabledFlag = false;
  }

  // sets suspendSubmitFlag true 
  function suspendSubmit() {
    this.suspendSubmitFlag = true;
  }

  // submit document if submitFlag and suspendSubmitFlag are
  // true; sets suspendSubmitFlag false
  function resumeSubmit() {
    if( this.submitFlag == true && this.suspendSubmitFlag == true ) {     
      setTimeout( 'eventHandler.submitDocument()', eventHandler.requestDelay );
    }
    this.suspendSubmitFlag = false;
  }

  /** submits the document. */
  function submitDocument() {
    if( eventHandler.suspendSubmitFlag == false ) {
      collectFocusSelection();
      storeScrollbarPositions();
      var hasFileToUpload = _hasPendingFileUpload();
      if( this.isAjaxEnabled() && !hasFileToUpload ) { 
        postAjaxRequest( false ); 
      } else {
        if( hasFileToUpload ) {
          _getW4TForm().setAttribute( "enctype", "multipart/form-data" );
        } else {
          _getW4TForm().setAttribute( "enctype", "application/x-www-form-urlencoded" );
        }
        this.submitFullDocument();
      }
    }
  }

  /** submits the document without regard to eventually enabled ajax */  
  function submitFullDocument() {
    _assignHiddenInput( "w4t_isAjaxRequest", "false" );
    adjustAvailableSize();
    document.W4TForm.submit();
  }
  
  function reportError( message ) {
    alert( message );
  }
  
  function adjustAvailableSize() {
    document.getElementById( "availWidth" ).value = screen.availWidth;
    document.getElementById( "availHeight" ).value = screen.availHeight;
  }
  
  function collectFocusSelection() {
    var focusInfo = document.getElementById( "focusElement" ); // hidden field
    if(    focusInfo != null
        && focusInfo.value != null
        && focusInfo.value != "" )
		{
	    var component = document.getElementById( focusInfo.value );
	    if ( component != null && component.type && component.type == "text" ) {
	      focusInfo.value 
	        = component.id 
	        + ";" + String( component.selectionStart ) 
	        + ";" + String( component.selectionEnd );
	    }
    }
  }  

  /** stores a reference to the passed obj (a checkbox on which a
    * mouseDown event occured). */
  function setClickedCheckBox( obj ) {  
    this.clickedCheckBox = obj;
    this.suspendSubmitFlag = true;
  }

  /** checks, whether obj is a formerly clicked checbox, if so, a
    * click event has occured (itemStateChanged must be triggered).
    */
  function triggerClickedCheckBox( obj ) {
    if( this.clickedCheckBox == obj ) {
      // the state( checked/unchecked ) has changed, but the value
      // has probably remained unchanged; since webItemStateChanged 
      // compares the value with the formerly focus-possesing element,
      // we must reset the value of the latter.
      this.focusElementValue = this.W4T_NULL; 

      this.webItemStateChanged( obj );
    } else {
      // click on checkbox was cancelled  
      this.clickedCheckBox = null;
    }
    if( this.submitFlag == true && this.suspendSubmitFlag == true ) {     
      setTimeout( 'eventHandler.submitDocument()', eventHandler.requestDelay );      
    }
    this.suspendSubmitFlag = false;    
  }

  
  /******************************************************************/ 
  /* the section with the Netscape specific event handler functions */
  /******************************************************************/ 

  /* a helping function for storing the keycode of a pressed key;
   * bound as eventhandler for document.onkeydown . */
  function onKeyDownHandler( evt ) {
    eventHandler.downKey = evt.which;
  }

  /* called when a focused checkbox got a keystroke. Sends a 
   * webItemStateChanged, if the key was the spacebar. */
  function keyOnCheckBox( obj ) {
    this.checkedElement = obj;
    setTimeout( 'eventHandler.help()', 10 );
  }

  function help() {
    if( this.downKey == 32 ) { // 'Space' was pressed
      // the value has probably remained unchanged; since
      // webItemStateChanged compares the value with the formerly 
      // focus-possesing element, we must reset the value of the
      // latter.
      this.focusElementValue = this.W4T_NULL; 
      // we must change the checked/unchecked state manually, since
      // we captured the keystroke
      eventHandler.checkedElement.checked 
        = !eventHandler.checkedElement.checked;
      this.webItemStateChanged( eventHandler.checkedElement );
    }
  }

  /** Determines whether AJAX-mode is enabled or not.
    * Returns true if enabled; false otherwise. */
  function isAjaxEnabled() {
    return document.getElementById( 'w4t_ajaxEnabled' ).value == 'true';
  }

  /** Returns the <ajax-response>-Element or null if not found.
    * See 'updatePage()' for a description of the required document structure */  
  function getAjaxResponseElement( xmlDocument ) {
    // nodeType 1 = element, 2 = attribute, 3 = text, 4 = cdata section
    //          7 = processing instruction, 8 = comment
    var ajaxResponse = null;
    var firstElement = xmlDocument.firstChild;
    // Junp over PI if the parser considers it as the first element
    while( firstElement.nodeType == 7 ) {
      firstElement = firstElement.nextSibling;
    }
    if(    firstElement != null
        && firstElement.nodeType == 1
        && firstElement.nodeName == 'ajax-response' )
    {
      ajaxResponse = firstElement;
    }
    return ajaxResponse;
  }
  
  function postAjaxRequest() {
    // ignore submit requests if the latest request lifecycle has not finished
    // yet.
    if( xmlHttpRequestSingleton == null && !eventHandler.isRequestRunning ) {
      eventHandler.isRequestRunning = true;
      try {
        adjustAvailableSize();
        // inform user about long-running action
        this.waitCursor();
        // set ajax-request-flag to true 
        _assignHiddenInput( "w4t_isAjaxRequest", "true" );
        // construct data to be POSTed  
        var inputElements = document.W4TForm.elements;
        var postContent = '';
        for( var i = 0; i < inputElements.length; i++ ) {
          if( i > 0 ) {
            postContent += '&';
          }
          if(    !isUncheckedCheckBox( inputElements[ i ] ) 
              && !isUnSelectedRadioButton( inputElements[ i ] ) )
          {
            postContent =   postContent
                          + inputElements[i].name + '='
                          + encodeURI( inputElements[i].value );
          }
        }
    
        // send request
        var http_request = this.getXmlHttpRequest();
        if( http_request != null ) {
          http_request.onreadystatechange = this.applyAjaxResponse;
          http_request.open( 'POST', 'W4TDelegate?w4t_enc=no', true );
          http_request.setRequestHeader( 'Content-type', 
                                         'application/x-www-form-urlencoded' );
          http_request.setRequestHeader( 'Content-length', postContent.length );
          eventHandler.requestStart = new Date();
          http_request.send( postContent );
        } else {
          eventHandler.isRequestRunning = false;
        }
        // reset the ajax flag, since the next request may not be an ajax request
        _assignHiddenInput( "w4t_isAjaxRequest", "false" );
      } catch( e ) {
        eventHandler.isRequestRunning = false;
        throw e;
      }
    }
  }
  
  function isUncheckedCheckBox( element ) {
    return    element.type 
           && element.type == 'checkbox' 
           && element.checked == false;
  }
  
  function isUnSelectedRadioButton( element ) {
    return     element.type
            && element.type == 'radio'
            && element.checked == false;
  }
  
  function applyAjaxResponse() {
    // XmlHttpRequest.readyState:
    // 0 = uninitialized, 1 = loading, 2 = loaded, 3 = interactive, 4 = complete
    var http_request = eventHandler.getXmlHttpRequest();
    if( http_request != null && http_request.readyState == 4 ) {
      var now = new Date();
      var timePassed = now.getTime() - eventHandler.requestStart.getTime();
      var remainingTimeToWait = eventHandler.minRequestDuration - timePassed;
      if( remainingTimeToWait > 0 ) {
        setTimeout( "doApplyAjaxResponse()", remainingTimeToWait );
      } else {
        doApplyAjaxResponse();
      }
    }
  }
  
  function doApplyAjaxResponse() {
    var http_request = eventHandler.getXmlHttpRequest();
    try {
      // obtain status within try-catch because exception is thrown when
      // server-connection is lost.
      var requestStatus;
      try {
        requestStatus = http_request.status;
      } catch( e ) {
        requestStatus = -1;
      }
      if( requestStatus == 200 ) {  // OK
        if( !updatePage( http_request.responseXML ) ) {
          // in case we didn't get a valid ajax-response (e.g. timeout or
          // malformed xml response) do a 'normal' submit
          var msg =  'The XML-HTTP-Request did not return a valid '
                   + 'AJaX-Response.\nRequesting the full document.';
//          eventHandler.reportError( msg );
          submitFullDocument();
        } 
        // restore state as if page was just loaded
        eventHandler.submitFlag = false;
        eventHandler.suspendSubmitFlag = false;
        eventHandler.focusElementValue = eventHandler.W4T_NULL;
      } else {
        var msg = 'The XML-HTTP-Request did not complete normally (' 
                + requestStatus + ')'
                + '\nRequesting the full document.';
//        eventHandler.reportError( msg );
        submitFullDocument();
      }
    } finally {
      xmlHttpRequestSingleton = null;
      resetCursor();
      eventHandler.isRequestRunning = false;
      eventHandler.minRequestDuration = 0;
    }
  }
  
  /**
   * Updates the html page with the elements contain in xmlDocument. The 
   * structure of xmlDocument must be of the following form:
   * <?xml version="1.0" encoding="..."?>
   * <ajax-request>
   *   <!-- here goes all to-be-updated html -->
   *   <html-tag-1></html-tag-1>
   *   <html-tag-2 />
   *   <html-tag-3>
   *     <nested-html-tag-1>
   *     </nested-html-tag-1>
   *     ...
   *   </html-tag-3>
   *   ...
   * </ajax-request>
   * This function blindly assumes the described structure.
   */
  function updatePage( xmlDocument ) {
    var result = false;
    // 
    this.disableFocusEvent();
    try {
      var ajaxResponse = getAjaxResponseElement( xmlDocument );
      var htmlElement = null;
      if ( ajaxResponse != null ) {
        result = true;
        htmlElement = ajaxResponse.firstChild;
      }
      while( htmlElement != null ) {
        updateNode( htmlElement );
        htmlElement = htmlElement.nextSibling;
      }
    } finally {
      this.restoreFocus();
      this.enableFocusEvent();
    }
    return result;
  }
  
  function updateNode( xmlElement ) {
    // for now, only consider html elements (nodeType == 1)
    if( xmlElement.nodeType == 1 ) {
      var id = xmlElement.getAttribute( 'id' );
      if( isScriptLibraryNode( xmlElement ) ) {
        appendScriptLibraries( xmlElement );
      } else if( isScriptNode( xmlElement ) ) {
        executeScriptSnippet( xmlElement );
      } else if( isStyleNode( xmlElement ) ) { 
        var current = document.styleSheets[ 0 ].ownerNode.innerHTML;
        document.styleSheets[ 0 ].ownerNode.innerHTML
          = current + ' ' + xmlElement.firstChild.nodeValue;
      } else if( isStyleLinkNode( xmlElement ) ) { 
        appendStyleLink( xmlElement );
      } else if( id != null ) {
        // replace node
        var destNode = document.getElementById( id );
        if( destNode == null ) {
          var msg = "updateElement: Could not find tag with id " + id + ".";
          eventHandler.reportError( msg );
        } else {
          if( xmlElement.nodeName == "envelope" ) {
            // replacing a former visible element by an invisible placeholder
            // remove all attributes
            var contentNode = xmlElement.firstChild;
            var dummyNode = document.createElement( "div" );
            dummyNode.innerHTML = contentNode.data;
            var parent = destNode.parentNode;
            var oldChild = parent.replaceChild( dummyNode.firstChild, destNode );
            if( oldChild != null ) {
              oldChild = null;
            }
          } else if( xmlElement.nodeName == "span" ) {
            // replace a former visible element by an invisible placeholder
            var spanNode = document.createElement( "span" );
            spanNode.setAttribute( "id", xmlElement.getAttribute( "id" ) );
            var parent = destNode.parentNode;
            var oldChild = parent.replaceChild( spanNode, destNode );
            if( oldChild != null ) {
              oldChild = null;
            }
          } else {
            // TODO not failsafe in all scenarios
            for( var i = 0; i < xmlElement.attributes.length; i++ ) {
              var xmlElemAttr = xmlElement.attributes.item( i );
              destNode.setAttribute( xmlElemAttr.nodeName, xmlElemAttr.nodeValue );
            }
            if( xmlElement.data != null ) {
              destNode.innerHTML = xmlElement.data;
            }
          }
        }
      }
    }
  }
  
  function isStyleLinkNode( xmlElement ) {
    return    xmlElement.nodeName.toLowerCase() == 'link'
           && xmlElement.getAttribute( 'rel' ) != null
           && xmlElement.getAttribute( 'rel' ) == 'stylesheet';
  }
  
  function appendStyleLink( xmlElement ) {
    var head = document.getElementsByTagName( 'head' )[ 0 ];
    var styleLink = document.createElement( 'link' );
    styleLink.setAttribute( 'rel', 'stylesheet' );
    styleLink.setAttribute( 'type', 'text/css' );
    styleLink.setAttribute( 'href', xmlElement.getAttribute( 'href' ) );
    head.appendChild( styleLink );
  }
  
  function isStyleNode( xmlElement ) {
    return    xmlElement.nodeName.toLowerCase() == 'style'
           && xmlElement.hasChildNodes();
  }
  
  function appendScriptLibraries( xmlElement ) {
    var head = document.getElementsByTagName( 'head' )[ 0 ];
    var child = xmlElement.firstChild;
    while( child ) {
      var scriptElement = document.createElement( 'script' );
      scriptElement.setAttribute( 'language', 
                                  child.getAttribute( 'language' ) );
      scriptElement.setAttribute( 'src', child.getAttribute( 'src' ) );
      scriptElement.setAttribute( 'type', child.getAttribute( 'type' ) );
      head.appendChild( scriptElement );
      child = child.nextSibling;
    }
  }
  
  function isScriptLibraryNode( xmlElement ) {
    return xmlElement.getAttribute( 'id' ) == 'w4t_userdefined_scripts';
  }

  function executeScriptSnippet( xmlElement ) {
    // execute script
    try {
      eval( xmlElement.firstChild.nodeValue );
    } catch( e ) {
      eventHandler.reportError(   "Failed to evaluate JavaScript snippet:\n"
                                + "Reason: " + e.message + "\n" 
                                + xmlElement.firstChild.nodeValue );
    }
  }
  
  /** Returns true if given 'node' is an element node wich contains script code */
  function isScriptNode( node ) {
    return    node.nodeName 
           && node.nodeName != null 
           && node.nodeName.toLowerCase() == "script"
           && node.firstChild != null; 
  }
  
  function getXmlHttpRequest() {
    if( xmlHttpRequestSingleton == null ) {
      xmlHttpRequestSingleton = new XMLHttpRequest();
      if( xmlHttpRequestSingleton == null ) {
        eventHandler.reportError( "Failed to create XMLHttpRequest instance." );
      } else {
        if( xmlHttpRequestSingleton.overrideMimeType ) {
          xmlHttpRequestSingleton.overrideMimeType( "text/xml" );
        }
      }
    }
    return xmlHttpRequestSingleton;
  }
  
  function waitCursor() {
    document.body.style.cursor = 'wait';
  }
  
  function resetCursor() {
    document.body.style.cursor = 'default';
  }
  
  /** Restores the focus after an AJaX request */
  function restoreFocus() {
    try {
      if ( !window.closed ) {
        var focusInfo = document.getElementById( "focusElement" ); // hidden field
        if(    focusInfo != null
            && focusInfo.value != null
            && focusInfo.value != "" )
        {
          var parts = focusInfo.value.split( ";" ); 
          var focusElement = document.getElementById( parts[ 0 ] );
          if ( focusElement != null && focusElement.focus ) {
            focusElement.focus();
            if( focusElement.getAttribute( "type" ) == "text" ) {
              focusElement.setSelectionRange( parts[ 1 ], parts[ 2 ] );
            }
          }
        }
      }
    } catch( e ) {
    }
  }
  
  /** Creates a hidden input field in the W4TForm. If no W4TForm exists the
   * function silently returns. The parameter id is used a the elements id 
   * and name. If there is already an element with given id, its value is set
   * to the given value. */
  function _assignHiddenInput( id, value ) {
    var w4tForm = _getW4TForm();
    if( w4tForm != null ) {
  	  var field = document.getElementById( id );
  	  if( field == null ) {
        field = document.createElement( "input" );
        field.setAttribute( "type", "hidden" );
        field.setAttribute( "id", id );
        field.setAttribute( "name", id );
        w4tForm.appendChild( field );
  	  }
  	  field.setAttribute( "value", value );
    }
  }
  
  function _hasPendingFileUpload() {
    var result = false;
    var w4tForm = _getW4TForm();
    if( w4tForm != null ) {
      var inputElements = w4tForm.elements;
      for( var i = 0; !result && i < inputElements.length; i++ ) {
        if(    typeof( inputElements[ i ].type ) != "undefined"
            && inputElements[ i ].type == "file" 
            && inputElements[ i ].value != "" ) {
          result = true;
        }
      }
    }
    return result;
  }
  
  function _getW4TForm() {
    var result = null;
    var allForms = document.getElementsByName( "W4TForm" );
    if( allForms.length == 1 ) {
      result = allForms[ 0 ];
    }
    return result;
  }

  /** Holds the EventHandler instance */    
  var eventHandler = new EventHandler();
  
  /** Holds the XmlHttpRequest object */    
  var xmlHttpRequestSingleton = null;

//--> end hide JavaScript