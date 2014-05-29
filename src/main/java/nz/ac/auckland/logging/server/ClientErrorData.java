package nz.ac.auckland.logging.server;

/**
 * author: Irina Benediktovich - http://plus.google.com/+IrinaBenediktovich
 */
public class ClientErrorData {

	// Deserializing into these fields
	public String message;
	public String file;
	public Long line;
	public Long column;
	public String errorObj;



	public String toString(){
		return str(message)+str(file)+str(line)+str(column)+str(errorObj);
	}

	/**
	 * The rest is probably not needed, will see later
	 */
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;

		if (!(obj instanceof ClientErrorData))
			return false;

		ClientErrorData other = (ClientErrorData)obj;

		return this.toString().equals(obj.toString());
		/*return eq(this.message, other.message)
				&& eq(this.file, other.file) && eq(this.line, other.line) && eq(this.column, other.column)
				&& eq(this.errorObj, other.errorObj);*/
	}

	@Override
	public int hashCode(){
		return toString().hashCode();
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

	private boolean eq(String s1, String s2){
		if (s1 == null || s1.trim().isEmpty())
			return (s2 == null || s2.trim().isEmpty());

		if (s2 == null || s2.trim().isEmpty())
			return false;

		return s1.trim().equals(s2.trim());
	}

	private boolean eq(Long l1, Long l2){
		if (l1 == null)
			return l2 == null;

		return l2 != null && l1.equals(l2);
	}
}
