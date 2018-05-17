/**
 * NUIX MAKES NO EXPRESS OR IMPLIED REPRESENTATIONS OR WARRANTIES WITH RESPECT TO THIS CODE (INCLUDING BUT NOT LIMITED
 * TO ANY WARRANTIES OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, OR SUITABILITY FOR
 * CUSTOMER’S REQUIREMENTS). WITHOUT LIMITING THE FOREGOING, NUIX DOES NOT WARRANT THAT THIS CODE WILL MEET CUSTOMER’S
 * REQUIREMENTS OR THAT ANY USE OF THIS CODE WILL BE ERROR-FREE OR THAT ANY ERRORS OR DEFECTS IN THIS CODE WILL BE CORRECTED.
 * THIS CODE IS PROVIDED TO CUSTOMER ON AN “AS IS” AND “AS AVAILABLE” BASIS AND FOR COMMERCIAL USE ONLY. CUSTOMER IS RESPONSIBLE
 * FOR DETERMINING WHETHER ANY INFORMATION GENERATED FROM USE OF THIS CODE IS ACCURATE AND SUFFICIENT FOR CUSTOMER’S PURPOSES.
 */
package com.nuix.tagmanager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import nuix.Case;
import nuix.Utilities;
import nuix.Window;
/**
 * NUIX MAKES NO EXPRESS OR IMPLIED REPRESENTATIONS OR WARRANTIES WITH RESPECT TO THIS CODE (INCLUDING BUT NOT LIMITED
 * TO ANY WARRANTIES OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, OR SUITABILITY FOR
 * CUSTOMER’S REQUIREMENTS). WITHOUT LIMITING THE FOREGOING, NUIX DOES NOT WARRANT THAT THIS CODE WILL MEET CUSTOMER’S
 * REQUIREMENTS OR THAT ANY USE OF THIS CODE WILL BE ERROR-FREE OR THAT ANY ERRORS OR DEFECTS IN THIS CODE WILL BE CORRECTED.
 * THIS CODE IS PROVIDED TO CUSTOMER ON AN “AS IS” AND “AS AVAILABLE” BASIS AND FOR COMMERCIAL USE ONLY. CUSTOMER IS RESPONSIBLE
 * FOR DETERMINING WHETHER ANY INFORMATION GENERATED FROM USE OF THIS CODE IS ACCURATE AND SUFFICIENT FOR CUSTOMER’S PURPOSES.
 */
/***
 * This class provides a way to hand required Nuix objects over to the library
 * for the methods that need them.  In the least you will probably want to call
 * {@link #setUtilities(Utilities)} before using any of the classes in the
 * package.
 * @author JasonWells
 *
 */
public class NuixConnection {
	private static Utilities utilities;
	private static Case currentCase;
	private static NuixVersion currentVersion;
	private static Map<String,Object> scriptData = new HashMap<String,Object>();
	private static Window window = null;
	private static Consumer<String> loggedMessageCallback;
	
	public static void whenMessageLogged(Consumer<String> callback){
		loggedMessageCallback = callback;
	}
	
	public static void logMessage(String message){
		if(loggedMessageCallback != null){
			loggedMessageCallback.accept(message);
		}
	}
	
	/***
	 * Gets the instance of Utilities provided by previous call to {@link #setUtilities(Utilities)}
	 * @return Utilities instance provided by code using library, or null if {@link #setUtilities(Utilities)} was not yet called
	 */
	public static Utilities getUtilities() {
		return utilities;
	}
	/***
	 * Sets the instance of Utilities for the current session.  It is important to note that without making this
	 * call aspects of this library may fail with exceptions as they need to have an instance of Utilities to function.
	 * It is recommended that any code using this library call this shortly after loading the JAR file.
	 * @param utilities The Nuix Utilities object associated with the current session.
	 */
	public static void setUtilities(Utilities utilities) {
		NuixConnection.utilities = utilities;
	}
	
	/***
	 * Get the Nuix case provided via {@link #setCurrentCase(Case)}
	 * @return The case previously provided or null if no case has been provided
	 */
	public static Case getCurrentCase() {
		return currentCase;
	}
	/***
	 * Set the Nuix case to be considered the "current" case
	 * @param currentCase The case to set as being the "current" case
	 */
	public static void setCurrentCase(Case currentCase) {
		NuixConnection.currentCase = currentCase;
	}
	
	/***
	 * Sets the current Nuix version.  This may be used by library to detect features which are not available
	 * in a given version of Nuix.
	 * @param version A String containing the current Nuix version.
	 */
	public static void setCurrentNuixVersion(String version){
		currentVersion = NuixVersion.parse(version);
	}
	/***
	 * Gets a {@link NuixVersion} object representing the current version of Nuix, assuming code using the library has
	 * previously made a call to {@link #setCurrentNuixVersion(String)} previously.
	 * @return A {@link NuixVersion} if previously set by call to {@link #setCurrentNuixVersion(String)}, else null.
	 */
	public static NuixVersion getCurrentNuixVersion(){
		return currentVersion;
	}
	
	public static Map<String,Object> getScriptData(){
		return scriptData;
	}
	
	public static void setScriptData(Map<String,Object> data){
		scriptData = data;
	}
	
	public static Window getWindow() {
		return window;
	}
	
	public static void setWindow(Window window) {
		NuixConnection.window = window;
	}
	
	public static void closeAllTabs(){
		if(window != null){
			window.closeAllTabs();
		}
	}
	public static void openTab(String tabName, Map<?, ?> openOptions) {
		if(window != null){
			window.openTab(tabName, openOptions);
		}
	}
	
	
}
