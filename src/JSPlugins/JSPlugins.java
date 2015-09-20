package JSPlugins;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Texts;

import ScriptInterpreter.ScriptInterpreter;

@Plugin(id="jsplugins",name="JavaScript Plugins",version="0.1")
public class JSPlugins {
	
	public static Game game;
	public static Object plugin;
	
	@Listener
	public void onPostStart(GameInitializationEvent e){
		plugin = this;
		game = e.getGame();
		EventManager.checkForEvents();
		try {
			ScriptInterpreter.addVariableToScript("Game",game);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			ScriptInterpreter.addClassToScript("EventManager", EventManager.class);
			ScriptInterpreter.addClassToScript("Texts", Texts.class);
		} catch (IllegalArgumentException | IllegalAccessException | ScriptException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		e.getGame().getEventManager().registerListeners(this, new EventManager());
		try {
			ScriptInterpreter.executeJavaScript(new FileReader(new File("mods/JSPlugins/Test/main.js")));
		} catch (FileNotFoundException | ScriptException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
