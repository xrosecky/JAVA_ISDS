package cz.abclinuxu.datoveschranky.examples;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 *
 * @author xrosecky
 */
public class Main {

    public static void main(String[] args) {
	String classToLoad = args[0];
	String[] nargs = Arrays.copyOfRange(args, 1, args.length);
	try {
	    Class<?> clazz = Main.class.forName(classToLoad);
	    Class[] argTypes = { args.getClass() };
	    Method met = clazz.getMethod("main", argTypes);
	    // String[] passedArgs = new String[]{ nargs };
	    Object passedArgs[] = { nargs };
	    met.invoke(null, passedArgs);
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
}
