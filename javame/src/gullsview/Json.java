package gullsview;

import java.io.*;
import java.util.*;


public class Json {
	public static final Object NULL = new Object();
	
	private static final String HEX_CHARS = "0123456789abcdef";
	
	private static final int STATE_AWAITING = 1;
	private static final int STATE_STRING = 2;
	private static final int STATE_STRING_ESCAPE = 3;
	private static final int STATE_STRING_ESCAPE_UNICODE = 4;
	private static final int STATE_NUMBER = 5;
	private static final int STATE_LITERAL = 6;
	
	private StringBuffer sb;
	private Object value;
	private Stack stack;
	private Hashtable keys;
	private int state;
	private int line, column;
	private int unicode;
	private int unicodeLength;
	
	public Json(){
		this.sb = new StringBuffer();
		this.stack = new Stack();
		this.keys = new Hashtable();
	}
	
	public Object parse(InputStream is) throws IOException, JsonException {
		return this.parse(new InputStreamReader(is));
	}
	
	public Object parse(Reader reader) throws IOException, JsonException {
		this.sb.setLength(0);
		this.value = null;
		this.stack.removeAllElements();
		this.keys.clear();
		this.state = STATE_AWAITING;
		this.line = 1;
		this.column = 0;
		this.lexer(reader);
		return this.value;
	}
	
	private void lexer(Reader reader) throws IOException, JsonException {
		char[] buffer = new char[1024];
		int count;
		while((count = reader.read(buffer, 0, buffer.length)) >= 0){
			for(int i = 0; i < count; i++){
				this.column++;
				char c = buffer[i];
				switch(this.state){
				case STATE_AWAITING:
					switch(c){
					case ' ':
					case '\f':
					case '\r':
					case '\n':
					case '\t':
						// NOOP
						break;
					case '"':
						this.state = STATE_STRING;
						break;
					case '-':
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						this.state = STATE_NUMBER;
						sb.append(c);
						break;
					case '{':
					case '}':
					case '[':
					case ']':
					case ':':
					case ',':
						this.special(c);
						break;
					default:
						if(Character.isUpperCase(c) || Character.isLowerCase(c)){
							this.state = STATE_LITERAL;
							sb.append(c);
						} else {
							throw new JsonException(this.line, this.column, "Unexpected char in awaiting state: '" + c + "'");
						}
					}
					break;
				case STATE_STRING:
					switch(c){
					case '"':
						this.string(sb.toString());
						this.sb.setLength(0);
						this.state = STATE_AWAITING;
						break;
					case '\\':
						this.state = STATE_STRING_ESCAPE;
						break;
					default:
						sb.append(c);
					}
					break;
				case STATE_STRING_ESCAPE:
					switch(c){
					case 'b':
						sb.append('\b');
						this.state = STATE_STRING;
						break;
					case 'f':
						sb.append('\f');
						this.state = STATE_STRING;
						break;
					case 'n':
						sb.append('\n');
						this.state = STATE_STRING;
						break;
					case 'r':
						sb.append('\r');
						this.state = STATE_STRING;
						break;
					case 't':
						sb.append('\t');
						this.state = STATE_STRING;
						break;
					case 'u':
						this.state = STATE_STRING_ESCAPE_UNICODE;
						this.unicode = 0;
						this.unicodeLength = 0;
						break;
					default:
						sb.append(c);
						this.state = STATE_STRING;
					}
					break;
				case STATE_STRING_ESCAPE_UNICODE:
					int hex = HEX_CHARS.indexOf(Character.toLowerCase(c));
					if(hex < 0) throw new JsonException(this.line, this.column, "Expected hex number, got '" + c + "'");
					this.unicode <<= 4;
					this.unicode |= hex;
					this.unicodeLength++;
					if(this.unicodeLength == 4){
						sb.append((char) this.unicode);
						this.state = STATE_STRING;
					}
					break;
				case STATE_NUMBER:
					switch(c){
					case ' ':
					case '\f':
					case '\r':
					case '\n':
					case '\t':
					case '{':
					case '}':
					case '[':
					case ']':
					case ':':
					case ',':
						i--;
						c = 0;
						this.number(sb.toString());
						this.sb.setLength(0);
						this.state = STATE_AWAITING;
						break;
					case '-':
					case '.':
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
					case 'e':
					case 'E':
						sb.append(c);
						break;
					default:
						throw new JsonException(this.line, this.column, "Unexpected character while reading number: '" + c + "'");
					}
					break;
				case STATE_LITERAL:
					switch(c){
					case ' ':
					case '\f':
					case '\r':
					case '\n':
					case '\t':
					case '{':
					case '}':
					case '[':
					case ']':
					case ':':
					case ',':
						i--;
						c = 0;
						this.literal(sb.toString());
						this.sb.setLength(0);
						this.state = STATE_AWAITING;
						break;
					default:
						sb.append(c);
					}
					break;
				default:
					throw new JsonException(this.line, this.column, "Unknown state " + this.state);
				}
				if(c == '\n'){
					this.line++;
					this.column = 0;
				}
			}
		}
		switch(this.state){
		case STATE_AWAITING:
			break;
		case STATE_STRING:
		case STATE_STRING_ESCAPE:
		case STATE_STRING_ESCAPE_UNICODE:
			throw new JsonException(this.line, this.column, "Premature end of input - string value unread");
		case STATE_NUMBER:
			this.number(sb.toString());
			this.sb.setLength(0);
			break;
		case STATE_LITERAL:
			this.literal(sb.toString());
			this.sb.setLength(0);
			break;
		}
	}
	
