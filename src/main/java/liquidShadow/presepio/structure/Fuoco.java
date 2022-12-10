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

public class Fuoco implements IchangeHourListener {

	private static Logger LOG = LogManager.getLogger(Fuoco.class);
	
	GpioPinDigitalOutput ledFuoco = null;
	GpioPinDigitalOutput pinFuocoFlat = null;
	boolean isBurning = false;
	private int currentHour;

	public Fuoco() {
		final GpioController gpio = GpioFactory.getInstance();

		ledFuoco = gpio.provisionDigitalOutputPin(ConfigPresepio.LED_FUOCO, "PinLED", PinState.LOW);
		pinFuocoFlat = gpio.provisionDigitalOutputPin(ConfigPresepio.PIN_FUOCO, "PinLED", PinState.HIGH);
	}

	public void changedHour(int newHour) {
		currentHour = newHour;
		//LOG.info("isBurning "+isBurning+ " at hour "+currentHour+" state "+ledFuoco.getState());
		Thread task = new Thread() {
			@Override
			public void run() {
				if (!isBurning && currentHour >=18 && currentHour <23) {
					isBurning = true;
					letItBurn();
					pinFuocoFlat.high();
				} else {
					pinFuocoFlat.low();
				} 
			}
		};
		task.start();
	}

	public void letItBurn() {
		LOG.info("Let it burn!");
		while (currentHour >=18 && currentHour <23) {
			try {
				Random rand = new Random();
				int blink = 120 + rand.nextInt(130);
				//LOG.info("blink at: " + blink);
				ledFuoco.blink(blink);
				Thread.sleep(1000);
				
				//shutDownFire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (isBurning) {
			try {
				shutDownFire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			isBurning = false;
		}
	}

	private void shutDownFire() throws InterruptedException {
		LOG.info("Shuting Down fire!");
		SoftPwm.softPwmCreate(ConfigPresepio.LED_FUOCO.getAddress(), 0, 100);
		for (int i = 85; i >= 0; i--) {
			Gpio.wiringPiSetup();
			SoftPwm.softPwmWrite(ConfigPresepio.LED_FUOCO.getAddress(), i);
			Thread.sleep(10);
		}
		ledFuoco.blink(0);
		ledFuoco.low();
	}

	@Override
	public void ended() {
		ledFuoco.low();
		pinFuocoFlat.high();
		isBurning = false;
	}

	@Override
	public void test() {
		LOG.info("Test Fuoco");
		pinFuocoFlat.high();
		SoftPwm.softPwmCreate(ConfigPresepio.LED_FUOCO.getAddress(), 0, 100);
		SoftPwm.softPwmWrite(ConfigPresepio.LED_FUOCO.getAddress(), 100);
	}

}
