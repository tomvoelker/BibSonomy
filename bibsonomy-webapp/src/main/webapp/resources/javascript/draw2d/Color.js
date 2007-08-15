/* This notice must be untouched at all times.

Open-jACOB Draw2D
The latest version is available at
http://www.openjacob.org

Copyright (c) 2006 Andreas Herz. All rights reserved.
Created 5. 11. 2006 by Andreas Herz (Web: http://www.freegroup.de )

LICENSE: LGPL

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License (LGPL) as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA,
or see http://www.gnu.org/copyleft/lesser.html
*/

/**
 * 
 * @version 0.7.15
 * @author Andreas Herz
 * @constructor
 */
function Color(red, green , blue)
{
  if(typeof green == "undefined")
  {
    var rgb = this.hex2rgb(red);
    /** @private **/
    this.red= rgb[0];
    /** @private **/
    this.green = rgb[1];
    /** @private **/
    this.blue = rgb[2];
  }
  else
  {
    /** @private **/
    this.red= red;
    /** @private **/
    this.green = green;
    /** @private **/
    this.blue = blue;
  }
}
/** @private **/
Color.prototype.type="Color";

/**
 * @private
 **/
Color.prototype.getHTMLStyle=function()
{
  return "rgb("+this.red+","+this.green+","+this.blue+")";
}

/**
 * Return the [red] part of the color.
 * @type int
 **/
Color.prototype.getRed=function()
{
  return this.red;
}


/**
 * Return the [green] part of the color.
 * @type int
 **/
Color.prototype.getGreen=function()
{
  return this.green;
}


/**
 * Return the [blue] part of the color.
 * @type int
 **/
Color.prototype.getBlue=function()
{
  return this.blue;
}

/**
 * Returns the ideal Text Color. Usefull for font color selection by a given background color.
 *
 * @returns The <i>ideal</i> inverse color.
 * @type Color
 **/
Color.prototype.getIdealTextColor=function()
{
   var nThreshold = 105;
   var bgDelta = (this.red * 0.299) + (this.green * 0.587) + (this.blue * 0.114);
   return (255 - bgDelta < nThreshold) ? new Color(0,0,0) : new Color(255,255,255);
}


/**
 * @private
 */
Color.prototype.hex2rgb=function(/*:String */hexcolor)
{
  hexcolor = hexcolor.replace("#","");
  return(
         {0:parseInt(hexcolor.substr(0,2),16),
          1:parseInt(hexcolor.substr(2,2),16),
          2:parseInt(hexcolor.substr(4,2),16)}
         );
}

/**
 * @private
 **/
Color.prototype.hex=function()
{ 
  return(this.int2hex(this.red)+this.int2hex(this.green)+this.int2hex(this.blue)); 
}

/**
 * @private
 */
Color.prototype.int2hex=function(v) 
{
  v=Math.round(Math.min(Math.max(0,v),255));
  return("0123456789ABCDEF".charAt((v-v%16)/16)+"0123456789ABCDEF".charAt(v%16));
}

