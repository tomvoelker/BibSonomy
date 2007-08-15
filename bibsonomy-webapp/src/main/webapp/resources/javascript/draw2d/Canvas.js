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
function Canvas(canvasId)
{
  this.construct(canvasId);
}

/** @private **/
Canvas.prototype.type="Canvas";

/**
 * @private
 **/
Canvas.prototype.construct=function(/*:String*/ canvasId)
{
  this.canvasId = canvasId;
  this.html = document.getElementById(this.canvasId);
}


/**
 * @param {Figure} figure The figure object to add to the canvas
 * @param {int} xPos The x coordinate for the figure
 * @param {int} yPos The y coordinate for the figure
 **/
Canvas.prototype.addFigure= function( /*:Figure*/figure,/*:int*/ xPos,/*:int*/ yPos)
{
    figure.setCanvas(this);
    if(xPos && yPos)
      figure.setPosition(xPos,yPos);

    this.html.appendChild(figure.getHTMLElement());
    figure.paint();
    return figure;
}

/**
 * @param {Figure} figure The figure which should be remove from the canvas
 **/
Canvas.prototype.removeFigure=function(/*:Figure*/ figure)
{
  this.html.removeChild(figure.html);
  figure.setCanvas(null);
}


/**
 * @type int
 **/
Canvas.prototype.getWidth=function()
{
  return parseInt(this.html.style.width);
}


/**
 * @type int
 **/
Canvas.prototype.getHeight=function()
{
  return parseInt(this.html.style.height);
}


/**
 * Set the background image of the canvas. The URL can be absolute, like http://www.any.com/myimg.png or relative.
 * 
 * Set the background image of the canvas
 * @param {String} imageUrl The url of the background image.
 * 
 **/
Canvas.prototype.setBackgroundImage=function(/*:String */ imageUrl, /*:boolean*/ repeat)
{
   if(repeat)
      this.html.style.background="transparent url("+imageUrl+") ";
   else
      this.html.style.background="transparent url("+imageUrl+") no-repeat";
}

