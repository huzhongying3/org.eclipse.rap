/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2007 1&1 Internet AG, Germany, http://www.1and1.org

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Sebastian Werner (wpbasti)
     * Andreas Ecker (ecker)

************************************************************************ */

/* ************************************************************************

#module(ui_core)
#optional(qx.ui.core.Parent)
#optional(qx.ui.basic.Terminator)

************************************************************************ */

/**
 * Each focus root delegates the focus handling to instances of the FocusHandler.
 */
qx.Class.define("qx.event.handler.FocusHandler",
{
  extend : qx.core.Target,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  construct : function(vWidget)
  {
    this.base(arguments);

    if (vWidget != null) {
      this._attachedWidget = vWidget;
    }
  },




  /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics :
  {
    mouseFocus   : false,




    /*
    ---------------------------------------------------------------------------
      TAB-EVENT HANDLING
    ---------------------------------------------------------------------------
    */

    // Check for TAB pressed
    // * use keydown on mshtml
    // * use keypress on other (correct) browsers
    // = same behaviour
    tabEventType : qx.core.Variant.isSet("qx.client", "mshtml") ? "keydown" : "keypress"
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /*
    ---------------------------------------------------------------------------
      UTILITIES
    ---------------------------------------------------------------------------
    */

    /**
     * TODOC
     *
     * @type member
     * @return {var} TODOC
     */
    getAttachedWidget : function() {
      return this._attachedWidget;
    },


    /**
     * TODOC
     *
     * @type member
     * @param vContainer {var} TODOC
     * @param vEvent {var} TODOC
     * @return {void}
     */
    _onkeyevent : function(vContainer, vEvent)
    {
      if (vEvent.getKeyIdentifier() != "Tab") {
        return;
      }

      // Stop all key-events with a TAB keycode
      vEvent.stopPropagation();
      vEvent.preventDefault();

      // But only react on the one to use for this browser.
      if (vEvent.getType() != qx.event.handler.FocusHandler.tabEventType) {
        return;
      }

      qx.event.handler.FocusHandler.mouseFocus = false;

      var vCurrent = this.getAttachedWidget().getFocusedChild();

      // Support shift key to reverse widget detection order
      if (!vEvent.isShiftPressed()) {
        var vNext = vCurrent ? this.getWidgetAfter(vContainer, vCurrent) : this.getFirstWidget(vContainer);
      } else {
        var vNext = vCurrent ? this.getWidgetBefore(vContainer, vCurrent) : this.getLastWidget(vContainer);
      }

      // If there was a widget found, focus it
      if (vNext)
      {
        vNext.setFocused(true);
        vNext._ontabfocus();
      }
    },


    /**
     * TODOC
     *
     * @type member
     * @param c1 {var} TODOC
     * @param c2 {var} TODOC
     * @return {int | var} TODOC
     */
    compareTabOrder : function(c1, c2)
    {
      // Sort-Check #1: Tab-Index
      if (c1 == c2) {
        return 0;
      }

      var t1 = c1.getTabIndex();
      var t2 = c2.getTabIndex();

      // The following are some ideas to handle focus after tabindex.
      // Sort-Check #2: Top-Position
      if (t1 != t2) {
        return t1 - t2;
      }

      var y1 = qx.html.Location.getPageBoxTop(c1.getElement());
      var y2 = qx.html.Location.getPageBoxTop(c2.getElement());

      if (y1 != y2) {
        return y1 - y2;
      }

      // Sort-Check #3: Left-Position
      var x1 = qx.html.Location.getPageBoxLeft(c1.getElement());
      var x2 = qx.html.Location.getPageBoxLeft(c2.getElement());

      if (x1 != x2) {
        return x1 - x2;
      }

      // Sort-Check #4: zIndex
      var z1 = c1.getZIndex();
      var z2 = c2.getZIndex();

      if (z1 != z2) {
        return z1 - z2;
      }

      return 0;
    },




    /*
    ---------------------------------------------------------------------------
      UTILITIES FOR TAB HANDLING
    ---------------------------------------------------------------------------
    */

    /**
     * TODOC
     *
     * @type member
     * @param vParentContainer {var} TODOC
     * @return {var} TODOC
     */
    getFirstWidget : function(vParentContainer) {
      return this._getFirst(vParentContainer, null);
    },


    /**
     * TODOC
     *
     * @type member
     * @param vParentContainer {var} TODOC
     * @return {var} TODOC
     */
    getLastWidget : function(vParentContainer) {
      return this._getLast(vParentContainer, null);
    },


    /**
     * TODOC
     *
     * @type member
     * @param vParentContainer {var} TODOC
     * @param vWidget {var} TODOC
     * @return {var | Array} TODOC
     */
    getWidgetAfter : function(vParentContainer, vWidget)
    {
      if (vParentContainer == vWidget) {
        return this.getFirstWidget(vParentContainer);
      }

      if (vWidget.getAnonymous()) {
        vWidget = vWidget.getParent();
      }

      if (vWidget == null) {
        return [];
      }

      var vAll = [];

      this._getAllAfter(vParentContainer, vWidget, vAll);

      vAll.sort(this.compareTabOrder);

      return vAll.length > 0 ? vAll[0] : this.getFirstWidget(vParentContainer);
    },


    /**
     * TODOC
     *
     * @type member
     * @param vParentContainer {var} TODOC
     * @param vWidget {var} TODOC
     * @return {var | Array} TODOC
     */
    getWidgetBefore : function(vParentContainer, vWidget)
    {
      if (vParentContainer == vWidget) {
        return this.getLastWidget(vParentContainer);
      }

      if (vWidget.getAnonymous()) {
        vWidget = vWidget.getParent();
      }

      if (vWidget == null) {
        return [];
      }

      var vAll = [];

      this._getAllBefore(vParentContainer, vWidget, vAll);

      vAll.sort(this.compareTabOrder);

      var vChildrenLength = vAll.length;
      return vChildrenLength > 0 ? vAll[vChildrenLength - 1] : this.getLastWidget(vParentContainer);
    },


    /**
     * TODOC
     *
     * @type member
     * @param vParent {var} TODOC
     * @param vWidget {var} TODOC
     * @param vArray {var} TODOC
     * @return {void}
     */
    _getAllAfter : function(vParent, vWidget, vArray)
    {
      var vChildren = vParent.getChildren();
      var vCurrentChild;
      var vChildrenLength = vChildren.length;

      for (var i=0; i<vChildrenLength; i++)
      {
        vCurrentChild = vChildren[i];

        if (!(vCurrentChild instanceof qx.ui.core.Parent) && !(vCurrentChild instanceof qx.ui.basic.Terminator)) {
          continue;
        }

        if (vCurrentChild.isFocusable() && vCurrentChild.getTabIndex() > 0 && this.compareTabOrder(vWidget, vCurrentChild) < 0) {
          vArray.push(vChildren[i]);
        }

        if (!vCurrentChild.isFocusRoot() && vCurrentChild instanceof qx.ui.core.Parent) {
          this._getAllAfter(vCurrentChild, vWidget, vArray);
        }
      }
    },


    /**
     * TODOC
     *
     * @type member
     * @param vParent {var} TODOC
     * @param vWidget {var} TODOC
     * @param vArray {var} TODOC
     * @return {void}
     */
    _getAllBefore : function(vParent, vWidget, vArray)
    {
      var vChildren = vParent.getChildren();
      var vCurrentChild;
      var vChildrenLength = vChildren.length;

      for (var i=0; i<vChildrenLength; i++)
      {
        vCurrentChild = vChildren[i];

        if (!(vCurrentChild instanceof qx.ui.core.Parent) && !(vCurrentChild instanceof qx.ui.basic.Terminator)) {
          continue;
        }

        if (vCurrentChild.isFocusable() && vCurrentChild.getTabIndex() > 0 && this.compareTabOrder(vWidget, vCurrentChild) > 0) {
          vArray.push(vCurrentChild);
        }

        if (!vCurrentChild.isFocusRoot() && vCurrentChild instanceof qx.ui.core.Parent) {
          this._getAllBefore(vCurrentChild, vWidget, vArray);
        }
      }
    },


    /**
     * TODOC
     *
     * @type member
     * @param vParent {var} TODOC
     * @param vFirstWidget {var} TODOC
     * @return {var} TODOC
     */
    _getFirst : function(vParent, vFirstWidget)
    {
      var vChildren = vParent.getChildren();
      var vCurrentChild;
      var vChildrenLength = vChildren.length;

      for (var i=0; i<vChildrenLength; i++)
      {
        vCurrentChild = vChildren[i];

        if (!(vCurrentChild instanceof qx.ui.core.Parent) && !(vCurrentChild instanceof qx.ui.basic.Terminator)) {
          continue;
        }

        if (vCurrentChild.isFocusable() && vCurrentChild.getTabIndex() > 0)
        {
          if (vFirstWidget == null || this.compareTabOrder(vCurrentChild, vFirstWidget) < 0) {
            vFirstWidget = vCurrentChild;
          }
        }

        if (!vCurrentChild.isFocusRoot() && vCurrentChild instanceof qx.ui.core.Parent) {
          vFirstWidget = this._getFirst(vCurrentChild, vFirstWidget);
        }
      }

      return vFirstWidget;
    },


    /**
     * TODOC
     *
     * @type member
     * @param vParent {var} TODOC
     * @param vLastWidget {var} TODOC
     * @return {var} TODOC
     */
    _getLast : function(vParent, vLastWidget)
    {
      var vChildren = vParent.getChildren();
      var vCurrentChild;
      var vChildrenLength = vChildren.length;

      for (var i=0; i<vChildrenLength; i++)
      {
        vCurrentChild = vChildren[i];

        if (!(vCurrentChild instanceof qx.ui.core.Parent) && !(vCurrentChild instanceof qx.ui.basic.Terminator)) {
          continue;
        }

        if (vCurrentChild.isFocusable() && vCurrentChild.getTabIndex() > 0)
        {
          if (vLastWidget == null || this.compareTabOrder(vCurrentChild, vLastWidget) > 0) {
            vLastWidget = vCurrentChild;
          }
        }

        if (!vCurrentChild.isFocusRoot() && vCurrentChild instanceof qx.ui.core.Parent) {
          vLastWidget = this._getLast(vCurrentChild, vLastWidget);
        }
      }

      return vLastWidget;
    }
  },




  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function() {
    this._disposeFields("_attachedWidget");
  }
});
