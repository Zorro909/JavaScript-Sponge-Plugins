package JSPlugins;

import java.io.File;
import java.io.FileReader;
import java.util.function.Function;

import javax.script.ScriptException;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import com.google.inject.Inject;

import ScriptInterpreter.ScriptInterpreter;

@Plugin(id="jsplugins",name="JavaScript Plugins",version="0.1")
public class JSPlugins {
	
	public static Game game;
	public static Object plugin;
	
	@Inject
	private Logger logger;
	
	@Listener
	public void onPostStart(GameInitializationEvent e){
		plugin = this;
		game = e.getGame();
		EventManager.checkForEvents();
		try {
			ScriptInterpreter.addVariableToScript("Game",game);
			ScriptInterpreter.addVariableToScript("broadcastString", new Function<String,Object>(){

				@Override
				public Object apply(String t) {
					game.getServer().getBroadcastSink().sendMessage(Texts.of(t));
					return null;
				}
				
			});
			ScriptInterpreter.addVariableToScript("broadcastText", new Function<Text,Object>(){

				@Override
				public Object apply(Text t) {
					game.getServer().getBroadcastSink().sendMessage(t);
					return null;
				}
				
			});
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
		lookForPlugins();
	}

	private void lookForPlugins() {
		File plugins = new File("mods/JSPlugins/");
		if(!plugins.exists())plugins.mkdirs();
		for(File f : new File("mods/JSPlugins/").listFiles()){
			File main = new File(f.getAbsolutePath() + "/main.js");
			if(main.exists()){
				try{
					ScriptInterpreter.executeJavaScript(new FileReader(main));
					logger.info("Plugin " + f.getName() + " was successfully loaded!");
				}catch(Exception e){
					logger.error("Plugin " + f.getName() + " got an Error! Is it up to date?\n" + e.getMessage());
				}
			}else{
				logger.warn("Found Plugin " + f.getName() + " without Main File!(main.js)");
			}
		}
	}
}
