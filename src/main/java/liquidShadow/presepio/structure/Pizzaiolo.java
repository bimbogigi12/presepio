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

public class Pizzaiolo implements IchangeHourListener {

private static Logger LOG = LogManager.getLogger(Pizzaiolo.class);
	
	GpioPinDigitalOutput ledPizza = null;
	boolean isBurning = false;
	private int currentHour;
	
	public Pizzaiolo() {
		final GpioController gpio = GpioFactory.getInstance();

		ledPizza = gpio.provisionDigitalOutputPin(ConfigPresepio.LED_PIZZAIOLO, "PinLED", PinState.HIGH);
	}
	
	public void changedHour(int newHour) {
		currentHour = newHour;
		Thread task = new Thread() {
			@Override
			public void run() {
				if (/*!isBurning && */isTimeToBurn()) {
					//isBurning = true;
					ledPizza.low();
					//letItBurn();
				} else {
					ledPizza.high();
					//isBurning = false;
				}
			}
		};
		task.start();

	}
	
	private boolean isTimeToBurn() {
		return (currentHour >=11 && currentHour <15) || (currentHour >=18 && currentHour <23);
	}

	@Override
	public void ended() {
		isBurning = false;
		ledPizza.high();
	}

	public void letItBurn() {
		LOG.info("It's Pizza time!");
		while (isTimeToBurn()) {
			try {
				Random rand = new Random();
				int blink = 120 + rand.nextInt(130);
				ledPizza.blink(blink);
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
		SoftPwm.softPwmCreate(ConfigPresepio.LED_PIZZAIOLO.getAddress(), 0, 100);
		for (int i = 85; i >= 0; i--) {
			Gpio.wiringPiSetup();
			SoftPwm.softPwmWrite(ConfigPresepio.LED_PIZZAIOLO.getAddress(), i);
			Thread.sleep(10);
		}
		ledPizza.blink(0);
		ledPizza.high();
	}
	
}
