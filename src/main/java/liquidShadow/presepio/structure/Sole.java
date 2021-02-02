package liquidShadow.presepio.structure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.wiringpi.SoftPwm;

import liquidShadow.presepio.ConfigPresepio;
import liquidShadow.presepio.IchangeHourListener;

public class Sole implements IchangeHourListener {

	private static Logger LOG = LogManager.getLogger(Sole.class);
	
	GpioPinDigitalOutput ledSun = null;
	private int currentHour;

	public Sole() {
		final GpioController gpio = GpioFactory.getInstance();

		ledSun = gpio.provisionDigitalOutputPin(ConfigPresepio.LED_SOLE, "PinLED", PinState.LOW);
		SoftPwm.softPwmCreate(ConfigPresepio.LED_SOLE.getAddress(), 0, maxSunPower());
		
		LOG.info("sun max power "+maxSunPower());
	}
	
	private int maxSunPower() {
		return (12 * 2) - ((ConfigPresepio.SUNRISE-1)*2);
	}

	@Override
	public void changedHour(int newHour) {
		currentHour = newHour;
		Thread task = new Thread() {
			@Override
			public void run() {
				try {
					SoftPwm.softPwmWrite(ConfigPresepio.LED_SOLE.getAddress(), getForceSun());

					LOG.info("sun shining at "+getForceSun());
					
					Thread.sleep(ConfigPresepio.WAIT_TIME_FOR_HOUR / 2);

					if (getForceSun() >0) {
						int delta = currentHour > 12? -1 : 1; 
						SoftPwm.softPwmWrite(ConfigPresepio.LED_SOLE.getAddress(), getForceSun()+ delta );
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		task.start();

	}

	private int getForceSun() {
		if (currentHour < ConfigPresepio.SUNRISE || currentHour > ConfigPresepio.SUNSET)
			return 0;
		int value = 0;
		if (currentHour > 12) {
			value = (ConfigPresepio.SUNSET + 1 - currentHour)*2;
		} else {
			value = (currentHour - ConfigPresepio.SUNRISE + 1) *2;
		}
		return value;
	}

	@Override
	public void ended() {
		SoftPwm.softPwmWrite(ConfigPresepio.LED_SOLE.getAddress(), 0);
	}

}
