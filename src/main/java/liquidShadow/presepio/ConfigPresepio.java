package liquidShadow.presepio;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public class ConfigPresepio {
	public static int BEGINHOUR = 7;
	public static int WAIT_TIME_FOR_HOUR = 1000*5;
	public static int SUNRISE = 6;
	public static int SUNSET = 18;
	public static boolean debug = false;
	public static int stopAfterDays = 5;
	
	public static Pin buttonStartPosition = RaspiPin.GPIO_02;
	public static Pin LED_MANGIATOIA = RaspiPin.GPIO_01;
	public static Pin LED_STELLA_CADENTE = RaspiPin.GPIO_03;
	public static Pin LED_FUOCO = RaspiPin.GPIO_04;
	public static Pin LED_PIZZAIOLO = RaspiPin.GPIO_05;
	public static Pin LED_SOLE = RaspiPin.GPIO_06;
	public static Pin LED_CASE = RaspiPin.GPIO_07;
	public static Pin LED_STAR1 = RaspiPin.GPIO_00;
	public static Pin LED_STAR2 = RaspiPin.GPIO_21;
	public static Pin LED_PONTE = RaspiPin.GPIO_22;
	public static Pin LED_CAPANNA = RaspiPin.GPIO_23;
	public static Pin PIN_FUOCO = RaspiPin.GPIO_24;
	public static Pin PIN_CASE_LONTANE = RaspiPin.GPIO_25;
	public static Pin PIN_POZZO = RaspiPin.GPIO_26;
	
	public static Pin PIN_TEST = RaspiPin.GPIO_29;
	
}
