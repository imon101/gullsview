package gullsview;


public class Main {
	public static void main(String[] argv){
		try {
			Console console = null;
			for(int i = 0; i < argv.length; i++){
				String arg = argv[i];
				if(arg.startsWith("-")){
					String svvitch = arg.substring(1);
					if("stdio".equals(svvitch)){
						console = new StdioConsole();
					} else if("properties".equals(svvitch)){
						String fileName = argv[++i];
						console = new PropertiesConsole(fileName);
					} else if("swing".equals(svvitch)){
						console = new SwingConsole();
					} else {
						error("Unknown switch \"" + svvitch + "\"");
					}
				} else {
					error("Unknown argument \"" + arg+ "\"");
				}
			}
			if(console == null) console = new SwingConsole();
			Packer packer = new Packer(console);
			packer.run();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private static void error(String message){
		System.err.println("================================================");
		System.err.println("Error: " + message);
		System.err.println("================================================");
		usage();
		System.exit(1);
	}
	
	private static void usage(){
		System.err.println("Usage: java " + Main.class.getName() + " [-stdio] [-properties FILE] [-swing]");
		System.err.println("\twhere:");
		System.err.println("\t-stdio           - runs application in text mode");
		System.err.println("\t-properties FILE - runs application in batch mode (FILE is Java Properties file containing requested values)");
		System.err.println("\t-swing           - runs application in graphical mode (default)");
	}
}


