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
						String resPrefix = "resource:";
						if(fileName.startsWith(resPrefix)){
							console = new PropertiesConsole(Main.class.getResourceAsStream(fileName.substring(resPrefix.length())));
						} else {
							console = new PropertiesConsole(fileName);
						}
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
			packer.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private static void error(String message){
		System.err.println("================================================");
		System.err.println("Error: " + message);
		System.err.println("================================================");
		System.exit(1);
	}
}


