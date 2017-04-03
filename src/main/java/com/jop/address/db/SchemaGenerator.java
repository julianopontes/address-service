package com.jop.address.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaGenerator {
	private Configuration cfg;

	private static Logger logger = LoggerFactory.getLogger(SchemaGenerator.class);

	/**
	 * @param args
	 *          first argument is the directory to generate the dll to
	 */
	public static void main(String[] args) throws Exception {
		logger.info("Init schema export.");
		final String packageName = args[0];
		logger.info("Package {}", packageName);
		SchemaGenerator gen = new SchemaGenerator(packageName);
		final String directory = args[1];
		logger.info("Export dir {}", directory);
		gen.generate(Dialect.HSQL, directory);
		gen.generate(Dialect.MYSQL, directory);
	}

	@SuppressWarnings("rawtypes")
	public SchemaGenerator(String packageName) throws Exception {
		cfg = new Configuration();
		cfg.setProperty("hibernate.hbm2ddl.auto", "create");

		for (Class clazz : getClasses(packageName)) {
			cfg.addAnnotatedClass(clazz);
		}
	}

	/**
	 * Utility method used to fetch Class list based on a package name.
	 * 
	 * @param packageName
	 *          (should be the package containing your annotated beans.
	 */
	@SuppressWarnings("rawtypes")
	private List<Class> getClasses(String packageName) throws Exception {
		File directory = null;
		try {
			ClassLoader cld = getClassLoader();
			URL resource = getResource(packageName, cld);

			if (resource.toString().startsWith("jar:")) {
				JarInputStream jarFile = new JarInputStream
				    (new FileInputStream(resource.toString().substring(9, resource.toString().indexOf("!"))));
				List<Class> clazz = new ArrayList<Class>();
				while (true) {
					JarEntry jarEntry = jarFile.getNextJarEntry();
					if (jarEntry == null) {
						break;
					}
					String name = jarEntry.getName();
					if (name.startsWith(packageName.replace(".", "/")) && name.endsWith(".class")) {
						clazz.add(Class.forName(name.replace("/", ".").substring(0, name.indexOf(".class"))));
					}
				}
				jarFile.close();
				return clazz;
			}

			directory = new File(resource.getFile());
			logger.info(directory.toString());
		} catch (NullPointerException ex) {
			throw new ClassNotFoundException(packageName + " (" + directory
			    + ") does not appear to be a valid package");
		}
		return collectClasses(packageName, directory);
	}

	private ClassLoader getClassLoader() throws ClassNotFoundException, MalformedURLException {
		URLClassLoader cld = (URLClassLoader) Thread.currentThread().getContextClassLoader();
		if (cld == null) {
			throw new ClassNotFoundException("Can't get class loader.");
		}

		List<URL> urls = new ArrayList<URL>();
		urls.addAll(Arrays.asList(cld.getURLs()));
		for (URL url : urls) {
			logger.info(url.toString());
		}
		cld = new URLClassLoader(cld.getURLs());

		return cld;
	}

	private URL getResource(String packageName, ClassLoader cld) throws ClassNotFoundException, MalformedURLException {
		String path = packageName.replace('.', '/');
		URL resource = cld.getResource(path);
		if (resource == null) {
			throw new ClassNotFoundException("No resource for " + path);
		}
		return resource;
	}

	@SuppressWarnings("rawtypes")
	private List<Class> collectClasses(String packageName, File directory) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<>();
		if (directory.exists()) {
			String[] files = directory.list();
			for (String file : files) {
				if (file.endsWith(".class")) {
					// removes the .class extension
					classes.add(Class.forName(packageName + '.'
					    + file.substring(0, file.length() - 6)));
				}
			}
		} else {
			throw new ClassNotFoundException(packageName
			    + " is not a valid package");
		}
		return classes;
	}

	/**
	 * Method that actually creates the file.
	 *
	 * @param dialect
	 *          to use
	 * @throws IOException
	 */
	private void generate(Dialect dialect, String directory) throws IOException {
		cfg.setProperty("hibernate.dialect", dialect.getDialectClass());

		// Auditoria
		// EnversSchemaGenerator enversSchemaGenerator = new
		// EnversSchemaGenerator(cfg);
		// enversSchemaGenerator.export();

		SchemaExport export = new SchemaExport(cfg);
		export.setDelimiter(";");
		String filename = directory + "ddl_" + dialect.name().toLowerCase() + ".sql";
		File dir = new File(directory);
		dir.mkdirs();
		File file = new File(filename);
		file.createNewFile();
		export.setOutputFile(filename);
		logger.info("Output {}", directory + "ddl_" + dialect.name().toLowerCase() + ".sql");
		export.setFormat(true);
		export.execute(true, false, false, false);

	}

	/**
	 * Holds the classnames of hibernate dialects for easy reference.
	 */
	private static enum Dialect {
		ORACLE("org.hibernate.dialect.Oracle10gDialect"),
		MYSQL("org.hibernate.dialect.MySQLDialect"),
		HSQL("org.hibernate.dialect.HSQLDialect"),
		H2("org.hibernate.dialect.H2Dialect"),
		SQL_SERVER("org.hibernate.dialect.SQLServerDialect");

		private String dialectClass;

		private Dialect(String dialectClass) {
			this.dialectClass = dialectClass;
		}

		public String getDialectClass() {
			return dialectClass;
		}
	}
}
