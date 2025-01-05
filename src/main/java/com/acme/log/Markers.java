package com.acme.log;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Markers {

    public static final String SECURITY = "SECURITY";
    public static final String MONITORING = "MONITORING";

    public static final Marker SECURITY_MARKER = MarkerFactory.getMarker(SECURITY);
    public static final Marker MONITORING_MARKER = MarkerFactory.getMarker(MONITORING);

    private Markers (){

    }
}
