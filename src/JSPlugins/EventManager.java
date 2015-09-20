package JSPlugins;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;

public class EventManager {

	private static HashMap<String, ArrayList<Function>> listeners = new HashMap<String, ArrayList<Function>>();

	public static HashMap<String, Class<? extends Event>> events = new HashMap<String, Class<? extends Event>>();

	public static void checkForEvents() {
		try {
			ArrayList<Class<?>> e = getClassesForPackage("org.spongepowered.api.event");
			for (Class c : e) {
				if (Event.class.isAssignableFrom(c)) {
					if (c.getSimpleName().contains("$")) {
						events.put(c.getSimpleName().split("$", 2)[0], c);
					} else {
						events.put(c.getSimpleName(), c);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void registerListener(String nam, Function f) {
		final String name = nam;
		ArrayList<Function> l = new ArrayList<Function>();
		if (listeners.containsKey(name)) {
			l = listeners.get(name);
		} else {
			Class<? extends Event> event = (Class<? extends Event>) events.get(name);
			if (event == null) {
				System.out.println("ERROR: Event " + name + " was not found!");
				return;
			}
			if(JSPlugins.plugin==null){
				System.out.println("ERROR: Plugin Instance is null!");
				return;
			}
			if(JSPlugins.game==null){
				System.out.println("ERROR: Game Instance is null!");
				return;
			}
			JSPlugins.game.getEventManager().registerListener(JSPlugins.plugin, event, new EventListener<Event>() {

				@Override
				public void handle(Event event) throws Exception {
					System.out.println(name);
					for (Function f : listeners.get(name)) {
						f.apply(event);
					}
				}

			});
		}
		l.add(f);
		listeners.put(name, l);
		System.out.println("Listener registered for " + name);
	}

	/**
	 * Attempts to list all the classes in the specified package as determined
	 * by the context class loader
	 * 
	 * @param pckgname
	 *            the package name to search
	 * @return a list of classes that exist within that package
	 * @throws ClassNotFoundException
	 *             if something went wrong
	 */
	private static ArrayList<Class<?>> getClassesForPackage(String pckgname) throws ClassNotFoundException {
		final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

		try {
			final ClassLoader cld = Thread.currentThread().getContextClassLoader();

			if (cld == null)
				throw new ClassNotFoundException("Can't get class loader.");

			final Enumeration<URL> resources = cld.getResources(pckgname.replace('.', '/'));
			URLConnection connection;

			for (URL url = null; resources.hasMoreElements() && ((url = resources.nextElement()) != null);) {
				try {
					connection = url.openConnection();

					if (connection instanceof JarURLConnection) {
						checkJarFile((JarURLConnection) connection, pckgname, classes);
					} else
						throw new ClassNotFoundException(
								pckgname + " (" + url.getPath() + ") does not appear to be a valid package");
				} catch (final IOException ioex) {
					throw new ClassNotFoundException(
							"IOException was thrown when trying to get all resources for " + pckgname, ioex);
				}
			}
		} catch (final NullPointerException ex) {
			throw new ClassNotFoundException(
					pckgname + " does not appear to be a valid package (Null pointer exception)", ex);
		} catch (final IOException ioex) {
			throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname,
					ioex);
		}

		return classes;
	}
	
	/**
	 * Private helper method.
	 * 
	 * @param connection
	 *            the connection to the jar
	 * @param pckgname
	 *            the package name to search for
	 * @param classes
	 *            the current ArrayList of all classes. This method will simply
	 *            add new classes.
	 * @throws ClassNotFoundException
	 *             if a file isn't loaded but still is in the jar file
	 * @throws IOException
	 *             if it can't correctly read from the jar file.
	 */
	private static void checkJarFile(JarURLConnection connection,
	        String pckgname, ArrayList<Class<?>> classes)
	        throws ClassNotFoundException, IOException {
	    final JarFile jarFile = connection.getJarFile();
	    final Enumeration<JarEntry> entries = jarFile.entries();
	    String name;

	    for (JarEntry jarEntry = null; entries.hasMoreElements()
	            && ((jarEntry = entries.nextElement()) != null);) {
	        name = jarEntry.getName();

	        if (name.contains(".class")) {
	            name = name.substring(0, name.length() - 6).replace('/', '.');

	            if (name.contains(pckgname)) {
	                classes.add(Class.forName(name));
	            }
	        }
	    }
	}
}
