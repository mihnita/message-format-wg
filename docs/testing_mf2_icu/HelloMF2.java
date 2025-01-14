import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.ibm.icu.message2.MessageFormatter;

public class HelloMF2 {
	public static void main(String[] argv) {
		MessageFormatter mf2 = MessageFormatter.builder()
                .setLocale(Locale.US)
                .setPattern("Hello {$user}, today is {$now :date year=numeric month=long day=numeric}!")
                .build();
		Map<String, Object> arguments = new HashMap<>();
		arguments.put("user", "John");
		arguments.put("now", new Date());
		System.out.println(mf2.formatToString(arguments));
	}
}
