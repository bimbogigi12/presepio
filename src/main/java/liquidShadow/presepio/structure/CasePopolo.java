package liquidShadow.presepio.structure;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

import liquidShadow.presepio.ConfigPresepio;
import liquidShadow.presepio.IchangeHourListener;

public class CasePopolo implements IchangeHourListener {

	GpioPinDigitalOutput ledCase = null;
	GpioPinDigitalOutput ledCaseLontane = null;

	public CasePopolo() {
		final GpioController gpio = GpioFactory.getInstance();

		ledCase = gpio.provisionDigitalOutputPin(ConfigPresepio.LED_CASE, "PinLED", PinState.LOW);
		ledCaseLontane = gpio.provisionDigitalOutputPin(ConfigPresepio.PIN_CASE_LONTANE, "PinLED", PinState.HIGH);
		
	}

	public void changedHour(int newHour) {
		if ((newHour > 18 && newHour < 23) || ConfigPresepio.debug) {
			ledCase.high();
			ledCaseLontane.low();
		} else {
			ledCase.low();
			ledCaseLontane.high();
		}
	}

	@Override
	public void ended() {
		ledCase.low();
		ledCaseLontane.high();
	}

}
