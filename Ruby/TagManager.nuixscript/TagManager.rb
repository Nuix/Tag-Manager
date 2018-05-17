script_directory = File.dirname(__FILE__)
require File.join(script_directory,"TagManager.jar")
java_import com.nuix.tagmanager.NuixConnection
java_import com.nuix.tagmanager.Tag
java_import com.nuix.tagmanager.TagCleanupOption
java_import com.nuix.tagmanager.gui.MainFrame

NuixConnection.setUtilities($utilities)
NuixConnection.setCurrentCase($current_case)
NuixConnection.whenMessageLogged{|message| puts message}

m = MainFrame.new
m.setVisible(true)