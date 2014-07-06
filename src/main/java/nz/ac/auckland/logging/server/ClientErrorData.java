package nz.ac.auckland.logging.server;

/**
 * Represents an error message that needs to be logged - data that comes from client and gets deserialized into this.
 *
 * NOTE. This class is also used as a key in the collection. Make sure equals and hashCode are valid.
 *
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
public class ClientErrorData {

	// Deserializing into these fields
	public String message;
	public String file;
	public Long line;
	public Long column;
	public String errorObj;

	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;

		if (!(obj instanceof ClientErrorData))
			return false;

		return this.toString().equals(obj.toString());
	}

	@Override
	public int hashCode(){
		return toString().hashCode();
	}

	public String toString(){
		return str(message)+str(file)+str(line)+str(column)+str(errorObj);
	}

	private String str(String s){
		if (s == null)
			return "";
		else
			return s.trim();
	}

	private String str(Long l){
		if (l == null)
			return "0";
		else
			return l.toString();
	}
}
