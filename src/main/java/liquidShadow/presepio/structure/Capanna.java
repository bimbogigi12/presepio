package liquidShadow.presepio.structure;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftPwm;

import liquidShadow.presepio.ConfigPresepio;
import liquidShadow.presepio.IchangeHourListener;

public class Capanna implements IchangeHourListener {

	GpioPinDigitalOutput ledMangiatoia = null;
	GpioPinDigitalOutput ledFallingStar = null;
	GpioPinDigitalOutput ledCapanna = null;

	private boolean isRising = false;
	private boolean isClosed = false;

	private static Logger LOG = LogManager.getLogger(Capanna.class);
	private int currentHour;

	public void changedHour(int newHour) {
		currentHour = newHour;
		isClosed = false;
		Thread task = new Thread() {

			@Override
			public void run() {
				if (currentHour == 0) {
					try {
						letJesusBorn();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					if (!isRising) {
						closeMangiatoia();
					}
				}

				if (currentHour >= 1 && currentHour < 9 && !isRising) {
					showFallingStar();
				} else {
					closeFallingStar();
				}
				
				if (currentHour > 18 && currentHour < 23) {
					ledCapanna.high();
				} else {
					ledCapanna.low();
				}

			}

		};

		task.start();

	}

	private void closeFallingStar() {
		ledFallingStar.low();
	}

	private void closeMangiatoia() {
		// LOG.info("Mangiatoioa led "+LedMangiatoia.isHigh());
		/*
		 * while(LedMangiatoia.isHigh() || !isRising) { LedMangiatoia.low(); }
		 */

		SoftPwm.softPwmWrite(ConfigPresepio.LED_MANGIATOIA.getAddress(), 0);
	}

	public Capanna() {
		final GpioController gpio = GpioFactory.getInstance();

		ledMangiatoia = gpio.provisionDigitalOutputPin(ConfigPresepio.LED_MANGIATOIA, "PinLED", PinState.LOW);
		ledFallingStar = gpio.provisionDigitalOutputPin(ConfigPresepio.LED_STELLA_CADENTE, "PinLED", PinState.LOW);
		ledCapanna = gpio.provisionDigitalOutputPin(ConfigPresepio.LED_CAPANNA, "PinLED", PinState.LOW);
		
	}

	private void letJesusBorn() throws InterruptedException {
		LOG.info("Jesus is born!");
		isRising = true;
		Gpio.wiringPiSetup();

		SoftPwm.softPwmCreate(ConfigPresepio.LED_MANGIATOIA.getAddress(), 0, 100);

		for (int i = 0; i <= 100 && !isClosed; i++) {
			SoftPwm.softPwmWrite(ConfigPresepio.LED_MANGIATOIA.getAddress(), i);
			Thread.sleep(15);
		}

		//ledMangiatoia.high();
		isRising = false;
	}

	private void showFallingStar() {

		if (ledFallingStar.isLow()) {
			LOG.info("Falling star shining!");
			ledFallingStar.high();
		}
	}

	@Override
	public void ended() {
		LOG.info("Closing Capanna");
		closeMangiatoia();
		closeFallingStar();
		ledCapanna.low();
		isClosed = true;
	}

}
