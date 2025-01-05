package com.acme.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.boolex.OnMarkerEvaluator;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.filter.EvaluatorFilter;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;


public class LogbackConfig {

    private LogbackConfig() {

    }

    public static void setup(String level) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.reset();

        PatternLayoutEncoder securityEncoder = new PatternLayoutEncoder();
        securityEncoder.setContext(context);
        securityEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - [SECURITY] %msg%n");
        securityEncoder.start();

        ConsoleAppender<ILoggingEvent> securityAppender = new ConsoleAppender<>();
        securityAppender.setContext(context);
        securityAppender.setName(String.format("%s-CONSOLE", Markers.SECURITY));
        securityAppender.setEncoder(securityEncoder);

        EvaluatorFilter<ILoggingEvent> securityFilter = new EvaluatorFilter<>();
        securityFilter.setEvaluator(new OnMarkerEvaluator());
        securityFilter.start();

        securityAppender.addFilter(securityFilter);
        securityAppender.start();

        PatternLayoutEncoder monitoringEncoder = new PatternLayoutEncoder();
        monitoringEncoder.setContext(context);
        monitoringEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - [MONITORING] %msg%n");
        monitoringEncoder.start();

        ConsoleAppender<ILoggingEvent> monitoringAppender = new ConsoleAppender<>();
        monitoringAppender.setContext(context);
        monitoringAppender.setName(String.format("%s-CONSOLE", Markers.MONITORING));
        monitoringAppender.setEncoder(monitoringEncoder);

        EvaluatorFilter<ILoggingEvent> monitoringFilter = new EvaluatorFilter<>();
        monitoringFilter.setEvaluator(new OnMarkerEvaluator());
        monitoringFilter.start();

        monitoringAppender.addFilter(monitoringFilter);
        monitoringAppender.start();

        PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
        consoleEncoder.setContext(context);
        consoleEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n");
        consoleEncoder.start();

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(context);
        consoleAppender.setName("CONSOLE");
        OnMarkerEvaluator evaluators = new OnMarkerEvaluator();
        evaluators.addMarker(Markers.SECURITY);
        evaluators.addMarker(Markers.MONITORING);
        EvaluatorFilter<ILoggingEvent> evaluatorFilter = new EvaluatorFilter<>();
        evaluatorFilter.setEvaluator(evaluators);
        consoleAppender.addFilter(evaluatorFilter);
        consoleAppender.setEncoder(consoleEncoder);
        consoleAppender.start();

        Logger jettyLogger = context.getLogger("org.eclipse.jetty");
        jettyLogger.setLevel(Level.INFO);

        Logger swaggerLogger = context.getLogger("io.swagger");
        swaggerLogger.setLevel(Level.INFO);

        Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.toLevel(level));
        rootLogger.addAppender(consoleAppender);

        // Print any status messages (warnings, errors) from Logback
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }
}
