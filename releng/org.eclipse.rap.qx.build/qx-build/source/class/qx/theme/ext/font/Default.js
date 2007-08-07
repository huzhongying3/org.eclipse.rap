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
   * Alexander Back (aback)

************************************************************************* */

/**
 * The default qooxdoo font theme.
 */
qx.Theme.define("qx.theme.ext.font.Default",
{
  title : "Ext",

  fonts :
  {
    "default" :
    {
      size : 11,
      family : [ "Verdana", "Helvetica", "Tahoma", "Bitstream Vera Sans", "Liberation Sans" ]
    },

    "bold" :
    {
      size : 11,
      family : [ "Verdana", "Helvetica", "Tahoma", "Bitstream Vera Sans", "Liberation Sans" ],
      bold : true
    },

    "large" :
    {
      size : 13,
      family : [ "Verdana", "Helvetica", "Tahoma", "Bitstream Vera Sans", "Liberation Sans" ]
    },

    "bold-large" :
    {
      size : 13,
      family : [ "Verdana", "Helvetica", "Tahoma", "Bitstream Vera Sans", "Liberation Sans" ],
      bold : true
    }
  }
});
