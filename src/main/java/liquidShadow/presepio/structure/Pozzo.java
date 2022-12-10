package liquidShadow.presepio.structure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

import liquidShadow.presepio.ConfigPresepio;
import liquidShadow.presepio.IchangeHourListener;

public class Pozzo implements IchangeHourListener {

	GpioPinDigitalOutput pinPozzo = null;

	private static Logger LOG = LogManager.getLogger(Pozzo.class);
	
	public Pozzo() {
		final GpioController gpio = GpioFactory.getInstance();
		pinPozzo = gpio.provisionDigitalOutputPin(ConfigPresepio.PIN_POZZO, "PinLED", PinState.HIGH);
	}

	public void changedHour(int newHour) {
		
		if (pinPozzo.isHigh()) {
			pinPozzo.low();
		} 
	}

	@Override
	public void ended() {
		try {
			Thread.sleep(ConfigPresepio.WAIT_TIME_FOR_HOUR);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		pinPozzo.high();

	}

	@Override
	public void test() {
		LOG.info("Test Pozzo");
		pinPozzo.low();
		
	}

}
