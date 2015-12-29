import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class Locales {

	
	/*
	 * https://countrycode.org/
	 */
	public static void main(String[] args) throws IOException {
		Properties enProp = new Properties();
		InputStream in = new FileInputStream(new File("intprefix.properties"));
		enProp.load(in);

		Reader cin = new FileReader(new File("countrycode-short-org.csv"));

		LineNumberReader lin = new LineNumberReader(cin);
		List<Country> countryList = new LinkedList<Country>();
		String line = lin.readLine();
		while (line != null) {
			String[] fields = line.split("\\t");
			Country country = new Country();
			country.setName(fields[0]);
			country.setPhonePrefix(fields[1].trim().replace("-", ""));
			String[] cells = fields[2].split("/");
			country.setISO2(cells[0].trim());
			country.setContinent(fields[3]);
			countryList.add(country);
			line = lin.readLine();
		}
		lin.close();
		
		Reader c2in = new FileReader(new File("countrycode-org.csv"));

		LineNumberReader l2in = new LineNumberReader(c2in);
		List<Country> country2List = new LinkedList<Country>();
		String line2 = l2in.readLine();
		while (line2 != null) {
			String[] fields = line2.split("\\t");
			Country country = new Country();
			country.setName(fields[0]);
			country.setISO2(fields[1].trim());
			country.setPhonePrefix(fields[2].trim().replace("-", ""));
			country.setContinent(fields[3]);
			country2List.add(country);
			line2 = l2in.readLine();
		}
		l2in.close();
		
		String[] locales = Locale.getISOCountries();

		System.out.println(Locale.getDefault().getDisplayCountry());

		for (String countryCode : locales) {

			Locale obj = new Locale("", countryCode);
			String phonePrefix = enProp.getProperty(countryCode);

			Object[] found = countryList
					.stream()
					.filter(c -> c.getISO2().equalsIgnoreCase(obj.getCountry()))
					.toArray();
			Object[] found2 = country2List
					.stream()
					.filter(c -> c.getISO2().equalsIgnoreCase(obj.getCountry()))
					.toArray();
			if (found.length > 0) {
				for (int i = 0; i < found.length; i++) {
					String[] pprefix = ((Country) found[i]).getPhonePrefix().split(",");				
					String prefix = getPrefix(pprefix,0);
					String[] p2prefix = ((Country) found2[i]).getPhonePrefix().split(",");				
					String prefix2 = getPrefix(p2prefix,0);
					System.out.println("Country Code = "
							+ ((Country) found[i]).getISO2()
							+ ", Country Name = "
							+ ((Country) found[i]).getName()
							+ ", prefix in prop = "
							+ phonePrefix
							+ " , equals = "
							+ checkPrefix(phonePrefix,prefix)
							+ ", prefix in stream = "
							+ prefix
							+ " , equals2 = "
							+ checkPrefix(phonePrefix,prefix2)
							+ ", prefix in stream2 = "
							+ prefix2
							+ ", continet = " + ((Country) found[i]).getContinent());
				}
			} else {
				System.out.println("== not found in stream ==");
				System.out.println("Country Code = " + obj.getCountry()
						+ ", Country Name = "
						+ obj.getDisplayCountry(Locale.getDefault())
						+ ", Phone prefix = " + phonePrefix);
			}
		}
		
		for(Country ctry : countryList) {
			String phonePrefix = enProp.getProperty(ctry.getISO2());
			if(phonePrefix == null) {
				System.out.println("Country ["+ctry.getISO2()+", "+ctry.getName()+"] not exists in intprefix.properties.");
			}
			
			int idx = Arrays.binarySearch(locales, ctry.getISO2());
			if (idx == -1) {
				System.out.println("Country ["+ctry.getISO2()+", "+ctry.getName()+"] not exists in locales.");
			}
		}

		System.out.println("Done");
	}

	private static Boolean checkPrefix(String phonePrefix, String prefix) {
		return phonePrefix.equals(prefix) || phonePrefix.equals("1"+prefix) || prefix.equals("1"+phonePrefix);
	}

	private static String getPrefix(String[] pprefix, int i) {
		if (pprefix.length >= i) {
			return pprefix[i];
		} else {
			return null;
		}
	}

}
