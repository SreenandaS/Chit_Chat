package com.chitchat;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;

/**
 * Embedded Apache Tomcat runner for ChitChat.
 * Allows running the entire web application locally with one command:
 *   mvn compile exec:java
 */
public class Main {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String portEnv = System.getenv("PORT");
        if (portEnv != null && !portEnv.trim().isEmpty()) {
            try {
                port = Integer.parseInt(portEnv.trim());
            } catch (NumberFormatException ignored) {}
        }

        String portProp = System.getProperty("server.port");
        if (portProp != null && !portProp.trim().isEmpty()) {
            try {
                port = Integer.parseInt(portProp.trim());
            } catch (NumberFormatException ignored) {}
        }

        // Set tomcat base directory
        String baseDir = new File("target/tomcat-embed").getAbsolutePath();
        new File(baseDir).mkdirs();

        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir);
        tomcat.setPort(port);
        tomcat.getConnector(); // Initialize default HTTP connector

        // Webapp root directory
        File webappDir = new File("src/main/webapp");
        if (!webappDir.exists()) {
            webappDir = new File("webapp");
        }

        StandardContext ctx = (StandardContext) tomcat.addWebapp("", webappDir.getAbsolutePath());

        // Set parent classloader to Main's classloader so Tomcat embedded can load all classes & servlets
        ClassLoader classLoader = Main.class.getClassLoader();
        tomcat.getEngine().setParentClassLoader(classLoader);
        ctx.setParentClassLoader(classLoader);

        // Mount compiled classes to /WEB-INF/classes
        File targetClasses = new File("target/classes");
        if (targetClasses.exists()) {
            WebResourceRoot resources = new StandardRoot(ctx);
            resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes",
                    targetClasses.getAbsolutePath(), "/"));
            ctx.setResources(resources);
        }

        System.out.println("============================================================");
        System.out.println("  💬 ChitChat - Social Networking Platform Started!         ");
        System.out.println("  🚀 Running on: http://localhost:" + port + "                   ");
        System.out.println("  👤 Demo Accounts:                                          ");
        System.out.println("     - Member: sarah_c / password123                        ");
        System.out.println("     - Admin:  admin   / admin123                           ");
        System.out.println("============================================================");

        tomcat.start();
        tomcat.getServer().await();
    }
}
