import flash.display.BitmapData;
import flash.events.Event;
import flash.events.TimerEvent;
import flash.external.ExternalInterface;
import flash.geom.Rectangle;
import flash.media.Camera;
import flash.media.SoundChannel;
import flash.media.Video;
import flash.net.SharedObject;
import flash.utils.Timer;
import flash.text.TextField;
import flash.text.TextFormat;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.BufferedImageLuminanceSource;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
import com.google.zxing.Result;
import com.google.zxing.client.result.ResultParser;

import mx.containers.TitleWindow;
import mx.core.FlexGlobals;
import mx.core.mx_internal;
import mx.controls.Text;
import mx.events.CloseEvent;
import mx.events.FlexEvent;
import mx.managers.PopUpManager;
	
import spark.components.mediaClasses.DynamicStreamingVideoSource;
import spark.components.HGroup;

use namespace mx_internal;

/**
 * detection rate. 10Hz in this case.
 */
private const DETECTION_RATE:int = 10;

/**		
 * container for camera 
 */
private var video:Video;

/**
 * timer for repetitive calling of qr code recognizing method
 */
private var refreshTimer:Timer;
		
/**
 * the error window
 */
private var errorWindow:TitleWindow;

/**
 * error text
 */
private var errorLabel:Text;

/**
 * the info window
 */
private var infoWindow:TitleWindow;

/**
 * info text
 */
private var infoLabel:Text;

/**
 * channel to play sound effect
 */
private var channel:SoundChannel;

/**
 * zxing qr code reader
 */
private var myReader:QRCodeMultiReader;

/**
 * camera width from javascript context
 */
[Bindable]
public var camWidth:int;

/**
 * camera height from javascript context
 */		
[Bindable]
public var camHeight:int;

/**
 * error header from javascript context
 */		
[Bindable]
public var errorHeader:String;

/**
 * error message from javascript context
 */		
[Bindable]
public var errorMessage:String;

/**
 * info message from javascript context
 */		
[Bindable]
public var infoMessage:String;


/**		
 * entry point for initialization of entities
 */
private function init():void {	
	
	/*
	 * get javascript variables
	 */
	camWidth = FlexGlobals.topLevelApplication.parameters.dynamicWidth;
	camHeight = FlexGlobals.topLevelApplication.parameters.dynamicHeight;
	errorHeader = FlexGlobals.topLevelApplication.parameters.dynamicErrorHeader;
	errorMessage = FlexGlobals.topLevelApplication.parameters.dynamicErrorMessage;
	infoMessage = FlexGlobals.topLevelApplication.parameters.dynamicInfoMessage;
	
	/*
	 * initialize the webcam
	 */ 			
	var cam:Camera = Camera.getCamera();
	
	myReader = new QRCodeMultiReader();
			
	if(cam != null) {		
		
		/*		
		 * set camera mode
		 */
		cam.setMode(camWidth,camHeight,25);
		
		video = new Video(cam.width, cam.height);
		video.attachCamera(cam);
		
		/*
		 * mirror the camera picture
		 */
		var matrix:Matrix = new Matrix();
		matrix.a = -1;
		matrix.tx = video.width;
		video.transform.matrix = matrix;
		
		/*
		 * add camera picture to stage
		 */
		theCam.addChild(video);
		
		/*
		 * check whether user has visited site for first time
		 */
		var cookie:SharedObject = SharedObject.getLocal('bibsonomyClipboardQReader', null, false);

		
		if(cookie.size == 0) {
			cookie.data.firstVisit = true;
			displayInfoMessage();
		}
		
		/*
		 * initialize the refresh timer
		 */
		refreshTimer = new Timer(1000/DETECTION_RATE);
		refreshTimer.addEventListener(TimerEvent.TIMER, decodeSnapshot);
		refreshTimer.start();
		
	} else {
		
		/*
		 * in error case disable camera and display error message
		 */
		theCam.visible = false;
		
		displayErrorMessage();	
	}
}

/**
 * gets the unmirrored picture of the camera and calls the decode method.
 * is called periodically from timer context.
 */
