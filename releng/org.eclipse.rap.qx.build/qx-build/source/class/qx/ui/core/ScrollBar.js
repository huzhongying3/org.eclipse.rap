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


************************************************************************ */

/**
 * A scroll bar.
 */
qx.Class.define("qx.ui.core.ScrollBar",
{
  extend : qx.ui.layout.BoxLayout,




  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
   * @param horizontal {Boolean ? false} whether the scroll bar should be
   *    horizontal. If false it will be vertical.
   */
  construct : function(horizontal)
  {
    this.base(arguments, horizontal ? "horizontal" : "vertical");

    this._horizontal = (horizontal == true);

    this._scrollBar = new qx.ui.core.ScrollArea;

    if (qx.core.Variant.isSet("qx.client", "gecko"))
    {
      // NOTE: We have to force not using position:absolute, because this causes
      //     strange looking scrollbars in some cases (e.g. in Firefox under
      //     Linux the horizontal scrollbar is too high)
      this._scrollBar.setStyleProperty("position", "");
    }

    this._scrollBar.setOverflow(horizontal ? "scrollX" : "scrollY");
    this._scrollBar.addEventListener("scroll", this._onscroll, this);

    this._scrollContent = new qx.ui.basic.Terminator;

    if (qx.core.Variant.isSet("qx.client", "gecko")) {
      this._scrollContent.setStyleProperty("position", "");
    }

    this._scrollBar.add(this._scrollContent);

    if (this._horizontal)
    {
      this._scrollContent.setHeight(5);
      this._scrollBar.setWidth("100%");
      this._scrollBar.setHeight(this._getScrollBarWidth());

      // IE needs that the scrollbar element has a width of +1
      if (qx.core.Variant.isSet("qx.client", "mshtml"))
      {
        this.setHeight(this._getScrollBarWidth());
        this.setOverflow("hidden");
        this._scrollBar.setHeight(this._getScrollBarWidth() + 1);
        this._scrollBar.setTop(-1);
      }
    }
    else
    {
      this._scrollContent.setWidth(5);
      this._scrollBar.setHeight("100%");
      this._scrollBar.setWidth(this._getScrollBarWidth());

      // IE needs that the scrollbar element has a width of +1
      if (qx.core.Variant.isSet("qx.client", "mshtml"))
      {
        this.setWidth(this._getScrollBarWidth());
        this.setOverflow("hidden");
        this._scrollBar.setWidth(this._getScrollBarWidth() + 1);
        this._scrollBar.setLeft(-1);
      }
    }

    this.add(this._scrollBar);

    this.setMaximum(0);
  },




  /*
  *****************************************************************************
     STATICS
  *****************************************************************************
  */

  statics :
  {
    /**
     * The delay when to update the scroll bar value after a scroll event if
     * {@link #mergeEvents} is true (in milliseconds). All scroll events that arrive
     * in shorter time will be merged.
     */
    EVENT_DELAY : 250
  },




  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    /**
     * The current value of the scroll bar. This value is between 0 and
     * (maxium - size), where size is the width of a horizontal resp. the height of
     * a vertical scroll bar in pixels.
     *
     * @see #maximum
     */
    value :
    {
      check : "Number",
      init : 0,
      apply : "_modifyValue",
      event : "changeValue",
      transform : "_checkValue"
    },


    /**
     * The maximum value of the scroll bar. Note that the size of the scroll bar is
     * substracted.
     *
     * @see #value
     */
    maximum :
    {
      check : "Integer",
      apply : "_modifyMaximum"
    },


    /**
     * Whether to merge consecutive scroll event. If true, events will be collected
     * until the user stops scrolling, so the scroll bar itself will move smoothly
     * and the scrolled content will update asynchroniously.
     */
    mergeEvents :
    {
      check : "Boolean",
      init : false
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    // property checker
    /**
     * TODOC
     *
     * @type member
     * @param propValue {var} Current value
     * @param propData {var} Property configuration map
     * @return {var} TODOC
     */
    _checkValue : function(propValue, propData)
    {
      var innerSize = !this.getElement() ? 0 : (this._horizontal ? this.getInnerWidth() : this.getInnerHeight());

      // NOTE: We can't use Number.limit here because our maximum may get negative
      //       (when the scrollbar isn't needed). In this case Number.limit returns
      //       this negative maximum instead of 0. But we need that the minimum is
      //       stronger than the maximum.
      //       -> We use Math.max and Math.min
      return Math.max(0, Math.min(this.getMaximum() - innerSize, propValue));
    },

    // property modifier
    /**
     * TODOC
     *
     * @type member
     * @param propValue {var} Current value
     * @param propOldValue {var} Previous value
     * @param propData {var} Property configuration map
     * @return {Boolean} TODOC
     */
    _modifyValue : function(propValue, propOldValue, propData)
    {
      if (!this._internalValueChange && this._isCreated) {
        this._positionKnob(propValue);
      }

      return true;
    },

    // property modifier
    /**
     * TODOC
     *
     * @type member
     * @param propValue {var} Current value
     * @param propOldValue {var} Previous value
     * @param propData {var} Property configuration map
     * @return {Boolean} TODOC
     */
    _modifyMaximum : function(propValue, propOldValue, propData)
    {
      if (this._horizontal) {
        this._scrollContent.setWidth(propValue);
      } else {
        this._scrollContent.setHeight(propValue);
      }

      // recheck the value
      this.setValue(this._checkValue(this.getValue()));

      return true;
    },

    // property modifier
    /**
     * TODOC
     *
     * @type member
     * @param propValue {var} Current value
     * @param propOldValue {var} Previous value
     * @param propData {var} Property configuration map
     * @return {var} TODOC
     */
    _modifyVisibility : function(propValue, propOldValue, propData)
    {
      if (!propValue) {
        this._positionKnob(0);
      } else {
        this._positionKnob(this.getValue());
      }

      return this.base(arguments, propValue, propOldValue, propData);
    },

    // overridden
    /**
     * TODOC
     *
     * @type member
     * @return {var} TODOC
     */
    _computePreferredInnerWidth : function() {
      return this._horizontal ? 0 : this._getScrollBarWidth();
    },

    // overridden
    /**
     * TODOC
     *
     * @type member
     * @return {var} TODOC
     */
    _computePreferredInnerHeight : function() {
      return this._horizontal ? this._getScrollBarWidth() : 0;
    },


    /**
     * Gets the width of vertical scroll bar.
     *
     * @type member
     * @return {Integer} the width in pixels.
     */
    _getScrollBarWidth : function()
    {
      // Auto-detect the scrollbar width
      if (qx.ui.core.ScrollBar._scrollBarWidth == null)
      {
        var dummy = document.createElement("div");
        dummy.style.width = "100px";
        dummy.style.height = "100px";
        dummy.style.overflow = "scroll";
        dummy.style.visibility = "hidden";
        document.body.appendChild(dummy);
        qx.ui.core.ScrollBar._scrollBarWidth = dummy.offsetWidth - dummy.clientWidth;
        document.body.removeChild(dummy);
      }

      return qx.ui.core.ScrollBar._scrollBarWidth;
    },


    /**
     * Event handler. Called when the user scrolled.
     *
     * @type member
     * @param evt {Map} the event.
     * @return {void}
     */
    _onscroll : function(evt)
    {
      var value = this._horizontal ? this._scrollBar.getScrollLeft() : this._scrollBar.getScrollTop();

      if (this.getMergeEvents())
      {
        this._lastScrollEventValue = value;
        window.clearTimeout(this._setValueTimerId);
        var self = this;

        this._setValueTimerId = window.setTimeout(function()
        {
          self._internalValueChange = true;
          self.setValue(self._lastScrollEventValue);
          self._internalValueChange = false;
          qx.ui.core.Widget.flushGlobalQueues();
        },
        qx.ui.core.ScrollBar.EVENT_DELAY);
      }
      else
      {
        this._internalValueChange = true;
        this.setValue(value);
        this._internalValueChange = false;
        qx.ui.core.Widget.flushGlobalQueues();
      }
    },


    /**
     * Positions the scroll bar knob at a certain value.
     *
     * @type member
     * @param value {Integer} The value where to postion the scroll bar.
     * @return {void}
     */
    _positionKnob : function(value)
    {
      if (this._horizontal) {
        this._scrollBar.setScrollLeft(value);
      } else {
        this._scrollBar.setScrollTop(value);
      }
    },

    // overridden
    /**
     * TODOC
     *
     * @type member
     * @return {void}
     */
    _afterAppear : function()
    {
      this.base(arguments);

      // this.debug("Setting to value: " + this.getValue());
      this._positionKnob(this.getValue());
    }
  },




  /*
  *****************************************************************************
     DESTRUCTOR
  *****************************************************************************
  */

  destruct : function() {
    this._disposeObjects("_scrollContent", "_scrollBar");
  }
});
