
public class InvalidEmailFormat extends RuntimeException{
	InvalidEmailFormat(){
		super("\nPlease Enter valid Email ID.");
	}
	InvalidEmailFormat(String msg){
		super(msg);
	}
}
