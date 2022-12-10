package liquidShadow.presepio.structure;

import java.util.Random;

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

public class Stelle implements IchangeHourListener {

	private static Logger LOG = LogManager.getLogger(Stelle.class);

	GpioPinDigitalOutput ledStar1 = null;
	GpioPinDigitalOutput ledStar2 = null;
	private int currentHour;
	boolean isShuning = false;
	int minBlink = 800;

	public Stelle() {
		final GpioController gpio = GpioFactory.getInstance();

		ledStar1 = gpio.provisionDigitalOutputPin(ConfigPresepio.LED_STAR1, "PinLED", PinState.LOW);
		ledStar2 = gpio.provisionDigitalOutputPin(ConfigPresepio.LED_STAR2, "PinLED", PinState.LOW);
	}

	@Override
	public void changedHour(int newHour) {

		currentHour = newHour;
		Thread task = new Thread() {
			@Override
			public void run() {
				if (!isShuning && currentHour > 19 || currentHour < 9) {
					isShuning = true;
					letItShin();
				}
			}
		};
		task.start();

	}

	private void letItShin() {
		LOG.info("Let it shin!");
		while (isShuning && (currentHour > 19 || currentHour < 8)) {
			try {
				Random rand = new Random();
				int blink1 = minBlink + rand.nextInt(minBlink/2);
				// LOG.info("blink at: " + blink);
				ledStar1.blink(blink1);
				int blink2 = minBlink + rand.nextInt(minBlink/2);
				ledStar2.blink(blink2);
				Thread.sleep(Math.max(blink1, blink2) + minBlink);

				// shutDownFire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ended();
	}

	@Override
	public void ended() {
		LOG.info("It's morning!");
		for (int i = 85; i >= 0; i--) {
			Gpio.wiringPiSetup();
			SoftPwm.softPwmWrite(ConfigPresepio.LED_STAR1.getAddress(), i);
			SoftPwm.softPwmWrite(ConfigPresepio.LED_STAR2.getAddress(), i);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ledStar1.blink(0);
		ledStar2.blink(0);
		ledStar1.low();
		ledStar2.low();
		isShuning = false;
	}

	@Override
	public void test() {
		LOG.info("Test Stelle");
		ledStar1.high();
		ledStar2.high();
	}

}