	private void string(String s) throws JsonException {
		if(!this.stack.empty()){
			Object top = this.stack.peek();
			if(top instanceof Hashtable){
				if(this.keys.get(top) == null){
					this.keys.put(top, s);
					return;
				}
			}
		}
		this.value(s);
	}
	
	private void literal(String s) throws JsonException {
		Object value;
		if("true".equals(s)){
			value = Boolean.TRUE;
		} else if("false".equals(s)){
			value = Boolean.FALSE;
		} else if("null".equals(s)){
			value = NULL;
		} else {
			throw new JsonException(this.line, this.column, "Unknown token \"" + s + "\"");
		}
		this.value(value);
	}
	
	private void number(String s) throws JsonException {
		Double d;
		try {
			d = Double.valueOf(s);
		} catch (Exception e){
			throw new JsonException(this.line, this.column, "Cannot parse value as number \"" + s + "\"");
		}
		this.value(d);
	}
	
	private void value(Object value) throws JsonException {
		if(this.stack.empty()){
			if(this.value != null)
				throw new JsonException(this.line, this.column, "Only one value on top level allowed");
			this.value = value;
		} else {
			Object top = this.stack.peek();
			if(top instanceof Vector){
				Vector v = (Vector) top;
				v.addElement(value);
			} else {
				Hashtable ht = (Hashtable) top;
				String key = (String) this.keys.remove(top);
				if(key == null) throw new JsonException(this.line, this.column, "Missing key for value");
				ht.put(key, value);
			}
		}
	}
	
	private void special(char c) throws JsonException {
		Object top;
		Vector v;
		Hashtable ht;
		
		switch(c){
		case '{':
			ht = new Hashtable();
			this.value(ht);
			this.stack.push(ht);
			break;
		case '}':
			if(this.stack.empty())
				throw new JsonException(this.line, this.column, "Unmatched object brackets { and }");
			top = this.stack.pop();
			if(!(top instanceof Hashtable))
				throw new JsonException(this.line, this.column, "Unmatched object brackets { and }");
			break;
		case '[':
			v = new Vector();
			this.value(v);
			this.stack.push(v);
			break;
		case ']':
			if(this.stack.empty())
				throw new JsonException(this.line, this.column, "Unmatched array brackets [ and ]");
			top = this.stack.pop();
			if(!(top instanceof Vector))
				throw new JsonException(this.line, this.column, "Unmatched array brackets [ and ]");
			break;
		case ':':
			if(this.stack.empty()) throw new JsonException(this.line, this.column, "Cannot use colon on the top level");
			top = this.stack.peek();
			if(!(top instanceof Hashtable)) throw new JsonException(this.line, this.column, "Colon used outside object structure");
			if(this.keys.get(top) == null) throw new JsonException(this.line, this.column, "Expecting key before colon");
			break;
		case ',':
			if(this.stack.empty()) throw new JsonException(this.line, this.column, "Cannot use colon on the top level");
			top = this.stack.peek();
			if(top instanceof Vector){
				v = (Vector) top;
				if(v.size() == 0) throw new JsonException(this.line, this.column, "Cannot use comma before first key-value pair");
			} else if(top instanceof Hashtable){
				ht = (Hashtable) top;
				if(ht.size() == 0) throw new JsonException(this.line, this.column, "Cannot use comma before first value");
			}
			break;
		}
	}
	
	public void format(Object value, OutputStream os) throws IOException {
		this.format(value, new OutputStreamWriter(os, "UTF-8"));
	}
	
	public void format(Object value, Writer writer) throws IOException {
		this.formatRec(value, writer, 0);
		writer.flush();
	}
	
	private void nl(Writer writer) throws IOException {
		writer.write('\n');
	}
	
	private void formatRec(Object value, Writer writer, int depth) throws IOException {
		if(value == NULL){
			writer.write("null");
		} else if(value instanceof String){
			writer.write('"');
			writer.write((String) value);
			writer.write('"');
		} else if(value instanceof Double){
			writer.write(((Double) value).toString());
		} else if(value instanceof Boolean){
			writer.write(((Boolean) value).booleanValue() ? "true" : "false");
		} else if(value instanceof Vector){
			Vector v = (Vector) value;
			writer.write('[');
			if(!v.isEmpty()){
				this.nl(writer);
				Enumeration en = ((Vector) value).elements();
				for(int i = 0; en.hasMoreElements(); i++){
					Object item = en.nextElement();
					if(i > 0){
						writer.write(',');
						this.nl(writer);
					}
					this.indent(writer, depth + 1);
					this.formatRec(item, writer, depth + 1);
				}
				this.nl(writer);
				this.indent(writer, depth);
			}
			writer.write(']');
		} else if(value instanceof Hashtable){
			Hashtable ht = (Hashtable) value;
			writer.write('{');
			if(!ht.isEmpty()){
				this.nl(writer);
				Enumeration en = ht.keys();
				for(int i = 0; en.hasMoreElements(); i++){
					String key = (String) en.nextElement();
					Object item = ht.get(key);
					if(i > 0){
						writer.write(',');
						this.nl(writer);
					}
					this.indent(writer, depth + 1);
					writer.write('"');
					writer.write(key);
					writer.write("\": ");
					this.formatRec(item, writer, depth + 1);
				}
				this.nl(writer);
				this.indent(writer, depth);
			}
			writer.write('}');
		} else {
			writer.write("UNKNOWN(");
			writer.write(String.valueOf(value));
			writer.write(')');
		}
	}
	
	private void indent(Writer writer, int count) throws IOException {
		for(int i = 0; i < count; i++) writer.write("    ");
	}
}


