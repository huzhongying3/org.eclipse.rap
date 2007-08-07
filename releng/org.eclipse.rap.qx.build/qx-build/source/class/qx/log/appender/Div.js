/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2006 STZ-IDA, Germany, http://www.stz-ida.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Til Schneider (til132)

************************************************************************ */

/* ************************************************************************

#module(log)

************************************************************************ */

/**
 * An appender that writes all messages to a div element.
 *
 * This class does not depend on qooxdoo widgets, so it also works when there
 * are problems with widgets or when the widgets are not yet initialized.
 */
qx.Class.define("qx.log.appender.Div",
{
  extend : qx.log.appender.Abstract,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
   * @param divId {String ? "qx_log"} the ID of the div element to write the
   *        messages to.
   */
  construct : function(divId)
  {
    this.base(arguments);

    this._divId = divId ? divId : "qx_log";
  },




  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    /**
     * The maximum number of messages to show. If null the number of messages is not
     * limited.
     */
    maxMessages :
    {
      check : "Integer",
      init : 500
    },

    useLongFormat :
    {
      refine : true,
      init : false
    },

    /** The CSS class name for the head div {containing the clear button}. */
    headClassName :
    {
      check : "String",
      init : "log-head"
    },

    /** The CSS class name for the body div {containing the log messages}. */
    bodyClassName :
    {
      check : "String",
      init : "log-body"
    },

    /** The CSS class name for a div showing the name of the current group. */
    groupClassName :
    {
      check : "String",
      init : "log-group"
    },

    /** The CSS class name for a div showing a debug message. */
    debugClassName :
    {
      check : "String",
      init : "log-debug"
    },

    /** The CSS class name for a div showing a info message. */
    infoClassName :
    {
      check : "String",
      init : "log-info"
    },

    /** The CSS class name for a div showing a warn message. */
    warnClassName :
    {
      check : "String",
      init : "log-warn"

    },

    /** The CSS class name for a div showing a error message. */
    errorClassName :
    {
      check : "String",
      init : "log-error"
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /**
     * Creates an onclick handler that clears a div element. This method is used to
     * create a minimum closure.
     *
     * @type member
     * @param logElem {Element} the element to clear when the handler is called.
     * @return {Function} the handler.
     */
    _createClearButtonHandler : function(logElem)
    {
      return function(evt) {
        logElem.innerHTML = "";
      };
    },

    // overridden
    appendLogEvent : function(evt)
    {
      var Logger = qx.log.Logger;

      // Initialize the log element if nessesary
      if (this._logElem == null)
      {
        var divElem = document.getElementById(this._divId);

        if (divElem == null) {
          throw new Error("Logging div with ID " + this._divId + " not found");
        }

        divElem.innerHTML = '<div class="' + this.getHeadClassName() + '"><button>Clear</button></div>' + '<div class="' + this.getBodyClassName() + '"></div>';

        this._clearBt = divElem.firstChild.firstChild;
        this._logElem = divElem.lastChild;

        this._clearBt.onclick = this._createClearButtonHandler(this._logElem);
      }

      // Append the group when needed
      var group = evt.logger.getName();

      if (evt.instanceId != null) {
        group += "[" + evt.instanceId + "]";
      }

      if (group != this._lastGroup)
      {
        var elem = document.createElement("div");
        elem.className = this.getGroupClassName();
        elem.innerHTML = group;

        this._logElem.appendChild(elem);
        this._lastGroup = group;
      }

      // Append the message
      var elem = document.createElement("div");

      switch(evt.level)
      {
        case Logger.LEVEL_DEBUG:
          elem.className = this.getDebugClassName();
          break;

        case Logger.LEVEL_INFO:
          elem.className = this.getInfoClassName();
          break;

        case Logger.LEVEL_WARN:
          elem.className = this.getWarnClassName();
          break;

        case Logger.LEVEL_ERROR:
          elem.className = this.getErrorClassName();
          break;
      }

      elem.innerHTML = this.formatLogEvent(evt).replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/  /g, " &#160;").replace(/[\n]/g, "<br>");
      this._logElem.appendChild(elem);

      // Remove superflous messages
      while (this._logElem.childNodes.length > this.getMaxMessages())
      {
        this._logElem.removeChild(this._logElem.firstChild);

        if (this._removedMessageCount == null) {
          this._removedMessageCount = 1;
        } else {
          this._removedMessageCount++;
        }
      }

      if (this._removedMessageCount != null)
      {
        this._logElem.firstChild.className = "";
        this._logElem.firstChild.innerHTML = "(" + this._removedMessageCount + " messages removed)";
      }
    }
  },




  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function()
  {
    if (this._clearBt) {
      this._clearBt.onclick = null;
    }

    this._disposeFields("_clearBt", "_logElem");
  }
});
