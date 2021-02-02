package liquidShadow.presepio.structure;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

import liquidShadow.presepio.ConfigPresepio;
import liquidShadow.presepio.IchangeHourListener;

public class Ponte implements IchangeHourListener {

	GpioPinDigitalOutput ledPonte = null;

	public Ponte() {
		final GpioController gpio = GpioFactory.getInstance();

		ledPonte = gpio.provisionDigitalOutputPin(ConfigPresepio.LED_PONTE, "PinLED", PinState.LOW);
	}

	public void changedHour(int newHour) {
		if ((newHour > 16 || newHour > 8) || ConfigPresepio.debug) {
			ledPonte.high();
		} else {
			ledPonte.low();
		}
	}

	@Override
	public void ended() {
		ledPonte.low();

	}

}
