package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import ie.gmit.sw.ui.ConsoleMenu;

public class Runner {
	public static void main(String[] args) {
		ConsoleMenu menu = new ConsoleMenu();

		menu.MainMenu();

		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("./wili-2018-Small-11750-Edited.txt")));
			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				String[] fileRecord = line.trim().split("@");

				if (fileRecord.length != 2)
					continue;
				System.out.println(fileRecord[0]);
				System.out.println(fileRecord[1]);
				
			}

			bufferedReader.close();

		} catch (Exception exception) {
			exception.printStackTrace();
		}


		
	}
}