private function decodeSnapshot(evt:TimerEvent):void {
	
	/*
	 * temp store of video matrix
	 */
    var oldMatrix:Matrix = video.transform.matrix;
    
	/*
	 * get unmirrored bitmapdata
	 */
    var matrix:Matrix = new Matrix();
	matrix.a = -1;
	matrix.tx = video.width;
	video.transform.matrix = matrix;
    var bmd:BitmapData = new BitmapData(video.width, video.height);
    bmd.draw(video);
    
	/*
	 * restore matrix
	 */
    video.transform.matrix = oldMatrix;
    
	/*
	 * decode bitmap data
	 */
    this.decodeBitmapData(bmd, video.width, video.height);
}

/**
 * decodes the given bitmap data and tries to find a qr code
 * to decode
 */
public function decodeBitmapData(bmpd:BitmapData, width:int, height:int):void {
	
	/*
	 * create the container to store the image width and height in
	 */
	var lsource:BufferedImageLuminanceSource = new BufferedImageLuminanceSource(bmpd);
	
	/*
	 * convert it to a binary bitmap
	 */
	var bitmap:BinaryBitmap = new BinaryBitmap(new HybridBinarizer(lsource));
					
	try {
	
		/*
		 * try to decode the image
		 */ 
		var res:Result = myReader.decode(bitmap);
		
		/*
		 * is there a legit result?
		 */
		if(res != null)
		{
			/*
			 * parse the result
			 */
			var parsedResult:ParsedResult = ResultParser.parseResult(res);
			
			/*
			 * regex for a valid bibsonomy, biblicious, puma url
			 */
			var regex:RegExp = /^(((ht|f)tp(s?))\:\/\/)?(www.|[a-zA-Z].)[a-zA-Z0-9\-]+(\b|\.org)(\:[0-9]+)*\/+bibtex\/[a-fA-F0-9]{32,33}\/[a-zA-z0-9]+$/;
			
			/*
			 * is found url valid?
			 */
			if (regex.test( parsedResult.getDisplayResult())) {
				
				/*
				 * stop refresh timer
				 */
				refreshTimer.stop();
			
				/*
				 * check if still image is already displayed
				 */
				if (!previewBox.visible) {
					
					/*
					 * call javascript method
					 */			
					if (ExternalInterface.available) {
						ExternalInterface.call("urlFromFlash",  parsedResult.getDisplayResult());
					}
					
					/*	
					 * get bitmapdata
					 */
					var picture:BitmapData = new BitmapData(theCam.width, theCam.height);
				
					/*
					 * draw it from stage
					 */
					picture.draw(theCam);		
				
					/*
					 * our preview's source is a new Bitmap made of picture's BitmapData
					 */ 
					preview.source = new Bitmap(picture);
					
					/*
					 * play sound
					 */
					channel = mySound.sound.play();
					channel.addEventListener(Event.SOUND_COMPLETE, stopSnap);
							
					/*
					 * makes the previewBox visible, so we can see our picture
					 */
					previewBox.visible = true;
				
					/*
					 * displays the flashLight
					 */
					flashLight.visible = true;
				
					/*
					 * makes the flashLight disappear
					 */
					flashLight.visible = false;
					
					/*
					 * register second timer to restart whole process
					 */
					var restartTimer:Timer = new Timer(2000, 1);
					restartTimer.addEventListener(TimerEvent.TIMER_COMPLETE, restart);
					restartTimer.start();
				}
			}
		}
	} catch(e:*) {
		/*
		 * do nothing here because of performance enhancement
		 */
	}
}

/**
 * restart method to disable still image and restart decoding
 * process
 */
private function restart(evt:TimerEvent):void {
	
	previewBox.visible = false;
	refreshTimer.start();
}

/**		
 * method to stop the sound effects
 */
private function stopSnap(evt:Event):void {
	channel.stop();
}

/**
 * method to display the error window
 */
