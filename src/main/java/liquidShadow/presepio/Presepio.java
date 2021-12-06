package liquidShadow.presepio;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;

import liquidShadow.presepio.structure.Capanna;
import liquidShadow.presepio.structure.CasePopolo;
import liquidShadow.presepio.structure.Fuoco;
import liquidShadow.presepio.structure.Pizzaiolo;
import liquidShadow.presepio.structure.Ponte;
import liquidShadow.presepio.structure.Pozzo;
import liquidShadow.presepio.structure.Sole;
import liquidShadow.presepio.structure.Stelle;

public class Presepio {

	private static int hour = ConfigPresepio.BEGINHOUR;
	private static boolean running = true;
	
	private static int daysGone = 0;
	
	private static List<IchangeHourListener> structures = new ArrayList<>();
	
	private static Logger LOG = LogManager.getLogger(Presepio.class);
	
	public static void main(String[] args) {

		final GpioController gpio = GpioFactory.getInstance();
		final GpioPinDigitalInput startButton = gpio.provisionDigitalInputPin(ConfigPresepio.buttonStartPosition, PinPullResistance.PULL_DOWN);
		gpio.provisionDigitalOutputPin(ConfigPresepio.PIN_TEST, "PinLED", PinState.HIGH);
		
		structures.add(new Capanna());
		structures.add(new Pozzo());
		structures.add(new Fuoco());
		structures.add(new Pizzaiolo());
		structures.add(new CasePopolo());
		structures.add(new Sole());
		structures.add(new Stelle());
		structures.add(new Ponte());
		
		while(true) {
			
			if (startButton.getState().isHigh() && !running) {
				beginTime();		
			}
			
			if (running) {
				nextHour();
				if (daysGone >= ConfigPresepio.stopAfterDays) {
					stopTime();
				}
			}
			
			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void beginTime() {
		running = true;
		LOG.info("Start Presepe");
	}
	
	private static void stopTime() {
		hour = ConfigPresepio.BEGINHOUR;
		daysGone = 0;
		running = false;
		LOG.info("Stop Presepe");
		
		for (IchangeHourListener struct: structures) {
			struct.ended();
		}
	}
	
	private static void nextHour() {
		hour++;
		
		if (hour % 24 ==0) {
			hour = 0;
			daysGone++;
		}
		
		LOG.info("Hour "+hour+" day "+daysGone);
		
		for (IchangeHourListener struct: structures) {
			struct.changedHour(hour);
		}
		
		try {
			Thread.sleep(ConfigPresepio.WAIT_TIME_FOR_HOUR);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
