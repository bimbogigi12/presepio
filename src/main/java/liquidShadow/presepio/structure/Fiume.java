package liquidShadow.presepio.structure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

import liquidShadow.presepio.ConfigPresepio;
import liquidShadow.presepio.IchangeHourListener;

public class Fiume implements IchangeHourListener{

	private static Logger LOG = LogManager.getLogger(Fiume.class);
	GpioPinDigitalOutput pinFiume = null;
	
	public Fiume() {
		final GpioController gpio = GpioFactory.getInstance();

		pinFiume = gpio.provisionDigitalOutputPin(ConfigPresepio.PIN_FIUME, "PinLED", PinState.HIGH);		
	}

	
	@Override
	public void changedHour(int newHour) {
		if ( newHour % 2 == 0/*newHour <6 || newHour >14*/) {
			LOG.info("Fiume on");
			pinFiume.low();
		} else {
			LOG.info("Fiume off");
			pinFiume.high();
		}
	}

	@Override
	public void ended() {
		pinFiume.high();
		
	}

	@Override
	public void test() {
		LOG.info("Test Fiume");
		pinFiume.low();
	}

}