private function displayErrorMessage():void {
	
	/*
	 * create new window and disable close button.
	 * gets error header and message from javascript context.
	 * takes 90% of the screen and disables vertical and horizontal scrolling.
	 * is added as a popup.
	 */				
	errorWindow = new TitleWindow();
	errorWindow.title = errorHeader;
	errorWindow.showCloseButton = false;
	errorWindow.explicitWidth = camWidth * 0.9;
	errorWindow.explicitHeight = camHeight * 0.9;
	errorWindow.horizontalScrollPolicy = "off";
	errorWindow.verticalScrollPolicy = "off";
	
	/*
	 * window movement can only be disabled after initialize event is thrown.
	 * therefor we register a listener to take care of that.
	 */
	errorWindow.addEventListener(FlexEvent.INITIALIZE, createErrorMessage);
	errorWindow.setStyle("backgroundColor", "0xeeeeee");	
	
	errorLabel = new Text();
	errorLabel.text = errorMessage;
	errorLabel.explicitWidth = errorWindow.explicitWidth * 0.95;
	
	/*
	 * if text is to big for the window we have to resize it. can only be done
	 * after creation_complete event is thrown. therefor we register a listener
	 * for that.
	 */
	errorLabel.addEventListener(FlexEvent.CREATION_COMPLETE, resizeErrorLabel);
	errorWindow.addChild(errorLabel);
				
	PopUpManager.addPopUp(errorWindow, this, true);
	PopUpManager.centerPopUp(errorWindow);
}

/**
 * method to resize text of error window
 */
private function resizeErrorLabel(evt:FlexEvent):void {
	
	/*
	 * update error window properties
	 */
	errorLabel.validateNow();
	
	/*
	 * set maximum height
	 */
	var maxTextHeight:int = errorWindow.explicitHeight * 0.8;
	
	if (maxTextHeight != 0) {
		
		/*
		 * get internal text representation
		 */
		var text_field:TextField = errorLabel.mx_internal::getTextField();
		var f:TextFormat = text_field.getTextFormat();
		
		/*
		 * resize text until it fits
		 */
		while (text_field.textHeight > maxTextHeight) {
			f.size = int(f.size) -1;
			text_field.setTextFormat(f);
		}
	}
	
}

/**
 * method to disable window movement
 */
private function createErrorMessage(evt:FlexEvent):void {
	errorWindow.isPopUp = false;
}

/**
 * method to display the info window
 */
private function displayInfoMessage():void {
	
	/*
	 * create new window and disable close button.
	 * gets error header and message from javascript context.
	 * takes 90% of the screen and disables vertical and horizontal scrolling.
	 * is added as a popup.
	 */				
	infoWindow = new TitleWindow();
	infoWindow.title = "Information";
	infoWindow.showCloseButton = true;
	infoWindow.explicitWidth = camWidth * 0.9;
	infoWindow.explicitHeight = camHeight * 0.9;
	infoWindow.horizontalScrollPolicy = "off";
	infoWindow.verticalScrollPolicy = "off";
	
	/*
	 * window movement can only be disabled after initialize event is thrown.
	 * therefor we register a listener to take care of that.
	 */
	infoWindow.addEventListener(FlexEvent.INITIALIZE, createInfoMessage);
	infoWindow.addEventListener(CloseEvent.CLOSE, closeInfoMessage);
	infoWindow.setStyle("backgroundColor", "0xeeeeee");	
	
	infoLabel = new Text();
	infoLabel.text = infoMessage;
	infoLabel.explicitWidth = infoWindow.explicitWidth * 0.95;
	
	/*
	 * if text is to big for the window we have to resize it. can only be done
	 * after creation_complete event is thrown. therefor we register a listener
	 * for that.
	 */
	infoLabel.addEventListener(FlexEvent.CREATION_COMPLETE, resizeInfoLabel);
	infoWindow.addChild(infoLabel);
				
	PopUpManager.addPopUp(infoWindow, this, true);
	PopUpManager.centerPopUp(infoWindow);
}

/**
 * method to resize text of info window
 */
private function resizeInfoLabel(evt:FlexEvent):void {
	
	/*
	 * update error window properties
	 */
	infoLabel.validateNow();
	
	/*
	 * set maximum height
	 */
	var maxTextHeight:int = infoWindow.explicitHeight * 0.8;
	
	if (maxTextHeight != 0) {
		
		/*
		 * get internal text representation
		 */
		var text_field:TextField = infoLabel.mx_internal::getTextField();
		var f:TextFormat = text_field.getTextFormat();
		
		/*
		 * resize text until it fits
		 */
		while (text_field.textHeight > maxTextHeight) {
			f.size = int(f.size) -1;
			text_field.setTextFormat(f);
		}
	}
	
}

/**
 * method to disable window movement
 */
private function createInfoMessage(evt:FlexEvent):void {
	infoWindow.isPopUp = false;
}

/**
 * method to remove info message
 */
private function closeInfoMessage(evt:CloseEvent):void {
	PopUpManager.removePopUp(infoWindow);
}