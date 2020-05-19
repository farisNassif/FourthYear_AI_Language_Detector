package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import ie.gmit.sw.ui.ConsoleMenu;

public class Runner {
	public static void main(String[] args) {
		ConsoleMenu menu = new ConsoleMenu();

		menu.MainMenu();
	}
}